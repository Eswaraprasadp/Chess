package com.eswar.chess;

import android.util.Log;

import static com.eswar.chess.BoardUtils.BLACK_WIN;
import static com.eswar.chess.BoardUtils.KING_SCORE;
import static com.eswar.chess.BoardUtils.MAX;
import static com.eswar.chess.BoardUtils.MIN;
import static com.eswar.chess.BoardUtils.NONE;
import static com.eswar.chess.BoardUtils.NO_RESULT;
import static com.eswar.chess.BoardUtils.WHITE_WIN;
import static com.eswar.chess.BoardUtils.depthString;
import static com.eswar.chess.BoardUtils.isCheck;
import static com.eswar.chess.BoardUtils.perspective;
import static com.eswar.chess.BoardUtils.tag;

public class AI {
    boolean whiteAI = false;

    public AI(boolean whiteAI){
        this.whiteAI = whiteAI;
    }

    private Move alphaBeta(Game game, boolean turnAI, int alpha, int beta, int depth) {
        Move bestMove = Move.getDummyMove();
        int currentScore = 0;
        int bestScore = ((turnAI) ? MIN : MAX);

        int result = game.getResult();

        if(depth <= 1){
            Log.d(depthString(depth), "Entered for: " + game.getBoard());
        }

        if (game.getResult() != NO_RESULT) {

            final int BONUS = 280, PENALTY = 40;

            currentScore = game.evaluate();
            currentScore *= perspective(whiteAI);

            // AI Win
            if(result == perspective(whiteAI) * WHITE_WIN){
                currentScore += BONUS - depth * PENALTY + KING_SCORE;
            }
            // AI Lost
            else if(result == perspective(whiteAI) * BLACK_WIN){
                currentScore += -BONUS + depth * PENALTY - KING_SCORE;
            }
            // Draw
            else{
                currentScore += BONUS/30;
            }

            if(depth <= 1) {
                Log.d(depthString(depth), "Terminal condition. Current Score = " + currentScore);
            }

            bestMove.setScore(currentScore);
            return bestMove;
        }

        else if (depth >= 3){
            currentScore = game.evaluate();
            currentScore *= perspective(whiteAI);
            bestMove.setScore(currentScore);
            return bestMove;

        }
        else {
            for (Move move : game.getAllPossibleMoves()) {
                game.makeMove(move);

                if (turnAI) {
                    currentScore = alphaBeta(game, false, alpha, beta, depth + 1).getScore();
                } else {
                    currentScore = alphaBeta(game, true, alpha, beta, depth + 1).getScore();
                }

                if (turnAI) {
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestMove = move;
                    }
                    if (bestScore > alpha) {
                        alpha = bestScore;
                    }
                } else {
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestMove = move;
                    }
                    if (bestScore < beta) {
                        beta = bestScore;
                    }
                }

                if(depth <= 1){
                    Log.d(depthString(depth), "Board: " + game.getBoard() + "currentScore = " + currentScore + ", bestScore = " + bestScore + ", bestMove = " + bestMove);
                }

                game.undoMove();

                if (alpha >= beta) {
                    break;
                }
            }
            if(depth <= 1) {
                Log.d(depthString(depth), "Finally bestScore = " + bestScore + ", bestMove = " + bestMove);
            }
            bestMove.setScore(bestScore);

            return bestMove;
        }
    }
    public Move getBestMove(Game game){
        Move move = alphaBeta(game, true, MIN, MAX, 0);
        if(isCheck(game.getGrids(), move, game.isWhiteTurn())){
            move.setThreateningMove(true);
        }
        Log.d(tag, "Finally, best move returned by AI:" + move);
        return move;
    }
}
