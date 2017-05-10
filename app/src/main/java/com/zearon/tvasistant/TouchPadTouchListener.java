package com.zearon.tvasistant;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhiyuangong on 17/5/10.
 */
public class TouchPadTouchListener implements View.OnTouchListener {
    private ServerController controller;
    private Config  config;

    private int status = 0;
    private float downX,  downY;
    private float x,      y;
    private float lastX,  lastY;
    private float deltaX, deltaY;
    private float accumulativeDeltaX, accumulativeDeltaY;
    private float scrollThresholdPixel;

    private TextView logTextView;

    public TouchPadTouchListener(Activity activity) {
        controller = ServerController.getInstance();
        config = Config.getInstance();
        logTextView = (TextView) activity.findViewById(R.id.logTextView);
        updateConfig();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        updateStatus(event);

        return true;
    }

    public void updateConfig() {
        scrollThresholdPixel = (float) config.getScrollThresholdPixel();
    }

    private void updateStatus(MotionEvent event) {
        int action = event.getActionMasked();

        switch (status) {
            case 0:
                // init status
                if (action == MotionEvent.ACTION_DOWN) {
                    status = 1;
                }
                lastX = downX = event.getX();
                lastY = downY = event.getY();
                break;
            case 1:
                // mouse down status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                    log("mouse", "Mouse click");
                    controller.sendMouseClick(ServerController.MouseButton.LEFT);
                } else if (action == MotionEvent.ACTION_MOVE) {
                    status = 2;
                    mouseMove(event);
                } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    status = 3;
                    accumulativeDeltaX = accumulativeDeltaY = 0;
                    log("mouse", "Mouse scroll mode - Two finger");
                }
                break;
            case 2:
                // mouse moving status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    mouseMove(event);
                } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    status = 3;
                    accumulativeDeltaX = accumulativeDeltaY = 0;
                    log("mouse", "Mouse scroll mode - Two finger");
                }
                break;
            case 3:
                // scroll status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    scrollMove(event);
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

        log("mouse", "Mouse move: (" + Math.round(deltaX) + "," + Math.round(deltaY) + "), total: (" + (x-downX) + "," + (y-downY));
        controller.sendMouseMove(Math.round(deltaX), Math.round(deltaY));
    }

    private void scrollMove(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        deltaX = x - lastX;
        deltaY = y - lastY;
        lastX = x;
        lastY = y;

        if (Math.abs(deltaY) > Math.abs(deltaX)) {
            // Vertical scroll
            accumulateDeltaForScroll(deltaY, true);
        } else {
            // horizontal scroll
            accumulateDeltaForScroll(deltaX, false);
        }
    }

    private void accumulateDeltaForScroll(float delta, boolean y) {
        if (y) {
            accumulativeDeltaY += delta;
            while (accumulativeDeltaY > scrollThresholdPixel) {
                accumulativeDeltaY -= scrollThresholdPixel;
                log("mouse", "Scroll down");
                controller.sendMouseClick(ServerController.MouseButton.WHEEL_DOWN);
            }
            while (accumulativeDeltaY < -scrollThresholdPixel) {
                accumulativeDeltaY += scrollThresholdPixel;
                log("mouse", "Scroll up");
                controller.sendMouseClick(ServerController.MouseButton.WHEEL_UP);
            }
        }

        else {
            accumulativeDeltaX += delta;
            controller.sendKeyEvent("shift", 1);
            while (accumulativeDeltaX > scrollThresholdPixel) {
                accumulativeDeltaX -= scrollThresholdPixel;
                log("mouse", "Scroll right");
                controller.sendMouseClick(ServerController.MouseButton.WHEEL_DOWN);
            }
            while (accumulativeDeltaX < -scrollThresholdPixel) {
                accumulativeDeltaX += scrollThresholdPixel;
                log("mouse", "Scroll left");
                controller.sendMouseClick(ServerController.MouseButton.WHEEL_UP);
            }
            controller.sendKeyEvent("shift", 2);
        }
    }

    private void log(String tag, String msg) {
        Log.v(tag, msg);
//        logTextView.setText(msg + "\n" + logTextView.getText());
    }
}
