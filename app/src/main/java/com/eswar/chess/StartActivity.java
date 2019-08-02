package com.eswar.chess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        dbh.deleteTable();

        final Button startPlay = findViewById(R.id.start_play);
        final Button viewHistory = findViewById(R.id.view_history);

        startPlay.setOnClickListener(clickListener);
        viewHistory.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_play:
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    break;
                case R.id.view_history:
                    startActivity(new Intent(StartActivity.this, HistoryActivity.class));
                    break;
            }
        }
    };
}
