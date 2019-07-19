package com.eswar.chess;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardUtils {
    static final int NO_PIECE = -200, NONE = -1;
    final static int rows = 8, cols = 8;
    final static int WHITE_WIN = 5, BLACK_WIN = -5, DRAW = 1, NO_RESULT = 0;
    final static int WHITE_KING = 100, WHITE_QUEEN = 10, WHITE_ROOK = 5, WHITE_BISHOP = 4, WHITE_KNIGHT = 3, WHITE_PAWN = 1;
    final static int BLACK_KING = -100, BLACK_QUEEN = -10, BLACK_ROOK = -5, BLACK_BISHOP = -4, BLACK_KNIGHT = -3, BLACK_PAWN = -1;
    final static int EMPTY = 0;
    private int grids[][];
    final static String tag = "tag";
    private List<Move> moveList = new ArrayList<>();
    private List<List<Integer>> brokenCastleMoves = new ArrayList<>();
    private int perpetualBreakPoint, previousBreakPoint;
    private List<int[][]> savedPositions = new ArrayList<>();
    private boolean check, whiteTurn, whiteCanKingCastle, whiteCanQueenCastle, blackCanKingCastle, blackCanQueenCastle;
    private int result;

    BoardUtils(){
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
        moveList.clear();
        brokenCastleMoves.clear();
        result = NO_RESULT;
        whiteTurn = true;
        whiteCanKingCastle = true;
        whiteCanQueenCastle = true;
        blackCanKingCastle = true;
        blackCanQueenCastle = true;
        perpetualBreakPoint = 0;
        previousBreakPoint = 0;
        savedPositions = new ArrayList<>();
        savedPositions.add(grids);
    }

    BoardUtils(int[][] grids, List<Move> moveList, List<List<Integer>> brokenCastleMoves, boolean whiteTurn, int perpetualBreakPoint, int previousBreakPoint, List<int[][]> savedPositions){
        this.grids = grids;
        this.moveList = moveList;
        this.brokenCastleMoves = brokenCastleMoves;
        this.perpetualBreakPoint = perpetualBreakPoint;
        this.previousBreakPoint = previousBreakPoint;
        this.whiteTurn = whiteTurn;
        this.savedPositions = savedPositions;

        boolean canCastle[] = canCastle(brokenCastleMoves);
        this.whiteCanKingCastle = canCastle[0];
        this.whiteCanQueenCastle = canCastle[1];
        this.blackCanKingCastle = canCastle[2];
        this.blackCanQueenCastle = canCastle[3];
    }

    public BoardUtils copy() {
        return new BoardUtils(this.getGrids(), this.getMoveList(), this.getBrokenCastleMoves(), this.isWhiteTurn(), this.getPerpetualBreakPoint(), this.getPreviousBreakPoint(), this.getSavedPositions());
    }

    private static void makeMove(int[][] grids, Move move, List<Move> moveList, List<List<Integer>> brokenCastleMoves, boolean whiteTurn){

        boolean[] canCastle = canCastle(brokenCastleMoves);
        boolean whiteCanKingCastle, whiteCanQueenCastle, blackCanKingCastle, blackCanQueenCastle;

        whiteCanKingCastle = canCastle[0];
        whiteCanQueenCastle = canCastle[1];
        blackCanKingCastle = canCastle[2];
        blackCanQueenCastle = canCastle[3];

        if(move.isKingCastle()){
            //King
            grids[move.getPreviousRow()][4] = EMPTY;
            grids[move.getCurrentRow()][cols-2] = move.getPiece();

            //Rook
            grids[move.getPreviousRow()][cols-3] = grids[move.getPreviousRow()][cols-1];
            grids[move.getPreviousRow()][cols-1] = EMPTY;

            if(whiteTurn){
                whiteCanKingCastle = false;
                whiteCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 1);
                brokenCastleMoves.add(list);
            }
            else{
                blackCanKingCastle = false;
                blackCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 1);
                brokenCastleMoves.add(list);
            }
        }
        else if(move.isQueenCastle()){
            //King
            grids[move.getPreviousRow()][4] = EMPTY;
            grids[move.getCurrentRow()][2] = move.getPiece();

            //Rook
            grids[move.getPreviousRow()][3] = grids[move.getPreviousRow()][0];
            grids[move.getPreviousRow()][0] = EMPTY;

            if(whiteTurn){
                whiteCanKingCastle = false;
                whiteCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 1);
                brokenCastleMoves.add(list);
            }
            else{
                blackCanKingCastle = false;
                blackCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 1);
                brokenCastleMoves.add(list);
            }
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

            if(move.getPiece() == WHITE_KING && (whiteCanKingCastle || whiteCanQueenCastle)){
                whiteCanKingCastle = false;
                whiteCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 1);
                brokenCastleMoves.add(list);
            }
            else if(move.getPiece() == BLACK_KING && (blackCanKingCastle || blackCanQueenCastle)){
                blackCanKingCastle = false;
                blackCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 1);
                brokenCastleMoves.add(list);
            }
            else if(move.getPiece() == WHITE_ROOK && move.getCurrentCol() == 0 && whiteCanKingCastle){
                whiteCanKingCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 0);
                brokenCastleMoves.add(list);
            }
            else if(move.getPiece() == WHITE_ROOK && move.getCurrentCol() == cols-1 && whiteCanQueenCastle){
                List<Integer> list = Arrays.asList(moveList.size(), 1, 0, 1);
                brokenCastleMoves.add(list);
                whiteCanQueenCastle = false;
            }
            else if(move.getPiece() == BLACK_ROOK && move.getCurrentCol() == 0 && blackCanKingCastle){
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 0);
                brokenCastleMoves.add(list);
                blackCanKingCastle = false;
            }
            else if(move.getPiece() == BLACK_ROOK && move.getCurrentCol() == cols-1 && blackCanQueenCastle){
                List<Integer> list = Arrays.asList(moveList.size(), 0, 0, 1);
                brokenCastleMoves.add(list);
                blackCanQueenCastle = false;
            }
        }
        moveList.add(move);
        whiteTurn = !whiteTurn;
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
            
            if(whiteTurn){
                whiteCanKingCastle = false;
                whiteCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 1);
                brokenCastleMoves.add(list);                
            }
            else{
                blackCanKingCastle = false;
                blackCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 1);
                brokenCastleMoves.add(list);
            }

            previousBreakPoint = perpetualBreakPoint;
            perpetualBreakPoint = moveList.size();
        }
        else if(move.isQueenCastle()){
            //King
            grids[move.getPreviousRow()][4] = EMPTY;
            grids[move.getCurrentRow()][2] = move.getPiece();

            //Rook
            grids[move.getPreviousRow()][3] = grids[move.getPreviousRow()][0];
            grids[move.getPreviousRow()][0] = EMPTY;

            if(whiteTurn){
                whiteCanKingCastle = false;
                whiteCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 1);
                brokenCastleMoves.add(list);
            }
            else{
                blackCanKingCastle = false;
                blackCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 1);
                brokenCastleMoves.add(list);
            }

            previousBreakPoint = perpetualBreakPoint;
            perpetualBreakPoint = moveList.size();
        }
        else if(move.isPromotion()){
            grids[move.getPreviousRow()][move.getPreviousCol()] = EMPTY;
            grids[move.getCurrentRow()][move.getCurrentCol()] = move.getPromotedPiece();

            previousBreakPoint = perpetualBreakPoint;
            perpetualBreakPoint = moveList.size();
        }
        else if(move.isEnpassant()){
            grids[move.getPreviousRow()][move.getPreviousCol()] = EMPTY;
            grids[move.getCurrentRow()][move.getCurrentCol()] = move.getPiece();
            grids[move.getEnpassantRow()][move.getEnpassantCol()] = EMPTY;

            previousBreakPoint = perpetualBreakPoint;
            perpetualBreakPoint = moveList.size();
        }
        else {
            grids[move.getPreviousRow()][move.getPreviousCol()] = EMPTY;
            grids[move.getCurrentRow()][move.getCurrentCol()] = move.getPiece();

            if(move.getPiece() == WHITE_KING && (whiteCanKingCastle || whiteCanQueenCastle)){
                whiteCanKingCastle = false;
                whiteCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 1);
                brokenCastleMoves.add(list);
            }
            else if(move.getPiece() == BLACK_KING && (blackCanKingCastle || blackCanQueenCastle)){
                blackCanKingCastle = false;
                blackCanQueenCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 1);
                brokenCastleMoves.add(list);
            }
            else if(move.getPiece() == WHITE_ROOK && move.getCurrentCol() == 0 && whiteCanKingCastle){
                whiteCanKingCastle = false;
                List<Integer> list = Arrays.asList(moveList.size(), 1, 1, 0);
                brokenCastleMoves.add(list);
            }
            else if(move.getPiece() == WHITE_ROOK && move.getCurrentCol() == cols-1 && whiteCanQueenCastle){
                List<Integer> list = Arrays.asList(moveList.size(), 1, 0, 1);
                brokenCastleMoves.add(list);
                whiteCanQueenCastle = false;
            }
            else if(move.getPiece() == BLACK_ROOK && move.getCurrentCol() == 0 && blackCanKingCastle){
                List<Integer> list = Arrays.asList(moveList.size(), 0, 1, 0);
                brokenCastleMoves.add(list);
                blackCanKingCastle = false;
            }
            else if(move.getPiece() == BLACK_ROOK && move.getCurrentCol() == cols-1 && blackCanQueenCastle){
                List<Integer> list = Arrays.asList(moveList.size(), 0, 0, 1);
                brokenCastleMoves.add(list);
                blackCanQueenCastle = false;
            }
        }
        check = move.isThreateningMove();
        moveList.add(move);
        savedPositions.add(grids);
        whiteTurn = !whiteTurn;
    }
    
    public void undoMove(){
        if(moveList.size() > 0) {
            Move move = moveList.get(moveList.size() - 1);

            if (move.isKingCastle()) {
                // Replace King
                grids[move.getPreviousRow()][cols - 4] = grids[move.getCurrentRow()][move.getCurrentCol()];
                grids[move.getCurrentRow()][move.getCurrentCol()] = EMPTY;

                // Replace Kingside Rook
                grids[move.getPreviousRow()][cols - 1] = grids[move.getCurrentRow()][cols - 3];
                grids[move.getCurrentRow()][cols - 3] = EMPTY;

                perpetualBreakPoint = previousBreakPoint;
            } else if (move.isQueenCastle()) {
                // Replace King
                grids[move.getPreviousRow()][4] = grids[move.getCurrentRow()][move.getCurrentCol()];
                grids[move.getCurrentRow()][move.getCurrentCol()] = EMPTY;

                // Replace Queenside Rook
                grids[move.getPreviousRow()][0] = grids[move.getCurrentRow()][3];
                grids[move.getCurrentRow()][3] = EMPTY;

                perpetualBreakPoint = previousBreakPoint;

            } else if (move.isPromotion()) {
                grids[move.getPreviousRow()][move.getPreviousCol()] = move.getPiece();
                grids[move.getCurrentRow()][move.getCurrentCol()] = move.getTakenPiece();

                perpetualBreakPoint = previousBreakPoint;
            } else if (move.isEnpassant()) {
                grids[move.getPreviousRow()][move.getPreviousCol()] = move.getPiece();
                grids[move.getCurrentRow()][move.getCurrentCol()] = EMPTY;
                grids[move.getEnpassantRow()][move.getEnpassantCol()] = move.getTakenPiece();

                perpetualBreakPoint = previousBreakPoint;
            } else {
                grids[move.getPreviousRow()][move.getPreviousCol()] = move.getPiece();
                grids[move.getCurrentRow()][move.getCurrentCol()] = move.getTakenPiece();
            }

            for (int i = 0; i < brokenCastleMoves.size(); ++i) {
                if (moveList.size() - 1 == brokenCastleMoves.get(i).get(0)) {
                    List<Integer> list = brokenCastleMoves.get(i);
                    if (list.get(1) == 1 && !whiteTurn) {
                        if (list.get(2) == 1) {
                            whiteCanKingCastle = true;
                        }
                        if (list.get(3) == 1) {
                            whiteCanQueenCastle = true;
                        }
                    } else if (list.get(1) == 0 && whiteTurn) {
                        if (list.get(2) == 1) {
                            blackCanKingCastle = true;
                        }
                        if (list.get(3) == 1) {
                            blackCanQueenCastle = true;
                        }
                    }
                }
            }

            whiteTurn = !whiteTurn;
            moveList.remove(moveList.size() - 1);
            savedPositions.remove(savedPositions.size() - 1);
            check = isCheck(moveList.get(moveList.size() - 1));
            result = NO_RESULT;
        }
    }

    public boolean isWhiteTurn() { return whiteTurn; }

    public boolean isCheck() { return check; }

    public int getResult() {

//        List<Move> allPossibleMoves = getAllPossibleMoves(grids, moveList, brokenCastleMoves, whiteTurn, false);

        // Perpetual check
        if(moveList.size() > 0){
            Set< int[][] > nonDuplicates = new HashSet<>();
            List< int[][] > duplicates = new ArrayList<>();

            for (int i = perpetualBreakPoint; i < savedPositions.size() - 1; ++i){
                if(!nonDuplicates.add(savedPositions.get(i))){
                   duplicates.add(savedPositions.get(i));
                }
            }
            for (int i = 0; i < duplicates.size(); ++i){
                int timesRepeating = 1;
                for (int j = i + 1; j < duplicates.size(); ++j){
                    if(Arrays.deepEquals(duplicates.get(j), (duplicates.get(i)))){
                        ++timesRepeating;
                    }
                    if(timesRepeating >= 3){
                        return DRAW;
                    }
                }
            }
        }

        // 50 Move Rule
        if(moveList.size() >= 100){
            for (int i = moveList.size() - 1; i >= 0; --i){
                Move move = moveList.get(i);
                if(move.isThreateningMove() || move.isKingCastle() || move.isQueenCastle() || move.isPromotion() || move.getTakenPiece() != NO_PIECE){
                    break;
                }
                else{
                    return DRAW;
                }
            }
        }
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                if(!oppositePiece(grids, row, col, whiteTurn)){
                    if(getPossibleMoves(grids, moveList, brokenCastleMoves, whiteTurn, row, col, true).size() > 0){
//                        Log.d(tag, "Possible moves in getResult: " + getPossibleMoves(grids, moveList, brokenCastleMoves, whiteTurn, row, col, true));
                        return NO_RESULT;
                    }
                }
            }
        }
        if(check) { result = (whiteTurn) ? BLACK_WIN : WHITE_WIN; }
        else { result = DRAW; }
        return result;
    }

    public static int getResult(int[][] grids, List<Move> moveList, List<List<Integer>> brokenCastleMoves, boolean whiteTurn){
        boolean check = false;
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                if(!oppositePiece(grids, row, col, whiteTurn)){
                    if(getPossibleMoves(grids, moveList, brokenCastleMoves, whiteTurn, row, col, true).size() > 0){
                        return NO_RESULT;
                    }
                }
            }
        }
        // Check for result
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                if(!oppositePiece(grids, row, col, !whiteTurn)){
                    List<Move> opponentMoves = getPossibleMoves(grids, moveList, brokenCastleMoves, !whiteTurn, row, col, false);
                    for (Move move : opponentMoves){
                        if(move.isFinishingMove()) { return (whiteTurn ? BLACK_WIN : WHITE_WIN); }
                        else if (moveList.size() > 0) {
                            Move prevMove = moveList.get(moveList.size() - 1);
                            if ((prevMove.isKingCastle() || prevMove.isQueenCastle()) && move.getCurrent() == prevMove.getCastleRookIndex()) {
                                return (whiteTurn ? BLACK_WIN : WHITE_WIN);
                            }
                        }
                    }
                }
            }
        }
        return DRAW;
    }

    public boolean isCheck(Move move){
        int[][] trialGrids = copyGrids(grids);
        List<Move> copiedMoveList = copyMoves(moveList);
        List<List<Integer>> copiedBrokenCastleMoves = copyCastleInfo(brokenCastleMoves);
        boolean trialWhiteTurn = whiteTurn;
        makeMove(trialGrids, move, copiedMoveList, copiedBrokenCastleMoves, trialWhiteTurn);

//        List<Move> allPossibleMoves = getAllPossibleMoves(trialGrids, copiedMoveList, copiedBrokenCastleMoves, whiteTurn, false);
//        Log.d(tag, "In isCheck");
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                if(!oppositePiece(trialGrids, row, col, whiteTurn)){
                    List<Move> possibleMoves = getPossibleMoves(trialGrids, copiedMoveList, copiedBrokenCastleMoves, whiteTurn, row, col, false);
                    for (Move possibleMove : possibleMoves){
                        if(possibleMove.isFinishingMove()){
                            return true;
                        }
                    }
                }
            }
        }
//        for (Move possibleMove : allPossibleMoves){
//            Log.d(tag, "Possible move: " + possibleMove);
//            if(possibleMove.isFinishingMove()) {
//                return true;
//            }
//        }
        return false;
    }

    public static boolean canEscapeCheck(int[][] grids, Move move, List<Move> moveList, List<List<Integer>> brokenCastleMoves, boolean whiteTurn){

        int[][] trialGrids = copyGrids(grids);
        List<Move> copiedMoveList = copyMoves(moveList);
        List<List<Integer>> copiedBrokenCastleMoves = copyCastleInfo(brokenCastleMoves);
        makeMove(trialGrids, move, copiedMoveList, copiedBrokenCastleMoves, whiteTurn);

//        List<Move> allPossibleMoves = getAllPossibleMoves(trialGrids, copiedMoveList, copiedBrokenCastleMoves, !whiteTurn, false);
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if(!oppositePiece(grids, row, col, !whiteTurn)) {
                    List<Move> possibleMoves = getPossibleMoves(trialGrids, copiedMoveList, copiedBrokenCastleMoves, !whiteTurn, row, col, false);
                    for (Move possibleMove : possibleMoves) {
                        if (possibleMove.isFinishingMove()) {
                            Log.d(tag, "Finishing move found: " + possibleMove.toString());
                            return false;
                        } else if (moveList.size() > 0) {
                            if ((move.isKingCastle() || move.isQueenCastle()) && possibleMove.getCurrent() == move.getCastleRookIndex()) {
                                Log.d(tag, "Attacking rook in castle found: " + possibleMove);
                                return false;
                            }
//                            else if(move.isKingCastle() || move.isQueenCastle()){
//                                Log.d(tag, "Thinking for move: " + move + ", castleRookIndex: " + move.getCastleRookIndex() + ", possibleMove: " + possibleMove);
//                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static List<Move> getAllPossibleMoves(int[][] grids, List<Move> moveList, List<List<Integer>> brokenCastleMoves, boolean whiteTurn, boolean checkForCheck){
        List<Move> allPossibleMoves = new ArrayList<>();
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                if(!oppositePiece(grids, row, col, whiteTurn)){
                    allPossibleMoves.addAll(getPossibleMoves(grids, moveList, brokenCastleMoves, whiteTurn, row, col, checkForCheck));
                }
            }
        }
        return allPossibleMoves;
    }

    public static List<Move> getPossibleMoves(int[][] grids, List<Move> moveList, List<List<Integer>> brokenCastleMoves, boolean whiteTurn, int row, int col, boolean checkForCheck){
        List<Move> moves = new ArrayList<>();
        int piece = grids[row][col];
        if(oppositePiece(grids, row, col, whiteTurn)){ return new ArrayList<>(); }
        else if(piece == EMPTY){ return new ArrayList<>(); }

        boolean whiteCanKingCastle = true, whiteCanQueenCastle = true, blackCanKingCastle = true, blackCanQueenCastle = true;
        boolean canCastle[] = canCastle(brokenCastleMoves);

        whiteCanKingCastle = canCastle[0];
        whiteCanQueenCastle = canCastle[1];
        blackCanKingCastle = canCastle[2];
        blackCanQueenCastle = canCastle[3];

        if(piece == WHITE_PAWN){

            // TODO: Enpassant
            if(moveList.size() > 0) {
//                Log.d(tag, "Last Move: " + moveList.get(moveList.size() - 1) + ", " + moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col - 1), index(row, col - 1))) + ", " + moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col + 1), index(row, col + 1))));
                if (moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col - 1), index(row, col - 1))) && piece(grids, row - 1, col - 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row - 1, col - 1), EMPTY, false, true));
                }
                if (moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col + 1), index(row, col + 1))) && piece(grids, row - 1, col + 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row - 1, col + 1), EMPTY, false, true));
                }
            }

            // TODO: Opening pawn move
            if(row == rows - 2){
                if(grids[row-1][col] == EMPTY && grids[row-2][col] == EMPTY){
                    moves.add(new Move(piece, index(row, col), index(row-1, col)));
                    moves.add(new Move(piece, index(row, col), index(row-2, col)));
                }
                else if(grids[row-1][col] == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row-1, col)));
                }
            }

            // TODO: Pawn promotion
            else if(row == 1){
                int[] promotedPieces = new int[] {WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT};
                if(!isBlocked(grids, piece, 0, col)) {
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(0, col), promotedPiece, piece(grids, 0, col), true));
                    }
                }
                if(!sameSigns(grids, piece, 0, col - 1) && validate(col - 1)){
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(0, col - 1), promotedPiece, piece(grids, 0, col - 1), true));
                    }
                }
                if(!sameSigns(grids, piece, 0, col + 1) && validate(col + 1)){
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(0, col + 1), promotedPiece, piece(grids, 0, col + 1), true));
                    }
                }
            }

            // TODO: Promoted pawn
            else if(row == 0){
                if(checkForCheck) {
                    int[][] trialGrids = copyGrids(grids);
                    int[] promotedPieces = new int[]{WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT};
                    for (int promotedPiece : promotedPieces) {
                        trialGrids[row][col] = promotedPiece;
                        moves.addAll(getPossibleMoves(trialGrids, moveList, brokenCastleMoves, whiteTurn, row, col, true));
                    }
                }
            }
            else if(!isBlocked(grids, piece, row - 1, col)){
                moves.add(new Move(piece, index(row, col), index(row-1, col)));

                // TODO: Capture
                if(oppositeSigns(grids, piece, row-1, col-1)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col-1), piece(grids, row-1, col-1)));
                }
                if(oppositeSigns(grids, piece, row-1, col+1)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col+1), piece(grids, row-1, col+1)));
                }
            }
        }

        else if(piece == BLACK_PAWN){

            // TODO: Enpassant
            if(moveList.size() > 0) {
//                Log.d(tag, "Last move: " + moveList.get(moveList.size() - 1) + ", " + moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col - 1), index(row, col - 1))) + ", " + moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col + 1), index(row, col + 1))));
                if (moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col - 1), index(row, col - 1))) && piece(grids, row + 1, col - 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row + 1, col - 1), EMPTY, false, true));
                }
                if (moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col + 1), index(row, col + 1))) && piece(grids, row + 1, col + 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row + 1, col + 1), EMPTY, false, true));
                }
            }

            // TODO: Opening pawn move
            if(row == 1){
                if(grids[row+1][col] == EMPTY && grids[row+2][col] == EMPTY){
                    moves.add(new Move(piece, index(row, col), index(row+1, col)));
                    moves.add(new Move(piece, index(row, col), index(row+2, col)));
                }
                else if(grids[row+1][col] == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row+1, col)));
                }
            }

            // TODO: Pawn promotion
            else if(row == rows - 2){
                int[] promotedPieces = new int[] {BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT};
                if(!isBlocked(grids, piece, rows - 1, col)) {
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(rows - 1, col), promotedPiece, piece(grids, rows - 1, col), true));
                    }
                }
                if(!sameSigns(grids, piece, 0, col - 1) && validate(col - 1)){
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(rows - 1, col - 1), promotedPiece, piece(grids, rows - 1, col - 1), true));
                    }
                }
                if(!sameSigns(grids, piece, 0, col + 1) && validate(col + 1)){
                    for (int promotedPiece : promotedPieces) {
                        moves.add(new Move(piece, index(row, col), index(rows - 1, col + 1), promotedPiece, piece(grids, rows - 1, col + 1), true));
                    }
                }
            }

            // TODO: Promoted Pawn
            else if(row == rows - 1){
                if(checkForCheck) {
                    int[][] trialGrids = copyGrids(grids);
                    int[] promotedPieces = new int[]{BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT};
                    for (int promotedPiece : promotedPieces) {
                        trialGrids[row][col] = promotedPiece;
                        moves.addAll(getPossibleMoves(trialGrids, moveList, brokenCastleMoves, whiteTurn, row, col, true));
                    }
                }
            }

            // TODO: Regular moves
            else if(!isBlocked(grids, piece, row + 1, col) && !oppositeSigns(grids, piece, row + 1, col)){
                moves.add(new Move(piece, index(row, col), index(row+1, col)));

                // TODO: Capture
                if(oppositeSigns(grids, piece, row+1, col-1)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col-1), piece(grids, row+1, col-1)));
                }
                if(oppositeSigns(grids, piece, row+1, col+1)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col+1), piece(grids, row+1, col+1)));
                }
            }
        }

        else if(Math.abs(piece) == WHITE_KING){
//            Log.d(tag, "All king moves: ");
            for (int i = row - 1; i <= row + 1; ++i){
                for (int j = col - 1; j <= col + 1; ++j) {
//                    Log.d(tag, "Row = " + i + ", Col = " + j +  ", " + validate(i, j) +  ", " + (i != row || j != col) +  ", " + !isBlocked(piece, i, j));
                    if (validate(i, j) && (i != row || j != col) && !isBlocked(grids, piece, i, j)) {
                        moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j)));
                    }
                }
            }
            if(row == rows - 1 && col == 4 && piece(grids, row, col+1) == EMPTY && piece(grids, row, col+2) == EMPTY && whiteCanKingCastle && whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col+2), EMPTY, true, false));
            }
            if(row == rows - 1 && col == 4 && piece(grids, row, col-1) == EMPTY && piece(grids, row, col-2) == EMPTY && piece(grids, row, col-3) == EMPTY && whiteCanQueenCastle && whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col-2), EMPTY, true, false));
            }
            if(row == 0 && col == 4 && piece(grids, row, col+1) == EMPTY && piece(grids, row, col+2) == EMPTY && blackCanKingCastle && !whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col+2), EMPTY, true, false));
            }
            if(row == 0 && col == 4 && piece(grids, row, col-1) == EMPTY && piece(grids, row, col-2) == EMPTY && piece(grids, row, col-3) == EMPTY && blackCanQueenCastle && !whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col-2), EMPTY, true, false));
            }
        }

        else if(Math.abs(piece) == WHITE_ROOK){

            for (int j = col + 1; validate(j); ++j){
                if(isBlocked(grids, piece, row, j)) { break; }
                else if(oppositeSigns(grids, piece, row, j)){
                    moves.add(new Move(piece, index(row, col), index(row, j), piece(grids, row, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(row, j), piece(grids, row, j))); }
            }
            for (int j = col - 1; validate(j); --j){
                if(isBlocked(grids, piece, row, j)) break;
                else if(oppositeSigns(grids, piece, row, j)){
                    moves.add(new Move(piece, index(row, col), index(row, j), piece(grids, row, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(row, j), piece(grids, row, j))); }
            }
            for (int i = row + 1; validate(i); ++i){
                if(isBlocked(grids, piece, i, col)) break;
                else if(oppositeSigns(grids, piece, i, col)){
                    moves.add(new Move(piece, index(row, col), index(i, col), piece(grids, i, col)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, col), piece(grids, i, col))); }
            }
            for (int i = row - 1; validate(i); --i){
                if(isBlocked(grids, piece, i, col)) break;
                else if(oppositeSigns(grids, piece, i, col)){
                    moves.add(new Move(piece, index(row, col), index(i, col), piece(grids, i, col)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, col), piece(grids, i, col))); }
            }
        }

        else if(Math.abs(piece) == WHITE_BISHOP){

            for (int d = 1; validate(row - d, col + d); ++d){
                int i = row - d, j = col + d;
                if(isBlocked(grids, piece, i, j)) { break; }
                else if(oppositeSigns(grids, piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j))); }
            }
            for (int d = 1; validate(row + d, col - d); ++d){
                int i = row + d, j = col - d;
                if(isBlocked(grids, piece, i, j)) { break; }
                else if(oppositeSigns(grids, piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j))); }
            }
            for (int d = 1; validate(row - d, col - d); ++d){
                int i = row - d, j = col - d;
                if(isBlocked(grids, piece, i, j)) { break; }
                else if(oppositeSigns(grids, piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j))); }
            }
            for (int d = 1; validate(row + d, col + d); ++d){
                int i = row + d, j = col + d;
                if(isBlocked(grids, piece, i, j)) { break; }
                else if(oppositeSigns(grids, piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j))); }
            }
        }

        else if(Math.abs(piece) == WHITE_QUEEN){

            // Horizontal and Vertical
            for (int j = col + 1; validate(j); ++j){
                if(isBlocked(grids, piece, row, j)) { break; }
                else if(oppositeSigns(grids, piece, row, j)){
                    moves.add(new Move(piece, index(row, col), index(row, j), piece(grids, row, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(row, j), piece(grids, row, j))); }
            }
            for (int j = col - 1; validate(j); --j){
                if(isBlocked(grids, piece, row, j)) break;
                else if(oppositeSigns(grids, piece, row, j)){
                    moves.add(new Move(piece, index(row, col), index(row, j), piece(grids, row, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(row, j), piece(grids, row, j))); }
            }
            for (int i = row + 1; validate(i); ++i){
                if(isBlocked(grids, piece, i, col)) break;
                else if(oppositeSigns(grids, piece, i, col)){
                    moves.add(new Move(piece, index(row, col), index(i, col), piece(grids, i, col)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, col), piece(grids, i, col))); }
            }
            for (int i = row - 1; validate(i); --i){
                if(isBlocked(grids, piece, i, col)) break;
                else if(oppositeSigns(grids, piece, i, col)){
                    moves.add(new Move(piece, index(row, col), index(i, col), piece(grids, i, col)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, col), piece(grids, i, col))); }
            }

            // Diagonal
            for (int d = 1; validate(row - d, col + d); ++d){
                int i = row - d, j = col + d;
                if(isBlocked(grids, piece, i, j)) { break; }
                else if(oppositeSigns(grids, piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j))); }
            }
            for (int d = 1; validate(row + d, col - d); ++d){
                int i = row + d, j = col - d;
                if(isBlocked(grids, piece, i, j)) { break; }
                else if(oppositeSigns(grids, piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j))); }
            }
            for (int d = 1; validate(row - d, col - d); ++d){
                int i = row - d, j = col - d;
                if(isBlocked(grids, piece, i, j)) { break; }
                else if(oppositeSigns(grids, piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j))); }
            }
            for (int d = 1; validate(row + d, col + d); ++d){
                int i = row + d, j = col + d;
                if(isBlocked(grids, piece, i, j)) { break; }
                else if(oppositeSigns(grids, piece, i, j)){
                    moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j)));
                    break;
                }
                else { moves.add(new Move(piece, index(row, col), index(i, j), piece(grids, i, j))); }
            }
        }

        else if(Math.abs(piece) == WHITE_KNIGHT){
            int[][] lMoves = lMoves(row, col);
            for (int[] lMove : lMoves) {
                if (validate(lMove[0], lMove[1]) && !isBlocked(grids, piece, lMove[0], lMove[1])) {
                    moves.add(new Move(piece, index(row, col), index(lMove[0], lMove[1]), piece(grids, lMove[0], lMove[1])));
                }
            }
        }
        if(checkForCheck){
            List<Move> validMoves = new ArrayList<>();
            for (Move move: moves){
                if(canEscapeCheck(grids, move, moveList, brokenCastleMoves, whiteTurn)){
                    validMoves.add(move);
                }
            }
            return validMoves;
        }
        return moves;
    }

    public List<Move> getPossibleMoves(int row, int col){
        List<Move> moves = new ArrayList<>();
        int piece = grids[row][col];
        if(oppositePiece(row, col)){ return new ArrayList<>(); }
        else if(piece == EMPTY){ return new ArrayList<>(); }
        if(piece == WHITE_PAWN){

            // TODO: Enpassant
            if(moveList.size() > 0) {
//                Log.d(tag, "Last Move: " + moveList.get(moveList.size() - 1) + ", " + moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col - 1), index(row, col - 1))) + ", " + moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col + 1), index(row, col + 1))));
                if (moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col - 1), index(row, col - 1))) && piece(row - 1, col - 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row - 1, col - 1), EMPTY, false, true));
                    previousBreakPoint = perpetualBreakPoint;
                    perpetualBreakPoint = moveList.size();
                }
                else if (moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col + 1), index(row, col + 1))) && piece(row - 1, col + 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row - 1, col + 1), EMPTY, false, true));
                    previousBreakPoint = perpetualBreakPoint;
                    perpetualBreakPoint = moveList.size();
                }
            }

            // TODO: Opening pawn move
            if(row == rows - 2){
                if((grids[row-1][col] == EMPTY) && (grids[row-2][col] == EMPTY)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col)));
                    moves.add(new Move(piece, index(row, col), index(row-2, col)));
                }
                else if(grids[row-1][col] == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row-1, col)));
                }
            }

            // TODO: Pawn Promotion
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

            //TODO: Regular moves
            else if(!isBlocked(piece, row - 1, col)){
                moves.add(new Move(piece, index(row, col), index(row-1, col)));

                // TODO: Capture
                if(oppositeSigns(piece, row-1, col-1)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col-1), piece(row-1, col-1)));
                }
                if(oppositeSigns(piece, row-1, col+1)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col+1), piece(row-1, col+1)));
                }
            }            
        }

        else if(piece == BLACK_PAWN){

            // TODO: Enpassant
            if(moveList.size() > 0) {
//                Log.d(tag, "Last move: " + moveList.get(moveList.size() - 1) + ", " + moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col - 1), index(row, col - 1))) + ", " + moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col + 1), index(row, col + 1))));
                if (moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col - 1), index(row, col - 1))) && piece(row + 1, col - 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row + 1, col - 1), EMPTY, false, true));

                    previousBreakPoint = perpetualBreakPoint;
                    perpetualBreakPoint = moveList.size();
                }
                else if (moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col + 1), index(row, col + 1))) && piece(row + 1, col + 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row + 1, col + 1), EMPTY, false, true));

                    previousBreakPoint = perpetualBreakPoint;
                    perpetualBreakPoint = moveList.size();
                }
            }

            // TODO: Opening pawn move
            if(row == 1){
                if((grids[row+1][col] == EMPTY) && (grids[row+2][col] == EMPTY)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col)));
                    moves.add(new Move(piece, index(row, col), index(row+2, col)));
                }
                else if(grids[row+1][col] == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row+1, col)));
                }
            }

            // TODO: Pawn Promotion
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

            // TODO: Regular moves
            else if(!isBlocked(piece, row + 1, col) && !oppositeSigns(piece, row + 1, col)){
                moves.add(new Move(piece, index(row, col), index(row+1, col)));

                // TODO: Capture
                if(oppositeSigns(piece, row+1, col-1)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col-1), piece(row+1, col-1)));
                }
                if(oppositeSigns(piece, row+1, col+1)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col+1), piece(row+1, col+1)));
                }
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
            if(row == rows - 1 && col == 4 && piece(row, col+1) == EMPTY && piece(row, col+2) == EMPTY && whiteCanKingCastle && whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col+2), EMPTY, true, false));

                previousBreakPoint = perpetualBreakPoint;
                perpetualBreakPoint = moveList.size();
            }
            if(row == rows - 1 && col == 4 && piece(row, col-1) == EMPTY && piece(row, col-2) == EMPTY && piece(row, col-3) == EMPTY && whiteCanQueenCastle && whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col-2), EMPTY, true, false));

                previousBreakPoint = perpetualBreakPoint;
                perpetualBreakPoint = moveList.size();
            }
            if(row == 0 && col == 4 && piece(row, col+1) == EMPTY && piece(row, col+2) == EMPTY && blackCanKingCastle && !whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col+2), EMPTY, true, false));

                previousBreakPoint = perpetualBreakPoint;
                perpetualBreakPoint = moveList.size();
            }
            if(row == 0 && col == 4 && piece(row, col-1) == EMPTY && piece(row, col-2) == EMPTY && piece(row, col-3) == EMPTY && blackCanQueenCastle && !whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col-2), EMPTY, true, false));

                previousBreakPoint = perpetualBreakPoint;
                perpetualBreakPoint = moveList.size();
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

        List<Move> validMoves = new ArrayList<>();
        for (Move move: moves){
//            Log.d(tag, "Probable move: " + move.toString());
            if(canEscapeCheck(grids, move, moveList, brokenCastleMoves, whiteTurn)){
                validMoves.add(move);
            }
        }

        for (Move move: validMoves){
            if(isCheck(move)){
                move.setThreateningMove(true);
            }
            Log.d(tag, "Valid move: " + move.toString());
        }

        return validMoves;
    }

    public List<Move> getMoveList(){
        return this.moveList;
    }

    public List<List<Integer>> getBrokenCastleMoves() {
        return brokenCastleMoves;
    }

    public static boolean[] canCastle(List<List<Integer>> brokenCastleMoves){
        boolean whiteCanKingCastle = true, whiteCanQueenCastle = true, blackCanKingCastle = true, blackCanQueenCastle = true;
        if(brokenCastleMoves.size() == 0){
            whiteCanKingCastle = true;
            whiteCanQueenCastle = true;
            blackCanKingCastle = true;
            blackCanQueenCastle = true;
        }
        else{
            for (int i = 0; i < brokenCastleMoves.size(); ++i){
                List<Integer> list = brokenCastleMoves.get(i);
                if(list.get(1) == 1){
                    if(list.get(2) == 1) { whiteCanKingCastle = false; }
                    if(list.get(3) == 1) { whiteCanQueenCastle = false; }
                }
                else{
                    if(list.get(2) == 1) { blackCanKingCastle = false; }
                    if(list.get(3) == 1) { blackCanQueenCastle = false; }
                }
            }
        }
        return new boolean[]{whiteCanKingCastle, whiteCanQueenCastle, blackCanKingCastle, blackCanQueenCastle};
    }

    public boolean oppositePiece(int row, int col){
        return (whiteTurn && grids[row][col] <= 0) || (!whiteTurn && grids[row][col] >= 0);
    }
    public static boolean oppositePiece(int[][] grids, int row, int col, boolean whiteTurn){
        return (whiteTurn && grids[row][col] <= 0) || (!whiteTurn && grids[row][col] >= 0);
    }

    public boolean isBlocked(int piece, int row, int col){
        if(Math.abs(piece) == WHITE_PAWN){
            return grids[row][col] != EMPTY;
        }
        else{
            return sameSigns(piece, row, col);
        }
    }
    public static boolean isBlocked(int[][] grids, int piece, int row, int col){
        if(Math.abs(piece) == WHITE_PAWN){
            return grids[row][col] != EMPTY;
        }
        else{
            return sameSigns(grids, piece, row, col);
        }
    }

    public static boolean validate(int row, int col){
        return ((row >= 0) && (row < rows) && (col >= 0) && (col < cols));
    }
    public static boolean validate(int r){
        return (r >= 0 && r < rows);
    }
    public static boolean validateIndex(int index){
        if(index < 0 || index >= cols * rows){
            return false;
        }
        else {
            return validate(rowCol(index)[0], rowCol(index)[1]);
        }
    }

    public boolean oppositeSigns(int piece, int row, int col){
        if(!validate(row, col)) { return false; }
        else { return piece * grids[row][col] < 0; }
    }
    public boolean sameSigns(int piece, int row, int col){
        if(!validate(row, col)) { return false; }
        else { return piece * grids[row][col] > 0; }
    }

    public static boolean oppositeSigns(int[][] grids, int piece, int row, int col){
        if(!validate(row, col)) { return false; }
        else { return piece * grids[row][col] < 0; }
    }
    public static boolean sameSigns(int[][] grids, int piece, int row, int col){
        if(!validate(row, col)) { return false; }
        else { return piece * grids[row][col] > 0; }
    }

    public static int index(int row, int col){ return row * rows + col; }

    public static int[] rowCol(int index){
        if(index < 0 || index >= cols * rows){
            return new int[]{NONE, NONE};
        }
        else {
            return new int[]{index / rows, index % rows};
        }
    }

    public int piece(int row, int col){
        if(!validate(row, col)) { return NO_PIECE; } 
        else { return grids[row][col]; }
    }

    public static int piece(int[][] grids, int row, int col){
        if(!validate(row, col)) { return NO_PIECE; }
        else { return grids[row][col]; }
    }

    public static int[][] lMoves(int row, int col){
        return new int[][]{
                {row-2, col+1},
                {row-2, col-1},
                {row+2, col+1},
                {row+2, col-1},
                {row-1, col+2},
                {row-1, col-2},
                {row+1, col+2},
                {row+1, col-2},
        };
    }
    public static int[][] copyGrids(int[][] grids){
        int[][] copied = new int[rows][cols];
        for (int i = 0; i < rows; ++i){
            copied[i] = Arrays.copyOf(grids[i], cols);
        }
        return copied;
    }
    public static List<Move> copyMoves(List<Move> moveList){
        List<Move> copied = new ArrayList<>();
        for (int i = 0; i < moveList.size(); ++i){
            copied.add(moveList.get(i));
        }
        return copied;
    }
    public static List<List<Integer>> copyCastleInfo(List<List<Integer>> brokenCastleMoves){
        List<List<Integer>> copied = new ArrayList<>();
        for (int i = 0; i < brokenCastleMoves.size(); ++i){
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < brokenCastleMoves.get(i).size(); ++j){
                list.add(brokenCastleMoves.get(i).get(j));
            }
            copied.add(list);
        }
        return copied;
    }
    public int indexOfOppositeKing(){
        if(!whiteTurn){
            for (int row = 0; row < rows; ++row){
                for (int col = 0; col < cols; ++col){
                    if(grids[row][col] == BLACK_KING){
                        return index(row, col);
                    }
                }
            }
        }
        else{
            for (int row = rows - 1; row >= 0; --row){
                for (int col = 0; col < cols; ++col){
                    if(grids[row][col] == WHITE_KING){
                        return index(row, col);
                    }
                }
            }
        }
        return NONE;
    }

    int[][] getGrids(){ return this.grids; }

    public List<int[][]> getSavedPositions() { return savedPositions; }

    public int getPerpetualBreakPoint() { return perpetualBreakPoint; }

    public int getPreviousBreakPoint() { return previousBreakPoint; }
}
