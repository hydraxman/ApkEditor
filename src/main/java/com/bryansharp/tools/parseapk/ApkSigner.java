package com.bryansharp.tools.parseapk;

import sun.misc.BASE64Encoder;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.*;
import java.util.zip.ZipEntry;

public class ApkSigner {
    private static X509Certificate readPublicKey(File file)
            throws IOException, GeneralSecurityException {
        FileInputStream input = new FileInputStream(file);
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(input);
        } finally {
            input.close();
        }
    }

    private static String readPassword(File keyFile) {
        System.out.print("Enter password for " + keyFile + " (password will not be hidden): ");
        System.out.flush();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        try {
            return stdin.readLine();
        } catch (IOException ex) {
        }
        return null;
    }

    private static KeySpec decryptPrivateKey(byte[] encryptedPrivateKey, File keyFile)
            throws GeneralSecurityException {
        EncryptedPrivateKeyInfo epkInfo;
        try {
            epkInfo = new EncryptedPrivateKeyInfo(encryptedPrivateKey);
        } catch (IOException ex) {
            return null;
        }

        char[] password = readPassword(keyFile).toCharArray();

        SecretKeyFactory skFactory = SecretKeyFactory.getInstance(epkInfo.getAlgName());
        Key key = skFactory.generateSecret(new PBEKeySpec(password));

        Cipher cipher = Cipher.getInstance(epkInfo.getAlgName());
        cipher.init(2, key, epkInfo.getAlgParameters());
        try {
            return epkInfo.getKeySpec(cipher);
        } catch (InvalidKeySpecException ex) {
            System.err.println("signapk: Password for " + keyFile + " may be bad.");
            throw ex;
        }
    }

    private static PrivateKey readPrivateKey(File file)
            throws IOException, GeneralSecurityException {
        DataInputStream input = new DataInputStream(new FileInputStream(file));
        try {
            byte[] bytes = new byte[(int) file.length()];
            input.read(bytes);

            KeySpec spec = decryptPrivateKey(bytes, file);
            if (spec == null) {
                spec = new PKCS8EncodedKeySpec(bytes);
            }
            try {
                return KeyFactory.getInstance("RSA").generatePrivate(spec);
            } catch (InvalidKeySpecException ex) {
                return KeyFactory.getInstance("DSA").generatePrivate(spec);
            }
        } finally {
            input.close();
        }
    }

    private static Manifest addDigestsToManifest(JarFile jar)
            throws IOException, GeneralSecurityException {
        Manifest input = jar.getManifest();

        Manifest output = new Manifest();
        Attributes main = output.getMainAttributes();

        if (input != null) {
            main.putAll(input.getMainAttributes());
        } else {
            main.putValue("Manifest-Version", "1.0");
            main.putValue("Created-By", "1.0 (Android SignApk)");
        }

        BASE64Encoder base64 = new BASE64Encoder();
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] buffer = new byte[4096];

        for (Enumeration e = jar.entries(); e.hasMoreElements(); ) {
            JarEntry entry = (JarEntry) e.nextElement();
            String name = entry.getName();
            if ((!entry.isDirectory()) && (!name.equals("META-INF/MANIFEST.MF"))) {
                InputStream data = jar.getInputStream(entry);
                int num;
                while ((num = data.read(buffer)) > 0) {
                    md.update(buffer, 0, num);
                }

                Attributes attr = null;
                if (input != null) {
                    attr = input.getAttributes(name);
                }
                attr = attr != null ? new Attributes(attr) : new Attributes();
                attr.putValue("SHA1-Digest", base64.encode(md.digest()));
                output.getEntries().put(name, attr);
            }
        }

        return output;
    }

    private static void writeSignatureFile(Manifest manifest, OutputStream out)
            throws IOException, GeneralSecurityException {
        Manifest sf = new Manifest();
        Attributes main = sf.getMainAttributes();
        main.putValue("Signature-Version", "1.0");
        main.putValue("Created-By", "1.0 (Android SignApk)");

        BASE64Encoder base64 = new BASE64Encoder();
        MessageDigest md = MessageDigest.getInstance("SHA1");
        PrintStream print = new PrintStream(new DigestOutputStream(new ByteArrayOutputStream(), md), true, "UTF-8");

        manifest.write(print);
        print.flush();
        main.putValue("SHA1-Digest-Manifest", base64.encode(md.digest()));

        Map<String, Attributes> entries = manifest.getEntries();
        for (Map.Entry<String, Attributes> entry : entries.entrySet()) {
            print.print("Name: " + (String) entry.getKey() + "\r\n");
            for (Map.Entry att : ((Attributes) entry.getValue()).entrySet()) {
                print.print(att.getKey() + ": " + att.getValue() + "\r\n");
            }
            print.print("\r\n");
            print.flush();

            Attributes sfAttr = new Attributes();
            sfAttr.putValue("SHA1-Digest", base64.encode(md.digest()));
            sf.getEntries().put(entry.getKey(), sfAttr);
        }

        sf.write(out);
    }

    private static void writeSignatureBlock(Signature signature, X509Certificate publicKey, OutputStream out)
            throws IOException, GeneralSecurityException {
        SignerInfo signerInfo = new SignerInfo(new X500Name(publicKey.getIssuerX500Principal().getName()), publicKey.getSerialNumber(), AlgorithmId.get("SHA1"), AlgorithmId.get("RSA"), signature.sign());

        PKCS7 pkcs7 = new PKCS7(new AlgorithmId[]{AlgorithmId.get("SHA1")}, new ContentInfo(ContentInfo.DATA_OID, null), new X509Certificate[]{publicKey}, new SignerInfo[]{signerInfo});

        pkcs7.encodeSignedData(out);
    }

    private static void copyFiles(Manifest manifest, JarFile in, JarOutputStream out)
            throws IOException {
        byte[] buffer = new byte[4096];

        Map<String, Attributes> entries = manifest.getEntries();
        for (String name : entries.keySet()) {
            JarEntry inEntry = in.getJarEntry(name);
            if (inEntry.getMethod() == 0) {
                out.putNextEntry(new JarEntry(inEntry));
            } else {
                out.putNextEntry(new JarEntry(name));
            }

            InputStream data = in.getInputStream(inEntry);
            int num;
            while ((num = data.read(buffer)) > 0) {
                out.write(buffer, 0, num);
            }
            out.flush();
        }
    }

    public static void replaceInApk(String nameToBeReplaced, File replaceTo, JarFile in, String outFile)
            throws IOException {
        byte[] buffer = new byte[4096];
        Manifest manifest = in.getManifest();
        Map<String, Attributes> entries = manifest.getEntries();
        JarOutputStream out = new JarOutputStream(new FileOutputStream(outFile));
        for (String name : entries.keySet()) {
            JarEntry inEntry = in.getJarEntry(name);
            if (inEntry.getMethod() == ZipEntry.STORED) {
                out.putNextEntry(new JarEntry(inEntry));
            } else {
                out.putNextEntry(new JarEntry(name));
            }
            InputStream data = null;
            if (name.equals(nameToBeReplaced)) {
                data = new FileInputStream(replaceTo);
            } else {
                data = in.getInputStream(inEntry);
            }
            int num;
            while ((num = data.read(buffer)) > 0) {
                out.write(buffer, 0, num);
            }
            out.flush();
            data.close();
        }
        out.close();
    }

    public static void signFile(String publickeyX509, String privatekeyPk8, String inputFile, String outputFile) {

        JarFile inputJar = null;
        JarOutputStream outputJar = null;
        try {
            X509Certificate publicKey = readPublicKey(new File(publickeyX509));
            PrivateKey privateKey = readPrivateKey(new File(privatekeyPk8));
            inputJar = new JarFile(new File(inputFile), false);
            outputJar = new JarOutputStream(new FileOutputStream(outputFile));
            //设置压缩等级
            outputJar.setLevel(9);
            //生成manifest信息
            Manifest manifest = addDigestsToManifest(inputJar);
            //删去证书项目
            manifest.getEntries().remove("META-INF/CERT.SF");
            manifest.getEntries().remove("META-INF/CERT.RSA");

            outputJar.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            manifest.write(outputJar);

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            outputJar.putNextEntry(new JarEntry("META-INF/CERT.SF"));
            writeSignatureFile(manifest, new SignatureOutputStream(outputJar, signature));

            outputJar.putNextEntry(new JarEntry("META-INF/CERT.RSA"));
            writeSignatureBlock(signature, publicKey, outputJar);

            copyFiles(manifest, inputJar, outputJar);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                if (inputJar != null) inputJar.close();
                if (outputJar != null) outputJar.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private static class SignatureOutputStream extends FilterOutputStream {
        private Signature mSignature;

        public SignatureOutputStream(OutputStream out, Signature sig) {
            super(out);
            this.mSignature = sig;
        }

        public void write(int b) throws IOException {
            try {
                this.mSignature.update((byte) b);
            } catch (SignatureException e) {
                throw new IOException("SignatureException: " + e);
            }
            super.write(b);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            try {
                this.mSignature.update(b, off, len);
            } catch (SignatureException e) {
                throw new IOException("SignatureException: " + e);
            }
            super.write(b, off, len);
        }
    }
}