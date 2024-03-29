package com.eswar.chess;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.eswar.chess.BoardUtils.BLACK_BISHOP;
import static com.eswar.chess.BoardUtils.BLACK_KING;
import static com.eswar.chess.BoardUtils.BLACK_KNIGHT;
import static com.eswar.chess.BoardUtils.BLACK_PAWN;
import static com.eswar.chess.BoardUtils.BLACK_QUEEN;
import static com.eswar.chess.BoardUtils.BLACK_ROOK;
import static com.eswar.chess.BoardUtils.BLACK_WIN;
import static com.eswar.chess.BoardUtils.DRAW;
import static com.eswar.chess.BoardUtils.EMPTY;
import static com.eswar.chess.BoardUtils.NONE;
import static com.eswar.chess.BoardUtils.NO_PIECE;
import static com.eswar.chess.BoardUtils.NO_RESULT;
import static com.eswar.chess.BoardUtils.WHITE_BISHOP;
import static com.eswar.chess.BoardUtils.WHITE_KING;
import static com.eswar.chess.BoardUtils.WHITE_KNIGHT;
import static com.eswar.chess.BoardUtils.WHITE_PAWN;
import static com.eswar.chess.BoardUtils.WHITE_QUEEN;
import static com.eswar.chess.BoardUtils.WHITE_ROOK;
import static com.eswar.chess.BoardUtils.WHITE_WIN;
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
    private Game game;
    private List<Move> possibleMoves = new ArrayList<>();
    private Move currentMove;
    private int touchIndex = NONE;
    private final String PROPERTY_X = "property_x", PROPERTY_Y = "property_y";
    private PromotedInfo promotedInfo;
    private boolean aiPlaying = true, whiteAI = false;
    private MediaPlayer pop, gameOverSound;
    private DatabaseHelper dbh;
    private final int maxVolume = 100;
    private float reducedVolume = (float)(Math.log(maxVolume - maxVolume/4)/Math.log(maxVolume));

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

        pop = createMediaPlayer(R.raw.pop_o);
        gameOverSound = createMediaPlayer(R.raw.game_over);

        gameOverSound.setVolume(reducedVolume, reducedVolume);

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
        game = new Game();
        grids = game.getGrids();
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
            if(aiPlaying && game.isWhiteTurn() != game.isWhiteAI()){
                game.undoMove();
                game.undoMove();
                possibleMoves.clear();
                touchIndex = NONE;
                moved = false;
                currentMove = Move.getDummyMove();
                invalidate();
            }
            else {
                game.undoMove();
                possibleMoves.clear();
                touchIndex = NONE;
                moved = false;
                currentMove = Move.getDummyMove();
                invalidate();
            }
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
        for (Move move : possibleMoves){
            final int row = move.getCurrentRow(), col = move.getCurrentCol();
            float paddedTop = getGridTop(row) + gridPadding, paddedBottom = getGridBottom(row) - gridPadding, paddedLeft = getGridLeft(col) + gridPadding, paddedRight = getGridRight(col) - gridPadding;

            if(move.isEnpassant() || (grids[row][col] != EMPTY)){
                canvas.drawRect(paddedLeft, paddedTop, paddedRight, paddedBottom, attackPaint);
            }
            else{
                canvas.drawRect(paddedLeft, paddedTop, paddedRight, paddedBottom, possiblePaint);
            }
        }

        if(touchIndex != NONE) {
            int row = touchIndex / rows, col = touchIndex % rows;
            float paddedTop = getGridTop(row) + gridPadding, paddedBottom = getGridBottom(row) - gridPadding, paddedLeft = getGridLeft(col) + gridPadding, paddedRight = getGridRight(col) - gridPadding;
            canvas.drawRect(paddedLeft, paddedTop, paddedRight, paddedBottom, touchPaint);
        }

        if(game.isCheck()){
            int row = game.indexOfOppositeKing() / rows, col = game.indexOfOppositeKing() % rows;
            float paddedTop = getGridTop(row) + gridPadding, paddedBottom = getGridBottom(row) - gridPadding, paddedLeft = getGridLeft(col) + gridPadding, paddedRight = getGridRight(col) - gridPadding;
            canvas.drawRect(paddedLeft, paddedTop, paddedRight, paddedBottom, attackPaint);
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
                currentMove = indexOfMove(possibleMoves, currentMove.getCurrent(), piece);
                game.makeMove(currentMove);
                showPromotionPieces = false;
                grids = game.getGrids();
                boardChangesAllowed = true;
                currentMove = Move.getDummyMove();
                possibleMoves.clear();

                touchIndex = NONE;
                invalidate();
            }
        }

        else if(x >= getGridLeft(0) || x <= getGridRight(cols) || y >= getGridTop(0) || y <= getGridBottom(rows)){
            final int col = (int)((x - getGridLeft(0))/(cellW + padding));
            final int row = (int)((y - getGridTop(0))/(cellW + padding));

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
                startAnimation();
                invalidate();
            }

            if(!moved && grids[row][col] != EMPTY) {
                touchIndex = BoardUtils.index(row, col);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        possibleMoves = game.getPossibleMoves(row, col);
                        invalidate();
                    }
                }, 8);
//                Log.d(tag, "Possible Moves: ");

//                for (int i = 0; i < possibleMoves.size(); ++i){
//                    Log.d(tag, possibleMoves.get(i).toString());
//                }
            }

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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(tag, "Moved: " + currentMove.toString());

                        if(aiPlaying && game.isWhiteTurn() != game.isWhiteAI()){
                            game.makeMove(currentMove);
                            grids = game.getGrids();
                            boardChangesAllowed = false;
                            possibleMoves.clear();
                            touchIndex = NONE;
                            invalidate();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    currentMove = game.getAIMove();
                                    touchIndex = currentMove.getPrevious();
                                    boardChangesAllowed = true;
                                    startAnimation();
                                }
                            }, 100);
                        }
                        else if(currentMove.isPromotion()){
                            showPromotionPieces = true;
                            promotedInfo.changeIndex(currentMove.getCurrent(), currentMove.getPiece());

                            List<Move> moves = new ArrayList<>();
                            for (Move move : possibleMoves){
                                if(move.isPromotion()){
                                    moves.add(move);
                                }
                            }
//                          Log.d(tag, "Filtered promotion moves: " + moves);
                            possibleMoves = moves;
                        }
                        else {
                            game.makeMove(currentMove);
                            grids = game.getGrids();
                            touchIndex = NONE;
                            currentMove = Move.getDummyMove();
                            possibleMoves.clear();
                            boardChangesAllowed = true;
                        }
                        invalidate();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int result = game.getResult();
                                if(result != BoardUtils.NO_RESULT){
                                    boardChangesAllowed = false;
                                    Log.d(tag, "Game over: " + getResultString(result));
                                    possibleMoves.clear();
                                    touchIndex = NONE;
                                    currentMove = Move.getDummyMove();
                                    Toast.makeText(context, getResultString(result), Toast.LENGTH_SHORT).show();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    sdf.setTimeZone(TimeZone.getDefault());
                                    String date = sdf.format(Calendar.getInstance().getTime());
                                    dbh.add(new GameRow(getPerspectiveResult(result), date, game.getMoveList()));
                                    playMedia(gameOverSound);
                                }
                            }
                        }, 25);
                    }
                }, 15);
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
//            Log.d(tag, "Move current: " + move.getCurrent() + ", promoted piece: " + move.getPromotedPiece());
            if(move.getCurrent() == index && move.getPromotedPiece() == promotedPiece){
                return move;
            }
        }
        return indexOfMove(moves, index);
    }
    public void finish(){
        game.finish();
        game = null;
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

    public String getPerspectiveResult(int result){
        if(result == DRAW){
            return "Draw";
        }
        else if(result == NO_RESULT){
            return "No result";
        }
        else if(aiPlaying){
            if(whiteAI){
                if(result == WHITE_WIN) { return "You Won"; }
                else if(result == BLACK_WIN) { return "You Lost"; }
            }
            else{
                if(result == WHITE_WIN) { return "You Lost"; }
                else if(result == BLACK_WIN) { return "You Won"; }
            }
        }
        else{
            if(result == WHITE_WIN) { return "White Won"; }
            else if(result == BLACK_WIN) { return "Black Won"; }
        }
        return "Unknown";
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
//            Log.d(tag, "Promoted pieces set up: " + set);
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

    private MediaPlayer createMediaPlayer(int resource){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resource);
        mediaPlayer.setLooping(false);
        return mediaPlayer;
    }

    public void destroyMediaResources(){
        destroyMediaPlayer(pop);
        destroyMediaPlayer(gameOverSound);
    }

    public void destroyMediaPlayer(MediaPlayer mediaPlayer){
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        catch (IllegalStateException ise){
//            Log.d(ERROR_TAG, "IllegalStateException in destroyMediaPlayer");
//            ise.printStackTrace();
        }
        catch (Exception e){
//            Log.d(ERROR_TAG, "Exception in destroyMediaPlayer");
//            e.printStackTrace();
        }
    }
    public void playMedia(MediaPlayer mediaPlayer){
        try {
            mediaPlayer.start();
        }
        catch (IllegalStateException ise){
//            Log.d(ERROR_TAG, "IllegalStateException in playMedia");
//            ise.printStackTrace();
        }
        catch (Exception e){
//            Log.d(ERROR_TAG, "Exception in playMedia");
//            e.printStackTrace();
        }
    }

    public boolean isAiPlaying() { return aiPlaying; }

    public void setAiPlaying(boolean aiPlaying) {
        this.aiPlaying = aiPlaying;
        start();
    }

    public void setDbh(DatabaseHelper dbh) { this.dbh = dbh; }
}
