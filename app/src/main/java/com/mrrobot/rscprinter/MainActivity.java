package com.mrrobot.rscprinter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.tscdll.TSCActivity;
import com.mrrobot.rscprinter.WorkerService;

import java.util.Set;

public class MainActivity
        extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 23;
    private static final int REQUEST_ENABLE_BT = 33;
    private static final String TAG = "MainActivity";
    private String hostAddress;

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission((Context)this, (String)"android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)this, (String)"android.permission.READ_CONTACTS")) {
                return;
            }
            ActivityCompat.requestPermissions((Activity)this, (String[])new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, (int)23);
        }
    }

    private String getBluetoothDevice() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String string2 = "";
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            for (BluetoothDevice bluetoothDevice : bluetoothAdapter.getBondedDevices()) {
                String string3 = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Bluetooth Device Address : ");
                stringBuilder.append(bluetoothDevice.getAddress());
                Log.d((String)string3, (String)stringBuilder.toString());
                string2 = bluetoothDevice.getAddress();
            }
        }
        return string2;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_main);
        this.checkPermission();
        this.hostAddress = this.getBluetoothDevice();
        this.startService();
    }

    protected void onDestroy() {
        this.stopService();
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int n, String[] arrstring, int[] arrn) {
        if (n != 23) {
            return;
        }
        if (arrn.length > 0 && arrn[0] == 0) {
            return;
        }
        this.finish();
    }

    public void startService() {
        Intent intent = new Intent((Context)this, WorkerService.class);
        intent.putExtra("hostAddress", this.hostAddress);
        intent.putExtra("inputExtra", "");
        ContextCompat.startForegroundService((Context)this, (Intent)intent);
    }

    public void stopService() {
        this.stopService(new Intent((Context)this, WorkerService.class));
    }
}

