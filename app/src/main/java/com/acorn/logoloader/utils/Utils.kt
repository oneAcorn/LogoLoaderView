package com.acorn.logoloader.utils

import android.graphics.PointF


/**
 * 根据角度angle获取在此角与圆心连线中距离圆心为distanceToCenter远的点
 *
 * @param angle            以圆心为起点水平向右作边a,与此边a按顺时针方向的夹角
 * @param distanceToCenter 距离圆心的距离
 * @param cX               圆心
 * @param cY               圆心
 */
fun getPositionByAngle(angle: Float, distanceToCenter: Float, cX: Float, cY: Float): PointF {
    val res = PointF()
    val x1: Double
    val y1: Double
    //角度转成弧度
    val radians = angle2radians(angle)
    x1 = cX + distanceToCenter * Math.cos(radians)
    y1 = cY + distanceToCenter * Math.sin(radians)
    res.set(x1.toFloat(), y1.toFloat())
    return res
}

/**
 * 角度转弧度
 *
 * @param angle
 * @return
 */
fun angle2radians(angle: Float): Double {
    return angle / 180f * Math.PI
}

/**
 * 弧度转角度
 *
 * @param radians
 * @return
 */
fun radians2angle(radians: Double): Double {
    return 180f * radians / Math.PI
}