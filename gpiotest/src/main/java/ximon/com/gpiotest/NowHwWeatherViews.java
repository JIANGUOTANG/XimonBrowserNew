package ximon.com.gpiotest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 11833 on 2017/12/5.
 */


public class NowHwWeatherViews extends View {

    private Paint mArcPaint;
    private Paint mLinePaint;
    private Paint mTextPaint;
    private Paint mPointPaint;

    private float mWidth;
    private float mHeight;
    private float radius;//半径

    private int startAngle;//圆弧开始角
    private int sweepAngle;//圆弧总角度数
    private int count;//圆弧被分的份数

    private int currentTemp;//当前温度
    private int maxTemp;
    private int minTemp;
    private Bitmap bitmap;//底下的天气图标
    private int ocAngle;//0度开始的刻度
    private int minTempAngle;//最小温度的角度
    private int maxTempAngle;//最大温度的角度

    private int offset;
    private Context context ;
    public NowHwWeatherViews(Context context) {
        this(context,null);
    }

    public NowHwWeatherViews(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NowHwWeatherViews(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        initPaint();
        this.context = context;
        startAngle=120;
        sweepAngle=300;
        count=70;//刻度份数
        currentTemp=26;
        maxTemp=27;
        minTemp=10;
        bitmapCreate(R.drawable.cloudy) ;
        ocAngle=30;//0°所在的刻度
        offset=22;
    }


    private void initPaint() {
        mArcPaint=new Paint();
        mArcPaint.setColor(Color.WHITE);
        mArcPaint.setStrokeWidth(2);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);
        mLinePaint=new Paint();
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mTextPaint=new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeWidth(4);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(144);
        mPointPaint=new Paint();
        mPointPaint.setColor(Color.WHITE);
        mPointPaint.setStrokeWidth(2);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int wrap_Len = 600;
        int width = measureDimension(wrap_Len, widthMeasureSpec);
        int height = measureDimension(wrap_Len, heightMeasureSpec);
        int len=Math.min(width,height);
        //保证是一个正方形
        setMeasuredDimension(len,len);

    }
    public int measureDimension(int defaultSize, int measureSpec){
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = defaultSize;   //UNSPECIFIED
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth=getWidth();
        mHeight=getHeight();
        radius=(mWidth-getPaddingLeft()-getPaddingRight())/2;//半径
        canvas.translate(mWidth/2,mHeight/2);
        drawLine(canvas);//画短线
//        drawTextBitmapView(canvas);//画中间的温度和下边的图片
//        drawTempLineView(canvas);//画动态温度

    }

    private void drawTempLineView(Canvas canvas) {
        mTextPaint.setTextSize(24);
        //canvas.drawText("0°C",getRealCosX(ocAngle,offset,true),getRealSinY(ocAngle,offset,true),mTextPaint);//固定0度的位置

//        int startTempAngle=getStartAngle(minTemp,maxTemp);
//       /* if(startTempAngle<=startAngle){//如果开始角小于startAngle，防止过边界
//            startTempAngle=startAngle+10;
//        }else if((startTempAngle+fgAngle)>=(startAngle+sweepAngle)){//如果结束角大于(startAngle+sweepAngle)
//            startTempAngle =startAngle+sweepAngle-20-fgAngle;
//        }*/
//        canvas.drawText(minTemp + "°", getRealCosX(startTempAngle, offset,true), getRealSinY(startTempAngle, offset,true), mTextPaint);
//        canvas.drawText(maxTemp + "°", getRealCosX(startTempAngle+fgAngle, offset,true), getRealSinY(startTempAngle+fgAngle, offset,true), mTextPaint);
//        int circleAngle = startTempAngle+(currentTemp-minTemp)*fgAngle/(maxTemp-minTemp);
//        mPointPaint.setColor(getRealColor(minTemp,maxTemp));
//        canvas.drawCircle(getRealCosX(circleAngle,50,false),getRealSinY(circleAngle,50,false),7,mPointPaint);
//
}

    private void drawArcView(Canvas canvas) {
        RectF mRect=new RectF(-radius,-radius,radius,radius);
        //canvas.drawRect(mRect,mArcPaint);
        canvas.drawArc(mRect,startAngle,sweepAngle,false,mArcPaint);
    }

    private void drawLine(Canvas canvas) {
        mTextPaint.setTextSize(24);
        canvas.save();
        float angle = (float)sweepAngle/count;//刻度间隔
        canvas.rotate(-270+startAngle);
        for(int i=0;i<=count;i++){
            if(i==0 || i==count){
                mLinePaint.setStrokeWidth(1);
                mLinePaint.setColor(Color.WHITE);
                canvas.drawLine(0,-radius+40,0,-radius+90,mLinePaint);
             }else if(i>=ocAngle+minTemp && i<=ocAngle+maxTemp){
                if (i==(minTemp+ocAngle)){
                    //绘制最小温度的值
                    canvas.drawText(minTemp + "°",0,-radius+20,mTextPaint);
                }
                if (i==(maxTemp+ocAngle)){
                    //绘制最大温度的值
                    canvas.drawText(maxTemp + "°",0,-radius+20,mTextPaint);

                }
                mLinePaint.setStrokeWidth(3);
                mLinePaint.setColor(getRealColor(minTemp,maxTemp));
                if (i==(currentTemp+ocAngle)){
                    mTextPaint.setColor(getRealColor(minTemp,maxTemp));
                    canvas.drawCircle(0,-radius+85,7,mTextPaint);//圆点
                    mTextPaint.setColor(Color.WHITE);
                }
                canvas.drawLine(0,-radius+40,0,-radius+80,mLinePaint);

            }else {
                mLinePaint.setStrokeWidth(2);
                mLinePaint.setColor(Color.WHITE);

                canvas.drawLine(0,-radius+40,0,-radius+80,mLinePaint);
            }
            canvas.rotate(angle);//逆时针旋转
        }
        canvas.restore();
    }
    private void drawTextBitmapView(Canvas canvas) {
        mTextPaint.setTextSize(144);

        canvas.drawText(currentTemp+"°",0,0+getTextPaintOffset(mTextPaint),mTextPaint);
        canvas.drawBitmap(bitmap,0-bitmap.getWidth()/2,radius-bitmap.getHeight()/2-30,null);

    }


    public float getTextPaintOffset(Paint paint){
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        return  -fontMetrics.descent+(fontMetrics.bottom-fontMetrics.top)/2;
    }
    //根据角获得X坐标  R*cos&+getTextPaintOffset(mTextPaint)-off
    private float getCosX(int Angle){

        return (float) (radius*Math.cos(Angle*Math.PI/180))+getTextPaintOffset(mTextPaint);
    }
    private float getSinY(int Angle){

        return (float)(radius*Math.sin(Angle*Math.PI/180))+getTextPaintOffset(mTextPaint);
    }
    //根据象限加一个偏移量
    private float getRealCosX(int Angle,int off,boolean outoff){
        if(!outoff){
            off=-off;
        }
        if(getCosX(Angle)<0){
            return getCosX(Angle)-off;
        }else{
            return getCosX(Angle)+off;
        }
    }
    private float getRealSinY(int Angle,int off,boolean outoff){
        if(!outoff){
            off=-off;
        }
        if(getSinY(Angle)<0){
            return getSinY(Angle)-off;
        }else{
            return getSinY(Angle)+off;
        }
    }

    //根据温度返回颜色值
    public int getRealColor(int minTemp,int maxTemp){
        if(maxTemp<=0){
            return Color.parseColor("#00008B");//深海蓝
        }else if(minTemp<=0 && maxTemp>0){
            return Color.parseColor("#4169E1");//黄君兰
        }else if(minTemp>0 && minTemp<15 ){
            return Color.parseColor("#40E0D0");//宝石绿
        }else if(minTemp>=15 && minTemp<25){
            return Color.parseColor("#00FF00");//酸橙绿
        }else if(minTemp>=25 &&minTemp<30){
            return Color.parseColor("#FFD700");//金色
        }else  if(minTemp>=30){
            return Color.parseColor("#CD5C5C");//印度红
        }

        return Color.parseColor("#00FF00");//酸橙绿;
    }

    /**
     * 根据天气状况来设置显示的图片
     * @param cond_txt 天气状况
     */
    public void setBitmap(String cond_txt){
        if (cond_txt.contains("雨")){
            bitmapCreate(R.drawable.rain);
        }else if (cond_txt.contains("云")){
            bitmapCreate(R.drawable.cloudy);
        }else if (cond_txt.contains("晴")){
            bitmapCreate(R.drawable.sunny);
        }else if (cond_txt.contains("阴")){
            bitmapCreate(R.drawable.overcast);
        }
        invalidate();
    }

    /**
     * 设置正下方显示的图片
     * @param bitmapId
     */
    private void bitmapCreate(int bitmapId){
        this.bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapId);
        // Matrix类进行图片处理（缩小或者旋转）
        Matrix matrix = new Matrix();
        // 缩小一倍
        matrix.postScale(0.3f, 0.3f);
        // 生成新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

    }
    /**
     *
     * @param currentTemp 当前温度
     * @param maxTemp 最大温度
     * @param minTemp 最小温度
     */
    public void setTemp(int currentTemp,int maxTemp,int minTemp){
        this.currentTemp = currentTemp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        invalidate();
    }

}