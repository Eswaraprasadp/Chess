package com.eswar.chess;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.eswar.chess.BoardUtils.BLACK_BISHOP;
import static com.eswar.chess.BoardUtils.BLACK_KING;
import static com.eswar.chess.BoardUtils.BLACK_KNIGHT;
import static com.eswar.chess.BoardUtils.BLACK_PAWN;
import static com.eswar.chess.BoardUtils.BLACK_QUEEN;
import static com.eswar.chess.BoardUtils.BLACK_ROOK;
import static com.eswar.chess.BoardUtils.EMPTY;
import static com.eswar.chess.BoardUtils.NONE;
import static com.eswar.chess.BoardUtils.NO_PIECE;
import static com.eswar.chess.BoardUtils.NO_RESULT;
import static com.eswar.chess.BoardUtils.DRAW;
import static com.eswar.chess.BoardUtils.WHITE_BISHOP;
import static com.eswar.chess.BoardUtils.WHITE_KING;
import static com.eswar.chess.BoardUtils.WHITE_KNIGHT;
import static com.eswar.chess.BoardUtils.WHITE_PAWN;
import static com.eswar.chess.BoardUtils.WHITE_QUEEN;
import static com.eswar.chess.BoardUtils.WHITE_ROOK;
import static com.eswar.chess.BoardUtils.WHITE_WIN;
import static com.eswar.chess.BoardUtils.BLACK_WIN;
import static com.eswar.chess.BoardUtils.canCastle;
import static com.eswar.chess.BoardUtils.cols;
import static com.eswar.chess.BoardUtils.copyCastleInfo;
import static com.eswar.chess.BoardUtils.copyMoves;
import static com.eswar.chess.BoardUtils.index;
import static com.eswar.chess.BoardUtils.rows;
import static com.eswar.chess.BoardUtils.tag;
import static com.eswar.chess.BoardUtils.validate;

public class Game {
    private List<Move> moveList = new ArrayList<>();
    private List<List<Integer>> brokenCastleMoves = new ArrayList<>();
    private boolean check = false, kingCastleWhite = true, queenCastleWhite = true, kingCastleBlack = true, queenCastleBlack = true, whiteTurn = true;
    private int result = NO_RESULT, breakPoint = 0, prevBreakPoint = 0;
    private Board board = new Board();
    private boolean whiteAI = false;
    private AI ai = new AI(whiteAI);

    Game(){
        kingCastleWhite = true;
        queenCastleWhite = true;
        kingCastleBlack = true;
        queenCastleBlack = true;
    }

    Game(Board board, List<Move> moveList, List<List<Integer>> brokenCastleMoves, boolean whiteTurn, int breakPoint, int prevBreakPoint){

        this.board = board;
        this.moveList = moveList;
        this.brokenCastleMoves = brokenCastleMoves;
        this.breakPoint = breakPoint;
        this.prevBreakPoint = prevBreakPoint;
        this.whiteTurn = whiteTurn;

        boolean canCastle[] = canCastle(brokenCastleMoves);
        kingCastleWhite = canCastle[0];
        queenCastleWhite = canCastle[1];
        queenCastleWhite = canCastle[2];
        queenCastleWhite = canCastle[3];
    }

    Game(Board board, List<Move> moveList, boolean whiteTurn, boolean kingCastleWhite, boolean queenCastleWhite, boolean kingCastleBlack, boolean queenCastleBlack){
        this.board = board;
        this.moveList = moveList;
        this.whiteTurn = whiteTurn;

        this.kingCastleWhite = kingCastleWhite;
        this.queenCastleWhite = queenCastleWhite;
        this.kingCastleBlack = kingCastleBlack;
        this.queenCastleBlack = queenCastleBlack;
    }

    Game(Board board, List<Move> moveList, boolean whiteTurn){
        this.board = board;
        this.moveList = moveList;
        this.whiteTurn = whiteTurn;

        kingCastleWhite = queenCastleWhite = kingCastleBlack = queenCastleBlack = true;
    }

    Game(Board board, boolean whiteTurn){
        this.board = board;
        this.whiteTurn = whiteTurn;

        kingCastleWhite = queenCastleWhite = kingCastleBlack = queenCastleBlack = true;
    }

    public List<Move> getPossibleMoves(int row, int col){
        return board.getPossibleMoves(row, col, true, true);
    }

    public List<Move> getAllPossibleMoves(){
        return board.getAllPossibleMoves();
    }

    public Move getAIMove(){
        return ai.getBestMove(this);
    }

    public void makeMove(Move move){
        if(move.equals(Move.getDummyMove()) || !validate(move.getPreviousRow(), move.getPreviousCol()) || !validate(move.getCurrentRow(), move.getCurrentCol()) || move.getPiece() == NO_PIECE){
            Log.d(tag, "Invalid move: " + move.toString());
            return;
        }
        board.makeMove(move);
        if(move.isKingCastle()){
            if(whiteTurn){
                kingCastleWhite = false;
                queenCastleWhite = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 1);
                brokenCastleMoves.add(list);
            }
            else{
                kingCastleBlack = false;
                queenCastleBlack = false;
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 1);
                brokenCastleMoves.add(list);
            }

            prevBreakPoint = breakPoint;
            breakPoint = moveList.size();
        }
        else if(move.isQueenCastle()){

            if(whiteTurn){
                kingCastleWhite = false;
                queenCastleWhite = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 1);
                brokenCastleMoves.add(list);
            }
            else{
                kingCastleBlack = false;
                queenCastleBlack = false;
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 1);
                brokenCastleMoves.add(list);
            }

            prevBreakPoint = breakPoint;
            breakPoint = moveList.size();
        }
        else if(move.isPromotion()){

            prevBreakPoint = breakPoint;
            breakPoint = moveList.size();
        }
        else if(move.isEnpassant()){

            prevBreakPoint = breakPoint;
            breakPoint = moveList.size();
        }
        else {

            if(move.getPiece() == WHITE_KING && (kingCastleWhite || queenCastleWhite)){
                kingCastleWhite = false;
                queenCastleWhite = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 1);
                brokenCastleMoves.add(list);
            }
            else if(move.getPiece() == BLACK_KING && (kingCastleBlack || queenCastleBlack)){
                kingCastleBlack = false;
                queenCastleBlack = false;
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 1);
                brokenCastleMoves.add(list);
            }
            else if(move.getPiece() == WHITE_ROOK && move.getCurrentCol() == 0 && kingCastleWhite){
                kingCastleWhite = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 0);
                brokenCastleMoves.add(list);
            }
            else if(move.getPiece() == WHITE_ROOK && move.getCurrentCol() == cols-1 && queenCastleWhite){
                List<Integer> list = Arrays.asList(moveList.size(), 1, 0, 1);
                brokenCastleMoves.add(list);
                queenCastleWhite = false;
            }
            else if(move.getPiece() == BLACK_ROOK && move.getCurrentCol() == 0 && kingCastleBlack){
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 0);
                brokenCastleMoves.add(list);
                kingCastleBlack = false;
            }
            else if(move.getPiece() == BLACK_ROOK && move.getCurrentCol() == cols-1 && queenCastleBlack){
                List<Integer> list = Arrays.asList(moveList.size(), 0, 0, 1);
                brokenCastleMoves.add(list);
                queenCastleBlack = false;
            }
        }
        check = move.isThreateningMove();
        moveList.add(move);
        board.setPrevMove(move);
        whiteTurn = !whiteTurn;
    }

    public void undoMove(){
        if(moveList.size() > 0) {
            Move move = moveList.get(moveList.size() - 1);
            board.undoMove(move);

            if(move.isKingCastle() || move.isQueenCastle() || move.isPromotion() || move.getTakenPiece() != EMPTY) {
                breakPoint = prevBreakPoint;
            }

            // Check for right to castle
            for (int i = 0; i < brokenCastleMoves.size(); ++i) {
                if (moveList.size() - 1 == brokenCastleMoves.get(i).get(0)) {
                    List<Integer> list = brokenCastleMoves.get(i);
                    if (list.get(1) == 1 && !whiteTurn) {
                        if (list.get(2) == 1) {
                            kingCastleWhite = true;
                        }
                        if (list.get(3) == 1) {
                            queenCastleWhite = true;
                        }
                    } else if (list.get(1) == 0 && whiteTurn) {
                        if (list.get(2) == 1) {
                            kingCastleBlack = true;
                        }
                        if (list.get(3) == 1) {
                            queenCastleBlack = true;
                        }
                    }
                }
            }
            whiteTurn = !whiteTurn;
            moveList.remove(moveList.size() - 1);
            if(moveList.size() > 0) {
                board.setPrevMove(moveList.get(moveList.size() - 1));
            }
            check = board.isCheck();
            result = NO_RESULT;
        }
    }



    public int getResult() {

        // 50 Move Rule
        if(moveList.size() >= 100){
            int i;
            for (i = moveList.size() - 1; i >= moveList.size() - 100; --i){
                Move move = moveList.get(i);
                if(move.isThreateningMove() || move.isKingCastle() || move.isQueenCastle() || move.isPromotion() || move.getTakenPiece() != NO_PIECE){
                    break;
                }
            }
            if(i < moveList.size() - 100){
                return DRAW;
            }
        }

//        boolean hasWhiteKing = false, hasBlackKing = false, stop = false;

//        // No king
//        for (int row = 0; row < rows && !stop; ++row){
//            for (int col = 0; col < cols; ++col){
//                if(board.piece(row, col) == WHITE_KING) { hasWhiteKing = true; }
//                else if(board.piece(row, col) == BLACK_KING) { hasBlackKing = true; }
//                if(hasWhiteKing && hasBlackKing) {
//                    stop = true;
//                    break;
//                }
//            }
//        }
//
//        if(!stop){
//            if(!hasWhiteKing) { return BLACK_WIN; }
//            else if(!hasBlackKing)  { return WHITE_WIN; }
//        }

        // Valid move exists
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                if(!board.oppositePiece(row, col)){
                    if(board.getPossibleMoves(row, col, true, false).size() > 0){
//                        Log.d(tag, "Possible moves in getResult: " + getPossibleMoves());
                        return NO_RESULT;
                    }
                }
            }
        }

        // No valid move exists
        if(check) { result = (whiteTurn) ? BLACK_WIN : WHITE_WIN; }
        else { result = DRAW; }
        return result;
    }

    public int indexOfOppositeKing(){
        if(!whiteTurn){
            for (int row = 0; row < rows; ++row){
                for (int col = 0; col < cols; ++col){
                    if(board.piece(row, col) == BLACK_KING){
                        return index(row, col);
                    }
                }
            }
        }
        else{
            for (int row = rows - 1; row >= 0; --row){
                for (int col = 0; col < cols; ++col){
                    if(board.piece(row, col) == WHITE_KING){
                        return index(row, col);
                    }
                }
            }
        }
        return NONE;
    }

    public boolean isCheck() { return check; }

    public boolean isWhiteTurn() { return whiteTurn; }

    public boolean isWhiteAI() { return whiteAI; }

    public Game copyAll() {
        return new Game(board.copy(), copyMoves(moveList), copyCastleInfo(brokenCastleMoves), whiteTurn, breakPoint, prevBreakPoint);
    }
    public Game copyOnlyMoves(){
        return new Game(board.copy(), copyMoves(moveList), whiteTurn);
    }
    public Game copyWithCastleInfo(){
        return new Game(board.copy(), copyMoves(moveList), whiteTurn, kingCastleWhite, queenCastleWhite, kingCastleBlack, queenCastleBlack);
    }
    public Game copyMinimal(){
        return new Game(board.copy(), whiteTurn);
    }

    public Board getBoard() { return board; }

    public int evaluate() { return board.evaluate(); }

    public int[][] getGrids() { return board.getGrids(); }

    public void finish(){
        board = null;
        moveList = null;
        brokenCastleMoves = null;
    }
}
