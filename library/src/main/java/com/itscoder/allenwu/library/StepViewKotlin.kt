package com.itscoder.allenwu.library

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*

class StepViewKotlin : View {
    private val TAG = "StepView"
    private val START_STEP = 1

    private var mSteps = ArrayList<String>()
    private var mCurrentStep = START_STEP

    private var mCircleColor: Int = 0
    private var mTextColor: Int = 0
    private var mSelectedColor: Int = 0
    private var mFillRadius: Int = 0
    private var mStrokeWidth: Int = 0
    private var mLineWidth: Int = 0
    private var mDrawablePadding: Int = 0
    private var mPaint: Paint

    /**
     * 选择带attrs参数的构造函数
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StepView, 0, R.style.StepView)
        mCircleColor = ta.getColor(R.styleable.StepView_svCircleColor, 0)
        mTextColor = ta.getColor(R.styleable.StepView_svTextColor, 0)
        mSelectedColor = ta.getColor(R.styleable.StepView_svSelectedColor, 0)
        mFillRadius = ta.getDimensionPixelSize(R.styleable.StepView_svFillRadius, 0)
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.StepView_svStrokeWidth, 0)
        mLineWidth = ta.getDimensionPixelSize(R.styleable.StepView_svLineWidth, 0)
        mDrawablePadding = ta.getDimensionPixelSize(R.styleable.StepView_svDrawablePadding, 0)
        val textSize = ta.getDimensionPixelSize(R.styleable.StepView_svTextSize, 0)
        ta.recycle()

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        mPaint.textSize = textSize.toFloat()
        mPaint.textAlign = Paint.Align.CENTER

        // 方便预览
        if (isInEditMode) {
            val steps = arrayOf("Step 1", "Step 2", "Step 3")
            setSteps(Arrays.asList(*steps))
        }
    }

    /**
     * steps: List<String>? steps不能为null
     */
    fun setSteps(steps: List<String>) {
        mSteps.clear()
        mSteps.addAll(steps)
        selectedStep(START_STEP)
    }

    /**
     * 一步一步选中Step
     */
    fun selectedStep(step: Int) {
        val selected = if (step < START_STEP)
            START_STEP
        else
            if (step > mSteps.size) mSteps.size else step
        mCurrentStep = selected
        invalidate()
    }

    /**
     * 获取当前的Step
     */
    fun getCurrentStep(): Int {
        Log.i(TAG,"mCurrentStep,${mCurrentStep}")
        return mCurrentStep
    }

    /**
     * 获取Step总数
     */
    fun getStepCount(): Int {
        Log.i(TAG,"mSteps.size,${mSteps.size}")
        return mSteps.size
    }

    /**
     * 复写View的测量过程
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        var height = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        if (heightMode == View.MeasureSpec.AT_MOST) {
            val fontHeight = Math.ceil((mPaint.descent() - mPaint.ascent()).toDouble()).toInt()
            height = (paddingTop + paddingBottom + (mFillRadius + mStrokeWidth) * 2
                    + mDrawablePadding + fontHeight)
        }
        setMeasuredDimension(width, height)
    }

    /**
     * 复写View的绘制过程
     */
    override fun onDraw(canvas: Canvas) {
        val stepSize = mSteps.size
        if (stepSize == 0) {
            return
        }
        val width = width

        val ascent = mPaint.ascent()
        val descent = mPaint.descent()
        val fontHeight = Math.ceil((descent - ascent).toDouble()).toInt()
        val halfFontHeightOffset = -(ascent + descent).toInt() / 2
        val bigRadius = mFillRadius + mStrokeWidth
        val startCircleY = paddingTop + bigRadius
        val childWidth = width / stepSize
        for (i in 1..mCurrentStep) {
            drawableStep(canvas, i, halfFontHeightOffset, fontHeight, bigRadius,
                    childWidth * i - childWidth / 2, startCircleY,true)
        }

        for (i in mCurrentStep + 1..stepSize){
            drawableStep(canvas, i, halfFontHeightOffset, fontHeight, bigRadius,
                    childWidth * i - childWidth / 2, startCircleY,false)
        }

        val halfLineLength = childWidth / 2 - bigRadius
        for (i in 1 until mCurrentStep) {
            val lineCenterX = childWidth * i
            drawableLine(canvas, lineCenterX - halfLineLength,
                    lineCenterX + halfLineLength, startCircleY,true)
        }

        for (i in mCurrentStep until stepSize) {
            val lineCenterX = childWidth * i
            drawableLine(canvas, lineCenterX - halfLineLength,
                    lineCenterX + halfLineLength, startCircleY,false)
        }

    }

    /**
     *  绘制选中时的圆圈
     */
    private fun drawableStep(canvas: Canvas, step: Int, halfFontHeightOffset: Int, fontHeight: Int,
                             bigRadius: Int, circleCenterX: Int, circleCenterY: Int,hasColor: Boolean) {
        val text = mSteps[step - 1]
        val isSelected = step == mCurrentStep

        // 选中时绘制样式
        if (hasColor) {
            mPaint.strokeWidth = mStrokeWidth.toFloat()
            mPaint.style = Paint.Style.STROKE
            mPaint.color = mCircleColor
            canvas.drawCircle(circleCenterX.toFloat(), circleCenterY.toFloat(), (mFillRadius + mStrokeWidth / 2).toFloat(), mPaint)

            mPaint.color = mSelectedColor
            mPaint.style = Paint.Style.FILL
            canvas.drawCircle(circleCenterX.toFloat(), circleCenterY.toFloat(), mFillRadius.toFloat(), mPaint)
        } else {  // 未选中时绘制样式
            mPaint.style = Paint.Style.FILL
            mPaint.color = mCircleColor
            canvas.drawCircle(circleCenterX.toFloat(), circleCenterY.toFloat(), bigRadius.toFloat(), mPaint)
        }

        // 绘制Step下面的字体，也分两种情况
        mPaint.isFakeBoldText = true
        mPaint.color = Color.WHITE
        val number = step.toString()
        canvas.drawText(number, circleCenterX.toFloat(), (circleCenterY + halfFontHeightOffset).toFloat(), mPaint)

        mPaint.isFakeBoldText = false
        mPaint.color = if (isSelected) mSelectedColor else mTextColor
        canvas.drawText(text, circleCenterX.toFloat(),
                (circleCenterY + bigRadius + mDrawablePadding + fontHeight / 2).toFloat(), mPaint)
    }

    /**
     * 绘制圈与圈之间的连线
     */
    private fun drawableLine(canvas: Canvas, startX: Int, endX: Int, centerY: Int,hasColor: Boolean) {
        if (hasColor){
            mPaint.color = mSelectedColor
        }else{
            mPaint.color = mCircleColor
        }
        mPaint.strokeWidth = mLineWidth.toFloat()
        canvas.drawLine(startX.toFloat(), centerY.toFloat(), endX.toFloat(), centerY.toFloat(), mPaint)
    }
}

