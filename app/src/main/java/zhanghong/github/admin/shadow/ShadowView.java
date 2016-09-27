package zhanghong.github.admin.shadow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by zhanghong on 16/8/30.
 */
public class ShadowView extends View {

    Activity mContext;
    RectF mRectFOfAlpha, mRectFOfShadowView;
    Rect mRectOfBitmap;
    Bitmap mBitmap;
    int mRadius;
    Paint mPaint;

    public ShadowView(Context context) {
        super(context);
        init(context);
    }

    public ShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (context instanceof Activity) {
            mContext = (Activity) context;
            mRadius = dp2px(4);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.RED);
        } else {
            throw new IllegalArgumentException("context must be activity");
        }
    }


    public ShadowView setAlphaArea(View view) {
        Rect outRect = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int titleBarHeight = outRect.top;

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        mRectFOfAlpha = new RectF(location[0], location[1] - titleBarHeight,
                location[0] + view.getWidth(), location[1] + view.getHeight() - titleBarHeight);
        invalidate();
        return this;
    }

    public ShadowView setAlphaArea(RectF rectF) {
        mRectFOfAlpha = rectF;
        invalidate();
        return this;
    }

    public ShadowView setBitmapRes(int bitmapRes) {
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), bitmapRes, null);
        mRectOfBitmap = new Rect(0, 0 , mBitmap.getWidth(), mBitmap.getHeight());
        invalidate();
        return this;
    }

    public ShadowView setRadius(int radius) {
        mRadius = dp2px(radius);
        invalidate();
        return this;
    }

    public ShadowView attach() {
        FrameLayout rootView = (FrameLayout) mContext.findViewById(android.R.id.content);
        rootView.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return this;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectFOfShadowView = new RectF(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRectFOfAlpha != null && mBitmap != null && mRectFOfShadowView != null) {
            canvas.save();
            canvas.clipRect(mRectFOfShadowView);
            Path alphaPath = new Path();
            alphaPath.addRoundRect(mRectFOfAlpha, mRadius, mRadius, Path.Direction.CW);
            canvas.clipPath(alphaPath, Region.Op.DIFFERENCE);
            canvas.drawBitmap(mBitmap, mRectOfBitmap, mRectFOfShadowView, new Paint());
            canvas.restore();
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        if (200 < x && x < 500) {
//            if (200 < y && y < 500) {
//                Toast.makeText(mContext, "123", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            this.setVisibility(GONE);
//        }
//        return super.onTouchEvent(event);
//    }

    private int dp2px(int dp) {
        return (int)getResources().getDisplayMetrics().density * dp;
    }

}
