package com.acorn.logoloader

import android.animation.ArgbEvaluator
import android.animation.Keyframe
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.acorn.logoloader.typeEvaluator.ColorEvaluator
import com.acorn.logoloader.typeEvaluator.LogoAnimEvaluator
import com.acorn.logoloader.typeEvaluator.LogoAnimEvaluator.LogoAnimEntry
import com.acorn.logoloader.utils.getPositionByAngle


class LogoLoaderView : View {
    private val dotPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val loopPaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val arcPaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    private val logoPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    //圆心
    private var cx: Float = 0f
    private var cy: Float = 0f
    //半径
    private var radius: Float = 0f
    private var logoAnim: ValueAnimator? = null
    //动画节点
    private val node0: Float = 0f
    private val node1: Float = 0.06f
    private val node2: Float = 0.35f
    private val node3: Float = 0.47f
    private val node4: Float = 0.59f
    private val node5: Float = 0.94f
    private val node6: Float = 1f
    //当前动画节点
    private var curFraction = 0f
    private var curAnimEntry = LogoAnimEntry(0f, 180f, 0f, 0f, 0f, 0f, 0f)
    //小球半径
    private var dotRadius = 0f
    //弧形范围
    private lateinit var arcBound: RectF
    //logo图片
    private var logoBitmap: Bitmap? = null
    //高光起始角度
    private var highlightAngle = 45f
    //高光矩形高度
    private var highlightHeight = 0f
    //高光矩形宽度
    private var highlightWidth = 0f
    private var duration: Long = 0

    private var curColor = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(context, attr, defStyleAttr) {
        context.obtainStyledAttributes(attr, R.styleable.LogoLoaderView, defStyleAttr, R.style.LogoLoaderView).apply {
            setLoopStrokeWidth(getDimension(R.styleable.LogoLoaderView_loop_stroke_width, 0f))
            setLoopColor(getColor(R.styleable.LogoLoaderView_loop_color, 0))
            setDotRadius(getDimension(R.styleable.LogoLoaderView_dot_radius, 0f))
            setDotColor(getColor(R.styleable.LogoLoaderView_dot_color, 0))
            setLogoDrawable(getDrawable(R.styleable.LogoLoaderView_logo_drawable))
            setHighlightWidth(getDimension(R.styleable.LogoLoaderView_highlight_width, 0f))
            setHighlightHeight(getDimension(R.styleable.LogoLoaderView_highlight_height, 0f))
            setHighlightColor(getColor(R.styleable.LogoLoaderView_highlight_color, 0))
            setHighlightAngle(getFloat(R.styleable.LogoLoaderView_highlight_angle, 0f))
            setDuration(getInteger(R.styleable.LogoLoaderView_duration, 0))
            recycle()
        }
    }

    fun setLoopStrokeWidth(strokeWidth: Float) {
        loopPaint.strokeWidth = strokeWidth
        invalidate()
    }

    fun setLoopColor(color: Int) {
        loopPaint.color = color
        invalidate()
    }

    fun setDotRadius(radius: Float) {
        dotRadius = radius
        arcPaint.strokeWidth = dotRadius * 2f
        invalidate()
    }

    fun setDotColor(color: Int) {
        dotPaint.color = color
        arcPaint.color = color
        invalidate()
    }

    fun setLogoDrawable(drawable: Drawable?) {
        if (null == drawable)
            return
        logoBitmap = (drawable as BitmapDrawable).bitmap
        invalidate()
    }

    fun setHighlightWidth(width: Float) {
        highlightWidth = width
        invalidate()
    }

    fun setHighlightHeight(height: Float) {
        highlightHeight = height
        invalidate()
    }

    fun setHighlightColor(color: Int) {
        highlightPaint.color = color
        invalidate()
    }

    fun setHighlightAngle(angle: Float) {
        highlightAngle = angle
        if (radius != 0f) {
            initAnim()
        }
        invalidate()
    }

    fun setDuration(duration: Int) {
        this.duration = duration.toLong()
        if (radius != 0f) {
            initAnim()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isRunning())
            return
        fun drawArc() {
            arcPaint.color = curColor
            canvas!!.drawArc(arcBound, curAnimEntry.leftAngle, curAnimEntry.sweepAngle, false, arcPaint)
            canvas.drawArc(arcBound, curAnimEntry.rightAngle, curAnimEntry.sweepAngle, false, arcPaint)
        }

        fun drawHighlight() {
            canvas!!.save()
            canvas.rotate(-highlightAngle, curAnimEntry.highlightCenter.x, curAnimEntry.highlightCenter.y)
            canvas.drawRect(curAnimEntry.highlightRect, highlightPaint)
            canvas.restore()
        }

        canvas!!.drawCircle(cx, cy, radius, loopPaint)
        if (null != logoBitmap)
            canvas.drawBitmap(logoBitmap!!, null, curAnimEntry.bitmapRect, logoPaint)
        when (curFraction) {
            in node0..node1, in node5..node6 -> {
                canvas.drawCircle(curAnimEntry.leftDotCx, curAnimEntry.leftDotCy, dotRadius, dotPaint)
                canvas.drawCircle(curAnimEntry.rightDotCx, curAnimEntry.rightDotCy, dotRadius, dotPaint)
            }
            in node1..node2, in node4..node5 -> {
                drawArc()
                drawHighlight()
            }
            in node2..node4 -> {
                drawArc()
            }
        }
    }


    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        //记录动画是否运行
        if (!hasWindowFocus && isRunning()) {
            stop()
        } else if (hasWindowFocus) {
            //自动开启
            start()
        }
    }

    private fun initAnim() {
        val keyframe0: Keyframe = Keyframe.ofObject(node0, LogoAnimEntry(0f, 180f, 0f, 0f, 0f, 0f, 0.2f))
        val keyframe1: Keyframe = Keyframe.ofObject(node1, LogoAnimEntry(180f, 360f, 180f, 0f, 2f, 0.8f, 0.2f))
        val keyframe2: Keyframe = Keyframe.ofObject(node2, LogoAnimEntry(180f, 360f, 210f, 30f, 120f, 0.8f, 0.8f))
        val keyframe3: Keyframe = Keyframe.ofObject(node3, LogoAnimEntry(180f, 360f, 360f, 180f, 2f, 0.8f, 0.2f))
        val keyframe4: Keyframe = Keyframe.ofObject(node4, LogoAnimEntry(180f, 360f, 390f, 210f, 120f, 0.8f, 0.2f))
        val keyframe5: Keyframe = Keyframe.ofObject(node5, LogoAnimEntry(180f, 0f, 540f, 360f, 2f, 0.8f, 0.8f))
        val keyframe6: Keyframe = Keyframe.ofObject(node6, LogoAnimEntry(360f, 180f, 0f, 0f, 0f, 0f, 0.2f))
        logoAnim =
            ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofKeyframe(
                    "Logo",
                    keyframe0,
                    keyframe1,
                    keyframe2,
                    keyframe3,
                    keyframe4,
                    keyframe5,
                    keyframe6
                ),
                PropertyValuesHolder.ofObject(
                    "arcColor",
                    ArgbEvaluator(),
                    Color.parseColor("#ff00ff00"),
                    Color.parseColor("#ffff00ff")
                )
            ).apply {
                setEvaluator(LogoAnimEvaluator())
//        logoAnim?.interpolator=LinearInterpolator()
                duration = this@LogoLoaderView.duration
                repeatCount = ValueAnimator.INFINITE
//        logoAnim.repeatMode
                val dotRadius: Float = radius / 2f
                val originLeftDotCx: Float = cx - (radius / 2f)
                val originLeftDotCy: Float = cy;
                val originRightDotCx: Float = cx + (radius / 2f)
                val originRightDotCy: Float = cy;
                addUpdateListener { animation ->
                    curAnimEntry = (animation.getAnimatedValue("Logo") as LogoAnimEntry).apply {
                        val leftPoint: PointF =
                            getPositionByAngle(curAnimEntry.leftDotAngle, dotRadius, originLeftDotCx, originLeftDotCy)
                        leftDotCx = leftPoint.x
                        leftDotCy = leftPoint.y
                        val rightPoint: PointF =
                            getPositionByAngle(
                                curAnimEntry.rightDotAngle,
                                dotRadius,
                                originRightDotCx,
                                originRightDotCy
                            )
                        rightDotCx = rightPoint.x
                        rightDotCy = rightPoint.y

                        //计算logo缩放
                        val scaleOffset = radius * curAnimEntry.logoScale
                        bitmapRect = RectF(cx - scaleOffset, cy - scaleOffset, cx + scaleOffset, cy + scaleOffset)

                        //计算高光矩形
                        val highlightPassLength = radius * 2f * curAnimEntry.highLightPercent
                        val angle = if (highlightPassLength < radius) 180f + highlightAngle else highlightAngle
                        val distance = Math.abs(highlightPassLength - radius)
                        highlightCenter = getPositionByAngle(angle, distance, cx, cy)
                        highlightRect = RectF(
                            curAnimEntry.highlightCenter.x - highlightWidth / 2f,
                            curAnimEntry.highlightCenter.y - highlightHeight / 2f,
                            curAnimEntry.highlightCenter.x + highlightWidth / 2f,
                            curAnimEntry.highlightCenter.y + highlightHeight / 2f
                        )
                    }
                    curFraction = animation.animatedFraction

                    curColor = animation.getAnimatedValue("arcColor") as Int
                    invalidate()
                }
            }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cx = w / 2f
        cy = h / 2f
        //半径留下一点空白,以免绘制越界
        radius = Math.min(w, h) / 2f - loopPaint.strokeWidth
        arcBound = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        initAnim()
    }

    private fun ensureAnim() {
        if (null == logoAnim) {
            initAnim()
        }
    }

    fun start() {
        ensureAnim()
        logoAnim?.start()
    }

    fun stop() {
        logoAnim?.removeAllUpdateListeners()
        logoAnim?.removeAllListeners()
        logoAnim?.cancel()
        logoAnim = null
        invalidate()
    }

    fun isRunning(): Boolean = logoAnim?.isRunning ?: false
}