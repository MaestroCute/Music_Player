package com.example.playingaudio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Playable {

    MediaPlayer mediaPlayer;
    ImageView playPauseIcon;
    TextView trackTitle;
    ImageView nextTrack;
    ImageView previousTrack;
    ImageView loop;
    SeekBar seekBar;
    ImageView Background;
    int index = 0;

    NotificationManager notificationManager;
    List<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTracks();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        Background = findViewById(R.id.backgroundImageView);
        Background.setImageResource(tracks.get(index).getImage());

        trackTitle = findViewById(R.id.trackTitleTextView);
        trackTitle.setText(tracks.get(index).getTitle());

        mediaPlayer = MediaPlayer.create(getApplicationContext(),tracks.get(index).getTrackId());

        nextTrack = findViewById(R.id.nextImageView);
        nextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTrackNext();
            }
        });

        playPauseIcon = findViewById(R.id.playImageView);
        playPauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    onTrackPause();
                } else {
                    onTrackPlay();
                }
            }
        });

        previousTrack = findViewById(R.id.previousImageView);
        previousTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTrackPrevious();
            }
        });

        loop = findViewById(R.id.loopImageView);
        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mediaPlayer.isLooping()) {
                    mediaPlayer.setLooping(true);
                    loop.animate().rotation(-180).setDuration(500);
                    loop.setImageResource(R.drawable.ic_baseline_loop_green_24);
                } else {
                    mediaPlayer.setLooping(false);
                    loop.animate().rotation(0).setDuration(500);
                    loop.setImageResource(R.drawable.ic_baseline_loop_24);
                }
            }
        });

        seekBar = findViewById(R.id.SeekBar);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        },0,50);

    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "Qqalld Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void addTracks(){
        tracks = new ArrayList<>();

        tracks.add(new Track("Non, je ne regrette rien", R.raw.non_je_ne_regrette_rien, "Evgeniy Ponasenkov", R.drawable.bravo));
        tracks.add(new Track("I love Paris", R.raw.i_love_paris, "Evgeniy Ponasenkov", R.drawable.silence));
        tracks.add(new Track("Ti voglio tanto bene", R.raw.ti_voglio_tanto_bene, "Evgeniy Ponasenkov", R.drawable.smile));
        tracks.add(new Track("Tu ca, nun chiagne", R.raw.tu_ca_nun_chiagne, "Evgeniy Ponasenkov", R.drawable.evening));
        tracks.add(new Track("Autumn Leaves", R.raw.autumn_leaves, "Evgeniy Ponasenkov", R.drawable.look_at_insects));
        tracks.add(new Track("Libiamo ne' lieti calici", R.raw.libiamo_ne_lieti_calici, "Evgeniy Ponasenkov", R.drawable.genius));
        tracks.add(new Track("La Spagnola", R.raw.la_spagnola, "Evgeniy Ponasenkov", R.drawable.for_capitalism));
        tracks.add(new Track("Lili Marleen", R.raw.lili_marleen, "Evgeniy Ponasenkov", R.drawable.for_you_health));
        tracks.add(new Track("No Puede Ser", R.raw.no_puede_ser, "Evgeniy Ponasenkov", R.drawable.goodbye_haters));
        tracks.add(new Track("Albinoni's Adagio", R.raw.albinonis_adagio, "Evgeniy Ponasenkov", R.drawable.deutch));

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case CreateNotification.ACTION_PREVIUOS:
                    onTrackPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (mediaPlayer.isPlaying()){
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onTrackNext();
                    break;
            }
        }
    };

    public void onTrackNext() {

        mediaPlayer.pause();

        if(++index>=tracks.size()) {
            index = 0;
        }
        Background.setImageResource(tracks.get(index).getImage());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),tracks.get(index).getTrackId());

        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        playPauseIcon.setImageResource(R.drawable.ic_baseline_pause_24);

        CreateNotification.createNotification(MainActivity.this, tracks.get(index),
                R.drawable.ic_baseline_pause_24, index, tracks.size()-1);
        trackTitle.setText(tracks.get(index).getTitle());

        autoPlay();
    }

    public void onTrackPlay() {

        mediaPlayer.start();

        CreateNotification.createNotification(MainActivity.this, tracks.get(index),
                R.drawable.ic_baseline_pause_24, index, tracks.size()-1);
        playPauseIcon.setImageResource(R.drawable.ic_baseline_pause_24);
        trackTitle.setText(tracks.get(index).getTitle());

        autoPlay();
    }

    public void onTrackPause() {

        mediaPlayer.pause();

        CreateNotification.createNotification(MainActivity.this, tracks.get(index),
                R.drawable.ic_baseline_play_arrow_24, index, tracks.size() - 1);
        playPauseIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        trackTitle.setText(tracks.get(index).getTitle());
    }

    public void onTrackPrevious() {

        mediaPlayer.pause();

        if(index>0) {
            index--;
        } else {
            index = tracks.size()-1;
        }
        Background.setImageResource(tracks.get(index).getImage());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),tracks.get(index).getTrackId());

        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        playPauseIcon.setImageResource(R.drawable.ic_baseline_pause_24);

        CreateNotification.createNotification(MainActivity.this, tracks.get(index),
                R.drawable.ic_baseline_pause_24, index, tracks.size()-1);
        trackTitle.setText(tracks.get(index).getTitle());

    }

    public void autoPlay() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                onTrackNext();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }

        unregisterReceiver(broadcastReceiver);
    }
}
