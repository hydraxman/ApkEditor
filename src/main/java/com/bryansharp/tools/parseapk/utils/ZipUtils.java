package com.bryansharp.tools.parseapk.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    public static File extractFileFromZip(String zipFilePath, String destDir, String extractedFile) throws FileNotFoundException {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        File outputFile = null;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();

                try {
                    if (fileName.equals(extractedFile)) {
                        File newFile = new File(destDir + File.separator + fileName);
                        System.out.println("Unzipping to " + newFile.getAbsolutePath());
                        //create directories for sub directories in zip
                        File parentDir = new File(newFile.getParent());
                        if (!parentDir.exists()) {
                            parentDir.mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                        outputFile = newFile;
                        break;
                    }
                } finally {
                    //close this ZipEntry
                    zis.closeEntry();
                }

                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

}
