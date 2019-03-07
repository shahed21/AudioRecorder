package com.shahedrahim.audiorecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
    implements AudioRecorder.OnCompletionListener{
    private static final String TAG = "MainActivity";

    private Button btnRecord, btnStopRecord, btnPlay, btnStop;

    private AudioRecorder audioRecorder;

    private final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord = findViewById(R.id.record_btn);
        btnStopRecord = findViewById(R.id.stop_record_btn);
        btnPlay = findViewById(R.id.play_btn);
        btnStop = findViewById(R.id.stop_play_btn);

        audioRecorder = new AudioRecorder(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator +
                UUID.randomUUID().toString() +
                "_audio_record.3gp", this,this);

        if (!checkPermissionFromDevice()) {
            requestPermission();
        }

        if (checkPermissionFromDevice()) {
            btnRecord.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    audioRecorder.btnRecOnClickHandler();

                    Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnStopRecord.setEnabled(true);
                    btnRecord.setEnabled(false);
                }
            });
        }

        btnStopRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioRecorder.btnStopRecOnClickHandler();

                Toast.makeText(MainActivity.this, "Stopped Recording...", Toast.LENGTH_SHORT).show();
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(true);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioRecorder.btnPlayOnClickHandler();

                Toast.makeText(MainActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
                btnPlay.setEnabled(false);
                btnStop.setEnabled(true);
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(false);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                audioRecorder.btnStopPlayOnClickHandler();

                Toast.makeText(MainActivity.this, "Stopped Playing...", Toast.LENGTH_SHORT).show();
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(true);
            }
        });
    }

    private boolean checkPermissionFromDevice() {
        int writeExternalStorageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudioResult = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return writeExternalStorageResult == PackageManager.PERMISSION_GRANTED &&
                recordAudioResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                },
                REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onCompletionListener() {
        Log.d(TAG, "onCompletionListener: mediaPlayer Completed playing");
    }
}
