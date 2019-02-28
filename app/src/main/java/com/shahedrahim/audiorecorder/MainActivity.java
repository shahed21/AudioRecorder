package com.shahedrahim.audiorecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Button btnRecord, btnStopRecord, btnPlay, btnStop;
    private String pathSave = "";
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;

    private final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord = findViewById(R.id.record_btn);
        btnStopRecord = findViewById(R.id.stop_record_btn);
        btnPlay = findViewById(R.id.play_btn);
        btnStop = findViewById(R.id.stop_play_btn);

        if (!checkPermissionFromDevice()) {
            requestPermission();
        }

        if (checkPermissionFromDevice()) {
            btnRecord.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    pathSave = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" +
                            UUID.randomUUID().toString() +
                            "_audio_record.3gp";
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
                if (mediaRecorder!=null) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    Toast.makeText(MainActivity.this, "Stopped Recording...", Toast.LENGTH_SHORT).show();
                }
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(true);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setupMediaPlayer();
                mediaPlayer.start();

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
                if (mediaPlayer!=null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Toast.makeText(MainActivity.this, "Stopped Playing...", Toast.LENGTH_SHORT).show();
                }
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(true);
            }
        });
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(pathSave);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
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
}
