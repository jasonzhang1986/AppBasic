package me.jasonzhang.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import me.jasonzhang.app.R;


public class CornerVew extends View {

    private String mTextContent;
    private int mTextColor;
    private float mTextSize;
    private boolean mTextBold;
    private boolean mFillTriangle;
    private boolean mTextAllCaps;
    private int mBackgroundColor;
    private float mMinSize;
    private int mTextPadding;
    private int mGravity;
    private static final int DEFAULT_DEGREES = 45;

    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();

    public CornerVew(Context context) {
        this(context, null);
    }

    public CornerVew(Context context, AttributeSet attrs) {
        super(context, attrs);

        obtainAttributes(context, attrs);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CornerVew);
        mTextContent = ta.getString(R.styleable.CornerVew_cv_text);
        mTextColor = ta.getColor(R.styleable.CornerVew_cv_text_color, Color.parseColor("#ffffff"));
        mTextSize = ta.getDimension(R.styleable.CornerVew_cv_text_size, sp2px(11));
        mTextBold = ta.getBoolean(R.styleable.CornerVew_cv_text_bold, true);
        mTextAllCaps = ta.getBoolean(R.styleable.CornerVew_cv_text_all_caps, true);
        mFillTriangle = ta.getBoolean(R.styleable.CornerVew_cv_fill_triangle, false);
        mBackgroundColor = ta.getColor(R.styleable.CornerVew_cv_background_color, Color.parseColor("#FF4081"));
        mMinSize = ta.getDimension(R.styleable.CornerVew_cv_min_size, mFillTriangle ? dp2px(35) : dp2px(50));
        mTextPadding = (int)(ta.getDimension(R.styleable.CornerVew_cv_text_padding, dp2px(5f))+0.5f);
        mGravity = ta.getInt(R.styleable.CornerVew_cv_gravity, Gravity.TOP | Gravity.LEFT);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size = getHeight();

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setFakeBoldText(mTextBold);
        mBackgroundPaint.setColor(mBackgroundColor);

        float textHeight = mTextPaint.descent() - mTextPaint.ascent();
        if (mFillTriangle) {
            if (mGravity == (Gravity.TOP | Gravity.LEFT)) {
                mPath.reset();
                mPath.moveTo(0, 0);
                mPath.lineTo(0, size);
                mPath.lineTo(size, 0);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawTextWhenFill(size, -DEFAULT_DEGREES, canvas, true);
            } else if (mGravity == (Gravity.TOP | Gravity.RIGHT)) {
                mPath.reset();
                mPath.moveTo(size, 0);
                mPath.lineTo(0, 0);
                mPath.lineTo(size, size);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);
                drawTextWhenFill(size, DEFAULT_DEGREES, canvas, true);
            } else if (mGravity == (Gravity.BOTTOM | Gravity.LEFT)) {
                mPath.reset();
                mPath.moveTo(0, size);
                mPath.lineTo(0, 0);
                mPath.lineTo(size, size);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawTextWhenFill(size, DEFAULT_DEGREES, canvas, false);
            } else if (mGravity == (Gravity.BOTTOM | Gravity.RIGHT)) {
                mPath.reset();
                mPath.moveTo(size, size);
                mPath.lineTo(0, size);
                mPath.lineTo(size, 0);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawTextWhenFill(size, -DEFAULT_DEGREES, canvas, false);
            }
        } else {
            double delta = (textHeight + mTextPadding * 2) * Math.sqrt(2);
            if (mGravity == (Gravity.TOP | Gravity.LEFT)) {
                mPath.reset();
                mPath.moveTo(0, (float) (size - delta));
                mPath.lineTo(0, size);
                mPath.lineTo(size, 0);
                mPath.lineTo((float) (size - delta), 0);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawText(size, -DEFAULT_DEGREES, canvas, textHeight, true);
            } else if (mGravity == (Gravity.TOP | Gravity.RIGHT)) {
                mPath.reset();
                mPath.moveTo(0, 0);
                mPath.lineTo((float) delta, 0);
                mPath.lineTo(size, (float) (size - delta));
                mPath.lineTo(size, size);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawText(size, DEFAULT_DEGREES, canvas, textHeight, true);
            } else if (mGravity == (Gravity.BOTTOM | Gravity.LEFT)) {
                mPath.reset();
                mPath.moveTo(0, 0);
                mPath.lineTo(0, (float) delta);
                mPath.lineTo((float) (size - delta), size);
                mPath.lineTo(size, size);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawText(size, DEFAULT_DEGREES, canvas, textHeight, false);
            } else if (mGravity == (Gravity.BOTTOM | Gravity.RIGHT)) {
                mPath.reset();
                mPath.moveTo(0, size);
                mPath.lineTo((float) delta, size);
                mPath.lineTo(size, (float) delta);
                mPath.lineTo(size, 0);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawText(size, -DEFAULT_DEGREES, canvas, textHeight, false);
            }
        }
    }

    private void drawText(int size, float degrees, Canvas canvas, float textHeight, boolean isTop) {
        canvas.save();
        canvas.rotate(degrees, size / 2f, size / 2f);
        float delta = isTop ? -(textHeight + mTextPadding * 2) / 2 : (textHeight + mTextPadding * 2) / 2;
        float textBaseY = size / 2 - (mTextPaint.descent() + mTextPaint.ascent()) / 2 + delta;
        canvas.drawText(mTextAllCaps ? mTextContent.toUpperCase() : mTextContent,
                getPaddingLeft() + (size - getPaddingLeft() - getPaddingRight()) / 2, textBaseY, mTextPaint);
        canvas.restore();
    }

    private void drawTextWhenFill(int size, float degrees, Canvas canvas, boolean isTop) {
        canvas.save();
        canvas.rotate(degrees, size / 2f, size / 2f);
        float delta = isTop ? -size / 4 : size / 4;
        float textBaseY = size / 2 - (mTextPaint.descent() + mTextPaint.ascent()) / 2 + delta;
        canvas.drawText(mTextAllCaps ? mTextContent.toUpperCase() : mTextContent,
                getPaddingLeft() + (size - getPaddingLeft() - getPaddingRight()) / 2, textBaseY, mTextPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measureWidth(widthMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredWidth);

        //以下代码是根据设置的cv:gravity值设定控件在父布局中的位置
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams)getLayoutParams()).gravity = mGravity;
        } else if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams)getLayoutParams()).gravity = mGravity;
        }
        if(getParent() instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            if ((mGravity&Gravity.LEFT) == Gravity.LEFT) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            }
            if ((mGravity&Gravity.TOP) == Gravity.TOP) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            }
            if ((mGravity&Gravity.RIGHT) == Gravity.RIGHT) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            }
            if ((mGravity&Gravity.BOTTOM) == Gravity.BOTTOM) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            }
        }
    }

    private int measureWidth(int widthMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            int padding = getPaddingLeft() + getPaddingRight();
            mTextPaint.setColor(mTextColor);
            mTextPaint.setTextSize(mTextSize);
            float textWidth = mTextPaint.measureText(mTextContent + "");
            result = (int) ((padding + (int) textWidth) * Math.sqrt(2));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
            result = Math.max((int) mMinSize, result);
        }

        return result;
    }

    protected int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}

