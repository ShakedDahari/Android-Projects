package com.example.app_sem_a;

import static android.media.AudioManager.STREAM_MUSIC;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
//implements View.OnClickListener

public class GuessNumberActivity extends AppCompatActivity {

    EditText editTextNumber;
    TextView guessNumTitle, remainGuess, guess_info;
    Button btnGuess, btnReset;
    int remainGuessCounter = 5;
    int level = 1;
    Random r = new Random();
    int randomNum;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_number);

        SoundPool sp = new SoundPool(5, STREAM_MUSIC, 0);
        int levelup = sp.load(this, R.raw.levelup, 1);
        int winning = sp.load(this, R.raw.winning, 1);
        int gameover = sp.load(this, R.raw.gameover, 1);

        initViews();

        initParameters();

        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(editTextNumber.getText().toString().isEmpty())) {
                    if (remainGuessCounter > 0) {
                        remainGuessCounter--;
                        remainGuess.setText("Remain Guesses: " + remainGuessCounter);
                        if (randomNum == Integer.parseInt(editTextNumber.getText().toString())) {
                            sp.play(levelup, 10, 10, 1, 0, 1);
                            level++;
                            if (level == 4) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(GuessNumberActivity.this);
                                builder.setMessage("You Win :)")
                                        .setTitle("Congrats!")
                                        .setCancelable(false)
                                        .setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                level = 1;
                                                modifyGame(5, 10 * level, "1-" + String.valueOf(10 * level), level);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                //Toast.makeText(GuessNumberActivity.this, "You Win The Game :)", Toast.LENGTH_SHORT).show();
                                sp.play(winning, 10, 10, 1, 0, 1);
                                level = 1;
                            }
                            modifyGame(5, 10 * level, "1-" + String.valueOf(10 * level), level);
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GuessNumberActivity.this);
                        builder.setMessage("Game Over!")
                                .setCancelable(false)
                                .setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        level = 1;
                                        modifyGame(5, 10 * level, "1-" + String.valueOf(10 * level), level);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        //Toast.makeText(GuessNumberActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                        sp.play(gameover, 10, 10, 1, 0, 1);
                    }
                } else {
                    Toast.makeText(GuessNumberActivity.this, "Insert A Number...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                level = 1;
                modifyGame(5, 10 * level, "1-" + String.valueOf(10 * level), level);
            }
        });

        guess_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GuessNumberActivity.this);
                builder.setMessage("Try to guess a randomly generated number within a certain range")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void initParameters() {
        sharedPreferences = GuessNumberActivity.this.getPreferences(MODE_PRIVATE);
        remainGuess.setText("Remain Guesses: " + remainGuessCounter);
        level = sharedPreferences.getInt("level", 1);
        modifyGame(5, 10 * level, "1-" + String.valueOf(10 * level), level);
    }

    private void initViews() {
        editTextNumber = findViewById(R.id.editTextNumber);
        btnGuess = findViewById(R.id.btnGuess);
        remainGuess = findViewById(R.id.remainGuess);
        btnReset = findViewById(R.id.btnReset);
        guessNumTitle = findViewById(R.id.guessNumTitle);
        guess_info = findViewById(R.id.guess_info);
    }

    public void modifyGame(int remainGuesses, int randomNumber, String levelUp, int level) {
        editor = sharedPreferences.edit();
        editor.putInt("level", level);
        editor.apply();
        guessNumTitle.setText("Guess Number " + levelUp);
        this.remainGuessCounter = remainGuesses;
        remainGuess.setText("Remain Guesses: " + remainGuesses);
        randomNum = r.nextInt(randomNumber);
        while (randomNum <= 0) {
            randomNum = r.nextInt(randomNumber);
        }
        Toast.makeText(GuessNumberActivity.this, "new random number is: " + randomNum, Toast.LENGTH_SHORT).show();
        editTextNumber.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
