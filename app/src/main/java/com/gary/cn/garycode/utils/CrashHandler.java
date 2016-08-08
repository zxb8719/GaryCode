package com.gary.cn.garycode.utils;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 在Activity或者Application中注册一下即可

 CrashHandler crashHandler = CrashHandler.getInstance();
 crashHandler.init(getApplicationContext());
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static String TAG = "CrashHandler";
    private static CrashHandler instance;

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 核心方法，当程序crash 会回调此方法， Throwable中存放这错误日志
     */
    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        // 这里还可以加上当前的系统版本，机型型号 等等信息
        StackTraceElement[] stackTrace = arg1.getStackTrace();
        LogUtil.e(TAG, arg1.getMessage() + "\n");
        for (int i = 0; i < stackTrace.length; i++) {
            LogUtil.e(TAG,"file:" + stackTrace[i].getFileName() + " class:"
                    + stackTrace[i].getClassName() + " method:"
                    + stackTrace[i].getMethodName() + " line:"
                    + stackTrace[i].getLineNumber() + "\n");
        }

        /*gary
        String logPath;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            logPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + File.separator
                    + File.separator
                    + "log";

            File file = new File(logPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                FileWriter fw = new FileWriter(logPath + File.separator
                        + "errorlog.log", true);
                fw.write(new Date() + "\n");
                // 错误信息
                // 这里还可以加上当前的系统版本，机型型号 等等信息
                StackTraceElement[] stackTrace = arg1.getStackTrace();
                fw.write(arg1.getMessage() + "\n");
                for (int i = 0; i < stackTrace.length; i++) {
                    fw.write("file:" + stackTrace[i].getFileName() + " class:"
                            + stackTrace[i].getClassName() + " method:"
                            + stackTrace[i].getMethodName() + " line:"
                            + stackTrace[i].getLineNumber() + "\n");
                }
                fw.write("\n");
                fw.close();
                // 上传错误信息到服务器
                // uploadToServer();
            } catch (IOException e) {
                LogUtil.d("crash handler", "load file failed..."+e.getCause());
            }
        }*/
        arg1.printStackTrace();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}