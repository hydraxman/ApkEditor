package com.bryansharp.tools.parseapk.utils;


/**
 * Created by bushaopeng on 17/2/8.
 */
public class LogUtils {
    private static final String TAG = "parseApk";

    public static void log(Object msg) {
        log(msg, false);
    }

    public static void log(Object msg, boolean toFile) {
        if (msg == null) {
            return;
        }
        if (toFile) {
            Log2File.fi(msg);
        }
        System.out.println(msg.toString());
    }

    public static void logDiv() {
        log("==============================");
    }

    public static void logEach(Object... msgs) {
        for (Object msg : msgs) {
            System.out.print(msg.toString());
            System.out.print("\t");
        }
        System.out.print("\n");
    }

    public static void logOpcode(int opCode) {
        LogUtils.log("指令：" + getOpName(opCode));
    }

    public static String getOpName(int opCode) {
        return Utils.getOpMap().get(opCode);
    }
}
