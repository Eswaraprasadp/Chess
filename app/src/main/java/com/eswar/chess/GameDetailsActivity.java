package com.eswar.chess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GameDetailsActivity extends AppCompatActivity {

    private String title, date;
    private int noOfMoves;
    private List<Move> moves = new ArrayList<>();
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        final Intent intent = getIntent();

        title = intent.getStringExtra("result");
        date = intent.getStringExtra("date");
        String movesString = intent.getStringExtra("moves");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Move>>(){}.getType();
        moves = gson.fromJson(movesString, type);
        noOfMoves = (moves.size() + 1)/2;

        final TextView titleText = findViewById(R.id.game_title);
        final TextView dateText = findViewById(R.id.game_date);
        final Button viewerButton = findViewById(R.id.game_viewer_button);
        recyclerView = findViewById(R.id.game_moves_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GameDetailsActivity.this));

        titleText.setText(title);
        dateText.setText(date);

        MoveAdapter adapter = new MoveAdapter(GameDetailsActivity.this, moves);
        recyclerView.setAdapter(adapter);

        viewerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
