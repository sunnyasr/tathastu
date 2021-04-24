package com.tathastushop.app.Activities;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.tathastushop.app.R;

import java.io.File;

public class ProjectionActivity extends AppCompatActivity {
    PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projection);


        playerView = findViewById(R.id.video_view);
        initializePlayer();
    }

    private void initializePlayer() {
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());
        String filePath = "http://teamtathastu.com/video/TATHASTU.mp4";
        Log.e("filepath", filePath);
        Uri uri = Uri.parse(filePath);

        ExtractorMediaSource audioSource = new ExtractorMediaSource(
                uri,
                new DefaultDataSourceFactory(this, "Tathastu"),
                new DefaultExtractorsFactory(),
                null,
                null
        );

        player.prepare(audioSource);
        playerView.setPlayer(player);
        player.setPlayWhenReady(true);

//        player.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerView.getPlayer().stop();
    }
}