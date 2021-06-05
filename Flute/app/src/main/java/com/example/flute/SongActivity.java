package com.example.flute;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class SongActivity extends AppCompatActivity {

    TextView p_playingSongName;
    ArrayList<File> songList;
    String playingSongName;
    MediaPlayer mediaPlayer;
    SeekBar p_seekBar;
    Thread seekBarUpdate;
    ImageView p_play,p_prev,p_next;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        getP();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        songList = (ArrayList) bundle.getParcelableArrayList("songsList");
        index = bundle.getInt("index",0);

        play(index);

        p_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying()){
                    p_play.setImageResource(R.drawable.image_part_004);
                    mediaPlayer.pause();
                }
                else{
                    p_play.setImageResource(R.drawable.image_part_005);
                    mediaPlayer.start();
                }

            }
        });

        p_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                index = (index-1);
                if (index == -1){
                    index = songList.size()-1;
                }
                play(index);
            }
        });

        p_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                index = (index+1)%songList.size();
                play(index);
            }
        });

        p_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        seekBarUpdate = new Thread(){
            @Override
            public void run() {
                int currPos = 0;

                try {
                    while(currPos<mediaPlayer.getDuration()){
                        currPos = mediaPlayer.getCurrentPosition();
                        p_seekBar.setProgress(currPos);
                        sleep(800);
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        seekBarUpdate.start();
    }


    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }

    void getP(){

        p_playingSongName = findViewById(R.id.playingSongName);
        p_seekBar = findViewById(R.id.seekBar);
        p_prev = findViewById(R.id.prev);
        p_play = findViewById(R.id.play);
        p_next = findViewById(R.id.next);

    }

    void play(int index) {

        playingSongName = songList.get(index).getName().replace(".mp3","");
        p_playingSongName.setText(playingSongName);
        p_playingSongName.setSelected(true);

        Uri uri = Uri.parse(songList.get(index).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        p_play.setImageResource(R.drawable.image_part_005);
        p_seekBar.setMax(mediaPlayer.getDuration());
        p_seekBar.setProgress(0);
    }

    void stop(){

        p_play.setImageResource(R.drawable.image_part_004);
        seekBarUpdate.interrupt();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}

