package com.fu.duckracing;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.fu.duckracing.animation.AnimatedSeekBar;
import com.fu.duckracing.model.Duck;
import com.fu.duckracing.model.DuckResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mp;
    private List<Duck> ducks;
    private List<DuckResult> results;
    private Handler handler;
    private Random random;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mp = MediaPlayer.create(MainActivity.this, R.raw.duck_racing);
        ImageButton btnStart = findViewById(R.id.btnStart);
        AnimatedSeekBar seekBarDuck1 = findViewById(R.id.seekBarDuck1);
        AnimatedSeekBar seekBarDuck2 = findViewById(R.id.seekBarDuck2);
        AnimatedSeekBar seekBarDuck3 = findViewById(R.id.seekBarDuck3);
        CheckBox checkBox1 = findViewById(R.id.checkBox1);
        CheckBox checkBox2 = findViewById(R.id.checkBox2);
        CheckBox checkBox3 = findViewById(R.id.checkBox3);

        ducks = new ArrayList<>();
        ducks.add(new Duck(seekBarDuck1, checkBox1, "duck1"));
        ducks.add(new Duck(seekBarDuck2, checkBox2, "duck2"));
        ducks.add(new Duck(seekBarDuck3, checkBox3, "duck3"));

        seekBarDuck1.setOnTouchListener((v, event) -> true);
        seekBarDuck2.setOnTouchListener((v, event) -> true);
        seekBarDuck3.setOnTouchListener((v, event) -> true);

        handler = new Handler(Looper.getMainLooper());
        random = new Random();

        btnStart.setOnClickListener(click -> {
            btnStart.setEnabled(false);
            startRace();
        });
    }

    private void startRace() {
        if (mp.isPlaying()) {
            mp.stop();
        }
        mp.reset();
        mp = MediaPlayer.create(MainActivity.this, R.raw.duck_racing);
        mp.start();

        results = new ArrayList<>();
        for (Duck duck : ducks) {
            duck.getSeekBar().setProgress(0); // Reset progress
        }

        updateDuckProgress();
    }

    private void updateDuckProgress() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean raceOver = false;
                for (Duck duck : ducks) {
                    if (duck.getSeekBar().getProgress() < 100) {
                        int currentProgress = duck.getSeekBar().getProgress();
                        int randomIncrement = random.nextInt(5) + 1; // Random increment between 1 and 5
                        duck.getSeekBar().setProgress(Math.min(currentProgress + randomIncrement, 100));
                    } else if (!resultsContainsDuck(duck)) {
                        results.add(new DuckResult(duck.getName(), duck.getSeekBar().getProgress()));
                    }
                }

                if (results.size() == ducks.size()) {
                    raceOver = true;
                }

                if (!raceOver) {
                    handler.postDelayed(this, 100); // Update every 100ms
                } else {
                    mp.stop();
                    Collections.sort(results, Comparator.comparingInt(DuckResult::getTime));
                    for (int i = 0; i < results.size(); i++) {
                        System.out.println("Position " + (i + 1) + ": " + results.get(i).getDuckName() + " Time: " + results.get(i).getTime());
                    }
                    runOnUiThread(() -> {
                        ImageButton btnStart = findViewById(R.id.btnStart);
                        btnStart.setEnabled(true);
                    });
                }
            }
        }, 100); // Initial delay of 100ms
    }

    private boolean resultsContainsDuck(Duck duck) {
        for (DuckResult result : results) {
            if (result.getDuckName().equals(duck.getName())) {
                return true;
            }
        }
        return false;
    }

}
