package com.shahedrahim.audiorecorder;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

public class AudioRecorder {
    private static final String TAG = "AudioRecorder";

    private String pathSave = "";

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    private MediaRecorder mediaRecorder;
    private OnCompletionListener arOncompletionListener = null;

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
            arOncompletionListener.onCompletionListener();
        }
    };

    public AudioRecorder(
            String pathSave,
            Context context,
            OnCompletionListener onCompletionListener) {
        this.pathSave = pathSave;
        arOncompletionListener = onCompletionListener;
        setupAudioManager(context);
    }

    public void btnRecOnClickHandler() {
        Log.d(TAG, "btnRecOnClickHandler: will start recording");
        setupMediaRecorder();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnStopRecOnClickHandler() {
        Log.d(TAG, "btnStopRecOnClickHandler: will stop recording");
        if (mediaRecorder!=null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public void btnPlayOnClickHandler() {
        releaseMediaPlayer();
        Log.d(TAG, "btnPlayOnClickHandler: Requesting AudioFocus");
        if (requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)) {
            Log.d(TAG, "btnPlayOnClickHandler: will start playing");
            setupMediaPlayer();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        } else {
            Log.d(TAG, "btnPlayOnClickHandler: AudioFocus denied");
        }
    }

    public void btnStopPlayOnClickHandler() {
        Log.d(TAG, "btnStopPlayOnClickHandler: will stop playing");
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void setupMediaPlayer() {
        Log.d(TAG, "setupMediaPlayer: setting up media player");
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(pathSave);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupMediaRecorder() {
        Log.d(TAG, "setupMediaRecorder: setting up media recorder");
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    /**
     * Clean up the media player by releasing its resources
     */
    private void releaseMediaPlayer() {
        Log.d(TAG, "releaseMediaPlayer: here");
        if (mediaPlayer!=null) {
            Log.d(TAG, "releaseMediaPlayer: releasing old mediaplayer");
            mediaPlayer.release();
            mediaPlayer=null;
        }
        if (afChangeListener!=null) {
            Log.d(TAG, "releaseMediaPlayer: releasing audioFocusChangeListener");
            /*TODO change deprecated code to latest code*/
            /*https://developer.android.com/guide/topics/media-apps/audio-focus#java*/
            audioManager.abandonAudioFocus(afChangeListener);
        }
    }

    /**
     * This method is needed because AudioManager.requestAudioFocus with OnFocusChangeListener is
     * deprecated after API 26.
     * @param focusChangeListener
     * @param streamType
     * @param audioFocusGain
     * @return
     */
    private boolean requestAudioFocus(AudioManager.OnAudioFocusChangeListener focusChangeListener,
                                      int streamType, int audioFocusGain) {
        int r;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            r = audioManager.requestAudioFocus(
                    new AudioFocusRequest.Builder(audioFocusGain)
                            .setAudioAttributes(
                                    new AudioAttributes.Builder()
                                            .setLegacyStreamType(streamType)
                                            .build())
                            .setOnAudioFocusChangeListener(focusChangeListener)
                            .build());
        } else {
            //noinspection deprecation
            r = audioManager.requestAudioFocus(focusChangeListener, streamType, audioFocusGain);
        }

        return r == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void setupAudioManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    // Pause playback because your Audio Focus was
                    // temporarily stolen, but will be back soon.
                    // i.e. for a phone call
                    if (mediaPlayer!=null) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    // Stop playback, because you lost the Audio Focus.
                    // i.e. the user started some other playback app
                    // Remember to unregister your controls/buttons here.
                    // And release the kra — Audio Focus!
                    // You’re done.
                    if (mediaPlayer!=null) {
                        mediaPlayer.stop();
                    }
                    releaseMediaPlayer();
                } else if (focusChange ==
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    // Lower the volume, because something else is also
                    // playing audio over you.
                    // i.e. for notifications or navigation directions
                    // Depending on your audio playback, you may prefer to
                    // pause playback here instead. You do you.
                    if (mediaPlayer!=null) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    // Resume playback, because you hold the Audio Focus
                    // again!
                    // i.e. the phone call ended or the nav directions
                    // are finished
                    // If you implement ducking and lower the volume, be
                    // sure to return it to normal here, as well.
                    if (mediaPlayer!=null) {
                        mediaPlayer.start();
                    }
                }
            }
        };
    }

    public interface OnCompletionListener {
        public void onCompletionListener();
    }
}
