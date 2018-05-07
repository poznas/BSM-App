package com.bsmapps.bulgarskaszkolamagii;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mlody Danon on 7/27/2017.
 */

public class PhotoVideoFullscreenDisplay extends AppCompatActivity {

    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.image_view)
    ImageView imageView;

    private Bundle bundle;
    private String type;
    private String Url;

    private MediaController mediaController;
    private int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_video_fullscreen);
        ButterKnife.bind(this);

        bundle = this.getIntent().getExtras();
        if ( bundle != null ){
            switch (bundle.getString("type")){
                case "photo":

                    Glide.with(this)
                            .load(bundle.getString("URL"))
                            .into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                    break;
                case "video":

                    try {
                        videoView.setVisibility(View.VISIBLE);
                        mediaController = new MediaController(this);
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                        videoView.setVideoPath(bundle.getString("URL"));
                        videoView.requestFocus();
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {

                                videoView.seekTo(position);
                                if (position == 0) {
                                    videoView.start();
                                }
                            }
                        });
                    }catch (Exception e){
                        Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Store current position.
        savedInstanceState.putInt("CurrentPosition", videoView.getCurrentPosition());
        videoView.pause();
    }
    // After rotating the phone. This method is called.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Get saved position.
        position = savedInstanceState.getInt("CurrentPosition");
        videoView.seekTo(position);
    }
}
