package com.mrrobot.rscprinter;


import android.os.Environment;
import android.util.Log;
import com.example.tscdll.TSCActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


public class PrintPrn
        implements Runnable {
    private static final String TAG = "PrintPrn";
    private TSCActivity TscDll;
    private final File download_path;
    private String filename;
    private final String watchFolder;

    private PrintPrn() {
        File file;
        this.download_path = file = Environment.getExternalStoragePublicDirectory((String)Environment.DIRECTORY_DOWNLOADS);
        this.watchFolder = file.getAbsolutePath();
    }

    PrintPrn(String string2, TSCActivity tSCActivity) {
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

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */


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
            this.TscDll.clearbuffer();
            if (this.filename.startsWith("wid")) {
                this.TscDll.setup(10, 22, 4, 15, 0, 3, 0);
                Log.d((String)TAG, (String)"wid Setup");
            } else if (this.filename.startsWith("wsn")) {
                this.TscDll.setup(10, 22, 4, 15, 0, 3, 0);
                Log.d((String)TAG, (String)"wsn Setup");
            } else if (this.filename.startsWith("ticket")) {
                this.TscDll.setup(10, 22, 4, 15, 0, 3, 0);
                this.TscDll.sendcommand("^XA\n^MCY^PMN\n^PW406\n^JZY\n^LH0,0^LRN\n^XZ");
                Log.d((String)TAG, (String)"ticket Setup");
            } else if (this.filename.startsWith("bag")) {
                this.TscDll.setup(55, 33, 4, 15, 0, 3, 0);
                Log.d((String)TAG, (String)"bagLabel Setup");
            } else if (this.filename.startsWith("shipment_display")) {
                Log.d((String)TAG, (String)"prepack Setup");
            } else {
                this.TscDll.setup(101, 152, 4, 15, 0, 3, 0);
                Log.d((String)TAG, (String)"IBL Setup");
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("File Working : ");
            stringBuilder2.append(file.getName());
            Log.d((String)TAG, (String)stringBuilder2.toString());
            BufferedReader bufferedReader = new BufferedReader((Reader)new FileReader(file));
            Log.d((String)TAG, (String)"File Content");
            do {
                String string2;
                if ((string2 = bufferedReader.readLine()) == null) {
                    bufferedReader.close();
                    this.TscDll.clearbuffer();
                    file.delete();
                    return;
                }
                Log.d((String)TAG, (String)string2);
                if (string2.length() == 0) continue;
                this.TscDll.sendcommand(string2);
            } while (true);
        }
        catch (Exception exception) {
            Log.d((String)TAG, (String)"There was a Exception");
            return;
        }
    }
}



