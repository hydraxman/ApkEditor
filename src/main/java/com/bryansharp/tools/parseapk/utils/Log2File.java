package com.bryansharp.tools.parseapk.utils;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.Adler32;

/**
 * Copyright  2016
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class Log2File {
    // 存放日志文件的目录全路径
    public static String sLogFolderPath = new File(".").getAbsolutePath();

    //日志打印日期
    private static SimpleDateFormat fileDateFormat = new SimpleDateFormat("MM-dd-mmss");
    //单线程 用写文件 防止 anr
    private static ExecutorService mSingleExecutors = Executors.newSingleThreadExecutor();
    public static final String TAG = "FILE_LOG";
    public static final String LOG_FILENAME = fileDateFormat.format(new Date()) + ".txt";

    /**
     * 4.指定Log文件名,仅输出Log 到文件
     *
     * @param msg
     */
    public static void fi(final Object msg) {
        try {
            if (msg == null)
                return;
            writeLog(msg, LOG_FILENAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写Log 到文件
     *
     * @param msg
     * @param logFileName
     */
    public static void writeLog(final Object msg, final String logFileName) {
        mSingleExecutors.execute(new Runnable() {
            @Override
            public void run() {
                fileLog(msg, logFileName);
            }
        });
    }

    /**
     * @param objectMsg
     * @param logFileName
     */
    private static void fileLog(Object objectMsg, String logFileName) {
//        String stringBuilder = simpleDateFormat.format(new Date()) +
//                "\t" +
//                objectMsg.toString();
        saveLog2File(objectMsg.toString(), logFileName);
    }

    /**
     * 将msg 写入日志文件
     *
     * @param msg
     * @param logFileName log 文件名
     */
    private static void saveLog2File(String msg, String logFileName) {
        FileWriter filerWriter = null;
        BufferedWriter bufferedWriter = null;
        do { // 非循环，只是为了减少分支缩进深度
            // 未安装 SD 卡
            File rootPath = new File(sLogFolderPath);
            if (rootPath.exists()) {
                File fileLogFilePath = new File(sLogFolderPath, logFileName);
                // 如果日志文件不存在，则创建它
                if (true != fileLogFilePath.exists()) {
                    try {
                        fileLogFilePath.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                // 如果执行到这步日志文件还不存在，就不写日志到文件了
                if (true != fileLogFilePath.exists()) {
                    LogUtils.log("Create log file failed!");
                    break;
                }

                try {
                    filerWriter = new FileWriter(fileLogFilePath, true);          // 续写不覆盖
                } catch (IOException e1) {
                    LogUtils.log("New FileWriter Instance failed");
                    e1.printStackTrace();
                    break;
                }

                bufferedWriter = new BufferedWriter(filerWriter);

                try {
                    bufferedWriter.write(msg + "\n");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    LogUtils.log("bufferedWriter.write or bufferedWriter.flush failed");
                    e.printStackTrace();
                }
            } else {
                LogUtils.log("Log savePath invalid!");
            }
        } while (false);

        if (null != bufferedWriter) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (null != filerWriter) {
            try {
                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeAll() {
        mSingleExecutors.shutdown();
    }
}