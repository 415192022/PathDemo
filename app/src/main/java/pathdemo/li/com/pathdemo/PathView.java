package pathdemo.li.com.pathdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Mingwei Li on 2017/1/20 0020.
 */

public class PathView extends View {

    public PathView(Context context) {
        super(context);
        init();
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private Paint paint;
    private PointF control;

    private void init() {
        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextSize(100);

        control = new PointF(0, 0);
    }

    private void drawBezier(Canvas canvas) {
        // 绘制贝塞尔曲线
        Path path = new Path();
        path.moveTo(0, getHeight() / 2);
        path.quadTo(control.x, control.y, getWidth(), getHeight() / 2);

        path.moveTo(0, getHeight() / 2);
        path.lineTo(control.x, control.y);

        path.moveTo(getWidth(), getHeight() / 2);
        path.lineTo(control.x, control.y);
        canvas.drawPath(path, paint);
        invalidate();
    }

    private void drawCircle(Canvas canvas, float x, float y) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, 100, paint);
    }


    private void drawBM(Canvas canvas, Bitmap bitmap, int x, int y) {
        canvas.translate(getWidth() / 2, getHeight() / 2);
        Rect size = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect position = new Rect(x + bitmap.getWidth() / 2, y + bitmap.getHeight() / 2, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, size, position, paint);
    }

    /**
     * 获取裁剪后的圆形图片
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else {
            squareBitmap = bmp;
        }
        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);
        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2,
                scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    private VPoint p2, p4;
    private HPoint p1, p3;

    class VPoint {
        public float x;
        public float y;
        public PointF top = new PointF();
        public PointF bottom = new PointF();

        public void setX(float x) {
            this.x = x;
            top.x = x;
            bottom.x = x;
        }

        public void adjustY(float offset) {
            top.y -= offset;
            bottom.y += offset;
        }

        public void adjustAllX(float offset) {
            this.x += offset;
            top.x += offset;
            bottom.x += offset;
        }
    }

    class HPoint {
        public float x;
        public float y;
        public PointF left = new PointF();
        public PointF right = new PointF();

        public void setY(float y) {
            this.y = y;
            left.y = y;
            right.y = y;
        }

        public void adjustAllX(float offset) {
            this.x += offset;
            left.x += offset;
            right.x += offset;
        }
    }

    private float radius = 50;
    private float blackMagic = 0.551915024494f * radius;
    private float distance = blackMagic * 0.45f;
    private float c = 0.551915024494f;
    private float maxLength = getWidth() - radius - radius;

    private void drawCicleBezierSquare3(Canvas canvas) {
        canvas.translate(getWidth() / 2, getHeight() / 2);
        Path path = new Path();
        path.moveTo(0, -100);
        path.cubicTo(blackMagic, 0, 0, blackMagic, 100, 0);

//        path.moveTo(100, 100);
//        path.cubicTo(c, -c, c, -10, 100, -100);
//        path.moveTo(100, -100);
//        path.cubicTo(-c, -10, -10, -c, -100, -100);
//        path.moveTo(-100, -100);
//        path.cubicTo(-10, c, -c, 10, -100, 100);
        canvas.drawPath(path, paint);

        paint.setColor(Color.GREEN);
        path.moveTo(-100, 100);
        path.lineTo(100, 100);
        path.moveTo(100, 100);
        path.lineTo(100, -100);
//        path.moveTo(100, -100);
//        path.lineTo(-100, -100);
//        path.moveTo(-100, -100);
//        path.lineTo(-100, 100);
        canvas.drawPath(path, paint);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBezier(canvas);

//        drawCircle(canvas, control.x, control.y);

//        drawBM(canvas, getCroppedRoundBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sky), 350), (int) control.x, (int) control.y);
//        drawCicleBezierSquare3(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 根据触摸位置更新控制点，并提示重绘
        control.x = event.getX();
        control.y = event.getY();
        invalidate();
        return true;
    }


}
