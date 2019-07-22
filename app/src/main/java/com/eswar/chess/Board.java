package com.eswar.chess;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.eswar.chess.BoardUtils.BISHOP_SCORE;
import static com.eswar.chess.BoardUtils.BISHOP_TABLE;
import static com.eswar.chess.BoardUtils.BLACK_BISHOP;
import static com.eswar.chess.BoardUtils.BLACK_KING;
import static com.eswar.chess.BoardUtils.BLACK_KNIGHT;
import static com.eswar.chess.BoardUtils.BLACK_PAWN;
import static com.eswar.chess.BoardUtils.BLACK_QUEEN;
import static com.eswar.chess.BoardUtils.BLACK_ROOK;
import static com.eswar.chess.BoardUtils.EMPTY;
import static com.eswar.chess.BoardUtils.KING_SCORE;
import static com.eswar.chess.BoardUtils.KING_TABLE_END_GAME;
import static com.eswar.chess.BoardUtils.KING_TABLE_MIDDLE_GAME;
import static com.eswar.chess.BoardUtils.KNIGHT_SCORE;
import static com.eswar.chess.BoardUtils.KNIGHT_TABLE;
import static com.eswar.chess.BoardUtils.NONE;
import static com.eswar.chess.BoardUtils.NO_PIECE;
import static com.eswar.chess.BoardUtils.PAWN_SCORE;
import static com.eswar.chess.BoardUtils.PAWN_TABLE;
import static com.eswar.chess.BoardUtils.QUEEN_SCORE;
import static com.eswar.chess.BoardUtils.QUEEN_TABLE;
import static com.eswar.chess.BoardUtils.ROOK_SCORE;
import static com.eswar.chess.BoardUtils.ROOK_TABLE;
import static com.eswar.chess.BoardUtils.WHITE_BISHOP;
import static com.eswar.chess.BoardUtils.WHITE_KING;
import static com.eswar.chess.BoardUtils.WHITE_KNIGHT;
import static com.eswar.chess.BoardUtils.WHITE_PAWN;
import static com.eswar.chess.BoardUtils.WHITE_QUEEN;
import static com.eswar.chess.BoardUtils.WHITE_ROOK;
import static com.eswar.chess.BoardUtils.arrayDeepEquals;
import static com.eswar.chess.BoardUtils.canEnpassant;
import static com.eswar.chess.BoardUtils.canEscapeCheck;
import static com.eswar.chess.BoardUtils.cols;
import static com.eswar.chess.BoardUtils.copyGrids;
import static com.eswar.chess.BoardUtils.index;
import static com.eswar.chess.BoardUtils.lMoves;
import static com.eswar.chess.BoardUtils.pieceString;
import static com.eswar.chess.BoardUtils.rows;
import static com.eswar.chess.BoardUtils.sign;
import static com.eswar.chess.BoardUtils.tag;
import static com.eswar.chess.BoardUtils.validate;

public class Board {
    private int grids[][];
    private boolean whiteTurn = true, kingCastleWhite = true, queenCastleWhite = true, kingCastleBlack = true, queenCastleBlack = true;
    private Move prevMove = Move.getDummyMove();

    Board(){
        grids = new int[][]{
                {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
                {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
                {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
        };
    }
    Board(int[][] grids, boolean whiteTurn){
        this.grids = grids;
        this.whiteTurn = whiteTurn;
    }
    Board(int[][] grids, boolean whiteTurn, Move prevMove){
        this.grids = grids;
        this.whiteTurn = whiteTurn;
        this.prevMove = prevMove;
    }
    Board(int[][] grids, boolean whiteTurn, Move prevMove, boolean kingCastleWhite, boolean queenCastleWhite, boolean kingCastleBlack, boolean queenCastleBlack){
        this.grids = grids;
        this.whiteTurn = whiteTurn;
        this.kingCastleWhite = kingCastleWhite;
        this.kingCastleBlack = kingCastleBlack;
        this.queenCastleWhite = queenCastleWhite;
        this.queenCastleBlack = queenCastleBlack;
        this.prevMove = prevMove;
    }

    public Board copy(){
        return new Board(copyGrids(grids), whiteTurn, prevMove, kingCastleWhite, queenCastleWhite, kingCastleBlack, queenCastleBlack);
    }

    public boolean isCheck(){
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                if(!oppositePiece(row, col)){
                    List<Move> possibleMoves = getPossibleMoves(row, col, true, false);
                    for (Move move : possibleMoves){
                        if(move.isFinishingMove()){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public int evaluate(){
        int score = 0;
        int queens = 0, minorPieces = 0, rowWhiteKing = NONE, colWhiteKing = NONE, rowBlackKing = NONE, colBlackKing = NONE;
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                if(piece(row, col) == WHITE_KING){
                    score += KING_SCORE + KING_TABLE_MIDDLE_GAME[row][col];
                    rowWhiteKing = row;
                    colWhiteKing = col;
                }
                else if(piece(row, col) == WHITE_QUEEN){
                    score += QUEEN_SCORE + QUEEN_TABLE[row][col];
                    ++queens;
                }
                else if(piece(row, col) == WHITE_ROOK){
                    score += ROOK_SCORE + ROOK_TABLE[row][col];
                }
                else if(piece(row, col) == WHITE_BISHOP){
                    score += BISHOP_SCORE + BISHOP_TABLE[row][col];
                    ++minorPieces;
                }
                else if(piece(row, col) == WHITE_KNIGHT){
                    score += KNIGHT_SCORE + KNIGHT_TABLE[row][col];
                    ++minorPieces;
                }
                else if(piece(row, col) == WHITE_PAWN){
                    score += PAWN_SCORE + PAWN_TABLE[row][col];
                }
                else if(piece(row, col) == BLACK_KING){
                    score -= KING_SCORE + KING_TABLE_MIDDLE_GAME[rows-1-row][col];
                    rowBlackKing = row;
                    colBlackKing = col;
                }
                else if(piece(row, col) == BLACK_QUEEN){
                    score -= QUEEN_SCORE + QUEEN_TABLE[rows-1-row][col];
                    ++queens;
                }
                else if(piece(row, col) == BLACK_ROOK){
                    score -= ROOK_SCORE + ROOK_TABLE[rows-1-row][col];
                }
                else if(piece(row, col) == BLACK_BISHOP){
                    score -= BISHOP_SCORE + BISHOP_TABLE[rows-1-row][col];
                    ++minorPieces;
                }
                else if(piece(row, col) == BLACK_KNIGHT){
                    score -= KNIGHT_SCORE + KNIGHT_TABLE[rows-1-row][col];
                    ++minorPieces;
                }
                else if(piece(row, col) == BLACK_PAWN){
                    score -= PAWN_SCORE + PAWN_TABLE[rows-1-row][col];
                }
            }
        }
        if(queens == 0 || queens == 1 && minorPieces <= 2){
            if(rowWhiteKing != NONE){
                score += KING_TABLE_END_GAME[rowWhiteKing][colWhiteKing] - KING_TABLE_MIDDLE_GAME[rowWhiteKing][colWhiteKing];
            }
            if(rowBlackKing != NONE){
                score -= KING_TABLE_END_GAME[rows-1-rowBlackKing][colBlackKing] - KING_TABLE_MIDDLE_GAME[rows-1-rowBlackKing][colBlackKing];
            }
        }
        return score;
    }

    public List<Move> getPossibleMoves(int row, int col, boolean validateCheck, boolean checkAttackingThreat){
        List<Move> moves = new ArrayList<>();
        int piece = grids[row][col];
        if(oppositePiece(row, col)){ return new ArrayList<>(); }
        else if(piece == EMPTY){ return new ArrayList<>(); }
//        Log.d(tag, "Previous move: " + prevMove);
        if(piece == WHITE_PAWN){
            // Enpassant
            if(!prevMove.equals(Move.getDummyMove())) {
//                Log.d(tag, "Last Move: " + moveList.get(moveList.size() - 1) + ", " + moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col - 1), index(row, col - 1))) + ", " + moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col + 1), index(row, col + 1))));
                if (canEnpassant(prevMove, piece, row, col) && piece(row - 2, col - 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row - 1, col - 1), EMPTY, false, true));
                }
                else if (canEnpassant(prevMove, piece, row, col) && piece(row - 2, col + 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row - 1, col + 1), EMPTY, false, true));
                }
            }

            // Capture
            if(row != 1){
                if(oppositeSigns(piece, row-1, col-1)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col-1), piece(row-1, col-1)));
                }
                if(oppositeSigns(piece, row-1, col+1)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col+1), piece(row-1, col+1)));
                }
            }

            // Opening pawn move
            if(row == rows - 2){
                if((grids[row-1][col] == EMPTY) && (grids[row-2][col] == EMPTY)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col)));
                    moves.add(new Move(piece, index(row, col), index(row-2, col)));
                }
                else if(grids[row-1][col] == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row-1, col)));
                }
            }

            // Pawn Promotion
            else if(row == 1){
                int[] promotedPieces = new int[] {WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT};
                if(!isBlocked(piece, 0, col)) {
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(0, col), promotedPiece, piece(0, col), true));
                    }
                }
                if(!sameSigns(piece, 0, col - 1) && validate(col - 1)){
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(0, col - 1), promotedPiece, piece(0, col - 1), true));
                    }
                }
                if(!sameSigns(piece, 0, col + 1) && validate(col + 1)){
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(0, col + 1), promotedPiece, piece(0, col + 1), true));
                    }
                }
            }

            // Regular move
            else if(!isBlocked(piece, row - 1, col)){
                moves.add(new Move(piece, index(row, col), index(row-1, col)));
            }
        }

        else if(piece == BLACK_PAWN){

            // Enpassant
            if(!prevMove.equals(Move.getDummyMove())) {
//                Log.d(tag, "Last move: " + moveList.get(moveList.size() - 1) + ", " + moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col - 1), index(row, col - 1))) + ", " + moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col + 1), index(row, col + 1))));
                if (canEnpassant(prevMove, piece, row, col) && piece(row + 2, col - 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row + 1, col - 1), EMPTY, false, true));
                }
                else if (canEnpassant(prevMove, piece, row, col) && piece(row + 2, col + 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row + 1, col + 1), EMPTY, false, true));
                }
            }

            // Capture
            if(row != rows - 2){
                if(oppositeSigns(piece, row+1, col-1)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col-1), piece(row+1, col-1)));
                }
                if(oppositeSigns(piece, row+1, col+1)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col+1), piece(row+1, col+1)));
                }
            }

            // Opening pawn move
            if(row == 1){
                if((grids[row+1][col] == EMPTY) && (grids[row+2][col] == EMPTY)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col)));
                    moves.add(new Move(piece, index(row, col), index(row+2, col)));
                }
                else if(grids[row+1][col] == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row+1, col)));
                }
            }

            // Pawn Promotion
            else if(row == rows - 2){
                int[] promotedPieces = new int[] {BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT};
                if(!isBlocked(piece, rows - 1, col)) {
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(rows - 1, col), promotedPiece, piece(rows - 1, col), true));
                    }
                }
                if(!sameSigns(piece, rows - 1, col - 1) && validate(col - 1)){
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(rows - 1, col - 1), promotedPiece, piece(rows - 1, col - 1), true));
                    }
                }
                if(!sameSigns(piece, rows - 1, col + 1) && validate(col + 1)){
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(rows - 1, col + 1), promotedPiece, piece(rows - 1, col + 1), true));
                    }
                }
            }

            // Regular moves
            else if(!isBlocked(piece, row + 1, col) && !oppositeSigns(piece, row + 1, col)){
                moves.add(new Move(piece, index(row, col), index(row+1, col)));
            }
        }

        else if(Math.abs(piece) == WHITE_KING){
//            Log.d(tag, "All king moves: ");
            for (int i = row - 1; i <= row + 1; ++i){
                for (int j = col - 1; j <= col + 1; ++j) {
//                    Log.d(tag, "Row = " + i + ", Col = " + j +  ", " + validate(i, j) +  ", " + (i != row || j != col) +  ", " + !isBlocked(piece, i, j));
                    if (validate(i, j) && (i != row || j != col) && !isBlocked(piece, i, j)) {
                        moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j)));
                    }
                }
            }
            if(row == rows - 1 && col == 4 && piece(row, col+1) == EMPTY && piece(row, col+2) == EMPTY && kingCastleWhite && whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col+2), EMPTY, true, false));
            }
            if(row == rows - 1 && col == 4 && piece(row, col-1) == EMPTY && piece(row, col-2) == EMPTY && piece(row, col-3) == EMPTY && queenCastleWhite && whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col-2), EMPTY, true, false));
            }
            if(row == 0 && col == 4 && piece(row, col+1) == EMPTY && piece(row, col+2) == EMPTY && kingCastleBlack && !whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col+2), EMPTY, true, false));
            }
            if(row == 0 && col == 4 && piece(row, col-1) == EMPTY && piece(row, col-2) == EMPTY && piece(row, col-3) == EMPTY && queenCastleBlack && !whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col-2), EMPTY, true, false));
            }
        }

        else if(Math.abs(piece) == WHITE_ROOK){

            for (int j = col + 1; validate(j); ++j){
                if(isBlocked(piece, row, j)) { break; }
                else if(oppositeSigns(piece, row, j)){
                    moves.add(new Move(piece, index(row, col), index(row, j), piece(row, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(row, j), piece(row, j))); }
            }
            for (int j = col - 1; validate(j); --j){
                if(isBlocked(piece, row, j)) break;
                else if(oppositeSigns(piece, row, j)){
                    moves.add(new Move(piece, index(row, col), index(row, j), piece(row, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(row, j), piece(row, j))); }
            }
            for (int i = row + 1; validate(i); ++i){
                if(isBlocked(piece, i, col)) break;
                else if(oppositeSigns(piece, i, col)){
                    moves.add(new Move(piece, index(row, col), index(i, col), piece(i, col)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, col), piece(i, col))); }
            }
            for (int i = row - 1; validate(i); --i){
                if(isBlocked(piece, i, col)) break;
                else if(oppositeSigns(piece, i, col)){
                    moves.add(new Move(piece, index(row, col), index(i, col), piece(i, col)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, col), piece(i, col))); }
            }
        }

        else if(Math.abs(piece) == WHITE_BISHOP){

            for (int d = 1; validate(row - d, col + d); ++d){
                int i = row - d, j = col + d;
                if(isBlocked(piece, i, j)) { break; }
                else if(oppositeSigns(piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j))); }
            }
            for (int d = 1; validate(row + d, col - d); ++d){
                int i = row + d, j = col - d;
                if(isBlocked(piece, i, j)) { break; }
                else if(oppositeSigns(piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j))); }
            }
            for (int d = 1; validate(row - d, col - d); ++d){
                int i = row - d, j = col - d;
                if(isBlocked(piece, i, j)) { break; }
                else if(oppositeSigns(piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j))); }
            }
            for (int d = 1; validate(row + d, col + d); ++d){
                int i = row + d, j = col + d;
                if(isBlocked(piece, i, j)) { break; }
                else if(oppositeSigns(piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j))); }
            }
        }

        else if(Math.abs(piece) == WHITE_QUEEN){

            // Horizontal and Vertical
            for (int j = col + 1; validate(j); ++j){
                if(isBlocked(piece, row, j)) { break; }
                else if(oppositeSigns(piece, row, j)){
                    moves.add(new Move(piece, index(row, col), index(row, j), piece(row, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(row, j), piece(row, j))); }
            }
            for (int j = col - 1; validate(j); --j){
                if(isBlocked(piece, row, j)) break;
                else if(oppositeSigns(piece, row, j)){
                    moves.add(new Move(piece, index(row, col), index(row, j), piece(row, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(row, j), piece(row, j))); }
            }
            for (int i = row + 1; validate(i); ++i){
                if(isBlocked(piece, i, col)) break;
                else if(oppositeSigns(piece, i, col)){
                    moves.add(new Move(piece, index(row, col), index(i, col), piece(i, col)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, col), piece(i, col))); }
            }
            for (int i = row - 1; validate(i); --i){
                if(isBlocked(piece, i, col)) break;
                else if(oppositeSigns(piece, i, col)){
                    moves.add(new Move(piece, index(row, col), index(i, col), piece(i, col)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, col), piece(i, col))); }
            }

            // Diagonal
            for (int d = 1; validate(row - d, col + d); ++d){
                int i = row - d, j = col + d;
                if(isBlocked(piece, i, j)) { break; }
                else if(oppositeSigns(piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j))); }
            }
            for (int d = 1; validate(row + d, col - d); ++d){
                int i = row + d, j = col - d;
                if(isBlocked(piece, i, j)) { break; }
                else if(oppositeSigns(piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j))); }
            }
            for (int d = 1; validate(row - d, col - d); ++d){
                int i = row - d, j = col - d;
                if(isBlocked(piece, i, j)) { break; }
                else if(oppositeSigns(piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j))); }
            }
            for (int d = 1; validate(row + d, col + d); ++d){
                int i = row + d, j = col + d;
                if(isBlocked(piece, i, j)) { break; }
                else if(oppositeSigns(piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(i, j))); }
            }
        }

        else if(Math.abs(piece) == WHITE_KNIGHT){
            int[][] lMoves = lMoves(row, col);
            for (int[] lMove : lMoves) {
                if (validate(lMove[0], lMove[1]) && !isBlocked(piece, lMove[0], lMove[1])) {
                    moves.add(new Move(piece, index(row, col), index(lMove[0], lMove[1]), piece(lMove[0], lMove[1])));
                }
            }
        }

        if(validateCheck) {
            List<Move> validMoves = new ArrayList<>();
//            Log.d(tag, "Probable moves: " + moves);
            for (Move move : moves) {
                if (canEscapeCheck(grids, move, whiteTurn, prevMove)) {
                    validMoves.add(move);

                    if (checkAttackingThreat && BoardUtils.isCheck(grids, move, whiteTurn))
                        move.setThreateningMove(true);
                }
            }
//            Log.d(tag, "Valid moves: " + validMoves);
            return validMoves;
        }
        else{
            return moves;
        }
    }

    public List<Move> getAllPossibleMoves(){
        List<Move> allPossibleMoves = new ArrayList<>();
        for (int col = cols/2; col <cols; ++col){
            if(whiteTurn) {
                for (int row = 0; row < rows; ++row) {
                    allPossibleMoves.addAll(getPossibleMoves(row, col, true, false));
                }
            }
            else{
                for (int row = rows - 1; row >= 0; --row){
                    allPossibleMoves.addAll(getPossibleMoves(row, col, true, false));
                }
            }
        }
        for(int col = cols/2 - 1; col >= 0; --col){
            if(whiteTurn) {
                for (int row = 0; row < rows; ++row) {
                    allPossibleMoves.addAll(getPossibleMoves(row, col, true, false));
                }
            }
            else{
                for (int row = rows - 1; row >= 0; --row){
                    allPossibleMoves.addAll(getPossibleMoves(row, col, true, false));
                }
            }
        }
        return allPossibleMoves;
    }

    public void makeMove(Move move){
        if(move.equals(Move.getDummyMove()) || !validate(move.getPreviousRow(), move.getPreviousCol()) || !validate(move.getCurrentRow(), move.getCurrentCol()) || move.getPiece() == NO_PIECE){
            Log.d(tag, "Invalid move: " + move.toString());
            return;
        }
        if(move.isKingCastle()){
            //King
            grids[move.getPreviousRow()][4] = EMPTY;
            grids[move.getCurrentRow()][cols-2] = move.getPiece();

            //Rook
            grids[move.getPreviousRow()][cols-3] = grids[move.getPreviousRow()][cols-1];
            grids[move.getPreviousRow()][cols-1] = EMPTY;

        }
        else if(move.isQueenCastle()){
            //King
            grids[move.getPreviousRow()][4] = EMPTY;
            grids[move.getCurrentRow()][2] = move.getPiece();

            //Rook
            grids[move.getPreviousRow()][3] = grids[move.getPreviousRow()][0];
            grids[move.getPreviousRow()][0] = EMPTY;
        }
        else if(move.isPromotion()){
            grids[move.getPreviousRow()][move.getPreviousCol()] = EMPTY;
            grids[move.getCurrentRow()][move.getCurrentCol()] = move.getPromotedPiece();
        }
        else if(move.isEnpassant()){
            grids[move.getPreviousRow()][move.getPreviousCol()] = EMPTY;
            grids[move.getCurrentRow()][move.getCurrentCol()] = move.getPiece();
            grids[move.getEnpassantRow()][move.getEnpassantCol()] = EMPTY;
        }
        else {
            grids[move.getPreviousRow()][move.getPreviousCol()] = EMPTY;
            grids[move.getCurrentRow()][move.getCurrentCol()] = move.getPiece();
        }
        whiteTurn = !whiteTurn;
    }

    public void undoMove(Move move){

        if (move.isKingCastle()) {
            // Replace King
            grids[move.getPreviousRow()][cols - 4] = grids[move.getCurrentRow()][move.getCurrentCol()];
            grids[move.getCurrentRow()][move.getCurrentCol()] = EMPTY;

            // Replace Kingside Rook
            grids[move.getPreviousRow()][cols - 1] = grids[move.getCurrentRow()][cols - 3];
            grids[move.getCurrentRow()][cols - 3] = EMPTY;

        } else if (move.isQueenCastle()) {
            // Replace King
            grids[move.getPreviousRow()][4] = grids[move.getCurrentRow()][move.getCurrentCol()];
            grids[move.getCurrentRow()][move.getCurrentCol()] = EMPTY;

            // Replace Queenside Rook
            grids[move.getPreviousRow()][0] = grids[move.getCurrentRow()][3];
            grids[move.getCurrentRow()][3] = EMPTY;

        } else if (move.isPromotion()) {
            grids[move.getPreviousRow()][move.getPreviousCol()] = move.getPiece();
            grids[move.getCurrentRow()][move.getCurrentCol()] = move.getTakenPiece();

        } else if (move.isEnpassant()) {
            grids[move.getPreviousRow()][move.getPreviousCol()] = move.getPiece();
            grids[move.getCurrentRow()][move.getCurrentCol()] = EMPTY;
            grids[move.getEnpassantRow()][move.getEnpassantCol()] = move.getTakenPiece();
        } else {
            grids[move.getPreviousRow()][move.getPreviousCol()] = move.getPiece();
            grids[move.getCurrentRow()][move.getCurrentCol()] = move.getTakenPiece();
        }
        whiteTurn = !whiteTurn;
    }

    public boolean isBlocked(int piece, int row, int col){
        if(Math.abs(piece) == WHITE_PAWN){
            return grids[row][col] != EMPTY;
        }
        else{
            return sameSigns(piece, row, col);
        }
    }

    boolean oppositePiece(int row, int col){
        return (whiteTurn && grids[row][col] <= 0) || (!whiteTurn && grids[row][col] >= 0);
    }

    boolean oppositeSigns(int piece, int row, int col){
        if(!validate(row, col)) { return false; }
        else { return piece * grids[row][col] < 0; }
    }
    boolean sameSigns(int piece, int row, int col){
        if(!validate(row, col)) { return false; }
        else { return piece * grids[row][col] > 0; }
    }

    int piece(int row, int col){
        if(!validate(row, col)) { return NO_PIECE; }
        else { return grids[row][col]; }
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("\n");
        for (int i = 0; i < rows; ++i){
            for (int j = 0; j < cols; ++j){
                string.append(pieceString(grids[i][j]));
                string.append(" ");
            }
            string.append("\n");
        }
        return string.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            Board anotherBoard = (Board)obj;
            return arrayDeepEquals(this.grids, anotherBoard.getGrids());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    private Board copyMinimal(){
        return new Board(copyGrids(grids), whiteTurn);
    }
    private Board copyWithPrevMove(){
        return new Board(copyGrids(grids), whiteTurn, prevMove);
    }
    private Board copyWithCastleInfo(){
        return new Board(copyGrids(grids), whiteTurn, prevMove, kingCastleWhite, queenCastleWhite, kingCastleBlack, queenCastleBlack);
    }

    int[][] getGrids() { return grids; }

    public boolean isWhiteTurn() { return whiteTurn; }

    void changeWhiteTurn() { whiteTurn = !whiteTurn; }

    public void setPrevMove(Move prevMove) { this.prevMove = prevMove; }

    public Move getPrevMove() { return prevMove; }
}
