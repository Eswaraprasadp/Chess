package com.eswar.chess;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MoveAdapter extends RecyclerView.Adapter<MoveAdapter.ViewHolder>{
    private List<Move> moves = new ArrayList<>();
    private Context context;
    private int maxMove;

    public MoveAdapter(Context context, List<Move> moves){
        this.context = context;
        this.moves = moves;
        maxMove = (moves.size() + 1) / 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public ViewHolder(View view){
            super(view);
            this.view = view;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        View view = viewHolder.view;
        final TextView moveText = view.findViewById(R.id.move_text);
        String moveString;
        if(i == maxMove - 1 && maxMove % 2 == 1){
            moveString = String.valueOf(i + 1) + ". " + moves.get(i * 2).toString();
        }
        else{
            moveString = String.valueOf(i + 1) + ". " + moves.get(i * 2).toString() + " " + moves.get(i * 2 + 1).toString();
        }

//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "regular.otf");
//        moveText.setTypeface(typeface);
        moveText.setText(moveString);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.move_row, viewGroup, false));
}

    @Override
    public int getItemCount() {
        return maxMove;
    }
}
