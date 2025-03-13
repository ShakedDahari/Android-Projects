package com.example.app_sem_a;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SevenBoomActivity extends AppCompatActivity {

    TextView currentNum, boom_info;
    Button btn_click, btn_boom, btn_reset_counter;
    Integer counter = 0;
    Integer score = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven_boom);

        currentNum = findViewById(R.id.currentNum);
        boom_info = findViewById(R.id.boom_info);
        btn_click = findViewById(R.id.btn_click);
        btn_boom = findViewById(R.id.btn_boom);
        btn_reset_counter = findViewById(R.id.btn_reset_counter);

        SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        int explosion = soundPool.load(this, R.raw.explosion, 1);
        int winning = soundPool.load(this, R.raw.winning, 1);
        int gameover = soundPool.load(this, R.raw.gameover, 1);

        boom_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SevenBoomActivity.this);
                builder.setMessage("press CLICK to continue\n" +
                                "press BOOM when the next number divides or contains 7")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                resetGame();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initParameters();
                if(counter % 7 == 0 || counter.toString().contains("7")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SevenBoomActivity.this);
                    builder.setMessage("Your Score is " + score.toString());
                    builder.setTitle("Game Over!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            resetGame();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                   //Toast.makeText(SevenBoomActivity.this, "Game Over! Your Score Is " + score.toString(), Toast.LENGTH_SHORT).show();
                    soundPool.play(gameover, 10, 10, 1, 0, 1);
               }
            }
        });

        btn_boom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initParameters();
                if(counter % 7 == 0 || counter.toString().contains("7")) {
                    score++;
                    soundPool.play(explosion, 10, 10, 1, 0, 1);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SevenBoomActivity.this);
                    builder.setMessage("Your Score is " + score.toString());
                    builder.setTitle("Game Over!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            resetGame();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    //Toast.makeText(SevenBoomActivity.this, "Game Over! Your Score Is " + score.toString(), Toast.LENGTH_SHORT).show();
                    soundPool.play(gameover, 10, 10, 1, 0, 1);

                }
                if (counter >= 100) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SevenBoomActivity.this);
                    builder.setMessage("Your Score is " + score.toString());
                    builder.setTitle("Congrats! You Win :)");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            resetGame();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    //Toast.makeText(SevenBoomActivity.this, "You Win The Game :) Your Score Is " + score.toString(), Toast.LENGTH_SHORT).show();
                    soundPool.play(winning, 10, 10, 1, 0, 1);

                }
            }
        });

        btn_reset_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGame();
            }
        });
    }

    private void resetGame() {
        counter = 0;
        score = 0;
        currentNum.setText("let's start");
    }

    private void initParameters() {
        counter++;
        currentNum.setText(counter.toString());
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