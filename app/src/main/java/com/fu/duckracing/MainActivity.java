package com.fu.duckracing;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
        AppCompatButton btnReset = findViewById(R.id.btnReset);
        AnimatedSeekBar seekBarDuck1 = findViewById(R.id.seekBarDuck1);
        AnimatedSeekBar seekBarDuck2 = findViewById(R.id.seekBarDuck2);
        AnimatedSeekBar seekBarDuck3 = findViewById(R.id.seekBarDuck3);
        CheckBox checkBox1 = findViewById(R.id.checkBox1);
        CheckBox checkBox2 = findViewById(R.id.checkBox2);
        CheckBox checkBox3 = findViewById(R.id.checkBox3);
        EditText txtBet1 = findViewById(R.id.txtBet1);
        EditText txtBet2 = findViewById(R.id.txtBet2);
        EditText txtBet3 = findViewById(R.id.txtBet3);

        ducks = new ArrayList<>();
        ducks.add(new Duck(seekBarDuck1, checkBox1, "duck1"));
        ducks.add(new Duck(seekBarDuck2, checkBox2, "duck2"));
        ducks.add(new Duck(seekBarDuck3, checkBox3, "duck3"));

        seekBarDuck1.setOnTouchListener((v, event) -> true);
        seekBarDuck2.setOnTouchListener((v, event) -> true);
        seekBarDuck3.setOnTouchListener((v, event) -> true);

        handler = new Handler(Looper.getMainLooper());
        random = new Random();

        updateBetEditTextState(checkBox1, txtBet1);
        updateBetEditTextState(checkBox2, txtBet2);
        updateBetEditTextState(checkBox3, txtBet3);

        checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> updateBetEditTextState(checkBox1, txtBet1));
        checkBox2.setOnCheckedChangeListener((buttonView, isChecked) -> updateBetEditTextState(checkBox2, txtBet2));
        checkBox3.setOnCheckedChangeListener((buttonView, isChecked) -> updateBetEditTextState(checkBox3, txtBet3));

        btnStart.setOnClickListener(click -> {
            boolean betRequired1 = checkBox1.isChecked();
            boolean betRequired2 = checkBox2.isChecked();
            boolean betRequired3 = checkBox3.isChecked();
            boolean allBetsValid = true;

            String betValue1 = txtBet1.getText().toString();
            String betValue2 = txtBet2.getText().toString();
            String betValue3 = txtBet3.getText().toString();

            // validate before start a game
            if (!betRequired1 && !betRequired2 && !betRequired3) {
                Toast.makeText(MainActivity.this, "Please choose at least a duck to start game!", Toast.LENGTH_SHORT).show();
                allBetsValid = false;
            }

            if (betRequired1 && betValue1.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please input bet value for Duck 1 before start!", Toast.LENGTH_SHORT).show();
                allBetsValid = false;
            }

            if (betRequired2 && betValue2.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please input bet value for Duck 2 before start!", Toast.LENGTH_SHORT).show();
                allBetsValid = false;
            }

            if (betRequired3 && betValue3.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please input bet value for Duck 3 before start!", Toast.LENGTH_SHORT).show();
                allBetsValid = false;
            }

            if (allBetsValid) {
                startRace();
                // unable 2 button and checkbox when a game processing
                btnStart.setEnabled(false);
                btnReset.setEnabled(false);
                checkBox1.setEnabled(false);
                checkBox2.setEnabled(false);
                checkBox3.setEnabled(false);
                btnStart.setAlpha(0.5f);
                btnReset.setAlpha(0.5f);
            }
        });

        btnReset.setOnClickListener(click -> {
            for (Duck duck : ducks) {
                duck.getSeekBar().setProgress(0); // Reset progress
            }

            // enable start button when end a game
            btnStart.setEnabled(true);
            btnStart.setAlpha(1f);

            // reset bet value
            txtBet1.setText(null);
            txtBet2.setText(null);
            txtBet3.setText(null);

            // reset checkbox
            checkBox1.setEnabled(true);
            checkBox1.setChecked(false);
            checkBox2.setEnabled(true);
            checkBox2.setChecked(false);
            checkBox3.setEnabled(true);
            checkBox3.setChecked(false);
        });
    }

    private void updateBetEditTextState(CheckBox checkBox, EditText editText) {
        if (checkBox.isChecked()) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER); // Allow decimals (adjust as needed)
            editText.setEnabled(true);
        } else {
            editText.setInputType(InputType.TYPE_NULL); // Prevent any input
            editText.setText(""); // Clear any entered value
            editText.setEnabled(false); // Disable EditText for unchecked checkbox
        }
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
                        raceOver = true;
                        break;
                    }
                }

                if (results.size() == ducks.size()) {
                    raceOver = true;
                }

                if (!raceOver) {
                    handler.postDelayed(this, 100); // Update every 100ms
                } else {
                    mp.stop();
                    results.sort(Comparator.comparingInt(DuckResult::getTime));
                    for (int i = 0; i < results.size(); i++) {
                        System.out.println("Position " + (i + 1) + ": " + results.get(i).getDuckName() + " Time: " + results.get(i).getTime());
                    }
                    runOnUiThread(() -> {
                        AppCompatButton btnReset = findViewById(R.id.btnReset);
                        btnReset.setEnabled(true);
                        btnReset.setAlpha(1f);
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
