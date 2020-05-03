package com.cc.camera.view.crop;

import android.graphics.RectF;

/**
 * @author liujc
 * @ClassName IOverlayView
 * @date 2019/11/26
 * @Description 取景框的坐标
 */
public interface IOverlayView {
    /**
     * 获取取景框
     * @param w
     * @param h
     * @return
     */
    RectF getCameraRectF(int w, int h);

    /**
     * 取景框拐角边线颜色值
     * @return
     */
    int getCornerColor();

    /**
     * 取景框边线颜色值
     * @return
     */
    int getBorderColor();

}
