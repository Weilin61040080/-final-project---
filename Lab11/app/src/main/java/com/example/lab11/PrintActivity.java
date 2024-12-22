package com.example.lab11;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PrintActivity extends AppCompatActivity {

    private static final String TAG = "PrintActivity";
    private AlertDialog persistentDialog;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);  // 使用 activity_print.xml 佈局文件

        // 綁定顯示對話框按鈕
        btn = findViewById(R.id.print_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPersistentDialog();
            }
        });

        // 接收來自 RaceActivity 的獲勝者名稱
        Intent intent = getIntent();
        if (intent.hasExtra("winner")) {
            String winner = intent.getStringExtra("winner");
            Log.d(TAG, "Winner received: " + winner);  // 添加日志
            showWinnerDialog(winner);
        } else {
            Log.d(TAG, "No winner received");
        }
    }

    private void showPersistentDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(PrintActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("這是彈出對話框，按下取消後才能繼續");

        dialog.setNeutralButton("取消", null);
        persistentDialog = dialog.create();
        persistentDialog.setCancelable(false);
        persistentDialog.show();

        persistentDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistentDialog.dismiss();
                Toast.makeText(PrintActivity.this, "對話框已關閉", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWinnerDialog(String winner) {
        Log.d(TAG, "Showing winner dialog for: " + winner);  // 添加日志
        AlertDialog.Builder dialog = new AlertDialog.Builder(PrintActivity.this);
        dialog.setTitle("比賽結果");
        dialog.setMessage("第一個獲勝的餐廳是：" + winner);

        dialog.setNeutralButton("確定", null);
        AlertDialog winnerDialog = dialog.create();
        winnerDialog.setCancelable(false);
        winnerDialog.show();
    }
}
