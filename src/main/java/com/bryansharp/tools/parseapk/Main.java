package com.bryansharp.tools.parseapk;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

/**
 * Created by bushaopeng on 17/3/30.
 */
public class Main {
    public static void main(String[] args) {
//        ApkParser.parseApk("com.android.vending.apk");
        org.jf.baksmali.Main.main(new String[]{"disassemble", new File("out.dex").getAbsolutePath()});
//        try {
//            ApkSigner.replaceInApk("classes.dex",new File("classes.dex"),new JarFile("com.android.vending.apk"),"com.android.vending-replaced.apk");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ApkSigner.signFile("publickey.rsa.pem","privatekey.pk8","com.android.vending-replaced.apk","com.android.vending-signed.apk");
    }
}
