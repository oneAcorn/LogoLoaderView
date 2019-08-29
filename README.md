# LogoLoaderView
加载动画效果
效果图,ps:图中不流畅效果是因为录制GIF工具的帧数限制所致
![github](https://github.com/oneAcorn/LogoLoaderView/blob/master/20190829_101138.gif)

使用方法
``` xml
<com.acorn.logoloader.LogoLoaderView
            android:layout_width="100dp"
            android:layout_height="100dp"
            app:highlight_height="10dp"
            app:highlight_width="45dp"
            app:logo_drawable="@drawable/success"
            app:dot_radius="2dp"
            app:duration="2000"
            />
```

所有可选自定义属性
``` xml
 <!--logo加载动画-->
    <declare-styleable name="LogoLoaderView">
        <!--外面的圆环的宽度-->
        <attr name="loop_stroke_width" format="dimension" />
        <!--外面圆环的颜色-->
        <attr name="loop_color" format="color" />
        <!--2个圆点的半径-->
        <attr name="dot_radius" format="dimension" />
        <!--左边圆点的颜色-->
        <attr name="left_dot_color" format="color" />
        <!--右边圆点的颜色-->
        <attr name="right_dot_color" format="color" />
        <!--logo图片-->
        <attr name="logo_drawable" format="reference" />
        <!--高光矩形宽度-->
        <attr name="highlight_width" format="dimension" />
        <!--高光矩形高度-->
        <attr name="highlight_height" format="dimension" />
        <!--高光矩形颜色-->
        <attr name="highlight_color" format="color" />
        <!--高光矩形倾斜角度-->
        <attr name="highlight_angle" format="float" />
        <!--动画时长(毫秒)-->
        <attr name="duration" format="integer" />
    </declare-styleable>
```
