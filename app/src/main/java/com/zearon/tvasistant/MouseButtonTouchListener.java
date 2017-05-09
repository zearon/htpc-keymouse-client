package com.zearon.tvasistant;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhiyuangong on 17/5/10.
 */
public class MouseButtonTouchListener implements View.OnTouchListener {
    private ServerController.MouseButton mouseButton;
    private ServerController controller;

    public MouseButtonTouchListener(ServerController.MouseButton mouseButton) {
        this.mouseButton = mouseButton;
        controller = ServerController.getInstance();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            controller.sendMouseEvent(mouseButton, 1);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            controller.sendMouseEvent(mouseButton, 2);
        }

        return true;
    }
}
