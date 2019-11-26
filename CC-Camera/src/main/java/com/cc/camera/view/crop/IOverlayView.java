package com.cc.camera.view.crop;

import android.graphics.RectF;

/**
 * @author liujc
 * @ClassName IOverlayView
 * @date 2019/11/26
 * @Description 取景框的坐标
 */
public interface IOverlayView {
    RectF getCameraRectF(int w, int h);
}
