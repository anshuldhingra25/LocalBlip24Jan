package com.ordiacreativeorg.localblip.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.ordiacreativeorg.localblip.R;

public class VideoDisplay extends Activity {
    MediaController mediaControls;
    RelativeLayout relvid;
    ProgressBar progvideo;
    private String recVideo;
    private ImageView cancel_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.maindialog);
        recVideo = getIntent().getExtras().getString("blip");

        cancel_video = (ImageView) findViewById(R.id.cancel_video);
        relvid = (RelativeLayout) findViewById(R.id.relvid);
        progvideo = (ProgressBar) findViewById(R.id.progvideo);
        final VideoView videoview = (VideoView) findViewById(R.id.videodialog);
        String path1 = recVideo;

        Uri uri = Uri.parse(path1);


        if (mediaControls == null) {
            // create an object of media controller class
            mediaControls = new MediaController(this);
            mediaControls.setAnchorView(videoview);

        }

        // set the media controller for video view
        videoview.setMediaController(mediaControls);
        // set the uri for the video view
        videoview.setVideoURI(uri);
        videoview.setZOrderOnTop(true);
        videoview.seekTo(100);


        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progvideo.setVisibility(View.GONE);
            //    mediaControls.show();

                videoview.start();

            }
        });
        cancel_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
