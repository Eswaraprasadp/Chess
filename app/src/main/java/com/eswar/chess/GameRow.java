package com.eswar.chess;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;

public class GameRow {
    private String result;
    private String date;
    private List<Move> moveList;

    public GameRow(String result, String date, List<Move> moveList){
        this.result = result;
        this.date = date;
        this.moveList = moveList;
    }

    public String getDate() {
        return date;
    }

    public String getResult() { return result; }

    public List<Move> getMoveList() {
        return moveList;
    }

    public String getMoveListString() { return new Gson().toJson(moveList); }

    @NonNull
    @Override
    public String toString() {
        return "Row: Result = " + result + ", date = " + date + ", formatted date = " + BaseActivity.getFormattedDate(date) + ", movesSize = " + moveList.size();
    }
}
