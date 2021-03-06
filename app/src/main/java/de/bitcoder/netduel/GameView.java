package de.bitcoder.netduel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class GameView extends View {
    private String mExampleString = new String("mExampleString"); // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private  GameModel game = null;
    Paint ground_paint = new Paint();
    Paint player1Paint = new Paint();
    Paint player2Paint = new Paint();
    Paint gunPaint = new Paint();
    Paint bulletPaint = new Paint();

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public Handler frame = new Handler();
    private static final int FRAME_RATE = 40; //25 frames per second
    //private static final int FRAME_RATE = 1000; //1 frame per second

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GameView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.GameView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.GameView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.GameView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.GameView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.GameView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        ground_paint.setStrokeWidth(1);
        ground_paint.setColor(Color.DKGRAY);
        ground_paint.setStyle(Paint.Style.FILL_AND_STROKE);

        player1Paint.setStrokeWidth(1);
        player1Paint.setColor(Color.GREEN);
        player1Paint.setStyle(Paint.Style.FILL_AND_STROKE);

        player2Paint.setStrokeWidth(1);
        player2Paint.setColor(Color.BLUE);
        player2Paint.setStyle(Paint.Style.FILL_AND_STROKE);

        gunPaint.setStrokeWidth(5);
        //gunPaint.setColor(paint.getColor()); // set in drawPlayer()
        gunPaint.setStyle(Paint.Style.STROKE);

        bulletPaint.setStrokeWidth(1);
        bulletPaint.setColor(Color.BLACK);
        bulletPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();

        isRunning = true;
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    private boolean isRunning = false;
    public void pause()
    {
        isRunning = false;
    }

    public void resume()
    {
        isRunning = true;
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    public Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
            //frame.removeCallbacks(frameUpdate);
            game.update();
            GameView.this.invalidate();
            if ( GameView.this.isRunning )
                frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };

    private void invalidateTextPaintAndMeasurements() {

        mExampleString = new String("mExampleString");
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);


        game = GameModel.getInstance();

        int height = this.getMeasuredHeight();
        int width = this.getMeasuredWidth();
        canvas.drawRect(0, height/2, width, height, ground_paint );

        drawPlayer(canvas, game.getPlayer1(), player1Paint);
        drawPlayer(canvas, game.getPlayer2(), player2Paint);

        drawBullet(canvas, game.getPlayer1());
        drawBullet(canvas, game.getPlayer2());

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    private void drawBullet(Canvas canvas, PlayerModel player) {
        BulletModel b = player.getBullet();
        if ( b.isVisible() )
        {
            canvas.drawRect((float)b.getPosX()-5, (float)b.getPosY()-5, (float)b.getPosX()+5, (float)b.getPosY()+5, bulletPaint);
        }
    }
    private void drawPlayer(Canvas canvas, PlayerModel player, Paint paint)
    {
        int height = this.getMeasuredHeight();
        int width = this.getMeasuredWidth();
        if ( player.getPlayerNumber() == 1 )
            player.setPosX(50);
        else
            player.setPosX(width-50);

        player.setPosY(height/2);

        int posX =  player.getPosX();
        int posY = player.getPosY();
        int angle = player.getAngle();

        canvas.drawRect(posX-20, posY, posX+20, posY-20, paint); // BASE
        canvas.drawRect(posX-10, posY-20, posX+10, posY-30, paint); // TOP

        int gun_base_x, gun_base_y, gun_end_x, gun_end_y;

        double gun_length = 35.0;

        if ( angle == 0 )
        {
            gun_base_x = posX;
            gun_base_y = posY-15;
            gun_end_x = posX;
            gun_end_y = posY-15-(int)gun_length;
        }
        else
        {
            double radians = Math.toRadians(angle+90);
            double x2 = gun_length * Math.cos(radians);
            double y2 = gun_length * Math.sin(radians);
            if ( angle < 0 )
            {
                gun_base_x = posX+10;
                gun_base_y = posY-15;
                gun_end_x = gun_base_x+(int)x2;
                gun_end_y = gun_base_y-(int)y2;
            }
            else
            {
                gun_base_x = posX-10;
                gun_base_y = posY-15;
                gun_end_x = gun_base_x+(int)x2;
                gun_end_y = gun_base_y-(int)y2;
            }
        }
        player.setGunPosX(gun_end_x);
        player.setGunPosY(gun_end_y);
        gunPaint.setColor(paint.getColor());
        canvas.drawLine(gun_base_x, gun_base_y, gun_end_x, gun_end_y, gunPaint); // GUN
    }


    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
