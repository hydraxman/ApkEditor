package com.bryansharp.editor;

import com.bryansharp.tools.parseapk.ApkSigner;
import com.bryansharp.tools.parseapk.manifest.ParserChunkUtils;
import com.bryansharp.tools.parseapk.manifest.XmlEditor;
import com.bryansharp.tools.parseapk.utils.ZipUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.jar.JarFile;

public class TestEditor {
    @Test
    public void testDebugApp() throws Exception {


        ApkSigner.replaceInApk("classes.dex", new File("out.dex"), new JarFile("com.android.vending.apk"), "com.android.vending-replaced.apk");
        ApkSigner.signFile("publickey.rsa.pem", "privatekey.pk8", "com.android.vending-replaced.apk", "com.android.vending-signed.apk");
    }

    @Test
    public void testZipExtract() throws Exception {
        String homeDir = System.getProperty("user.home");
        String workingDir = homeDir + File.separator + "Desktop" + File.separator + "buildOpt";
        String sourceApk = workingDir + File.separator + "launcher-Beta-5.5.0.1051865-hockeyapp-publicRelease-signed-aligned.apk";
        File extractFile = ZipUtils.extractFileFromZip(sourceApk, workingDir, "AndroidManifest.xml");

    }

    @Test
    public void testMM() throws Exception {
        String homeDir = System.getProperty("user.home");
        String workingDir = homeDir + File.separator + "Desktop" + File.separator + "buildOpt";
        String sourceApk = workingDir + File.separator + "launcher-Beta-5.5.0.1051865-hockeyapp-publicRelease-signed-aligned.apk";
        File extractFile = ZipUtils.extractFileFromZip(sourceApk, workingDir, "AndroidManifest.xml");

        ParserChunkUtils.xmlStruct.byteSrc = IOUtils.toByteArray(new FileInputStream(extractFile));

        XmlEditor.addAttr("application", "package", "debuggable", "true");

        String outputFile = workingDir + File.separator + "AndroidManifest-modified.xml";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            fos.write(ParserChunkUtils.xmlStruct.byteSrc);
            fos.close();
        } catch (Exception e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testMM2() throws Exception {
        String homeDir = System.getProperty("user.home");
        String workingDir = homeDir + File.separator + "Desktop" + File.separator + "buildOpt";
        String sourceApk = workingDir + File.separator + "launcher-Beta-5.5.0.1051865-hockeyapp-publicRelease-signed-aligned.apk";
        File extractFile = ZipUtils.extractFileFromZip(sourceApk, workingDir, "AndroidManifest.xml");
        String outputFile = workingDir + File.separator + "AndroidManifest-modified.xml";

        File apkJar = new File("AXMLEditor.jar");
        Runtime runtime = Runtime.getRuntime();
        // java -jar AXMLEditor.jar -attr -m application package debuggable true AndroidManifest.xml AndroidManifest_out.xml
        String command = String.format("java -jar %s  -attr -i application package debuggable true %s %s", apkJar.getAbsoluteFile(), extractFile.getAbsoluteFile(), outputFile);
        Process process = runtime.exec(command);
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("gbk")));
        String line;
        while ((line = outputReader.readLine()) != null) {
            System.out.println(line);
        }
        outputReader.close();
        int code = process.waitFor();
        if (code != 0) {
            return;
        }

        String outApk = workingDir + File.separator + "launcher-Beta-5.5.0.1051865-debuggable-2.apk";
        ApkSigner.replaceInApk("AndroidManifest.xml", new File(outputFile), new JarFile(sourceApk), outApk);
        String outApkSigned = workingDir + File.separator + "launcher-Beta-5.5.0.1051865-debuggable-signed-2.apk";
        ApkSigner.signFile("publickey.rsa.pem", "privatekey.pk8", outApk, outApkSigned);
    }

    @Test
    public void testModifyManifestByApkTool() throws Exception {
        String homeDir = System.getProperty("user.home");
        String workingDir = homeDir + File.separator + "Desktop" + File.separator + "buildOpt";
        String sourceApk = workingDir + File.separator + "launcher-Beta-5.5.0.1051865-hockeyapp-publicRelease-signed-aligned.apk";
        File apkJar = new File("apktool_2.4.0.jar");
        Runtime runtime = Runtime.getRuntime();
        String command = String.format("java -jar %s d -o %s %s", apkJar.getAbsoluteFile(), workingDir + File.separator + "output-dir", sourceApk);
        Process process = runtime.exec(command);
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("utf8")));
        String line;
        while ((line = outputReader.readLine()) != null) {
            System.out.println(line);
        }
        outputReader.close();
        int code = process.waitFor();
        if (code != 0) {
            return;
        }
    }

    @Test
    public void assembleModifyManifestByApkTool() throws Exception {
        String homeDir = System.getProperty("user.home");
        String workingDir = homeDir + File.separator + "Desktop" + File.separator + "buildOpt";
        String newApk = workingDir + File.separator + "launcher-Beta-5.5.0.1051865-modified.apk";
        File apkJar = new File("apktool_2.4.0.jar");
        Runtime runtime = Runtime.getRuntime();
        String command = String.format("java -jar %s b -o %s %s", apkJar.getAbsoluteFile(), newApk, workingDir + File.separator + "output-dir");
        System.out.println(command);
        Process process = runtime.exec(command);
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("utf8")));
        String line;
        while ((line = outputReader.readLine()) != null) {
            System.out.println(line);
        }
        outputReader.close();
        int code = process.waitFor();
        if (code != 0) {
            return;
        }
        System.out.println("start to sign apk");
        String outApkSigned = workingDir + File.separator + "launcher-Beta-5.5.0.1051865-debuggable-new-signed.apk";

        ApkSigner.signFile("publickey.rsa.pem", "privatekey.pk8", newApk, outApkSigned);
    }

    @Test
    public void testModifyManifest() throws Exception {
        String homeDir = System.getProperty("user.home");
        String workingDir = homeDir + File.separator + "Desktop" + File.separator + "buildOpt";
        String sourceApk = workingDir + File.separator + "com.teslacoilsw.launcher_6.1.11-61161.apk";
        File extractFile = ZipUtils.extractFileFromZip(sourceApk, workingDir, "AndroidManifest.xml");

        ParserChunkUtils.xmlStruct.byteSrc = IOUtils.toByteArray(new FileInputStream(extractFile));

        XmlEditor.modifyAttr("application", "package", "label", "Resigned");
//        XmlEditor.addAttr("application", "package", "debuggable", "true");

        String outputFile = workingDir + File.separator + "AndroidManifest-modified-1.xml";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            fos.write(ParserChunkUtils.xmlStruct.byteSrc);
            fos.close();
        } catch (Exception e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String outApk = workingDir + File.separator + "com.teslacoilsw.launcher_6.1.11-61161-debug.apk";
        ApkSigner.replaceInApk("AndroidManifest.xml", new File(outputFile), new JarFile(sourceApk), outApk);
        String outApkSigned = workingDir + File.separator + "com.teslacoilsw.launcher_6.1.11-61161-debug-signed.apk";
        ApkSigner.signFile("publickey.rsa.pem", "privatekey.pk8", outApk, outApkSigned);
    }
}
