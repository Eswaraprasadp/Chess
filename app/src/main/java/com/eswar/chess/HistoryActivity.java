package com.eswar.chess;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private GameRowAdapter adapter;
    private List<GameRow> gameRows;
    private TextView emptyHistoryText;
    private Button deleteAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.history_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        gameRows = dbh.getAllRows();

        emptyHistoryText = findViewById(R.id.empty_history_text);
        deleteAllButton = findViewById(R.id.delete_all_games);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "regular.otf");
        deleteAllButton.setTypeface(typeface);

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dbh.deleteTable();
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setMessage("Do you want to delete all saved games?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbh.deleteTable();
                                updateRecyclerView();
                                Toast.makeText(HistoryActivity.this, "All games deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Delete all");
                alertDialog.show();
            }
        });

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
        updateRecyclerView();
    }

    public void updateRecyclerView(){
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
