package com.mrrobot.rscprinter;


import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

import com.example.tscdll.TSCActivity;

import java.io.File;

public class WorkerService
        extends Service {
    private static final String TAG = "WorkerService";
    private TSCActivity TscDll;
    private final File download_path;
    private String hostAddress;
    private FileObserver observer;
    private final String watchFolder;

    public WorkerService() {
        File file;
        this.download_path = file = Environment.getExternalStoragePublicDirectory((String)Environment.DIRECTORY_DOWNLOADS);
        this.watchFolder = file.getAbsolutePath();
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        super.onCreate();
        Log.d("DONE" , "Entered Worker Class");
        this.hostAddress = "";
    }

    public void onDestroy() {
        this.observer.stopWatching();
        this.TscDll.closeport();
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int n, int n2) {
        String string2;
        intent.getStringExtra("inputExtra");
        this.hostAddress = string2 = intent.getStringExtra("hostAddress");
        Log.d("DONE" , "Entered Worker Class 2");
        if (!string2.isEmpty()) {
            TSCActivity tSCActivity;
            this.TscDll = tSCActivity = new TSCActivity();
            tSCActivity.openport(this.hostAddress);
            Log.d("Port" , "Opened:");
        }
        this.observer = new FileObserver(this.watchFolder, FileObserver.CLOSE_WRITE){

            public void onEvent(int n, String string2) {
                if (n == 8) {
                    String string3 = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("onEvent: File Found : ");
                    stringBuilder.append(string2);
                    Log.d((String)string3, (String)stringBuilder.toString());
                    if (string2.endsWith(".prn")) {
                        new Thread((Runnable)new PrintPrn(string2, WorkerService.this.TscDll)).start();
                    }
                    if (string2.endsWith(".zip")) {
                        new Thread((Runnable)new PrintZip(string2, WorkerService.this.TscDll)).start();
                    }
                    if (string2.endsWith(".pdf")){

                    }
                }
            }
        };

        this.observer.startWatching();
        return Service.START_STICKY;
    }

    public boolean stopService(Intent intent) {
        this.observer.stopWatching();
        this.TscDll.closeport();
        return super.stopService(intent);
    }

}

