package com.eswar.chess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class HistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private GameRowAdapter adapter;
    private List<GameRow> gameRows;
    private TextView emptyHistoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.history_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        gameRows = dbh.getAllRows();

        emptyHistoryText = findViewById(R.id.empty_history_text);

//        Log.d(tag, "Rows: " + gameRows);
//
//        if(gameRows == null || gameRows.size() == 0){
//            emptyHistoryText.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//        }
//        else{
//            emptyHistoryText.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//
//            adapter = new GameRowAdapter(HistoryActivity.this, gameRows);
//            recyclerView.setAdapter(adapter);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameRows = dbh.getAllRows();

        Log.d(tag, "Rows: " + gameRows);

        if(gameRows == null || gameRows.size() == 0){
            emptyHistoryText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else{
            emptyHistoryText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new GameRowAdapter(HistoryActivity.this, gameRows);
            recyclerView.setAdapter(adapter);
        }
    }
}
