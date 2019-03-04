package com.shahedrahim.audiorecorder;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.IOException;

public class AudioRecorder {
    private String pathSave = "";
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;

    public AudioRecorder(String pathSave) {
        this.pathSave = pathSave;
    }

    public void btnRecOnClickHandler() {
        setupMediaRecorder();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnStopRecOnClickHandler() {
        if (mediaRecorder!=null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public void btnPlayOnClickHandler() {
        setupMediaPlayer();
        mediaPlayer.start();
    }

    public void btnStopPlayOnClickHandler() {
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
}
