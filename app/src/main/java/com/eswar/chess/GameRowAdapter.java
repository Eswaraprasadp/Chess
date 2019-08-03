package com.eswar.chess;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class GameRowAdapter extends RecyclerView.Adapter<GameRowAdapter.ViewHolder>{

    private Context context;
    private List<GameRow> gameRows;
    private GameRow gameRow;

    public GameRowAdapter(Context context, List<GameRow> gameRows){
        this.context = context;
        this.gameRows = gameRows;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public ViewHolder(View view){
            super(view);
            this.view = view;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.game_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        View view = viewHolder.view;
        final TextView result = view.findViewById(R.id.row_result);
        final TextView date = view.findViewById(R.id.row_date);
        final TextView moves = view.findViewById(R.id.row_move_size);
        final LinearLayout layout = view.findViewById(R.id.game_row_layout);

        gameRow = gameRows.get(i);

        result.setText(gameRow.getResult());
        date.setText(BaseActivity.getFormattedDate(gameRow.getDate()));
        setTextColors(moves, "Moves: ", String.valueOf((gameRow.getMoveList().size()+1)/2), R.color.white, R.color.white, view);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GameDetailsActivity.class);
                intent.putExtra("result", gameRow.getResult());
                intent.putExtra("date", BaseActivity.getFormattedDate(gameRow.getDate()));
                intent.putExtra("moves", gameRow.getMoveListString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameRows.size();
    }

    public void setTextColors(TextView textView, String title, String content, int colorTitle, int colorContent, View view){

        String colorCodeTitle = "#" + Integer.toHexString(view.getResources().getColor(colorTitle) & 0x00ffffff);
        String colorCodeContent = "#" + Integer.toHexString(view.getResources().getColor(colorContent) & 0x00ffffff);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml("<font color=" + colorCodeTitle +"><b>" + title + "</b></font><font color=" + colorCodeContent + ">" + content + "</font>", Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            textView.setText(Html.fromHtml("<font color=" + colorCodeTitle +"><b>" + title + "</b></font><font color=" + colorCodeContent + ">" + content + "</font>"));
        }
    }
}
