package com.eswar.chess;

import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
    private BoardView boardView;

    private ImageView background;
    private Button undo, restart, undoLanscape, restartLandscape;
    private CheckBox computerCheckBox, computerCheckBoxLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardView = findViewById(R.id.board_view);
        boardView.setDbh(dbh);

        background = findViewById(R.id.background_image);
        undo = findViewById(R.id.undo_button);
        restart = findViewById(R.id.restart_button);
        computerCheckBox = findViewById(R.id.computer_check_box);

        undoLanscape = findViewById(R.id.undo_button_landscape);
        restartLandscape = findViewById(R.id.restart_button_landscape);
        computerCheckBoxLandscape = findViewById(R.id.computer_check_box_landscape);

        undo.setEnabled(true);
        restart.setEnabled(true);
        computerCheckBox.setEnabled(true);

        undoLanscape.setEnabled(false);
        restartLandscape.setEnabled(false);
        computerCheckBoxLandscape.setEnabled(false);

        boardView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                boardView.changeDimen();
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardView.undo();
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardView.start();
            }
        });

        computerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boardView.setAiPlaying(isChecked);
            }
        });

        undoLanscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardView.undo();
            }
        });

        restartLandscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardView.start();
            }
        });

        computerCheckBoxLandscape.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boardView.setAiPlaying(isChecked);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){

            undoLanscape.setVisibility(View.VISIBLE);
            restartLandscape.setVisibility(View.VISIBLE);
            computerCheckBoxLandscape.setVisibility(View.VISIBLE);

            undo.setVisibility(View.GONE);
            restart.setVisibility(View.GONE);
            computerCheckBox.setVisibility(View.GONE);

            undo.setEnabled(false);
            restart.setEnabled(false);
            computerCheckBox.setEnabled(false);

            undoLanscape.setEnabled(true);
            restartLandscape.setEnabled(true);
            computerCheckBoxLandscape.setEnabled(true);
        }
        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            undoLanscape.setVisibility(View.GONE);
            restartLandscape.setVisibility(View.GONE);
            computerCheckBoxLandscape.setVisibility(View.GONE);

            undo.setVisibility(View.VISIBLE);
            restart.setVisibility(View.VISIBLE);
            computerCheckBox.setVisibility(View.VISIBLE);

            undo.setEnabled(true);
            restart.setEnabled(true);
            computerCheckBox.setEnabled(true);

            undoLanscape.setEnabled(false);
            restartLandscape.setEnabled(false);
            computerCheckBoxLandscape.setEnabled(false);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();

        boardView.destroyMediaResources();
    }
}
