package com.example.lab11;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText ed_book;
    private Button btn_query, btn_insert, btn_delete, btn_start;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items = new ArrayList<>();

    private SQLiteDatabase dbrw;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbrw.close(); // 關閉資料庫
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed_book = findViewById(R.id.ed_book);
        btn_query = findViewById(R.id.btn_query);
        btn_insert = findViewById(R.id.btn_insert);
        btn_delete = findViewById(R.id.btn_delete);
        btn_start = findViewById(R.id.btn_start);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        dbrw = new MyDBHelper(this).getWritableDatabase();

        btn_insert.setOnClickListener(view -> {
            if (ed_book.length() < 1) {
                Toast.makeText(MainActivity.this, "餐廳名請勿留空", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    dbrw.execSQL("INSERT INTO myTable(book) values(?)",
                            new Object[]{ed_book.getText().toString()});
                    Toast.makeText(MainActivity.this, "成功新增餐廳名: " + ed_book.getText().toString(), Toast.LENGTH_SHORT).show();
                    ed_book.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "新增失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_delete.setOnClickListener(view -> {
            if (ed_book.length() < 1) {
                Toast.makeText(MainActivity.this, "餐廳名請勿留空", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    dbrw.execSQL("DELETE FROM myTable WHERE book LIKE '" + ed_book.getText().toString() + "'");
                    Toast.makeText(MainActivity.this, "刪除餐廳名: " + ed_book.getText().toString(), Toast.LENGTH_SHORT).show();
                    ed_book.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "刪除失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_query.setOnClickListener(view -> {
            Cursor c;
            if (ed_book.length() < 1) {
                c = dbrw.rawQuery("SELECT * FROM myTable", null);
            } else {
                c = dbrw.rawQuery("SELECT * FROM myTable WHERE book LIKE '" + ed_book.getText().toString() + "'", null);
            }

            c.moveToFirst();
            items.clear();
            Toast.makeText(MainActivity.this, "共有" + c.getCount() + "間可以選擇", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < c.getCount(); i++) {
                items.add("餐廳名稱: " + c.getString(0));
                c.moveToNext();
            }
            adapter.notifyDataSetChanged();
            c.close();
        });

        btn_start.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RaceActivity.class);
            startActivity(intent);
        });
    }
}
