package com.example.lab11;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RaceActivity extends AppCompatActivity {

    private Button btn_start2;
    private LinearLayout raceTrackLayout;
    private List<SeekBar> seekBars = new ArrayList<>();
    private List<String> restaurantNames = new ArrayList<>();
    private SQLiteDatabase dbrw;
    private Handler handler;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbrw.close(); // 關閉資料庫
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        btn_start2 = findViewById(R.id.btn_start2);
        raceTrackLayout = findViewById(R.id.raceTrackLayout);

        // 取得資料庫實例
        dbrw = new MyDBHelper(this).getWritableDatabase();

        // 查詢資料庫，並設定賽道
        queryAndSetupRaceTracks();

        btn_start2.setOnClickListener(v -> startRace());

        handler = new Handler(Looper.myLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                SeekBar seekBar = seekBars.get(msg.what);
                seekBar.setProgress(seekBar.getProgress() + 1);

                if (seekBar.getProgress() >= 100) {
                    Toast.makeText(RaceActivity.this, restaurantNames.get(msg.what) + "勝", Toast.LENGTH_SHORT).show();
                    btn_start2.setEnabled(true);
                    return true;
                }
                return false;
            }
        });
    }

    private void queryAndSetupRaceTracks() {
        // 查詢資料庫
        Cursor c = dbrw.rawQuery("SELECT * FROM myTable", null);
        c.moveToFirst();
        restaurantNames.clear();
        while (!c.isAfterLast()) {
            restaurantNames.add(c.getString(0));
            c.moveToNext();
        }
        c.close();

        // 動態分配賽道
        setupRaceTracks();
    }

    private void setupRaceTracks() {
        raceTrackLayout.removeAllViews();
        seekBars.clear();

        for (int i = 0; i < restaurantNames.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(restaurantNames.get(i));
            raceTrackLayout.addView(textView);

            SeekBar seekBar = new SeekBar(this);
            seekBar.setMax(100);
            raceTrackLayout.addView(seekBar);

            seekBars.add(seekBar);
        }
    }

    private void startRace() {
        btn_start2.setEnabled(false); // 比賽期間，不可重複操作
        for (SeekBar seekBar : seekBars) {
            seekBar.setProgress(0); // 初始化進度
        }

        for (int i = 0; i < seekBars.size(); i++) {
            int index = i;
            new Thread(() -> {
                while (seekBars.get(index).getProgress() < 100) {
                    try {
                        Thread.sleep((long) (Math.random() * 300));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = handler.obtainMessage();
                    msg.what = index;
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }
}
