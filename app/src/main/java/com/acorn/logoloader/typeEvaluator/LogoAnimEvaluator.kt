package com.acorn.logoloader.typeEvaluator

import android.animation.TypeEvaluator
import android.graphics.PointF
import android.graphics.RectF

/**
 * Logo估值器
 * Created by acorn on 2019-06-03.
 */
class LogoAnimEvaluator : TypeEvaluator<LogoAnimEvaluator.LogoAnimEntry> {
    override fun evaluate(fraction: Float, startValue: LogoAnimEntry, endValue: LogoAnimEntry): LogoAnimEntry {
        fun average(f1: Float, f2: Float): Float {
            return f1 + ((f2 - f1) * fraction)
        }
        return LogoAnimEntry(
            average(startValue.leftDotAngle, endValue.leftDotAngle),
            average(startValue.rightDotAngle, endValue.rightDotAngle),
            average(startValue.leftAngle, endValue.leftAngle),
            average(startValue.rightAngle, endValue.rightAngle),
            average(startValue.sweepAngle, endValue.sweepAngle),
            average(startValue.logoScale, endValue.logoScale),
            average(startValue.highLightPercent, endValue.highLightPercent)
        )
    }

    class LogoAnimEntry(
        var leftDotAngle: Float,
        var rightDotAngle: Float,
        var leftAngle: Float,
        var rightAngle: Float,
        var sweepAngle: Float,
        var logoScale: Float,
        var highLightPercent: Float
    ) {
        //起始在左边的点的圆心
        var leftDotCx: Float = 0f
        var leftDotCy: Float = 0f
        //起始在右边的点的圆心
        var rightDotCx: Float = 0f
        var rightDotCy: Float = 0f
        //logo图片的绘制范围
        var bitmapRect = RectF(0f, 0f, 0f, 0f)
        var highlightCenter = PointF(0f, 0f)
        //高光矩形
        var highlightRect = RectF(0f, 0f, 0f, 0f)
    }
}