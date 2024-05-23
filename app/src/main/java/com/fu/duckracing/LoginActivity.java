package com.fu.duckracing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText txtUsername, txtPassword;
    private MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtUsername = (EditText) findViewById(R.id.txtUserName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        mp = MediaPlayer.create(LoginActivity.this, R.raw.login_music);
        mp.setLooping(true);
        mp.start();

        btnLogin.setOnClickListener(v -> {
            if(!CheckLogin()){
                Toast.makeText(LoginActivity.this, "Username and Password not correct\nUsername: iloveyou | Password: metoo", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("username", txtUsername.getText().toString());
            startActivity(intent);
            finish();
        });
    }

    private boolean CheckLogin(){
        if(txtUsername.getText().toString().trim().equals("")
        || txtPassword.getText().toString().trim().equals("")){
            return false;
        }
        if(txtUsername.getText().toString().trim().equals("iloveyou") &&
                txtPassword.getText().toString().trim().equals("metoo")) {
            SharedPreferences sharedPref = getSharedPreferences("login_pref", MODE_PRIVATE);
            sharedPref.edit().putBoolean("isLoggedIn", true).apply(); // Save login state
            sharedPref.edit().putString("username", txtUsername.getText().toString()).apply(); // Save username
            mp.stop();
            return true;
        }

        return false;
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null && mp.isPlaying()) {
            mp.pause();  // Pause playback when activity goes out of screen
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mp != null && !mp.isPlaying()) {
            mp.start();
        }
    }

    // ... other methods

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();  // Release resources when done
        }
    }
}
