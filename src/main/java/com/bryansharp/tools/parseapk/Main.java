package com.bryansharp.tools.parseapk;

import com.bryansharp.tools.parseapk.utils.LogUtils;
import com.github.yeriomin.playstoreapi.DetailsResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.PlayStoreApiBuilder;
import com.github.yeriomin.playstoreapi.PropertiesDeviceInfoProvider;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * Created by bushaopeng on 17/3/30.
 */
public class Main {
    public static void main(String[] args) {
//        ApkParser.parseApk("com.android.vending.apk");
        //cn.xender
        if (downloadApp("com.xvideostudio.videoeditor")) {
            return;
        }
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

    private static boolean downloadApp(String assetId) {
        try {
            Properties properties = new Properties();
            try {
                properties.load(Main.class.getClassLoader().getSystemResourceAsStream("device-honami.properties"));
            } catch (IOException e) {
                System.out.println("device-honami.properties not found");
                return true;
            }
            PropertiesDeviceInfoProvider deviceInfoProvider = new PropertiesDeviceInfoProvider();
            deviceInfoProvider.setProperties(properties);
            deviceInfoProvider.setLocaleString(Locale.ENGLISH.toString());

            // Provide valid google account info
            PlayStoreApiBuilder builder = new PlayStoreApiBuilder()
                    // Extend HttpClientAdapter using a http library of your choice
                    .setHttpClient(new DebugHttpClientAdapter())
                    .setDeviceInfoProvider(deviceInfoProvider)
                    .setTokenDispenserUrl("http://tokendispenser-yeriomin.rhcloud.com")
                    .setEmail(null)
                    .setPassword(null);
            GooglePlayAPI api = builder.build();

            // We are logged in now
            // Save and reuse the generated auth token and gsf id,
            // unless you want to get banned for frequent relogins
            // The token has a very long validity time. Months.
            api.getToken();
            api.getGsfId();

            // API wrapper instance is ready
            DetailsResponse response = api.details("com.cpuid.cpu_z");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


}
