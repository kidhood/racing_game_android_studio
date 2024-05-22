package com.fu.duckracing;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.fu.duckracing.animation.AnimatedSeekBar;
import com.fu.duckracing.model.Duck;
import com.fu.duckracing.model.DuckResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mp;
    private List<Duck> ducks;
    private List<DuckResult> results;
    private Handler handler;
    private Random random;
    List<Duck> userSelectedDucks = new ArrayList<>();

    // Charge Component
    private Dialog chargeDialog;
    private Button btnCharge;
    private TextView btnChargeClose;
    private TextView txtAmount;
    private Button btnDeposit;
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
        TextView balance = findViewById(R.id.txtBalance);

        // Charge Component
        chargeDialog = new Dialog(this);
        chargeDialog.setContentView(R.layout.charge_dialog);
        chargeDialog.setCancelable(false);
        chargeDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnCharge = (Button) chargeDialog.findViewById(R.id.btnCharge);
        btnChargeClose = (TextView) chargeDialog.findViewById(R.id.btnChargeClose);
        btnDeposit = (Button) findViewById(R.id.btnDeposit);
        txtAmount = chargeDialog.findViewById(R.id.txtChargeAmount);
        chargeDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.charge_background_xml));

        ducks = new ArrayList<>();
        ducks.add(new Duck(seekBarDuck1, checkBox1, "Vịt gangster"));
        ducks.add(new Duck(seekBarDuck2, checkBox2, "Vịt baby"));
        ducks.add(new Duck(seekBarDuck3, checkBox3, "Vịt bầu"));

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
            int totalBet = 0;

            String betValue1 = txtBet1.getText().toString();
            String betValue2 = txtBet2.getText().toString();
            String betValue3 = txtBet3.getText().toString();
            try {
                if (!betValue1.isEmpty()) {
                    totalBet += Integer.parseInt(betValue1);
                }
                if (!betValue2.isEmpty()) {
                    totalBet += Integer.parseInt(betValue2);
                }
                if (!betValue3.isEmpty()) {
                    totalBet += Integer.parseInt(betValue3);
                }

                if (totalBet > Integer.parseInt(balance.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Balance is not enough, please deposit more money!", Toast.LENGTH_SHORT).show();
                    allBetsValid = false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid bet value(s)", Toast.LENGTH_SHORT).show();
                allBetsValid = false;
            }

            // validate before start a game
            if (!betRequired1 && !betRequired2 && !betRequired3) {
                Toast.makeText(MainActivity.this, "Please choose at least a duck to start game!", Toast.LENGTH_SHORT).show();
                allBetsValid = false;
            }

            if (betRequired1 && betValue1.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please input bet value for " + ducks.get(0).getName() + " before start!", Toast.LENGTH_SHORT).show();
                allBetsValid = false;
            }

            if (betRequired2 && betValue2.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please input bet value for " + ducks.get(1).getName() + " before start!", Toast.LENGTH_SHORT).show();
                allBetsValid = false;
            }

            if (betRequired3 && betValue3.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please input bet value for "  + ducks.get(2).getName() + " before start!", Toast.LENGTH_SHORT).show();
                allBetsValid = false;
            }

            if (allBetsValid) {
                if (betRequired1) {
                    userSelectedDucks.add(ducks.get(0));
                    txtBet1.setEnabled(false);
                }
                if (betRequired2) {
                    userSelectedDucks.add(ducks.get(1));
                    txtBet2.setEnabled(false);
                }
                if (betRequired3) {
                    userSelectedDucks.add(ducks.get(2));
                    txtBet3.setEnabled(false);
                }
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

        btnDeposit.setOnClickListener(click -> {
            chargeDialog.show();
        });

        btnChargeClose.setOnClickListener(click -> {
            chargeDialog.dismiss();
        });

        btnCharge.setOnClickListener( click -> {
            if(chargeDialogVisible(balance)){
                chargeDialog.dismiss();
            }
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
                    showRaceResultDialog(results);
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

    private void showRaceResultDialog(List<DuckResult> results) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.result_dialog, null);

        int winnerIndex = 0;
        int profit = 0;
        TextView balance = findViewById(R.id.txtBalance);
        int balanceValue = 0;

        int betValue1 = 0;
        int betValue2 = 0;
        int betValue3 = 0;

        EditText txtBet1 = findViewById(R.id.txtBet1);
        EditText txtBet2 = findViewById(R.id.txtBet2);
        EditText txtBet3 = findViewById(R.id.txtBet3);

        ImageView image = dialogView.findViewById(R.id.winner_image);
        StringBuilder resultMessage = new StringBuilder();

        for (int i = 1; i < results.size(); i++) {
            if (results.get(i).getTime() < results.get(winnerIndex).getTime()) {
                winnerIndex = i;
            }
        }

        DuckResult winner = results.get(winnerIndex);
        String winnerName = winner.getDuckName();
        if (winnerName == "Vịt gangster") {
            image.setImageResource(R.drawable.ic_duck_1);
        } else if (winnerName == "Vịt baby") {
            image.setImageResource(R.drawable.ic_duck_2);
        } else {
            image.setImageResource(R.drawable.ic_duck_3);
        }

        if (!txtBet1.getText().toString().isEmpty()) {
            betValue1 = Integer.parseInt(txtBet1.getText().toString());
        }
        if (!txtBet2.getText().toString().isEmpty()) {
            betValue2 = Integer.parseInt(txtBet2.getText().toString());
        }
        if (!txtBet3.getText().toString().isEmpty()) {
            betValue3 = Integer.parseInt(txtBet3.getText().toString());
        }

        resultMessage.append("Winner: " +  winnerName + "\n");


        for (Duck duck : userSelectedDucks) {
            if (duck.getName().equals(winnerName)) {
                if (winnerName.equals("Vịt gangster")) {
                    profit = betValue1;
                } else if (winnerName.equals("Vịt baby")) {
                    profit = betValue2;
                } else {
                    profit = betValue3;
                }
                break;
            }
        }
        if (winnerName.equals("Vịt gangster")) {
            profit = profit - betValue2 - betValue3;
        } else if (winnerName.equals("Vịt baby")) {
            profit = profit - betValue1 - betValue3;
        } else {
            profit = profit - betValue1 - betValue2;
        }

        // handle cal bet
        if (profit >= 0) {
            // User chose the winner!
            resultMessage.append("Congrats! You won " + profit + "$");
            balanceValue = Integer.parseInt(balance.getText().toString()) + profit;
            String balanceText = String.valueOf(balanceValue);
            balance.setText(balanceText);
        } else {
            // User chose the wrong duck
            resultMessage.append("Unlucky, you lost " + profit + "$");
            balanceValue = Integer.parseInt(balance.getText().toString()) + profit;
            String balanceText = String.valueOf(balanceValue);
            balance.setText(balanceText);
        }

        TextView message = dialogView.findViewById(R.id.message);
        message.setText(resultMessage.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false) // Prevent dismissing without clicking a button
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss()); // Dismiss dialog on button click

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean resultsContainsDuck(Duck duck) {
        for (DuckResult result : results) {
            if (result.getDuckName().equals(duck.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean chargeDialogVisible(TextView balance) {
        String strAmount = this.txtAmount.getText().toString();
        if(strAmount.equals("")){
            Toast.makeText(MainActivity.this, "Please enter amount: ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(Integer.parseInt(strAmount) <= 0){
            Toast.makeText(MainActivity.this, "Please enter amount greater than 0: ", Toast.LENGTH_SHORT).show();
            return false;
        }

        int balanceValue = Integer.parseInt(balance.getText().toString());
        int newBalance = balanceValue + Integer.parseInt(strAmount);
        String balanceText = String.valueOf(newBalance);
        balance.setText(balanceText);
        MediaPlayer me = MediaPlayer.create(MainActivity.this, R.raw.ring);
        me.start();
        this.txtAmount.setText("");
        return true;
    }

}
