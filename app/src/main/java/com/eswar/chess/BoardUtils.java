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
    final static int MAX = Integer.MAX_VALUE, MIN = Integer.MIN_VALUE;
//    private int grids[][];
    final static String tag = "tag";

    final static int KING_SCORE = 20000, QUEEN_SCORE = 900, ROOK_SCORE = 500, BISHOP_SCORE = 330, KNIGHT_SCORE = 320, PAWN_SCORE = 100;
    final static int DOUBLED_PAWN_SCORE = 50, BLOCKED_PAWN_SCORE = 50, ISOLATED_PAWN_SCORE = 50;
    final static int MOBILITY_SCORE = 10;

    final static int[][] PAWN_TABLE = new int[][]{
            {  0,  0,  0,  0,  0,  0,  0,  0},
            { 50, 50, 50, 50, 50, 50, 50, 50},
            { 10, 10, 20, 30, 30, 20, 10, 10},
            {  5,  5, 10, 25, 25, 10,  5,  5},
            {  0,  0,  0, 20, 20,  0,  0,  0},
            { -5, -5,-10,  0,  0,-10, -5, -5},
            {  5, 10, 10,-20,-20, 10, 10,  5},
            {  0,  0,  0,  0,  0,  0,  0,  0}
    };
    final static int[][] KNIGHT_TABLE = new int[][]{
            {-50,-40,-30,-30,-30,-30,-40,-50},
            {-40,-20,  0,  0,  0,  0,-20,-40},
            {-30,  0, 10, 15, 15, 10,  0,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30},
            {-30,  0, 15, 20, 20, 15,  0,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30},
            {-40,-20,  0,  0,  0,  0,-20,-40},
            {-50,-40,-30,-30,-30,-30,-40,-50}
    };
    final static int[][] BISHOP_TABLE = new int[][]{
            {-20,-10,-10,-10,-10,-10,-10,-20},
            {-10,0,0,0,0,0,0,-10},
            {-10,0,5,10,10,5,0,-10},
            {-10,5,5,10,10,5,5,-10},
            {-10,0,10,10,10,10,0,-10},
            {-10,10,10,10,10,10,10,-10},
            {-10,5,0,0,0,0,5,-10},
            {-20,-10,-10,-10,-10,-10,-10,-20},
    };
    final static int[][] ROOK_TABLE = new int[][]{
            {0,0,0,0,0,0,0,0},
            {5,10,10,10,10,10,10,5},
            {-5,0,0,0,0,0,0,-5},
            {-5,0,0,0,0,0,0,-5},
            {-5,0,0,0,0,0,0,-5},
            {-5,0,0,0,0,0,0,-5},
            {-5,0,0,0,0,0,0,-5},
            {0,0,0,5,5,0,0,0}
    };
    final static int[][] QUEEN_TABLE = new int[][]{
            {-20,-10,-10,-5,-5,-10,-10,-20},
            {-10,0,0,0,0,0,0,-10},
            {-10,0,5,5,5,5,0,-10},
            {-5,0,5,5,5,5,0,-5},
            {0,0,5,5,5,5,0,-5},
            {-10,5,5,5,5,5,0,-10},
            {-10,0,5,0,0,0,0,-10},
            {-20,-10,-10,-5,-5,-10,-10,-20}
    };
    final static int[][] KING_TABLE_MIDDLE_GAME = new int[][]{
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            { 20, 20,  0,  0,  0,  0, 20, 20},
            { 20, 30, 10,  0,  0, 10, 30, 20}
    };
    final static int[][] KING_TABLE_END_GAME = new int[][]{
            {-50,-40,-30,-20,-20,-30,-40,-50},
            {-30,-20,-10,  0,  0,-10,-20,-30},
            {-30,-10, 20, 30, 30, 20, 20,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-30,  0,  0,  0,  0,-30,-30},
            {-50,-30,-30,-30,-30,-30,-30,-50}
    };

    public static void makeMove(int[][] grids, Move move, boolean whiteTurn){

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
    }


//    public static int getResult(int[][] grids, List<Move> moveList, List<List<Integer>> brokenCastleMoves, boolean whiteTurn){
//        boolean check = false;
//        for (int row = 0; row < rows; ++row){
//            for (int col = 0; col < cols; ++col){
//                if(!oppositePiece(grids, row, col, whiteTurn)){
//                    if(getPossibleMoves(grids, moveList, brokenCastleMoves, whiteTurn, row, col, true).size() > 0){
//                        return NO_RESULT;
//                    }
//                }
//            }
//        }
//        // Check for result
//        for (int row = 0; row < rows; ++row){
//            for (int col = 0; col < cols; ++col){
//                if(!oppositePiece(grids, row, col, !whiteTurn)){
//                    List<Move> opponentMoves = getPossibleMoves(grids, moveList, brokenCastleMoves, !whiteTurn, row, col, false);
//                    for (Move move : opponentMoves){
//                        if(move.isFinishingMove()) { return (whiteTurn ? BLACK_WIN : WHITE_WIN); }
//                        else if (moveList.size() > 0) {
//                            Move prevMove = moveList.get(moveList.size() - 1);
//                            if ((prevMove.isKingCastle() || prevMove.isQueenCastle()) && move.getCurrent() == prevMove.getCastleRookIndex()) {
//                                return (whiteTurn ? BLACK_WIN : WHITE_WIN);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return DRAW;
//    }

//    public boolean isCheck(Move move){
//        int[][] trialGrids = copyGrids(grids);
//        List<Move> copiedMoveList = copyMoves(moveList);
//        List<List<Integer>> copiedBrokenCastleMoves = copyCastleInfo(brokenCastleMoves);
//        boolean trialWhiteTurn = whiteTurn;
//        makeMove(trialGrids, move, copiedMoveList, copiedBrokenCastleMoves, trialWhiteTurn);
//
////        List<Move> allPossibleMoves = getAllPossibleMoves(trialGrids, copiedMoveList, copiedBrokenCastleMoves, whiteTurn, false);
////        Log.d(tag, "In isCheck");
//        for (int row = 0; row < rows; ++row){
//            for (int col = 0; col < cols; ++col){
//                if(!oppositePiece(trialGrids, row, col, whiteTurn)){
//                    List<Move> possibleMoves = getPossibleMoves(trialGrids, copiedMoveList, copiedBrokenCastleMoves, whiteTurn, row, col, false);
//                    for (Move possibleMove : possibleMoves){
//                        if(possibleMove.isFinishingMove()){
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//
//        return false;
//    }


    public static boolean isCheck(int[][] grids, Move move, boolean whiteTurn){
        int[][] trialGrids = copyGrids(grids);
        makeMove(trialGrids, move, whiteTurn);

//        List<Move> allPossibleMoves = getAllPossibleMoves(trialGrids, copiedMoveList, copiedBrokenCastleMoves, whiteTurn, false);
//        Log.d(tag, "In isCheck");
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                if(!oppositePiece(trialGrids, row, col, whiteTurn)){
                    List<Move> possibleMoves = getPossibleMoves(trialGrids, whiteTurn, row, col, true, true, true, true, Move.getDummyMove(), false);
                    for (Move possibleMove : possibleMoves){
                        if(possibleMove.isFinishingMove()){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean canEscapeCheck(int[][] grids, Move move, boolean whiteTurn, Move prevMove){

        int[][] trialGrids = copyGrids(grids);
//        List<Move> copiedMoveList = copyMoves(moveList);
//        List<List<Integer>> copiedBrokenCastleMoves = copyCastleInfo(brokenCastleMoves);
        makeMove(trialGrids, move, whiteTurn);

//        List<Move> allPossibleMoves = getAllPossibleMoves(trialGrids, copiedMoveList, copiedBrokenCastleMoves, !whiteTurn, false);
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                if(!oppositePiece(grids, row, col, !whiteTurn)) {
                    List<Move> possibleMoves = getPossibleMoves(trialGrids, !whiteTurn, row, col, true, true, true, true, Move.getDummyMove(), false);
                    for (Move possibleMove : possibleMoves) {
                        if (possibleMove.isFinishingMove()) {
//                            Log.d(tag, "Finishing move found: " + possibleMove.toString());
                            return false;
                        } else if ((move.isKingCastle() || move.isQueenCastle()) && possibleMove.getCurrent() == move.getCastleRookIndex()) {
//                            Log.d(tag, "Attacking rook in castle found: " + possibleMove);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

//    public static List<Move> getAllPossibleMoves(int[][] grids, boolean whiteTurn, boolean kingCastleWhite, boolean queenCastleWhite, boolean kingCastleBlack, boolean queenCastleBlack, Move prevMove, boolean checkForCheck){
//        List<Move> allPossibleMoves = new ArrayList<>();
//        for (int row = 0; row < rows; ++row){
//            for (int col = 0; col < cols; ++col){
//                if(!oppositePiece(grids, row, col, whiteTurn)){
//                    allPossibleMoves.addAll(getPossibleMoves(grids, whiteTurn, row, col, kingCastleWhite, queenCastleWhite, kingCastleBlack, queenCastleBlack, prevMove, checkForCheck));
//                }
//            }
//        }
//        return allPossibleMoves;
//    }
//
    public static List<Move> getPossibleMoves(int[][] grids, boolean whiteTurn, int row, int col, boolean kingCastleWhite, boolean queenCastleWhite, boolean kingCastleBlack, boolean queenCastleBlack, Move prevMove, boolean validateCheck){
        List<Move> moves = new ArrayList<>();
        int piece = grids[row][col];
        if(oppositePiece(grids, row, col, whiteTurn)){ return new ArrayList<>(); }
        else if(piece == EMPTY){ return new ArrayList<>(); }

        if(piece == WHITE_PAWN){

            // Enpassant
            if(!prevMove.equals(Move.getDummyMove())) {
//                Log.d(tag, "Last Move: " + moveList.get(moveList.size() - 1) + ", " + moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col - 1), index(row, col - 1))) + ", " + moveList.get(moveList.size() - 1).equals(new Move(BLACK_PAWN, index(row - 2, col + 1), index(row, col + 1))));
                if (prevMove.equals(new Move(BLACK_PAWN, index(row - 2, col - 1), index(row, col - 1))) && piece(grids, row - 1, col - 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row - 1, col - 1), EMPTY, false, true));
                }
                if (prevMove.equals(new Move(BLACK_PAWN, index(row - 2, col + 1), index(row, col + 1))) && piece(grids, row - 1, col + 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row - 1, col + 1), EMPTY, false, true));
                }
            }

            // Capture
            if(row != 1){
                if(oppositeSigns(grids, piece, row-1, col-1)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col-1), piece(grids, row-1, col-1)));
                }
                if(oppositeSigns(grids, piece, row-1, col+1)){
                    moves.add(new Move(piece, index(row, col), index(row-1, col+1), piece(grids, row-1, col+1)));
                }
            }

            // Opening pawn move
            if(row == rows - 2){
                if(grids[row-1][col] == EMPTY && grids[row-2][col] == EMPTY){
                    moves.add(new Move(piece, index(row, col), index(row-1, col)));
                    moves.add(new Move(piece, index(row, col), index(row-2, col)));
                }
                else if(grids[row-1][col] == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row-1, col)));
                }
            }

            // Pawn promotion
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

//            // Promoted pawn
//            else if(row == 0){
//                if(validateCheck) {
//                    int[][] trialGrids = copyGrids(grids);
//                    int[] promotedPieces = new int[]{WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT};
//                    for (int promotedPiece : promotedPieces) {
//                        trialGrids[row][col] = promotedPiece;
//                        moves.addAll(getPossibleMoves(trialGrids, whiteTurn, row, col, tru));
//                    }
//                }
//            }
            else if(!isBlocked(grids, piece, row - 1, col)){
                moves.add(new Move(piece, index(row, col), index(row-1, col)));
            }
        }

        else if(piece == BLACK_PAWN){

            // Enpassant
            if(!prevMove.equals(Move.getDummyMove())) {
//                Log.d(tag, "Last move: " + moveList.get(moveList.size() - 1) + ", " + moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col - 1), index(row, col - 1))) + ", " + moveList.get(moveList.size() - 1).equals(new Move(WHITE_PAWN, index(row + 2, col + 1), index(row, col + 1))));
                if (prevMove.equals(new Move(WHITE_PAWN, index(row + 2, col - 1), index(row, col - 1))) && piece(grids, row + 1, col - 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row + 1, col - 1), EMPTY, false, true));
                }
                if (prevMove.equals(new Move(WHITE_PAWN, index(row + 2, col + 1), index(row, col + 1))) && piece(grids, row + 1, col + 1) == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row + 1, col + 1), EMPTY, false, true));
                }
            }

            // Capture
            if(row != rows - 2){
                if(oppositeSigns(grids, piece, row+1, col-1)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col-1), piece(grids, row+1, col-1)));
                }
                if(oppositeSigns(grids, piece, row+1, col+1)){
                    moves.add(new Move(piece, index(row, col), index(row+1, col+1), piece(grids, row+1, col+1)));
                }
            }

            // Opening pawn move
            if(row == 1){
                if(grids[row+1][col] == EMPTY && grids[row+2][col] == EMPTY){
                    moves.add(new Move(piece, index(row, col), index(row+1, col)));
                    moves.add(new Move(piece, index(row, col), index(row+2, col)));
                }
                else if(grids[row+1][col] == EMPTY) {
                    moves.add(new Move(piece, index(row, col), index(row+1, col)));
                }
            }

            // Pawn promotion
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

//            // Promoted Pawn
//            else if(row == rows - 1){
//                if(checkForCheck) {
//                    int[][] trialGrids = copyGrids(grids);
//                    int[] promotedPieces = new int[]{BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT};
//                    for (int promotedPiece : promotedPieces) {
//                        trialGrids[row][col] = promotedPiece;
//                        moves.addAll(getPossibleMoves(trialGrids, moveList, brokenCastleMoves, whiteTurn, row, col, true));
//                    }
//                }
//            }

            // Regular moves
            else if(!isBlocked(grids, piece, row + 1, col) && !oppositeSigns(grids, piece, row + 1, col)){
                moves.add(new Move(piece, index(row, col), index(row+1, col)));
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
            if(row == rows - 1 && col == 4 && piece(grids, row, col+1) == EMPTY && piece(grids, row, col+2) == EMPTY && kingCastleWhite && whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col+2), EMPTY, true, false));
            }
            if(row == rows - 1 && col == 4 && piece(grids, row, col-1) == EMPTY && piece(grids, row, col-2) == EMPTY && piece(grids, row, col-3) == EMPTY && queenCastleWhite && whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col-2), EMPTY, true, false));
            }
            if(row == 0 && col == 4 && piece(grids, row, col+1) == EMPTY && piece(grids, row, col+2) == EMPTY && kingCastleBlack && !whiteTurn){
                moves.add(new Move(piece, index(row, col), index(row, col+2), EMPTY, true, false));
            }
            if(row == 0 && col == 4 && piece(grids, row, col-1) == EMPTY && piece(grids, row, col-2) == EMPTY && piece(grids, row, col-3) == EMPTY && queenCastleBlack && !whiteTurn){
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
        if(validateCheck){
            List<Move> validMoves = new ArrayList<>();
            for (Move move: moves){
                if(canEscapeCheck(grids, move, whiteTurn, prevMove)){
                    validMoves.add(move);
                }
            }
            return validMoves;
        }
        return moves;
    }

//    public List<Move> getMoveList(){
//        return this.moveList;
//    }
//
//    public List<List<Integer>> getBrokenCastleMoves() {
//        return brokenCastleMoves;
//    }

    public static boolean[] canCastle(List<List<Integer>> brokenCastleMoves){
        boolean kingCastleWhite = true, queenCastleWhite = true, kingCastleBlack = true, queenCastleBlack = true;
        if(brokenCastleMoves.size() > 0){
            for (int i = 0; i < brokenCastleMoves.size(); ++i){
                List<Integer> list = brokenCastleMoves.get(i);
                if(list.get(1) == 1){
                    if(list.get(2) == 1) { kingCastleWhite = false; }
                    if(list.get(3) == 1) { queenCastleWhite = false; }
                }
                else{
                    if(list.get(2) == 1) { kingCastleBlack = false; }
                    if(list.get(3) == 1) { queenCastleBlack = false; }
                }
            }
        }
        return new boolean[]{kingCastleWhite, queenCastleWhite, kingCastleBlack, queenCastleBlack};
    }

    public static boolean oppositePiece(int[][] grids, int row, int col, boolean whiteTurn){
        return (whiteTurn && grids[row][col] <= 0) || (!whiteTurn && grids[row][col] >= 0);
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
        for (Move move : moveList){
            copied.add(move);
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

    public static List< int[][] > copySavedPositions(List< int[][] > savedPositions){
        List< int[][] > copied = new ArrayList<>();
        for (int i = 0; i < savedPositions.size(); ++i){
            int[][] copiedGrids = new int[rows][cols];
            int[][] savedGrids = savedPositions.get(i);
            for (int j = 0; j < cols; ++j){
                copiedGrids[j] = Arrays.copyOf(savedGrids[j], cols);
            }
            copied.add(copiedGrids);
        }
        return copied;
    }

    public static boolean arrayDeepEquals(int[][] a, int[][] b){
        for (int i = 0; i < a.length; ++i){
            if(!Arrays.equals(a[i], b[i])){
                return false;
            }
        }
        return true;
    }

    public static boolean canEnpassant(Move prevMove, int piece, int row, int col){
        if(!validate(row, col)) {
            return false;
        }
        if(piece == WHITE_PAWN && prevMove.getPiece() == BLACK_PAWN && row == 3){
            if(validate(row, col - 1) && prevMove.getCurrent() == index(row, col - 1) && prevMove.getPrevious() == index(row - 2, col - 1)){
                return true;
            }
            if(validate(row, col + 1) && prevMove.getCurrent() == index(row, col + 1) && prevMove.getPrevious() == index(row - 2, col + 1)){
                return true;
            }
        }
        if(piece == BLACK_PAWN && prevMove.getPiece() == WHITE_PAWN && row == 4){
            if(validate(row, col - 1) && prevMove.getCurrent() == index(row, col - 1) && prevMove.getPrevious() == index(row + 2, col - 1)){
                return true;
            }
            if(validate(row, col + 1) && prevMove.getCurrent() == index(row, col + 1) && prevMove.getPrevious() == index(row + 2, col + 1)){
                return true;
            }
        }
        return false;
    }

//    public static String  printGrids(int[][] grids){
//        StringBuilder string = new StringBuilder();
//        for (int i = 0; i < rows; ++i){
//            for (int j = 0; j < cols; ++j){
//                string.append(pieceString(grids[i][j]));
//                string.append(" ");
//            }
//            string.append("\n");
//        }
//        return string.toString();
//    }

    public static String pieceString(int piece){
        String string;
        if(piece > 0){ string = "W"; }
        else if(piece < 0){ string = "B"; }
        else { return "__"; }
        switch (Math.abs(piece)){
            case WHITE_KING: return string + "K";
            case WHITE_QUEEN: return string + "Q";
            case WHITE_ROOK: return string + "R";
            case WHITE_BISHOP: return string + "B";
            case WHITE_KNIGHT: return string + "N";
            case WHITE_PAWN: return string + "P";
            default: return "";
        }

    }
    static int sign(int piece){
        if (piece > 0)
            return 1;
        else if(piece < 0){
            return -1;
        }
        else{
            return 0;
        }
    }
    static int perspective(boolean whiteAI){
        return (whiteAI ? 1 : -1);
    }

    static String depthString(int depth){
        return "depth" + depth;
    }

//    int[][] getGrids(){ return this.grids; }
//
//    public List<int[][]> getSavedPositions() { return savedPositions; }
//
//    public int getPerpetualBreakPoint() { return perpetualBreakPoint; }
//
//    public int getPreviousBreakPoint() { return previousBreakPoint; }
}
