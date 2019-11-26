package com.cc.camera.view.crop;

import android.graphics.RectF;

/**
 * @author liujc
 * @ClassName SquareOverlayView
 * @date 2019/11/26
 * @Description 正方形取景框
 */
public class SquareOverlayView implements IOverlayView {
    @Override
    public RectF getCameraRectF(int w, int h) {
        RectF frameRect = new RectF();
        frameRect.left = (int) (w * 0.25);
        frameRect.top = (int) (h * 0.2);
        frameRect.right = w - frameRect.left;
        frameRect.bottom = frameRect.top + frameRect.right - frameRect.left;
        return frameRect;
    }
}
