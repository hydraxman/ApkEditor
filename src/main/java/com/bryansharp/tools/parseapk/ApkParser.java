package com.bryansharp.tools.parseapk;

import com.android.dex.Dex;
import com.bryansharp.tools.parseapk.utils.Log2File;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by bushaopeng on 17/3/20.
 */

public class ApkParser {
    public static void parseApk(String apkPath) {
        try {
            JarFile jarFile = new JarFile(new File(apkPath));
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(".dex")) {
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    parseDex(inputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseDex(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len;
        while ((len = inputStream.read(bytes)) > 0) {
            outputStream.write(bytes, 0, len);
        }
        byte[] dexData = outputStream.toByteArray();
        Dex dex = new Dex(dexData);
        DexData dexObj = new DexData();
        dexObj.fillHeaders(dexData);
        dexObj.fillData();
        dexObj.printData();
        Log2File.closeAll();

    }
}
