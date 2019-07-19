package com.eswar.chess;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
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
import static com.eswar.chess.BoardUtils.WHITE_BISHOP;
import static com.eswar.chess.BoardUtils.WHITE_KING;
import static com.eswar.chess.BoardUtils.WHITE_KNIGHT;
import static com.eswar.chess.BoardUtils.WHITE_PAWN;
import static com.eswar.chess.BoardUtils.WHITE_QUEEN;
import static com.eswar.chess.BoardUtils.WHITE_ROOK;
import static com.eswar.chess.BoardUtils.cols;
import static com.eswar.chess.BoardUtils.rows;
import static com.eswar.chess.BoardUtils.tag;

public class BoardView extends View {
    private Context context;
    private Paint whitePaint = new Paint(), blackPaint = new Paint(), touchPaint = new Paint(), possiblePaint = new Paint(), attackPaint = new Paint();
    private final float padding = 4.0f, extPadding = 40.0f;
    private float cellW, gridPadding = 6.0f, animatedX, animatedY, rookAnimatedX;
    private int height, width;
    private boolean moved = false, boardChangesAllowed = true, showPromotionPieces = false;
    private int grids[][] = new int[rows][cols];
    private BoardUtils board;
    private List<Move> possibleMoves = new ArrayList<>();
    private Move currentMove;
    private int touchIndex = NONE;
    private final String PROPERTY_X = "property_x", PROPERTY_Y = "property_y";
    private PromotedInfo promotedInfo;

    public BoardView(Context context){
        super(context);
        init(context);
    }
    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    public void init(Context context){
        this.context = context;
        whitePaint.setColor(getResources().getColor(R.color.darker_white));
        blackPaint.setColor(getResources().getColor(R.color.dark_gray));
        touchPaint.setColor(getResources().getColor(R.color.dark_blue));
        possiblePaint.setColor(getResources().getColor(R.color.purple));
        attackPaint.setColor(getResources().getColor(R.color.red));

        touchPaint.setStyle(Paint.Style.STROKE);
        possiblePaint.setStyle(Paint.Style.STROKE);
        attackPaint.setStyle(Paint.Style.STROKE);

        touchPaint.setStrokeWidth(10.0f);
        possiblePaint.setStrokeWidth(10.0f);
        attackPaint.setStrokeWidth(10.0f);
        changeDimen();
    }
    public void changeDimen(){
        height = getHeight() - (int)extPadding;
        width = getWidth() - (int)extPadding;

        if(height >= width){
            cellW = width/cols * 1.0f - padding * 1.0f;
        }
        else{
            cellW = height/rows * 1.0f - padding * 1.0f;
        }
        gridPadding = cellW/12.0f;
        start();
    }
    public void start(){
        board = new BoardUtils();
        grids = board.getGrids();
        moved = false;
        boardChangesAllowed = true;
        showPromotionPieces = false;
        possibleMoves = new ArrayList<>();

        touchIndex = NONE;

        currentMove = Move.getDummyMove();
        promotedInfo = new PromotedInfo(NONE, NO_PIECE);

        invalidate();
    }
    public void undo(){
        if(boardChangesAllowed) {
            board.undoMove();
            possibleMoves.clear();
            touchIndex = NONE;
            moved = false;
            currentMove = Move.getDummyMove();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int row = 0; row < rows; ++row){
            for (int col = 0; col < cols; ++col){
                float top = getGridTop(row), bottom = getGridBottom(row), left = getGridLeft(col), right = getGridRight(col);

                if((row + col) % 2 == 0) {
                    canvas.drawRect(left, top, right, bottom, whitePaint);
                }
                else {
                    canvas.drawRect(left, top, right, bottom, blackPaint);
                }

                Move move = indexOfMove(possibleMoves, BoardUtils.index(row, col));

                if(BoardUtils.index(row, col) == touchIndex){
                    float paddedTop = top + gridPadding, paddedBottom = bottom - gridPadding, paddedLeft = left + gridPadding, paddedRight = right - gridPadding;
                    canvas.drawRect(paddedLeft, paddedTop, paddedRight, paddedBottom, touchPaint);
                }
                else if(!move.equals(Move.getDummyMove())){
                    float paddedTop = top + gridPadding, paddedBottom = bottom - gridPadding, paddedLeft = left + gridPadding, paddedRight = right - gridPadding;

                    if(move.isEnpassant() || (grids[row][col] != EMPTY)){
                        canvas.drawRect(paddedLeft, paddedTop, paddedRight, paddedBottom, attackPaint);
                    }
                    else{
                        canvas.drawRect(paddedLeft, paddedTop, paddedRight, paddedBottom, possiblePaint);
                    }
                }
                else if(board.isCheck() && BoardUtils.index(row, col) == board.indexOfOppositeKing()){
                    float paddedTop = top + gridPadding, paddedBottom = bottom - gridPadding, paddedLeft = left + gridPadding, paddedRight = right - gridPadding;
                    canvas.drawRect(paddedLeft, paddedTop, paddedRight, paddedBottom, attackPaint);
                }
                if(grids[row][col] != EMPTY && ((BoardUtils.index(row, col) != touchIndex && BoardUtils.index(row, col) != currentMove.getCastleRookIndex()) || boardChangesAllowed)){
                    Drawable drawable = ContextCompat.getDrawable(context, getPieceDrawable(grids[row][col]));
                    try {
                        drawable.setBounds((int) getGridLeft(col), (int) getGridTop(row), (int) getGridRight(col), (int) getGridBottom(row));
                        drawable.draw(canvas);
                        drawable.invalidateSelf();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        if(!boardChangesAllowed){
            Drawable drawable = ContextCompat.getDrawable(context, getPieceDrawable(currentMove.getPiece()));
            float left = animatedX - cellW/2, right = animatedX + cellW/2, top = animatedY - cellW/2, bottom = animatedY + cellW/2;
            try {
                drawable.setBounds((int)left, (int)top, (int)right, (int)bottom);
                drawable.draw(canvas);
                drawable.invalidateSelf();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(currentMove.getCastleRookIndex() != NONE){
                drawable = ContextCompat.getDrawable(context, getPieceDrawable(currentMove.getCastleRookPiece()));
                left = rookAnimatedX - cellW/2; right = rookAnimatedX + cellW/2;
                top = getGridTop(BoardUtils.rowCol(currentMove.getCastleRookIndex())[0]);
                bottom = getGridBottom(BoardUtils.rowCol(currentMove.getCastleRookIndex())[0]);

                try {
                    drawable.setBounds((int)left, (int)top, (int)right, (int)bottom);
                    drawable.draw(canvas);
                    drawable.invalidateSelf();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(showPromotionPieces){
                promotedInfo.drawPromotedPieces(canvas);
            }
        }
    }

    public void handleTouch(float x, float y){

        if(showPromotionPieces){
            int piece = promotedInfo.getSelectedPiece(x, y);
            if(piece != NO_PIECE){
                Log.d(tag, "Before clicking current move was " + currentMove + ", current index of move = " + currentMove.getCurrent());
                currentMove = indexOfMove(possibleMoves, currentMove.getCurrent(), piece);
                Log.d(tag, "Clicked on piece " + Move.pieceString(piece) + " ie. Move " + currentMove);
                board.makeMove(currentMove);
                showPromotionPieces = false;
                grids = board.getGrids();
                boardChangesAllowed = true;
                currentMove = Move.getDummyMove();
                possibleMoves.clear();

                touchIndex = NONE;
                invalidate();
            }
        }

        else if(x >= getGridLeft(0) || x <= getGridRight(cols) || y >= getGridTop(0) || y <= getGridBottom(rows)){
            int col = (int)((x - getGridLeft(0))/(cellW + padding));
            int row = (int)((y - getGridTop(0))/(cellW + padding));

            if (!BoardUtils.validate(row) || !BoardUtils.validate(col)){
                return;
            }

//            Log.d(tag, "Clicked on row = " + (row + 1) + ", col = " + (col + 1) + ", ie. " + Move.colString(col) + (8 - row));

            if(BoardUtils.index(row, col) == touchIndex){
                touchIndex = NONE;
                possibleMoves.clear();
                invalidate();
                return;
            }

            if(moved){ moved = false; }

            Move move = indexOfMove(possibleMoves, BoardUtils.index(row, col));

            if(!move.equals(Move.getDummyMove())){
                moved = true;
                currentMove = move;
                Log.d(tag, "About to start move: " + currentMove);
                startAnimation();
            }

            if(!moved && grids[row][col] != EMPTY) {
                touchIndex = BoardUtils.index(row, col);
                possibleMoves = board.getPossibleMoves(row, col);
//                Log.d(tag, "Possible Moves: ");

//                for (int i = 0; i < possibleMoves.size(); ++i){
//                    Log.d(tag, possibleMoves.get(i).toString());
//                }
            }

            invalidate();

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(boardChangesAllowed) {
                float x = event.getX(), y = event.getY();
                handleTouch(x, y);
                performClick();
            }
            else if(showPromotionPieces){
                float x = event.getX(), y = event.getY();
                handleTouch(x, y);
                performClick();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void startAnimation(){
        int pr = currentMove.getPreviousRow(), pc = currentMove.getPreviousCol(), cr = currentMove.getCurrentRow(), cc = currentMove.getCurrentCol();
        final PropertyValuesHolder valueX = PropertyValuesHolder.ofFloat(PROPERTY_X, getX(pc), getX(cc));
        final PropertyValuesHolder valueY = PropertyValuesHolder.ofFloat(PROPERTY_Y, getY(pr), getY(cr));
        boardChangesAllowed = false;
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(valueX, valueY);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                BoardView.this.animatedX = (float)animation.getAnimatedValue(PROPERTY_X);
                BoardView.this.animatedY = (float)animation.getAnimatedValue(PROPERTY_Y);
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.d(tag, "Moved: " + currentMove.toString());

                if(currentMove.isPromotion()){
                    showPromotionPieces = true;
                    promotedInfo.changeIndex(currentMove.getCurrent(), currentMove.getPiece());

                    List<Move> moves = new ArrayList<>();
                    for (Move move : possibleMoves){
                        if(move.isPromotion()){
                            moves.add(move);
                        }
                    }
                    Log.d(tag, "Filtered promotion moves: " + moves);
                    possibleMoves = moves;
                }
                else {
                    board.makeMove(currentMove);
                    grids = board.getGrids();
                    touchIndex = NONE;
                    currentMove = Move.getDummyMove();
                    possibleMoves.clear();
                    boardChangesAllowed = true;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(board.getResult() != BoardUtils.NO_RESULT){
                            boardChangesAllowed = false;
                            Log.d(tag, "Game over: " + getResultString(board.getResult()));
                            possibleMoves.clear();
                            touchIndex = NONE;
                            currentMove = Move.getDummyMove();
                        }
                    }
                }, 100);
                invalidate();
            }
        });

        ValueAnimator rookAnimator;
        if(currentMove.isKingCastle()){
            final PropertyValuesHolder rookValueX = PropertyValuesHolder.ofFloat(PROPERTY_X, getX(cols - 1), getX(cols - 3));
            rookAnimator = ValueAnimator.ofPropertyValuesHolder(rookValueX);
            rookAnimator.setDuration(200);
            rookAnimator.setInterpolator(new LinearInterpolator());

            rookAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    BoardView.this.rookAnimatedX = (float)animation.getAnimatedValue(PROPERTY_X);
                    invalidate();
                }
            });

            rookAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    invalidate();
                }
            });
            rookAnimator.start();
        }
        else if(currentMove.isQueenCastle()){
            final PropertyValuesHolder rookValueX = PropertyValuesHolder.ofFloat(PROPERTY_X, getX(0), getX(3));
            rookAnimator = ValueAnimator.ofPropertyValuesHolder(rookValueX);
            rookAnimator.setDuration(200);
            rookAnimator.setInterpolator(new LinearInterpolator());

            rookAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    BoardView.this.rookAnimatedX = (float)animation.getAnimatedValue(PROPERTY_X);
                    invalidate();
                }
            });

            rookAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    invalidate();
                }
            });
            rookAnimator.start();
        }

        animator.start();

    }

    private float getX(int col){
        return ((width * 1.0f + extPadding/1.0f - cols * (cellW + padding))/2.0f + (cellW + padding)/2.0f + (cellW + padding) * col * 1.0f);
    }
    private float getY(int row){
        return ((height * 1.0f + extPadding/2.0f - rows * (cellW + padding))/2.0f + (cellW + padding)/2.0f + (cellW + padding) * row * 1.0f);
    }
    private float getGridLeft(int col){
        return (getX(col) - (cellW + padding)/2.0f);
    }
    private float getGridTop(int row){
        return (getY(row) - (cellW + padding)/2.0f);
    }
    private float getGridRight(int col){
        return (getX(col) + (cellW + padding)/2.0f);
    }
    private float getGridBottom(int row){
        return (getY(row) + (cellW + padding)/2.0f);
    }

    private int getPieceDrawable(int grid){
        switch (grid){
            case WHITE_KING: return R.drawable.white_king;
            case WHITE_QUEEN: return R.drawable.white_queen;
            case WHITE_ROOK: return R.drawable.white_rook;
            case WHITE_BISHOP: return R.drawable.white_bishop;
            case WHITE_KNIGHT: return R.drawable.white_knight;
            case WHITE_PAWN: return R.drawable.white_pawn;
            case BLACK_KING: return R.drawable.black_king;
            case BLACK_QUEEN: return R.drawable.black_queen;
            case BLACK_ROOK: return R.drawable.black_rook;
            case BLACK_BISHOP: return R.drawable.black_bishop;
            case BLACK_KNIGHT: return R.drawable.black_knight;
            case BLACK_PAWN: return R.drawable.black_pawn;
            default: return R.drawable.transparent;
        }
    }

    private Move indexOfMove(List<Move> moves, int index){
        for (int i = 0; i < moves.size(); ++i){
            if(moves.get(i).getCurrent() == index){
                return moves.get(i);
            }
        }
        return Move.getDummyMove();
    }
    private Move indexOfMove(List<Move> moves, int index, int promotedPiece){
        for (Move move: moves){
            Log.d(tag, "Move current: " + move.getCurrent() + ", promoted piece: " + move.getPromotedPiece());
            if(move.getCurrent() == index && move.getPromotedPiece() == promotedPiece){
                return move;
            }
        }
        return indexOfMove(moves, index);
    }

    public static String getResultString(int result){
        switch (result){
            case BoardUtils.WHITE_WIN: return "White Wins!";
            case BoardUtils.BLACK_WIN: return "Black Wins!";
            case BoardUtils.DRAW: return "Draw!";
            case BoardUtils.NO_RESULT: return "No result";
            default: return "Unknown";
        }
    }

    public class PromotedInfo{
        private int col, row;
        private boolean white, set;
        private int[][] gridCoordinates = new int[4][4];
        private Paint strokePaint = new Paint(), transparentPaint = new Paint();
        private float gridWidth;

        PromotedInfo(int index, int piece){
            this.col = BoardUtils.rowCol(index)[1];
            this.white = piece > 0;
            this.row = BoardUtils.rowCol(index)[0] + (white ? -1 : 1);
            this.set = false;
            gridWidth = cellW * 4/5;
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(getResources().getColor(R.color.colorPrimary));
            strokePaint.setStrokeWidth(10.0f);
            transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        }

        void changeIndex(int index, int piece){

            this.col = BoardUtils.rowCol(index)[1];
            this.white = piece > 0;
            this.row = BoardUtils.rowCol(index)[0] + (white ? -1 : 1);

            if(col < 2){
                for (int i = 0; i < 4; ++i){
                    gridCoordinates[i][0] = (int)(getGridLeft(0) + gridWidth*i);
                    gridCoordinates[i][1] = (int)(getGridTop(row));
                    gridCoordinates[i][2] = (int)(getGridLeft(0) + gridWidth*(i + 1));
                    gridCoordinates[i][3] = (int)(getGridTop(row) + gridWidth);
                }
            }
            else if(col >= cols - 2){
                for (int i = 3; i >= 0; --i){
                    gridCoordinates[i][0] = (int)(getGridLeft(cols - 1) - gridWidth*i);
                    gridCoordinates[i][1] = (int)(getGridTop(row));
                    gridCoordinates[i][2] = (int)(getGridLeft(cols - 1) - gridWidth*(i - 1));
                    gridCoordinates[i][3] = (int)(getGridTop(row) + gridWidth);
                }
            }
            else {
                for (int i = 0; i < 4; ++i){
                    gridCoordinates[i][0] = (int)(getGridLeft(2) + gridWidth*i);
                    gridCoordinates[i][1] = (int)(getGridTop(row));
                    gridCoordinates[i][2] = (int)(getGridLeft(2) + gridWidth*(i + 1));
                    gridCoordinates[i][3] = (int)(getGridTop(row) + gridWidth);
                }
            }
            set = true;
        }

        void drawPromotedPieces(Canvas canvas){
            Log.d(tag, "Promoted pieces set up: " + set);
            if(set) {
                for (int i = 0; i < 4; ++i) {
                    try {
                        Drawable drawable = ContextCompat.getDrawable(context, getPieceDrawable(promotedPiece(i)));
                        drawable.setBounds(gridCoordinates[i][0], gridCoordinates[i][1], gridCoordinates[i][2], gridCoordinates[i][3]);
                        drawable.draw(canvas);
                        drawable.invalidateSelf();
                        canvas.drawRect(gridCoordinates[i][0], gridCoordinates[i][1], gridCoordinates[i][2], gridCoordinates[i][3], strokePaint);
                        invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        int getSelectedPiece(float x, float y){
            if(y < getGridTop(row) || y > getGridTop(row) + gridWidth || x < gridCoordinates[0][0] || x > gridCoordinates[3][2]){
                return NO_PIECE;
            }
            else{
                return promotedPiece((int)((x - gridCoordinates[0][0]) / gridWidth));
            }
        }
        int promotedPiece(int r){
            switch (r){
                case 0 : return (white ? 1 : -1) * WHITE_QUEEN;
                case 1 : return (white ? 1 : -1) * WHITE_ROOK;
                case 2 : return (white ? 1 : -1) * WHITE_BISHOP;
                case 3 : return (white ? 1 : -1) * WHITE_KNIGHT;
                default: return NO_PIECE;
            }
        }
    }

}
