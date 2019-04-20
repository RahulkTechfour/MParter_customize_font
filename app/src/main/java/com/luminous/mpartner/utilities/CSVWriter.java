package com.luminous.mpartner.utilities;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CSVWriter {

    public void writeAsCSV(List<Map<String, String>> flatJson, String fileName, Context context) throws FileNotFoundException {
        Set<String> headers = collectHeaders(flatJson);
        String output = StringUtils.join(headers.toArray(), ",") + "\n";
        for (Map<String, String> map : flatJson) {
            output = output + getCommaSeperatedRow(headers, map) + "\n";
        }
//        writeToFile(output, fileName);
        writeStringAsFile(output, fileName, context);
    }

    private void writeToFile(String output, String fileName) throws FileNotFoundException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }

    public static File getFile(String fileName){
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, fileName);
        return file;
    }

    public static void writeStringAsFile(final String fileContents, String fileName, Context context) {
        try {
            /*File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, fileName);
            file.createNewFile();
            FileWriter out = new FileWriter(file);
            CommonMethods.showLog("file----------->", file.getAbsolutePath());
            out.write(fileContents);
            out.close();*/


            File file = getFile(fileName);
            file.createNewFile();
            FileWriter out = new FileWriter(file);
            Log.e("file----------->", file.getAbsolutePath());
            out.write(fileContents);
            out.close();

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            dm.addCompletedDownload(fileName, "Report downloaded", false, "text/csv",
                    file.getAbsolutePath(), file.length(), true);
            Toast.makeText(context, fileName + " downloaded!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void close(BufferedWriter writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCommaSeperatedRow(Set<String> headers, Map<String, String> map) {
        List<String> items = new ArrayList<String>();
        for (String header : headers) {
            String value = map.get(header) == null ? "" : map.get(header).replace(",", "");
            items.add(value);
        }
        return StringUtils.join(items.toArray(), ",");
    }

    private Set<String> collectHeaders(List<Map<String, String>> flatJson) {
        Set<String> headers = new TreeSet<String>();
        for (Map<String, String> map : flatJson) {
            headers.addAll(map.keySet());
        }
        return headers;
    }
}
