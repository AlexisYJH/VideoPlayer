package com.example.mediaplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {
    private static final String TAG = "MPlayer";
    private static final String VIDEO_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()
            + "/input.mp4";
    private static final String[] PERMISSIONS_STORAGE = {
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Button mBtnPlay, mBtnPause, mBtnStop;
    private MediaPlayer mPlayer;
    private boolean mStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions();
        bindViews();
    }

    private void verifyStoragePermissions() {
        //检测是否有写的权限
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 0);
        }
    }


    private void bindViews() {
        mSurfaceView = findViewById(R.id.surfaceView);
        //初始化SurfaceHolder类，SurfaceView的控制器
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mBtnPlay = findViewById(R.id.btn_play);
        mBtnPause = findViewById(R.id.btn_pause);
        mBtnStop = findViewById(R.id.btn_stop);

        mBtnPlay.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        resetEnabled();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                //开始播放
                if (mStopped) {
                    mStopped = false;
                    try {
                        mPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mPlayer.start();
                Log.d(TAG, "开始播放");
                setEnabled(false, true, true);
                break;
            case R.id.btn_pause:
                //暂停播放
                mPlayer.pause();
                Log.d(TAG, "暂停播放");
                setEnabled(true, false, true);
                break;
            case R.id.btn_stop:
                //停止播放
                mPlayer.stop();
                mStopped = true;
                Log.d(TAG, "停止播放");
                resetEnabled();
                break;
            default:
                break;
        }
    }

    private void resetEnabled() {
        setEnabled(true, false, false);
    }

    private void setEnabled(boolean playEnabled, boolean pauseEnabled, boolean stopEnabled) {
        mBtnPlay.setEnabled(playEnabled);
        mBtnPause.setEnabled(pauseEnabled);
        mBtnStop.setEnabled(stopEnabled);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mPlayer = new MediaPlayer();
        mPlayer.setDisplay(mSurfaceHolder);
        try {
            mPlayer.setDataSource(VIDEO_PATH);
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "播放完成");
                    resetEnabled();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
    }
}