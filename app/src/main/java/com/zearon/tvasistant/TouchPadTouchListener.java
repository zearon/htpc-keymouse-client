package com.zearon.tvasistant;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhiyuangong on 17/5/10.
 */
public class TouchPadTouchListener implements View.OnTouchListener {
    private ServerController controller;

    private int status = 0;
    private float downX,  downY;
    private float x,      y;
    private float lastX,  lastY;
    private float deltaX, deltaY;

    public TouchPadTouchListener() {
        controller = ServerController.getInstance();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Log.v("mouse", "Mouse event:" + event.toString());
        updateStatus(event);

        return true;
    }

    private void updateStatus(MotionEvent event) {
        int action = event.getAction();

        switch (status) {
            case 0:
                // init status
                if (action == MotionEvent.ACTION_DOWN) {
                    status = 1;
                    lastX = downX = event.getX();
                    lastY = downY = event.getY();
                }
                break;
            case 1:
                // mouse down status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                    Log.v("mouse", "Mouse click");
                    controller.sendMouseClick(ServerController.MouseButton.LEFT);
                } else if (action == MotionEvent.ACTION_MOVE) {
                    status = 2;

                    mouseMove(event);
                }
                break;
            case 2:
                // mouse moving status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    mouseMove(event);
                }
                break;
        }
    }

    private void mouseMove(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        deltaX = x - lastX;
        deltaY = y - lastY;
        lastX = x;
        lastY = y;

        Log.v("mouse", "Mouse move: (" + Math.round(deltaX) + "," + Math.round(deltaY) + "), total: (" + (x-downX) + "," + (y-downY));
        controller.sendMouseMove(Math.round(deltaX), Math.round(deltaY));
    }
}
