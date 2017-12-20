package jian.com.ximon.charge;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
/**
 * Created by 11833 on 2017/12/13.
 */
public class ChargeView extends View {

    private Paint mPaint;
    private Path flightPath;
    private Paint mPaintWhite;
    private Paint mPaintLine;//画插脚用的
    private int w;//宽度
    private int h;//高度
    public ChargeView(Context context) {
        this(context, null);
    }

    public ChargeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChargeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChargeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaintWhite =new Paint();
        mPaintWhite.setColor(Color.WHITE);
        flightPath = new Path();
        mPaintWhite.setAntiAlias(true);
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.BLACK);
        mPaintLine.setStrokeWidth(5);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStyle(Paint.Style.STROKE);
    }

    private Rect rect;
    private RectF rect2;//中间白色矩形
    private RectF rect3;//底部黑色矩形
    private RectF rect4;//右边的插座
    private RectF rect5;//插头
    private int state  = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(rect, mPaint);
        canvas.drawRect(rect2, mPaintWhite);
        //绘制闪电图标
        Log.d("w",(Math.tan(60)*2/16)+"(Math.tan(60)*2/16)");
        double radians = Math.toRadians(60);
        float flightHeight = ((float) (Math.tan(radians)*w/16));
        //画闪电图标
        flightPath.moveTo(w*3/8,h/2-10);
        flightPath.lineTo(w*5/16, h/2-10+flightHeight);//左边的点
        flightPath.lineTo(w*3/8,h/2-10+flightHeight);
        flightPath.lineTo(w*3/8,h/2-10+flightHeight*3/2);
        flightPath.lineTo(w*7/16,h/2-10+flightHeight/2);
        flightPath.lineTo(w*3/8,h/2-10+flightHeight/2);
        flightPath.close();
        canvas.drawPath(flightPath,mPaintWhite);
        //底部黑色矩形
        canvas.drawRoundRect(rect3,5,5,mPaint);
        //右边的插座
        canvas.drawRoundRect(rect4,5,5,mPaint);
        //插头绘制
        rect5 = new RectF(plugLeftX, h /2-60, plugRightX, h/2+20);//插座
        canvas.drawArc(rect5,90,180,true,mPaintWhite);
        canvas.drawLine(plugLeftX+plugWidth/2,h/2-40,plugLeftX+plugWidth/2+30,h/2-40,mPaintLine);//上面插脚
        canvas.drawLine(plugLeftX+plugWidth/2,h/2,plugLeftX+plugWidth/2+30,h/2,mPaintLine);//下面插脚
        drawLine(canvas);
    }

    /**
     * 画电线
     */
    private void drawLine(Canvas canvas){
        Path path = new Path();
        path.moveTo(w / 2-w/17, h  / 4+w/7);
        path.cubicTo(w*5/8,h*3/4,w / 2, h /2,plugLeftX,h/2-20);
        canvas.drawPath(path,mPaintLine);
        if (state==1&&plugRightX<=w+30){
            plugRightX = plugRightX+10;
            plugLeftX= plugLeftX+10;
            invalidate();
        }else if (state==0&&plugLeftX>w*3/4-20){
            plugRightX = plugRightX-10;
            plugLeftX= plugLeftX-10;
            invalidate();
        }
    }
   public void  update(){
       state =state==0?1:0;//改变通电状态
//       if (state == 0){
//           plugLeftX =w*3/4-20;
//           plugRightX = w*3/4+100;
//       }
       invalidate();
   }

    public void  update(int s){
        state =s;

        invalidate();
    }
   private float plugLeftX;
   private float plugRightX;
    private float plugWidth;
    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        w = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        h = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(500, 800);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(500, h);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(w, 800);
        }
        rect2 = new RectF(w / 4+w/17, h / 4+w/17, w / 2-w/17, h  / 4+w/7);
        rect = new Rect(w / 4, h / 4, w / 2, h * 3 / 4);
        rect3 = new RectF(w / 4-w/17, h * 3 / 4-20, w / 2+w/17, h * 3 / 4);
        rect4 = new RectF(w-30, h /2-90, w-5, h/2+50);
        plugLeftX =w*3/4-20;
        plugRightX = w*3/4+100;
        plugWidth = 120;
        rect5 = new RectF(plugLeftX, h /2-60, plugRightX, h/2+20);//插座
        //w / 2-w/17, h  / 4+w/7   w*3/4-20，h/2-20)
    }
}
