package me.edu.gdqy.com.matrixdome;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnTouchListener {
    private ImageView mImageView_page;
    private Matrix matrix;
    private float location_1_x, location_1_y;
    private float location_2_x, location_2_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.init();
    }

    private void init() {
        mImageView_page = (ImageView) this.findViewById(R.id.id_iv_page);
        mImageView_page.setOnTouchListener(this);
        matrix = new Matrix();

        matrix.reset();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionType = event.getActionMasked();
        int index = event.getActionIndex();
        switch (actionType) {
            case MotionEvent.ACTION_DOWN:
                Log.i("Motion", "第一根手指按下:" + " index = " + index + "ID = " + event.getPointerId(index) +
                        "x = " + event.getX() + "y = " + event.getY());
                this.location_1_x = event.getX();
                this.location_1_y = event.getY();
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i("Motion", "非第一根手指按下:" + " index = " + index + "ID = " + event.getPointerId(index) +
                        "x = " + event.getX() + "y = " + event.getY());
                if (index > 1) {
                    return false;
                }
                this.location_2_x = event.getX();
                this.location_2_y = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.i("Motion", "手指移动:" + " index = " + index + "ID = " + event.getPointerId(index) +
                        "x = " + event.getX() + "y = " + event.getY());
                handleMove(event);
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                Log.i("Motion", "非最后一根手指离开:" + " index = " + index + "ID = " + event.getPointerId(index) +
                        "x = " + event.getX() + "y = " + event.getY());
                return true;
            case MotionEvent.ACTION_UP:
                Log.i("Motion", "最后一根手指离开:" + " index = " + index + "ID = " + event.getPointerId(index) +
                        "x = " + event.getX() + "y = " + event.getY());

                return true;
            case MotionEvent.ACTION_OUTSIDE:
                Log.i("Motion", "手指离开触控范围");
                return true;
        }
        return false;
    }

    private void handleMove(MotionEvent event) {
        int historySize = event.getHistorySize();
        int pointerCount = event.getPointerCount();
        if (pointerCount == 1) {
            return;
        }
        float x1, x2, y1, y2;
        for (int i = 0; i < historySize; i++) {
            x1 = event.getHistoricalX(0, i);
            x2 = event.getHistoricalX(1, i);
            y1 = event.getHistoricalY(0, i);
            y2 = event.getHistoricalY(1, i);
            this.handleImage(x1, y1, x2, y2);
            this.location_1_x = x1;
            this.location_1_y = y1;
            this.location_2_x = x2;
            this.location_2_y = y2;

        }
        x1 = event.getX(0);
        x2 = event.getX(1);
        y1 = event.getY(0);
        y2 = event.getY(1);
        this.handleImage(x1, y1, x2, y2);
        this.location_1_x = x1;
        this.location_1_y = y1;
        this.location_2_x = x2;
        this.location_2_y = y2;

    }

    private void handleImage(float x1, float y1, float x2, float y2) {
        double K1 = (this.location_2_y - this.location_1_y) / (this.location_2_x - this.location_1_x);
        double K2 = (y2 - y1) / (x2 - x1);

        double a = Math.abs((K1 - K2) / (1 + K1 * K2));
        double radian = (180 * Math.atan(a) / Math.PI);
        float newDistance = this.distance(x1, x2, y1, y2);
        float oldDistance = this.distance(this.location_1_x, this.location_2_x, this.location_1_y, this.location_2_y);
        Log.i("RADIAN", "k1=" + K1 + "  k2=" + K2 + "  夹角tan值=" + a + "  弧度:" + radian);
        if (radian >= 0.08) {                 //判断是否是旋转

             radian = (K1 < K2 ) ? (radian * 1) : (radian * -1) ; //确定旋转方向
            this.rotate((float) radian);
        } else {
             this.move(oldDistance,newDistance );
        }
    }

    /***
     * 旋转图片
     *
     * @param a 角度
     */
    private void rotate(float a) {
        int height = mImageView_page.getHeight();
        int width = mImageView_page.getWidth();
        matrix.postRotate(a, width / 2, height / 2);
        mImageView_page.setImageMatrix(matrix);
        Toast.makeText(MainActivity.this, "旋转了:" + a, Toast.LENGTH_SHORT).show();
    }

    /***
     * 移动图片
     */
    private void move(float oldDistance, float newDistance) {
        this.matrix.postScale((newDistance / oldDistance), (newDistance / oldDistance),
                (this.location_1_x + this.location_2_x) / 2, (this.location_1_y + this.location_2_y) / 2);
        mImageView_page.setImageMatrix(matrix);


    }

    //计算两点之间的距离
    private float distance(float x, float y, float x1, float y1) {
        return (float) Math.sqrt(Math.pow((x - x1), 2.0) + Math.pow((y - y1), 2.0));
    }
}
