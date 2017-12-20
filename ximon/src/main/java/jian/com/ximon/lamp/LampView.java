package jian.com.ximon.lamp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 11833 on 2017/12/12.
 */

public class LampView extends View {

    public LampView(Context context) {
        this(context, null);
    }

    public LampView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LampView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LampView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    int colors[] = new int[3];
    float positions[] = new float[3];

    private void init() {
        // 第1个点
        colors[0] = 0xFFD0D0D0;
        positions[0] = 0;
        // 第2个点
        colors[1] = 0xFFEBEBEB;
        positions[1] = 0.8f;
        // 第3个点
        colors[2] = 0xFFEEEEEE;
        positions[2] = 1;
    }

    private Paint mPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lampState == 1) {
            //开灯的时候才绘制光束
            drawAnimatorLight(canvas);
        }else{
            rightLight=2;
        }
        //只绘制一次
        drawLamp(canvas);
    }

    private void drawAnimatorLight(Canvas canvas) {

            drawLight(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
         height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(500, 800);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(500, height);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, 800);
        }
        LinearGradient shader = new LinearGradient(
                0, 0,
                0, height,
                colors,
                positions,
                Shader.TileMode.MIRROR);
        mPaint.setShader(shader);
    }

    /**
     * 绘制灯
     *
     * @param canvas
     */
    int lmapTop = 50;//顶上圆角矩形的大小
    int lampshadeHeight;
    Paint paint = new Paint();

    private void drawLamp(Canvas canvas) {
        lampshadeHeight = height / 5 < 400 ? 400 : height / 5;
        paint.setColor(0xFFD0D0D0);
        RectF rectF = new RectF(width / 2 - radius, 0, width / 2 + radius, radius * 2);
        canvas.drawArc(rectF, -180, 180, true, paint);
        drawBulb(canvas);
    }

    /**
     * 画灯泡
     *
     * @param canvas
     */
    int colorBulb = 0xFFF2E8CE;
    int lampState = 0;//灯的状态
    int lampLastState = 1;//灯的状体

    private void drawBulb(Canvas canvas) {
        colorBulb = lampState == 1 ? 0xFFFFFF9D : 0xFFF7F7F7;//灯的颜色
        paint.setColor(0xFFE0E0E0);
        //画灯泡外圈
        RectF rectF2 = new RectF(width / 2 - radius / 2, radius / 2, width / 2 + radius / 2, radius * 3 / 2);
        canvas.drawArc(rectF2, 0, 180, true, paint);
        paint.setColor(colorBulb);
        //画灯泡内圈
        rectF2 = new RectF(width / 2 - radius / 2 + 20, radius / 2 + 20, width / 2 + radius / 2 - 20, radius * 3 / 2 - 20);
        canvas.drawArc(rectF2, 0, 180, true, paint);
    }

    int radius = 150;
    int rightLight = 2;
    int heightLight = 2;
    //绘制光束

    public void drawLight(Canvas canvas) {
        Path path = new Path();
        heightLight = rightLight * 3 * (height - radius - 100) / width;
        path.moveTo(width / 2 - radius / 2, radius);
        path.lineTo(width / 2 + radius / 2, radius);
        path.lineTo(width / 2 + radius / 2 + rightLight, radius + heightLight);
        path.lineTo(width / 2 - radius / 2 - rightLight, radius + heightLight);
        path.close();
        canvas.drawPath(path, mPaint);
        rightLight = rightLight + 20;
        if(rightLight<=width/3) {
            postInvalidate();
        }
    }

    int width, height;

    public void setLampState() {
        lampLastState = lampState;
        lampState = lampState == 0 ? 1 : 0;//改变灯的状态

        invalidate();
    }

    public void setLampState(int state) {
        lampLastState = lampState;
        if (state>0) {
            lampState = 1;//改变灯的状态
        }
        else{
            lampState = 0;//改变灯的状态
        }

        invalidate();
    }
}
