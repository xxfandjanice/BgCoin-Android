package com.lzy.imagepicker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.lzy.imagepicker.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ================================================
 * ��    �ߣ�����Ң
 * ��    ����1.0
 * �������ڣ�2016/1/7
 * ��    ����
 * Matrix ��9��ֵ�ֱ�Ϊ  ����  ƽ��  ��б
 * MSCALE_X	 MSKEW_X	MTRANS_X
 * MSKEW_Y	 MSCALE_Y	MTRANS_Y
 * MPERSP_0  MPERSP_1	MPERSP_2
 * �޶���ʷ��
 * ================================================
 */

public class CropImageView extends AppCompatImageView {

    /******************************** �м��FocusView��ͼ��صĲ��� *****************************/
    public enum Style {
        RECTANGLE, CIRCLE
    }

    private Style[] styles = {Style.RECTANGLE, Style.CIRCLE};

    private int mMaskColor = 0xAF000000;   //��ɫ
    private int mBorderColor = 0xAA808080; //�����ı߿���ɫ
    private int mBorderWidth = 1;         //����߿�Ŀ�ȣ����ʿ�ȣ�
    private int mFocusWidth = 250;         //�����Ŀ��
    private int mFocusHeight = 250;        //�����ĸ߶�
    private int mDefaultStyleIndex = 0;    //Ĭ�Ͻ�������״

    private Style mStyle = styles[mDefaultStyleIndex];
    private Paint mBorderPaint = new Paint();
    private Path mFocusPath = new Path();
    private RectF mFocusRect = new RectF();

    /******************************** ͼƬ����λ�ƿ��ƵĲ��� ************************************/
    private static final float MAX_SCALE = 4.0f;  //������űȣ�ͼƬ���ź�Ĵ�С���м�ѡ������ı�ֵ
    private static final int NONE = 0;   // ��ʼ��
    private static final int DRAG = 1;   // ��ק
    private static final int ZOOM = 2;   // ����
    private static final int ROTATE = 3; // ��ת
    private static final int ZOOM_OR_ROTATE = 4;  // ���Ż���ת

    private static final int SAVE_SUCCESS = 1001;  // ���Ż���ת
    private static final int SAVE_ERROR = 1002;  // ���Ż���ת

    private int mImageWidth;
    private int mImageHeight;
    private int mRotatedImageWidth;
    private int mRotatedImageHeight;
    private Matrix matrix = new Matrix();      //ͼƬ�任��matrix
    private Matrix savedMatrix = new Matrix(); //��ʼ��õ�ʱ��ͼƬ��matrix
    private PointF pA = new PointF();          //��һ����ָ���µ������
    private PointF pB = new PointF();          //�ڶ�����ָ���µ������
    private PointF midPoint = new PointF();    //������ָ���м��
    private PointF doubleClickPos = new PointF();  //˫��ͼƬ��ʱ��˫���������
    private PointF mFocusMidPoint = new PointF();  //�м�View���м��
    private int mode = NONE;            //��ʼ��ģʽ
    private long doubleClickTime = 0;   //�ڶ���˫����ʱ��
    private double rotation = 0;        //��ָ��ת�ĽǶȣ�����90��������������Ϊ����ֵ����Ҫת����level
    private float oldDist = 1;          //˫ָ��һ�εľ���
    private int sumRotateLevel = 0;     //��ת�ĽǶȣ�90��������
    private float mMaxScale = MAX_SCALE;//������ݲ�ͬͼƬ�Ĵ�С����̬�õ���������ű�
    private boolean isInited = false;   //�Ƿ񾭹��� onSizeChanged ��ʼ��
    private boolean mSaving = false;    //�Ƿ����ڱ���
    private static Handler mHandler = new InnerHandler();

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mFocusWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mFocusWidth, getResources().getDisplayMetrics());
        mFocusHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mFocusHeight, getResources().getDisplayMetrics());
        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources().getDisplayMetrics());

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FreeCropImageView);
        mMaskColor = a.getColor(R.styleable.FreeCropImageView_cropMaskColor, mMaskColor);
        mBorderColor = a.getColor(R.styleable.FreeCropImageView_cropBorderColor, mBorderColor);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.FreeCropImageView_cropBorderWidth, mBorderWidth);
        mFocusWidth = a.getDimensionPixelSize(R.styleable.FreeCropImageView_cropFocusWidth, mFocusWidth);
        mFocusHeight = a.getDimensionPixelSize(R.styleable.FreeCropImageView_cropFocusHeight, mFocusHeight);
        mDefaultStyleIndex = a.getInteger(R.styleable.FreeCropImageView_cropStyle, mDefaultStyleIndex);
        mStyle = styles[mDefaultStyleIndex];
        a.recycle();

        //ֻ����ͼƬΪ��ǰ������ģʽ
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initImage();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initImage();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        initImage();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initImage();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        isInited = true;
        initImage();
    }

    /** ��ʼ��ͼƬ�ͽ���� */
    private void initImage() {
        Drawable d = getDrawable();
        if (!isInited || d == null) return;

        mode = NONE;
        matrix = getImageMatrix();
        mImageWidth = mRotatedImageWidth = d.getIntrinsicWidth();
        mImageHeight = mRotatedImageHeight = d.getIntrinsicHeight();
        //������������е��������ϡ��¡����ұߵ�x��y��ֵ
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float midPointX = viewWidth / 2;
        float midPointY = viewHeight / 2;
        mFocusMidPoint = new PointF(midPointX, midPointY);

        if (mStyle == Style.CIRCLE) {
            int focusSize = Math.min(mFocusWidth, mFocusHeight);
            mFocusWidth = focusSize;
            mFocusHeight = focusSize;
        }
        mFocusRect.left = mFocusMidPoint.x - mFocusWidth / 2;
        mFocusRect.right = mFocusMidPoint.x + mFocusWidth / 2;
        mFocusRect.top = mFocusMidPoint.y - mFocusHeight / 2;
        mFocusRect.bottom = mFocusMidPoint.y + mFocusHeight / 2;

        //���佹�������ű�����ͼƬ����С�߲�С�ڽ�������С�ߣ�
        float fitFocusScale = getScale(mImageWidth, mImageHeight, mFocusWidth, mFocusHeight, true);
        mMaxScale = fitFocusScale * MAX_SCALE;
        //������ʾͼƬ��ImageView�����ű�����ͼƬ������һ����������Ļ����ʾ�����Σ�
        float fitViewScale = getScale(mImageWidth, mImageHeight, viewWidth, viewHeight, false);
        //ȷ�����յ����ű���,�����佹����ǰ����������ʾͼƬ��ImageView��
        //�����������������佹����������������ʾͼƬ��ImageView��������������ȡ���ű��������ֵ��
        //��ȡ���ַ�����ԭ���п���ͼƬ�ܳ����ߺܸߣ�������ImageView��ʱ����ܻ��/���Ѿ�С�ڽ����Ŀ�/��
        float scale = fitViewScale > fitFocusScale ? fitViewScale : fitFocusScale;
        //ͼ���е�Ϊ���Ľ�������
        matrix.setScale(scale, scale, mImageWidth / 2, mImageHeight / 2);
        float[] mImageMatrixValues = new float[9];
        matrix.getValues(mImageMatrixValues); //��ȡ���ź��mImageMatrix��ֵ
        float transX = mFocusMidPoint.x - (mImageMatrixValues[2] + mImageWidth * mImageMatrixValues[0] / 2);  //X�᷽���λ��
        float transY = mFocusMidPoint.y - (mImageMatrixValues[5] + mImageHeight * mImageMatrixValues[4] / 2); //Y�᷽���λ��
        matrix.postTranslate(transX, transY);
        setImageMatrix(matrix);
        invalidate();
    }

    /** ����߽����ű��� isMinScale �Ƿ���С������true ��С���ű����� false ������ű��� */
    private float getScale(int bitmapWidth, int bitmapHeight, int minWidth, int minHeight, boolean isMinScale) {
        float scale;
        float scaleX = (float) minWidth / bitmapWidth;
        float scaleY = (float) minHeight / bitmapHeight;
        if (isMinScale) {
            scale = scaleX > scaleY ? scaleX : scaleY;
        } else {
            scale = scaleX < scaleY ? scaleX : scaleY;
        }
        return scale;
    }

    /** ���ƽ���� */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Style.RECTANGLE == mStyle) {
            mFocusPath.addRect(mFocusRect, Path.Direction.CCW);
            canvas.save();
            canvas.clipRect(0, 0, getWidth(), getHeight());
            canvas.clipPath(mFocusPath, Region.Op.DIFFERENCE);
            canvas.drawColor(mMaskColor);
            canvas.restore();
        } else if (Style.CIRCLE == mStyle) {
            float radius = Math.min((mFocusRect.right - mFocusRect.left) / 2, (mFocusRect.bottom - mFocusRect.top) / 2);
            mFocusPath.addCircle(mFocusMidPoint.x, mFocusMidPoint.y, radius, Path.Direction.CCW);
            canvas.save();
            canvas.clipRect(0, 0, getWidth(), getHeight());
            canvas.clipPath(mFocusPath, Region.Op.DIFFERENCE);
            canvas.drawColor(mMaskColor);
            canvas.restore();
        }
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setAntiAlias(true);
        canvas.drawPath(mFocusPath, mBorderPaint);
        mFocusPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSaving || null == getDrawable()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:  //��һ���㰴��
                savedMatrix.set(matrix);   //�Ժ�ÿ����Ҫ�任��ʱ�������ڵ�״̬Ϊ�������б任
                pA.set(event.getX(), event.getY());
                pB.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:  //�ڶ����㰴��
                if (event.getActionIndex() > 1) break;
                pA.set(event.getX(0), event.getY(0));
                pB.set(event.getX(1), event.getY(1));
                midPoint.set((pA.x + pB.x) / 2, (pA.y + pB.y) / 2);
                oldDist = spacing(pA, pB);
                savedMatrix.set(matrix);  //�Ժ�ÿ����Ҫ�任��ʱ�������ڵ�״̬Ϊ�������б任
                if (oldDist > 10f) mode = ZOOM_OR_ROTATE;//����֮��ľ������10����Ч
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM_OR_ROTATE) {
                    PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x, event.getY(1) - event.getY(0) + pA.y);
                    double a = spacing(pB.x, pB.y, pC.x, pC.y);
                    double b = spacing(pA.x, pA.y, pC.x, pC.y);
                    double c = spacing(pA.x, pA.y, pB.x, pB.y);
                    if (a >= 10) {
                        double cosB = (a * a + c * c - b * b) / (2 * a * c);
                        double angleB = Math.acos(cosB);
                        double PID4 = Math.PI / 4;
                        //��תʱ��Ĭ�ϽǶ��� 45 - 135 ��֮��
                        if (angleB > PID4 && angleB < 3 * PID4) mode = ROTATE;
                        else mode = ZOOM;
                    }
                }
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - pA.x, event.getY() - pA.y);
                    fixTranslation();
                    setImageMatrix(matrix);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        // ����֮������ maxPostScale ����һ�£���Ҫ�Ƿ�ֹ���ŵ����ʱ����������ͼƬ�����λ��
                        float tScale = Math.min(newDist / oldDist, maxPostScale());
                        if (tScale != 0) {
                            matrix.postScale(tScale, tScale, midPoint.x, midPoint.y);
                            fixScale();
                            fixTranslation();
                            setImageMatrix(matrix);
                        }
                    }
                } else if (mode == ROTATE) {
                    PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x, event.getY(1) - event.getY(0) + pA.y);
                    double a = spacing(pB.x, pB.y, pC.x, pC.y);
                    double b = spacing(pA.x, pA.y, pC.x, pC.y);
                    double c = spacing(pA.x, pA.y, pB.x, pB.y);
                    if (b > 10) {
                        double cosA = (b * b + c * c - a * a) / (2 * b * c);
                        double angleA = Math.acos(cosA);
                        double ta = pB.y - pA.y;
                        double tb = pA.x - pB.x;
                        double tc = pB.x * pA.y - pA.x * pB.y;
                        double td = ta * pC.x + tb * pC.y + tc;
                        if (td > 0) {
                            angleA = 2 * Math.PI - angleA;
                        }
                        rotation = angleA;
                        matrix.set(savedMatrix);
                        matrix.postRotate((float) (rotation * 180 / Math.PI), midPoint.x, midPoint.y);
                        setImageMatrix(matrix);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mode == DRAG) {
                    if (spacing(pA, pB) < 50) {
                        long now = System.currentTimeMillis();
                        if (now - doubleClickTime < 500 && spacing(pA, doubleClickPos) < 50) {
                            doubleClick(pA.x, pA.y);
                            now = 0;
                        }
                        doubleClickPos.set(pA);
                        doubleClickTime = now;
                    }
                } else if (mode == ROTATE) {
                    int rotateLevel = (int) Math.floor((rotation + Math.PI / 4) / (Math.PI / 2));
                    if (rotateLevel == 4) rotateLevel = 0;
                    matrix.set(savedMatrix);
                    matrix.postRotate(90 * rotateLevel, midPoint.x, midPoint.y);
                    if (rotateLevel == 1 || rotateLevel == 3) {
                        int tmp = mRotatedImageWidth;
                        mRotatedImageWidth = mRotatedImageHeight;
                        mRotatedImageHeight = tmp;
                    }
                    fixScale();
                    fixTranslation();
                    setImageMatrix(matrix);
                    sumRotateLevel += rotateLevel;
                }
                mode = NONE;
                break;
        }
        //������ֻ����޷��϶�������
        ViewCompat.postInvalidateOnAnimation(this);
        return true;
    }

    /** ����ͼƬ�����ű� */
    private void fixScale() {
        float imageMatrixValues[] = new float[9];
        matrix.getValues(imageMatrixValues);
        float currentScale = Math.abs(imageMatrixValues[0]) + Math.abs(imageMatrixValues[1]);
        float minScale = getScale(mRotatedImageWidth, mRotatedImageHeight, mFocusWidth, mFocusHeight, true);
        mMaxScale = minScale * MAX_SCALE;

        //��֤ͼƬ��С��ռ���м�Ľ���ռ�
        if (currentScale < minScale) {
            float scale = minScale / currentScale;
            matrix.postScale(scale, scale);
        } else if (currentScale > mMaxScale) {
            float scale = mMaxScale / currentScale;
            matrix.postScale(scale, scale);
        }
    }

    /** ����ͼƬ��λ�� */
    private void fixTranslation() {
        RectF imageRect = new RectF(0, 0, mImageWidth, mImageHeight);
        matrix.mapRect(imageRect);  //��ȡ��ǰͼƬ�������Ժ�ģ�����ڵ�ǰ�ؼ���λ�����򣬳����ؼ����ϱ�Ե�����ԵΪ��
        float deltaX = 0, deltaY = 0;
        if (imageRect.left > mFocusRect.left) {
            deltaX = -imageRect.left + mFocusRect.left;
        } else if (imageRect.right < mFocusRect.right) {
            deltaX = -imageRect.right + mFocusRect.right;
        }
        if (imageRect.top > mFocusRect.top) {
            deltaY = -imageRect.top + mFocusRect.top;
        } else if (imageRect.bottom < mFocusRect.bottom) {
            deltaY = -imageRect.bottom + mFocusRect.bottom;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /** ��ȡ��ǰͼƬ�����������ű� */
    private float maxPostScale() {
        float imageMatrixValues[] = new float[9];
        matrix.getValues(imageMatrixValues);
        float curScale = Math.abs(imageMatrixValues[0]) + Math.abs(imageMatrixValues[1]);
        return mMaxScale / curScale;
    }

    /** ��������֮��ľ��� */
    private float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    /** ��������֮��ľ��� */
    private float spacing(PointF pA, PointF pB) {
        return spacing(pA.x, pA.y, pB.x, pB.y);
    }

    /** ˫�������ķ��� */
    private void doubleClick(float x, float y) {
        float p[] = new float[9];
        matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);
        float minScale = getScale(mRotatedImageWidth, mRotatedImageHeight, mFocusWidth, mFocusHeight, true);
        if (curScale < mMaxScale) {
            //ÿ��˫����ʱ�����ż� minScale
            float toScale = Math.min(curScale + minScale, mMaxScale) / curScale;
            matrix.postScale(toScale, toScale, x, y);
        } else {
            float toScale = minScale / curScale;
            matrix.postScale(toScale, toScale, x, y);
            fixTranslation();
        }
        setImageMatrix(matrix);
    }

    /**
     * @param expectWidth     �����Ŀ��
     * @param exceptHeight    �����ĸ߶�
     * @param isSaveRectangle �Ƿ񰴾������򱣴�ͼƬ
     * @return �ü����Bitmap
     */
    public Bitmap getCropBitmap(int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        if (expectWidth <= 0 || exceptHeight < 0) return null;
        Bitmap srcBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        srcBitmap = rotate(srcBitmap, sumRotateLevel * 90);  //�����level����Ϊ�Ƕȿ��ܲ���90������
        return makeCropBitmap(srcBitmap, mFocusRect, getImageMatrixRect(), expectWidth, exceptHeight, isSaveRectangle);
    }

    /**
     * @param bitmap  Ҫ��ת��ͼƬ
     * @param degrees ѡ��ĽǶȣ���λ �ȣ�
     * @return ��ת���Bitmap
     */
    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != rotateBitmap) {
//                    bitmap.recycle();
                    return rotateBitmap;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * @return ��ȡ��ǰͼƬ��ʾ�ľ�������
     */
    private RectF getImageMatrixRect() {
        RectF rectF = new RectF();
        rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        matrix.mapRect(rectF);
        return rectF;
    }

    /**
     * @param bitmap          ��Ҫ�ü���ͼƬ
     * @param focusRect       �м���Ҫ�ü��ľ�������
     * @param imageMatrixRect ��ǰͼƬ����Ļ�ϵ���ʾ��������
     * @param expectWidth     ϣ����õ�ͼƬ��ȣ����ͼƬ��Ȳ���ʱ������ͼƬ
     * @param exceptHeight    ϣ����õ�ͼƬ�߶ȣ����ͼƬ�߶Ȳ���ʱ������ͼƬ
     * @param isSaveRectangle �Ƿ�ϣ�����������򱣴�ͼƬ
     * @return �ü����ͼƬ��Bitmap
     */
    private Bitmap makeCropBitmap(Bitmap bitmap, RectF focusRect, RectF imageMatrixRect, int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        if (imageMatrixRect == null || bitmap == null){
            return null;
        }
        float scale = imageMatrixRect.width() / bitmap.getWidth();
        int left = (int) ((focusRect.left - imageMatrixRect.left) / scale);
        int top = (int) ((focusRect.top - imageMatrixRect.top) / scale);
        int width = (int) (focusRect.width() / scale);
        int height = (int) (focusRect.height() / scale);

        if (left < 0) left = 0;
        if (top < 0) top = 0;
        if (left + width > bitmap.getWidth()) width = bitmap.getWidth() - left;
        if (top + height > bitmap.getHeight()) height = bitmap.getHeight() - top;

        try {
            bitmap = Bitmap.createBitmap(bitmap, left, top, width, height);
            if (expectWidth != width || exceptHeight != height) {
                bitmap = Bitmap.createScaledBitmap(bitmap, expectWidth, exceptHeight, true);
                if (mStyle == CropImageView.Style.CIRCLE && !isSaveRectangle) {
                    //�����Բ�Σ��ͽ�ͼƬ�ü���Բ��
                    int length = Math.min(expectWidth, exceptHeight);
                    int radius = length / 2;
                    Bitmap circleBitmap = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(circleBitmap);
                    BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Paint paint = new Paint();
                    paint.setShader(bitmapShader);
                    canvas.drawCircle(expectWidth / 2f, exceptHeight / 2f, radius, paint);
                    bitmap = circleBitmap;
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * @param folder          ϣ��������ļ���
     * @param expectWidth     ϣ�������ͼƬ���
     * @param exceptHeight    ϣ�������ͼƬ�߶�
     * @param isSaveRectangle �Ƿ�ϣ�����������򱣴�ͼƬ
     */
    public void saveBitmapToFile(File folder, int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        if (mSaving) return;
        mSaving = true;
        final Bitmap croppedImage = getCropBitmap(expectWidth, exceptHeight, isSaveRectangle);
        Bitmap.CompressFormat outputFormat = Bitmap.CompressFormat.JPEG;
        File saveFile = createFile(folder, "IMG_", ".jpg");
        if (mStyle == CropImageView.Style.CIRCLE && !isSaveRectangle) {
            outputFormat = Bitmap.CompressFormat.PNG;
            saveFile = createFile(folder, "IMG_", ".png");
        }
        final Bitmap.CompressFormat finalOutputFormat = outputFormat;
        final File finalSaveFile = saveFile;
        new Thread() {
            @Override
            public void run() {
                saveOutput(croppedImage, finalOutputFormat, finalSaveFile);
            }
        }.start();
    }

    /** ����ϵͳʱ�䡢ǰ׺����׺����һ���ļ� */
    private File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        try {
            File nomedia = new File(folder, ".nomedia");  //�ڵ�ǰ�ļ��е��´���һ�� .nomedia �ļ�
            if (!nomedia.exists()) nomedia.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    /** ��ͼƬ�����ڱ��� */
    private void saveOutput(Bitmap croppedImage, Bitmap.CompressFormat outputFormat, File saveFile) {
        OutputStream outputStream = null;
        try {
            outputStream = getContext().getContentResolver().openOutputStream(Uri.fromFile(saveFile));
            if (outputStream != null) croppedImage.compress(outputFormat, 90, outputStream);
            Message.obtain(mHandler, SAVE_SUCCESS, saveFile).sendToTarget();
        } catch (IOException ex) {
            ex.printStackTrace();
            Message.obtain(mHandler, SAVE_ERROR, saveFile).sendToTarget();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mSaving = false;
        croppedImage.recycle();
    }

    private static class InnerHandler extends Handler {
        public InnerHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            File saveFile = (File) msg.obj;
            switch (msg.what) {
                case SAVE_SUCCESS:
                    if (mListener != null) mListener.onBitmapSaveSuccess(saveFile);
                    break;
                case SAVE_ERROR:
                    if (mListener != null) mListener.onBitmapSaveError(saveFile);
                    break;
            }
        }
    }

    /** ͼƬ������ɵļ��� */
    private static OnBitmapSaveCompleteListener mListener;

    public interface OnBitmapSaveCompleteListener {
        void onBitmapSaveSuccess(File file);

        void onBitmapSaveError(File file);
    }

    public void setOnBitmapSaveCompleteListener(OnBitmapSaveCompleteListener listener) {
        mListener = listener;
    }

    /** ���ؽ������ */
    public int getFocusWidth() {
        return mFocusWidth;
    }

    /** ���ý����Ŀ�� */
    public void setFocusWidth(int width) {
        mFocusWidth = width;
        initImage();
    }

    /** ��ȡ�����ĸ߶� */
    public int getFocusHeight() {
        return mFocusHeight;
    }

    /** ���ý����ĸ߶� */
    public void setFocusHeight(int height) {
        mFocusHeight = height;
        initImage();
    }

    /** ������Ӱ��ɫ */
    public int getMaskColor() {
        return mMaskColor;
    }

    /** ������Ӱ��ɫ */
    public void setMaskColor(int color) {
        mMaskColor = color;
        invalidate();
    }

    /** ���ؽ����߿���ɫ */
    public int getFocusColor() {
        return mBorderColor;
    }

    /** ���ý����߿���ɫ */
    public void setBorderColor(int color) {
        mBorderColor = color;
        invalidate();
    }

    /** ���ؽ����߿���ƿ�� */
    public float getBorderWidth() {
        return mBorderWidth;
    }

    /** ���ý���߿��� */
    public void setBorderWidth(int width) {
        mBorderWidth = width;
        invalidate();
    }

    /** ���ý�������״ */
    public void setFocusStyle(Style style) {
        this.mStyle = style;
        invalidate();
    }

    /** ��ȡ��������״ */
    public Style getFocusStyle() {
        return mStyle;
    }
}