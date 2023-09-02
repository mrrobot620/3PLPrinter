package com.mrrobot.rscprinter;

import android.os.Environment;
import android.util.Log;
import com.example.tscdll.TSCActivity;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PrintZip
        implements Runnable {
    private static final String TAG = "PrintZip";
    private TSCActivity TscDll;
    private final File download_path;
    private String filename;
    private final String watchFolder;

    private PrintZip() {
        File file;
        this.download_path = file = Environment.getExternalStoragePublicDirectory((String)Environment.DIRECTORY_DOWNLOADS);
        this.watchFolder = file.getAbsolutePath();
    }

    PrintZip(String string2, TSCActivity tSCActivity) {
        File file;
        this.download_path = file = Environment.getExternalStoragePublicDirectory((String)Environment.DIRECTORY_DOWNLOADS);
        this.watchFolder = file.getAbsolutePath();
        this.filename = string2;
        this.TscDll = tSCActivity;
    }

    private void readfile(String string2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.watchFolder);
        stringBuilder.append("/");
        stringBuilder.append(string2);
        File file = new File(stringBuilder.toString());
        Log.d((String)TAG, (String)"Waiting for file to Read");
        while (file.length() == 0L) {
            Log.d((String)TAG, (String)"Waiting for file to Read");
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(this.watchFolder);
            stringBuilder2.append("/");
            stringBuilder2.append(string2);
            file = new File(stringBuilder2.toString());
        }
    }

    private static void unzip(File sourceZipFile, File destinationDirectory) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourceZipFile)))) {
            byte[] buffer = new byte[8192];
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File outputFile = new File(destinationDirectory, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    File parentDir = outputFile.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }
                    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                        int bytesRead;
                        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }

    public void run() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.watchFolder);
            stringBuilder.append("/");
            stringBuilder.append(this.filename);
            File file = new File(stringBuilder.toString());
            if (file.length() == 0L) {
                Log.d((String)TAG, (String)"File is Empty");
                this.readfile(this.filename);
            }
            if (this.filename.endsWith(".zip")) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(this.watchFolder);
                stringBuilder2.append("extracted");
                PrintZip.unzip(file, new File(stringBuilder2.toString()));
                Log.d("XX" , "Extracted Succesfully");
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(this.watchFolder);
                stringBuilder3.append("extracted");
                File file2 = new File(stringBuilder3.toString());
                for (File file3 : file2.listFiles()) {
                    String string2;
                    this.TscDll.setup(76,50, 4, 15, 0, 3, 0);
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append("File Working : ");
                    stringBuilder4.append(file3.getName());
                    Log.d((String)TAG, (String)stringBuilder4.toString());
                    BufferedReader bufferedReader = new BufferedReader((Reader)new FileReader(file3));
                    Log.d((String)TAG, (String)"File Content");
                    while ((string2 = bufferedReader.readLine()) != null) {
                        Log.d("XX" ,"Full string: " +string2 );
                        String modifiedString = replaceMultipleValues(string2);
                        this.TscDll.sendcommand(modifiedString);
                    }
                    bufferedReader.close();
                    file3.delete();
                    this.TscDll.clearbuffer();
                }
                file2.delete();
            }
            file.delete();
            return;
        }
        catch (Exception exception) {
            Log.d((String)TAG, (String)"There was a Exception");
            return;
        }
    }

    private String replaceMultipleValues(String inputContent) {
        // Define the replacements you need
        String[][] replacements = {
                {"^FT16,270", "^FT200,245"},
                {"^FT40,448" , "^FT40,400"},
                {"^FT384,288^BXN,12,200" , "^FT384,200^BXN,8,150"},
                {"^FT16,308" , "^FT16,275"},
                {"^FT553,388" , "^FT400,245"},
                {"^FT527,428" , "^FT400,275"},
        };

        for (String[] replacement : replacements) {
            inputContent = inputContent.replace(replacement[0], replacement[1]);
        }
        return inputContent;
    }
}


