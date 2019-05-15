package com.example.yfsl.timeviewdemo;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 广告页右上角倒计时控件
 */

public class CountDownProgressView extends View {
    private int circSolidColor;//圆实心颜色
    private int circFrameColor;//圆边框颜色
    private int circFrameWidth = 4;//圆边框宽度
    private int circRadius;//圆半径
    private int progressColor;//进度条颜色
    private int progressWidth = 4;//进度条宽度
    private int textColor;//文字颜色

    private Rect mBounds;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private RectF mArcRectF;

    private int mCenterX;
    private int mCenterY;

    private String text = "跳过";

    private long timeMillis = 3000;//倒计时时长
    private OnProgressListener mProgressListener;//进度条监听
    private int progress = 100;
    //进度条类型  顺数和倒数两种类型
    private ProgressType mProgressType = ProgressType.COUNT_BACK;

    /**
     * 进度条类型 方便做扩展 此处默认为倒数
     */
    public enum ProgressType{
        //顺数 0-100
        COUNT,
        //倒数 100-0
        COUNT_BACK
    }

    public void setmProgressType(ProgressType progressType){
        mProgressType = progressType;
        resetProgress();
        //请求重绘 更新ui
        invalidate();
    }

    /**
     * 进度条监听  方法onProgress传入参数进度值
     */
    public interface OnProgressListener{
        void onProgress(int progress);
    }

    public CountDownProgressView(Context context) {
        super(context);
        initPaint();
    }

    /**
     * 初始化画笔画布等
     */
    private void initPaint() {
        mPaint = new Paint();
        mTextPaint = new TextPaint();
        mArcRectF = new RectF();
        mBounds = new Rect();
    }

    /**
     * 重写测量方法
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (width>height){
            height = width;
        }
        else {
            width = height;
        }
        circRadius = width/2;
        setMeasuredDimension(width,height);
    }

    /**
     * 重写绘制方法 canvas在绘制过程中是一层层覆盖的 有绘制顺序
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取view的边界
        getDrawingRect(mBounds);
        mCenterX = mBounds.centerX();
        mCenterY = mBounds.centerY();

        //画实心圆
        mPaint.setAntiAlias(true);//设置抗锯齿
        mPaint.setStyle(Paint.Style.FILL);//设置实心填充
        mPaint.setColor(circSolidColor);
        canvas.drawCircle(mCenterX,mCenterY,circRadius,mPaint);

        //画外边框
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);//空心
        mPaint.setStrokeWidth(circFrameWidth);//设置空心线宽
        mPaint.setColor(circFrameColor);
        canvas.drawCircle(mCenterX,mCenterY,circRadius-circFrameWidth,mPaint);

        //画文字
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(90);
        float textY = mCenterY-(mTextPaint.descent()+mTextPaint.ascent())/2;
        canvas.drawText(text,mCenterX,textY,mTextPaint);

        //画进度条
        mPaint.setColor(progressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(progressWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//设置笔刷的图形样式  ROUND 圆形 SQUARE 方形
        mArcRectF.set(mBounds.left+progressWidth,mBounds.top+progressWidth,mBounds.right-progressWidth,mBounds.bottom-progressWidth);
        canvas.drawArc(mArcRectF,-90,360*progress/100,false,mPaint);//-90表方向
    }

    public void start(){
        stop();
        post(progressChangeTask);
    }

    public void stop() {
        removeCallbacks(progressChangeTask);
    }

    public void reStart() {
        resetProgress();
        start();
    }

    public void resetProgress() {
        switch (mProgressType){
            case COUNT:
                progress = 0;
                break;
            case COUNT_BACK:
                progress = 100;
                break;
            default:
                break;
        }
    }

    private Runnable progressChangeTask = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(this);
            switch (mProgressType){
                case COUNT:
                    progress += 1;
                    break;
                case COUNT_BACK:
                    progress -= 1;
                    break;
                default:
                    break;
            }
            if (progress >= 0 && progress <= 100){
                if (mProgressListener != null){
                    mProgressListener.onProgress(progress);
                }
                invalidate();
                postDelayed(progressChangeTask,timeMillis/60);
            }
            else {
                progress = validateProgress(progress);
            }
        }
    };

    private int validateProgress(int progress) {
        if (progress >= 100){
            progress = 100;
        }
        else if (progress <= 0){
            progress = 0;
        }
        return progress;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setTimeMillis(long timeMillis){
        this.timeMillis = timeMillis;
        invalidate();
    }

    public void setmProgressListener(OnProgressListener onProgressListener){
        mProgressListener = onProgressListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (Math.abs(x-(mBounds.centerX()))<=(circRadius)*2 && Math.abs(y -  (mBounds.centerY())) <=(circRadius)*2   ){
                    Log.e("TAG", "-----------------onTouchEvent---------------------");
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public CountDownProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        //获取控件属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CountDownProgressView);
        if (typedArray != null){
            int count = typedArray.getIndexCount();
            for (int i = 0;i<count;i++){
                int index = typedArray.getIndex(i);
                //获取布局时设置的颜色
                switch (index){
                    case R.styleable.CountDownProgressView_circSolidColor:
                        circSolidColor = typedArray.getColor(R.styleable.CountDownProgressView_circSolidColor,Color.WHITE);
                        break;
                    case R.styleable.CountDownProgressView_circFrameColor:
                        circFrameColor = typedArray.getColor(R.styleable.CountDownProgressView_circFrameColor,Color.GRAY);
                        break;
                    case R.styleable.CountDownProgressView_progressColor:
                        progressColor = typedArray.getColor(R.styleable.CountDownProgressView_progressColor,Color.BLUE);
                        break;
                    case R.styleable.CountDownProgressView_textColor:
                        textColor = typedArray.getColor(R.styleable.CountDownProgressView_textColor,Color.BLACK);
                        break;
                }
            }
            //不要忘记回收
            typedArray.recycle();
        }
    }

}
