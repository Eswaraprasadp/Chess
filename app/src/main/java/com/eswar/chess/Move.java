package com.eswar.chess;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.eswar.chess.BoardUtils.BLACK_BISHOP;
import static com.eswar.chess.BoardUtils.BLACK_KING;
import static com.eswar.chess.BoardUtils.BLACK_KNIGHT;
import static com.eswar.chess.BoardUtils.BLACK_PAWN;
import static com.eswar.chess.BoardUtils.BLACK_QUEEN;
import static com.eswar.chess.BoardUtils.BLACK_ROOK;
import static com.eswar.chess.BoardUtils.EMPTY;
import static com.eswar.chess.BoardUtils.NONE;
import static com.eswar.chess.BoardUtils.NO_PIECE;
import static com.eswar.chess.BoardUtils.WHITE_BISHOP;
import static com.eswar.chess.BoardUtils.WHITE_KING;
import static com.eswar.chess.BoardUtils.WHITE_KNIGHT;
import static com.eswar.chess.BoardUtils.WHITE_PAWN;
import static com.eswar.chess.BoardUtils.WHITE_QUEEN;
import static com.eswar.chess.BoardUtils.WHITE_ROOK;

public class Move{
    private int piece, previous, current, takenPiece, promotedPiece, score;
    private boolean enpassant, kingCastle, queenCastle, threateningMove, promotion;
    private final int rows = 8, cols = 8;

    public Move(int piece, int previous, int current, int takenPiece){
        this.piece = piece;
        this.previous = previous;
        this.current = current;
        this.takenPiece = takenPiece;
        this.enpassant = false;
        this.kingCastle = false;
        this.queenCastle = false;
        this.threateningMove = false;
        this.promotedPiece = NO_PIECE;
    }

    public Move(int piece, int previous, int current){
        this.piece = piece;
        this.previous = previous;
        this.current = current;
        this.takenPiece = EMPTY;
        this.enpassant = false;
        this.kingCastle = false;
        this.queenCastle = false;
        this.threateningMove = false;
        this.promotedPiece = NO_PIECE;
    }

    public Move(int piece, int previous, int current, int takenPiece, boolean castle, boolean enpassant){
        this.piece = piece;
        this.previous = previous;
        this.current = current;
        this.takenPiece = takenPiece;
        this.enpassant = enpassant;
        this.threateningMove = false;
        this.promotedPiece = NO_PIECE;

        if(castle) {
            if (current % rows == 2) {
                kingCastle = false;
                queenCastle = true;
            }
            else if (current % rows == cols - 2){
                kingCastle = true;
                queenCastle = false;
            }
            else{
                kingCastle = false;
                queenCastle = false;
            }
        }
        else if(enpassant){
            if(piece > 0) { this.takenPiece = BLACK_PAWN; }
            else if(piece < 0) { this.takenPiece = WHITE_PAWN; }
        }
    }
    public Move(int piece, int previous, int current, int promotedPiece, int takenPiece, boolean promotion){
        this.piece = piece;
        this.previous = previous;
        this.current = current;
        this.promotion = promotion;
        this.takenPiece = takenPiece;
        this.promotedPiece = NO_PIECE;

        if(promotion && piece * promotedPiece > 0){
            this.promotedPiece = promotedPiece;
        }
    }

    public int getCurrent() { return current; }

    public int getPiece() { return piece; }

    public int getPrevious() { return previous; }

    public int getTakenPiece() { return takenPiece; }

    public boolean isKingCastle() { return kingCastle; }

    public boolean isQueenCastle() { return queenCastle; }

    public boolean isEnpassant() { return enpassant; }

    public boolean isFinishingMove(){ return Math.abs(takenPiece) == WHITE_KING; }

    public boolean isThreateningMove() { return threateningMove; }

    public boolean isPromotion() { return promotion; }

    public int getPreviousRow(){ return previous/rows; }

    public int getPreviousCol(){ return previous % rows; }

    public int getCurrentRow(){ return current/rows; }

    public int getCurrentCol(){ return current % rows; }

    public int getPromotedPiece() { return promotedPiece; }

    public int getScore() { return score; }

    public int getCastleRookIndex(){
        if(kingCastle){ return BoardUtils.index(getCurrentRow(), cols - 3); }
        else if(queenCastle){ return BoardUtils.index(getCurrentRow(), 3); }
        else{ return NONE; }
    }
    public int getCastleRookPiece(){
        if(piece > 0){ return WHITE_ROOK; }
        else if(piece < 0){ return BLACK_ROOK; }
        else{ return NO_PIECE; }
    }
    public int getEnpassantIndex(){
        if(piece > 0){ return current + cols; }
        else if(piece < 0){ return current - cols; }
        else{ return EMPTY; }
    }
    public int getEnpassantRow(){
        return getEnpassantIndex() / rows;
    }
    public int getEnpassantCol(){
        return getEnpassantIndex() % rows;
    }

    public void setThreateningMove(boolean threateningMove) { this.threateningMove = threateningMove; }

    public void setScore(int score) { this.score = score; }

    @NonNull
    @Override
    public String toString() {
        if(this.equals(Move.getDummyMove())){
            return "Invalid Move";
        }
        return pieceString(piece, kingCastle, queenCastle, enpassant) + captured() + colString(current % rows) + (8 - current / rows) + promotedPieceString(promotedPiece) +(threateningMove ? "+" : "") + (isFinishingMove() ? "#" : "");
    }

    public static String pieceString(int piece){
        String player = "";
//        if(piece > 0){ player += "White: "; }
//        else if(piece < 0) { player += "Black: "; }
//        else { return "Unknown"; }

        switch (piece){
            case WHITE_KING: player += "K"; break;
            case WHITE_QUEEN: player += "Q"; break;
            case WHITE_ROOK: player += "R"; break;
            case WHITE_BISHOP: player += "B"; break;
            case WHITE_KNIGHT: player += "N"; break;
            case WHITE_PAWN: player += ""; break;

            case BLACK_KING: player += "K"; break;
            case BLACK_QUEEN: player += "Q"; break;
            case BLACK_ROOK: player += "R"; break;
            case BLACK_BISHOP: player += "B"; break;
            case BLACK_KNIGHT: player += "N"; break;
            case BLACK_PAWN: player += ""; break;
        }
        return player;
    }

    public static String pieceString(int piece, boolean kingCastle, boolean queenCastle, boolean enpassant){
        String player = "";
//        if(piece > 0){ player += "White: "; }
//        else if(piece < 0) { player += "Black: "; }
//        else { return "Unknown"; }
//
//        if(kingCastle){
//            player += "Kingside Castle ";
//        }
//        if(queenCastle){
//            player += "Queenside Castle ";
//        }
//        if(enpassant){
//            player += "Enpassant ";
//        }

        switch (piece){
            case WHITE_KING: player += "K"; break;
            case WHITE_QUEEN: player += "Q"; break;
            case WHITE_ROOK: player += "R"; break;
            case WHITE_BISHOP: player += "B"; break;
            case WHITE_KNIGHT: player += "N"; break;
            case WHITE_PAWN: player += ""; break;

            case BLACK_KING: player += "K"; break;
            case BLACK_QUEEN: player += "Q"; break;
            case BLACK_ROOK: player += "R"; break;
            case BLACK_BISHOP: player += "B"; break;
            case BLACK_KNIGHT: player += "N"; break;
            case BLACK_PAWN: player += ""; break;
        }
        return player;
    }

    public static String promotedPieceString(int promotedPiece){
        switch (Math.abs(promotedPiece)){
            case WHITE_QUEEN: return " Q";
            case WHITE_ROOK: return " R";
            case WHITE_BISHOP: return " B";
            case WHITE_KNIGHT: return " N";
            default: return "";
        }
    }

    public String captured(){
        if(Math.abs(getPiece()) == WHITE_PAWN && takenPiece != EMPTY && !promotion){
            return colString(previous % rows) + "x";
        }
        return (takenPiece == EMPTY) ? "" : "x";
    }

    public static String colString(int col){
        return String.valueOf((char)((int)'a' + col));
    }

    public static Move getDummyMove(){
        return new Move(NO_PIECE, NONE, NONE);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            Move compareMove = (Move)obj;
            if (compareMove != null) {
                return (compareMove.getPiece() == getPiece()) && (compareMove.getPrevious() == getPrevious()) && (compareMove.getCurrent() == getCurrent()) && (compareMove.getTakenPiece() == getTakenPiece() && (compareMove.getPromotedPiece() == getPromotedPiece()) && (compareMove.isEnpassant() == isEnpassant()));
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
