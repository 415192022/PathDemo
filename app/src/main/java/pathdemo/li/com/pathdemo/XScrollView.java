package pathdemo.li.com.pathdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Mingwei Li on 2017/1/24 0024.
 */

public class XScrollView extends ScrollView {
    public XScrollView(Context context) {
        super(context);
        init();

    }

    public XScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public XScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private Paint paint;

    private void init() {
        pointF = new PointF();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.colorAccent));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);
        paint.setTextSize(50);
    }

    private Path drawBezierShadow(float cx, float cy) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.quadTo(cx, cy, getWidth(), 0);
        return path;
    }

    private Path drawTest(float cx, float cy, float[] floats) {
        RectF rectF = new RectF(cx, cy, 200, 200);
        Path path = new Path();
        path.addOval(rectF, Path.Direction.CCW);
        Matrix matrix = new Matrix();
        matrix.setValues(floats);
        path.transform(matrix);
        return path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pointF.y > getHeight() / 8) {
            pointF.y = getHeight() / 8;
        }
        canvas.drawPath(drawBezierShadow(pointF.x, pointF.y), paint);
    }

    /**
     * 同步方法，如果不用同步会出现闪烁
     */
    private synchronized void release() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isDrawShadow = false;
                if (pointF.y > getHeight() / 10) {
                    pointF.y = getHeight() / 10;
                }
                for (float i = pointF.y; i > 0; i--) {
                    pointF.y = i;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myHandler.sendEmptyMessage(0);
                }
                isDrawShadow = true;
            }
        }).start();
    }

    private PointF pointF;
    private float dy;
    private volatile boolean isDrawShadow = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("top" + top);
        if (isDrawShadow) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    release();
                    break;
                case MotionEvent.ACTION_MOVE:
                    pointF.x = event.getX();
                    float pd = event.getY() - dy;
                    if (pd > getHeight() / 10) {
                        pd = getHeight() / 10;
                    }
                    pointF.y = pd;
                    break;
                case MotionEvent.ACTION_DOWN:
                    dy = event.getY();
                    break;
            }
        }
        invalidate();
        if ((pointF.y - dy) < 0) {
            return super.onTouchEvent(event);
        } else {
            return true;
        }
    }

    private int top = 100;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        top = t;
        System.out.println("------- top: " + t + "\n-------oldtop:" + oldt);
    }


    private MyHandler myHandler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    }
}
