package com.cc.camera.view.crop;

import android.graphics.Color;
import android.graphics.RectF;

/**
 * @author liujc
 * @ClassName RectangleOverlayView
 * @date 2019/11/26
 * @Description 长方形的取景框
 */
public class RectangleOverlayView implements IOverlayView {
    @Override
    public RectF getCameraRectF(int w, int h) {
        RectF frameRect = new RectF();
        frameRect.left = (int) (w * 0.10);
        frameRect.top = (int) (h * 0.15);
        frameRect.right = w - frameRect.left;
        frameRect.bottom = frameRect.top + (int) (w * 0.25);
        return frameRect;
    }

    @Override
    public int getCornerColor() {
        return Color.parseColor("#06FB0B");
    }

    @Override
    public int getBorderColor() {
//        return Color.parseColor("#06FB0B");
        return Color.TRANSPARENT;
    }
}
