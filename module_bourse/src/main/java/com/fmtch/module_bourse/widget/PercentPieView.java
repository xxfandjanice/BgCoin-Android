package com.fmtch.module_bourse.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.fmtch.module_bourse.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * Description：不带动画的百分比数据类型饼状统计图
 * </p>
 *
 * @author tangzhijie
 */
public class PercentPieView extends View {

    //使用wrap_content时默认的尺寸
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 500;

    /**
     * 中心坐标
     */
    private int centerX;
    private int centerY;

    /**
     * 半径
     */
    private float radius;

    /**
     * 弧形外接矩形
     */
    private RectF rectF;

    /**
     * 中间文本的大小
     */
    private Rect centerTextBound = new Rect();

    /**
     * 数据文本的大小
     */
    private Rect dataTextBound = new Rect();

    /**
     * 扇形画笔
     */
    private Paint mArcPaint;

    /**
     * 中心文本画笔
     */
    private Paint centerTextPaint;

    /**
     * 数据画笔
     */
    private Paint dataPaint;

    /**
     * 数据源数字数组
     */
    private List<BigDecimal> numbers;

    /**
     * 数据源总和
     */
    private BigDecimal sum;

    /**
     * 颜色数组
     */
    private List<Integer> colors;

    private Random random = new Random();

    //自定义属性 Start

    /**
     * 中间字体大小
     */
    private float centerTextSize = 70;

    /**
     * 数据字体大小
     */
    private float dataTextSize = 24;

    /**
     * 中间字体颜色
     */
    private int centerTextColor = Color.BLACK;

    /**
     * 数据字体颜色
     */
    private int dataTextColor = Color.BLACK;

    /**
     * 圆圈的宽度
     */
    private float circleWidth = 100;

    /**
     * 中间文本
     */
    private String centerText;

    //自定义属性 End

    public PercentPieView(Context context) {
        super(context);
        init();
    }

    public PercentPieView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentPieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieView);
        centerTextSize = typedArray.getDimension(R.styleable.PieView_centerTextSize, centerTextSize);
        dataTextSize = typedArray.getDimension(R.styleable.PieView_dataTextSize, dataTextSize);
        circleWidth = typedArray.getDimension(R.styleable.PieView_circleWidth, circleWidth);
        centerTextColor = typedArray.getColor(R.styleable.PieView_centerTextColor, centerTextColor);
        dataTextColor = typedArray.getColor(R.styleable.PieView_dataTextColor, dataTextColor);
        centerText = typedArray.getString(R.styleable.PieView_centerText);
        typedArray.recycle();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mArcPaint = new Paint();
        mArcPaint.setStrokeWidth(circleWidth);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);

        centerTextPaint = new Paint();
        centerTextPaint.setTextSize(centerTextSize);
        centerTextPaint.setAntiAlias(true);
        centerTextPaint.setColor(centerTextColor);

        dataPaint = new Paint();
        dataPaint.setStrokeWidth(2);
        dataPaint.setTextSize(dataTextSize);
        dataPaint.setAntiAlias(true);
        dataPaint.setColor(dataTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (measureWidthMode == MeasureSpec.AT_MOST
                && measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } else if (measureWidthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_WIDTH, measureHeightSize);
        } else if (measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(measureWidthSize, DEFAULT_HEIGHT);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;
        //设置半径为宽高最小值的1/3
        radius = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 3;
        //设置扇形外接矩形
        rectF = new RectF(centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateAndDraw(canvas);
    }

    /**
     * 计算比例并且绘制扇形和数据
     */
    private void calculateAndDraw(Canvas canvas) {
        if (numbers == null || numbers.size() == 0) {
            return;
        }
        if (sum.compareTo(BigDecimal.ZERO) < 1) {
            //总和小于等于0时
            sum = BigDecimal.ONE;
        }
        //扇形开始度数
        int startAngle = 0;
        //所占百分比
        float percent;
        //所占度数
        float angle;
        for (int i = 0; i < numbers.size(); i++) {
            percent = numbers.get(i).divide(sum, 8, BigDecimal.ROUND_DOWN).floatValue();
            //获取百分比在360中所占度数
            if (i == numbers.size() - 1) {//保证所有度数加起来等于360
                angle = 360 - startAngle;
            } else {
                angle = (float) Math.ceil(percent * 360);
            }
            //绘制第i段扇形
            drawArc(canvas, startAngle, angle, colors.get(i));
            startAngle += angle;

            //绘制数据
            if (numbers.get(i).compareTo(BigDecimal.ZERO) < 1) {
                continue;
            }
            //当前弧线中心点相对于纵轴的夹角度数,由于扇形的绘制是从三点钟方向开始，所以加90
//            float arcCenterDegree = 90 + startAngle - angle / 2;
//            drawData(canvas, arcCenterDegree, i, percent);
        }
        //绘制中心数字总和
        if (TextUtils.isEmpty(centerText))
            canvas.drawText(sum + "", centerX - centerTextBound.width() / 2, centerY + centerTextBound.height() / 2, centerTextPaint);
        else
            canvas.drawText(centerText, centerX - centerTextBound.width() / 2, centerY + centerTextBound.height() / 2, centerTextPaint);
    }

    /**
     * 计算每段弧度的中心坐标
     *
     * @param degree 当前扇形中心度数
     */
    private float[] calculatePosition(float degree) {
        //由于Math.sin(double a)中参数a不是度数而是弧度，所以需要将度数转化为弧度
        //而Math.toRadians(degree)的作用就是将度数转化为弧度
        //sin 一二正，三四负 sin（180-a）=sin(a)
        //扇形弧线中心点距离圆心的x坐标
        float x = (float) (Math.sin(Math.toRadians(degree)) * radius);
        //cos 一四正，二三负
        //扇形弧线中心点距离圆心的y坐标
        float y = (float) (Math.cos(Math.toRadians(degree)) * radius);

        //每段弧度的中心坐标(扇形弧线中心点相对于view的坐标)
        float startX = centerX + x;
        float startY = centerY - y;

        float[] position = new float[2];
        position[0] = startX;
        position[1] = startY;
        return position;
    }

    /**
     * 绘制数据
     *
     * @param canvas  画布
     * @param degree  第i段弧线中心点相对于纵轴的夹角度数
     * @param i       第i段弧线
     * @param percent 数据百分比
     */
    private void drawData(Canvas canvas, float degree, int i, float percent) {
        //弧度中心坐标
        float startX = calculatePosition(degree)[0];
        float startY = calculatePosition(degree)[1];

        //拼接百分比并获取文本大小
        DecimalFormat df = new DecimalFormat("0.0");
        String percentString = df.format(percent * 100) + "%";
        dataPaint.getTextBounds(percentString, 0, percentString.length(), dataTextBound);

        //绘制百分比数据，20为纵坐标偏移量
        canvas.drawText(percentString,
                startX - dataTextBound.width() / 2,
                startY + dataTextBound.height() * 2 - 20,
                dataPaint);
    }

    /**
     * 绘制扇形
     *
     * @param canvas     画布
     * @param startAngle 开始度数
     * @param angle      扇形的度数
     * @param color      颜色
     */
    private void drawArc(Canvas canvas, float startAngle, float angle, int color) {
        mArcPaint.setColor(color);
        //+0.5是为了让每个扇形之间没有间隙
        if (angle != 0) {
            angle += 0.5f;
        }
        canvas.drawArc(rectF, startAngle, angle, false, mArcPaint);
    }

    /**
     * 生成随机颜色
     */
    private int randomColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.rgb(red, green, blue);
    }

    /**
     * 设置数据(使用随机颜色)
     *
     * @param numbers 数字数组
     */
    public void setData(List<BigDecimal> numbers) {
        if (numbers == null || numbers.size() == 0) {
            return;
        }
        this.numbers = numbers;
        colors = new ArrayList<>();
        sum = BigDecimal.ZERO;
        for (int i = 0; i < this.numbers.size(); i++) {
            //计算总和
            sum = sum.add(numbers.get(i));
            //随机颜色
            colors.add(randomColor());
        }
        //计算总和数字的宽高
        if (!TextUtils.isEmpty(centerText))
            centerTextPaint.getTextBounds(centerText, 0, centerText.length(), centerTextBound);
        else
            centerTextPaint.getTextBounds(sum + "", 0, (sum + "").length(), centerTextBound);
        invalidate();
    }

    /**
     * 设置数据(自定义颜色)
     *
     * @param numbers 数字数组
     * @param colors  颜色数组
     */
    public void setData(List<BigDecimal> numbers, List<Integer> colors) {
        if (numbers == null || numbers.size() == 0
                || colors == null || colors.size() == 0) {
            return;
        }
        this.numbers = numbers;
        this.colors = colors;
        sum = BigDecimal.ZERO;
        for (int i = 0; i < this.numbers.size(); i++) {
            //计算总和
            sum = sum.add(numbers.get(i));
        }
        //计算总和数字的宽高
        if (!TextUtils.isEmpty(centerText))
            centerTextPaint.getTextBounds(centerText, 0, centerText.length(), centerTextBound);
        else
            centerTextPaint.getTextBounds(sum + "", 0, (sum + "").length(), centerTextBound);
        invalidate();
    }
}
