package com.bryansharp.tools.parseapk;

import com.bryansharp.tools.parseapk.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

/**
 * Created by bushaopeng on 17/3/30.
 */
public class Main {
    public static void main(String[] args) {
//        ApkParser.parseApk("com.android.vending.apk");
        try {
            //TODO 已有apk 将apk中的dex提取出来，然后进行 disassemble 再移动到out对应目录下

            org.jf.smali.Main.main(new String[]{"assemble", new File("out").getAbsolutePath()});
            ApkSigner.replaceInApk("classes.dex", new File("out.dex"), new JarFile("com.android.vending.apk"), "com.android.vending-replaced.apk");
            LogUtils.log("dex is created");
            ApkSigner.signFile("publickey.rsa.pem", "privatekey.pk8", "com.android.vending-replaced.apk", "com.android.vending-signed.apk");
            new File("com.android.vending-replaced.apk").delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
