package com.sferadev.danacast.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

public class FileUtils {

    public static String filename(String url) {
        return url.split("&")[0].replace("/", "%2F").replace(":", "%3A");
    }

    public static boolean saveFile(final Context context, final String filename, final String content) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(context.getFilesDir(), filename);
                    FileOutputStream fileOutput = new FileOutputStream(file);
                    Writer out = new OutputStreamWriter(fileOutput, Charset.forName("UTF-8"));
                    out.write(content);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFile(final Context context, final String filename) {
        File file = new File(context.getFilesDir(), filename);
        return file.delete();
    }

    public static String getFile(Context context, String filename) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(context.getFilesDir() + "/" + filename), Charset.forName("UTF-8")));
            return readAll(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
