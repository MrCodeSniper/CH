package com.ch.ch;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    /**
     * 检测sdcard是否可用
     *
     * @return true为可用，否则为不可用
     */
    public static boolean isSDCardAvailable() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isCheckSDCardWarning() {
        return !isSDCardAvailable();
    }

    public static boolean createDir(String path) {
        if (isCheckSDCardWarning()) {
            return false;
        }

        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return true;
    }

    public static File createFile(String path, String filename) {
        if (!createDir(path)) {
            return null;
        }

        if (TextUtils.isEmpty(filename)) {
            return null;
        }

        File file = null;
        file = new File(path, filename);
        if (file.exists()) {
            return file;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            return null;
        }

        return file;
    }

    public static File createFile(String absolutePath) {
        if (TextUtils.isEmpty(absolutePath)) {
            return null;
        }

        if (isCheckSDCardWarning()) {
            return null;
        }

        File file = new File(absolutePath);
        if (file.exists()) {
            return file;
        } else {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {

                file.createNewFile();
            } catch (IOException e) {
                return null;

            }
        }
        return file;
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return false;

        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return;

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            file = null;
        }
    }

    public static File createNewFile(String path, String name) {
       if (isCheckSDCardWarning()) {
           return null;
       }

        File file = new File(path, name);
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {

        }
        return file;
    }

    public static File getApkFile(Context context, String fileName) {
    
        return FileUtil.createFile(FileUtil.getSDCardAppCachePath(context) + File.separator + "apks", fileName);
    }

    public static String getApkAbsolutePath(Context context, String fileName) {
        return getApkFile(context,fileName).getPath();
    
    }
    // 程序sdcard目录
    public static String getSDCardAppCachePath(@NonNull Context context) {
        File file = context.getExternalCacheDir();
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }
    public static String getExternalStorageDirectory(@NonNull Context context) {
        File file = Environment.getExternalStorageDirectory();
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }
}