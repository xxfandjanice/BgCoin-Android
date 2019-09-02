package com.fmtch.bourse;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.GesturesFingerPwdUtils;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.video_view);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video);
        videoView.setOnPreparedListener(mp -> mp.setOnInfoListener((mp1, what, extra) -> {

            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // video started
                videoView.setBackgroundColor(Color.TRANSPARENT);
                return true;
            }
            return false;
        }));

        //播放视频
        videoView.setVideoURI(uri);
        videoView.start();

        new Handler().postDelayed(() -> {
            if ((boolean)SpUtils.get(KeyConstant.KEY_FIRST_OPEN, true)){
                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                finish();
            }else {
                GesturesFingerPwdUtils.Check2GesturesOrFinger(SplashActivity.this,RouterMap.MAIN_PAGE);
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}
