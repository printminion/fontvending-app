package com.example.fontvending.client;

import java.io.File;

import com.example.fontvending.client.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

public class FontView extends TextView {

	private static final String TAG = "FontView";
	private static final float FONT_SIZE_DIP = 30;
	private static final float FONT_STORKE_DIP = 2;
	private Paint mPaintFont, mPaintText, mPaintBackground;
	private String mLabelText = "";
	private Shader mBG;
	private int mHeight, mWidth;
	private final int LABEL_STROKE_COLOR = Color.parseColor("#000000");
	private final int LABEL_COLOR = Color.parseColor("#CCCCCC");

	public FontView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public FontView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FontView(Context context) {
		super(context);
		init();
	}

	public void setFontPreview(Context context, String pathFont, String strLabel, boolean bShowIntentIcon) {

		this.mLabelText = strLabel;

		mPaintFont = new Paint(Paint.ANTI_ALIAS_FLAG);

		mPaintFont.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintFont.setTypeface(Typeface.DEFAULT_BOLD);
		mPaintFont.setTextSize(FontView.dipToPx(getContext(), FONT_SIZE_DIP));
		mPaintFont.setColor(LABEL_STROKE_COLOR);

		mPaintFont.setDither(true);
		mPaintFont.setStrokeJoin(Paint.Join.ROUND);
		mPaintFont.setStrokeCap(Paint.Cap.ROUND);
		mPaintFont.setStrokeWidth(FontView.dipToPx(getContext(), FONT_STORKE_DIP));

		try {
			final Typeface typeface = Typeface.createFromFile(new File(pathFont));
			mPaintFont.setTypeface(typeface);
		} catch (Exception e) {
			Log.e(TAG, "Failed to get typeface of font:" + pathFont);
		}

		mPaintText = new Paint(mPaintFont);
		mPaintText.setStyle(Style.FILL);
		mPaintText.setColor(LABEL_COLOR);

		/*
		 * set default text color if no text there
		 */
		if (strLabel.length() == 0) {
			strLabel = context.getString(R.string.no_text);

			mPaintFont.setColor(Color.WHITE);
			mPaintText.setColor(Color.GRAY);

		}

		invalidate();

	}

	@Override
	protected void onDraw(Canvas canvas) {

		// Get the size of the control based on the last call to onMeasure.
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();

		canvas.drawColor(Color.parseColor("#D0CECE"));

		if (mPaintBackground == null) {
			init();
		}

		// draw the checker-board pattern
		if (mPaintBackground != null) {
			canvas.drawRect(0, 0, mWidth, mHeight, mPaintBackground);
		}

		if (mPaintText != null) {

			FontMetrics metrics = mPaintText.getFontMetrics();

			float mTextWidth = mPaintText.measureText(mLabelText);
			float mTextHeight = metrics.top;

			float iFontX = (int) (mWidth / 2 - (mTextWidth / 2));
			float iFontY = (int) (mHeight / 2 - (mTextHeight / 2));

			if (mPaintFont != null) {
				canvas.drawText(mLabelText, iFontX, iFontY, mPaintFont);
			}
			canvas.drawText(mLabelText, iFontX, iFontY, mPaintText);
		}

	}

	private void init() {

		/*
		 * make a ckeckerboard pattern
		 */
		try {

			Bitmap bm = Bitmap.createBitmap(new int[] { 0xFFFFFFFF, 0xFFCCCCCC, 0xFFCCCCCC, 0xFFFFFFFF }, 2, 2,
					Bitmap.Config.RGB_565);
			mBG = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			Matrix m = new Matrix();
			m.setScale(10, 10);
			mBG.setLocalMatrix(m);

			mPaintBackground = new Paint();
			mPaintBackground.setFilterBitmap(false);
			mPaintBackground.setStyle(Paint.Style.FILL);
			mPaintBackground.setShader(mBG);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
		int measuredWidth = measureWidth(wMeasureSpec);
		int measuredHeight = measureHeight(hMeasureSpec);
		// MUST make this call to setMeasuredDimension
		// or you will cause a runtime exception when
		// the control is laid out.
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	private int measureHeight(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// Default size if no limits are specified.
		int result = (int) FontView.dipToPx(getContext(), 60);
		if (specMode == MeasureSpec.AT_MOST) {
			// Calculate the ideal size of your
			// control within this maximum size.
			// If your control fills the available
			// space return the outer bound.
			// result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			// If your control can fit within these bounds return that value.
			// result = specSize;
		}

		return result;
	}

	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// Default size if no limits are specified.
		int result = (int) FontView.dipToPx(getContext(), 200);
		if (specMode == MeasureSpec.AT_MOST) {
			// Calculate the ideal size of your control
			// within this maximum size.
			// If your control fills the available space
			// return the outer bound.
			// result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			// If your control can fit within these bounds return that value.
			result = specSize;
		}

		return result;
	}

	public static float dipToPx(Context context, float dip) {
		Resources r = context.getResources();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
	}
}