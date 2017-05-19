package com.zearon.tvasistant;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by zhiyuangong on 17/5/10.
 */
public class TouchPadTouchListener implements View.OnTouchListener {
    private ServerController controller;
    private Config  config;

    private int status = 0;
    private float downX,        downY;
    private float x,            y;
    private float lastX,        lastY;
    private float deltaX,       deltaY;
    private float deltaXTotal,  deltaYTotal;
    private float accumulativeDeltaX, accumulativeDeltaY;
    private float scrollThresholdPixel, moveThresholdPixel, mouseMoveSensitivity, mouseFineMoveSensitivity;

    private TextView logTextView;
    private ToggleButton fineMouseMovingButton;

    public TouchPadTouchListener(Activity activity) {
        controller = ServerController.getInstance();
        config = Config.getInstance();
        config.addUpdateListener(() -> this.updateConfig());
        logTextView = (TextView) activity.findViewById(R.id.logTextView);
        fineMouseMovingButton = (ToggleButton) activity.findViewById(R.id.fineMouseMovingButton);
        updateConfig();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        updateStatus(event);

        return true;
    }

    public void updateConfig() {
        scrollThresholdPixel = (float) config.getScrollThresholdPixel();
        moveThresholdPixel = (float) config.getMoveThresholdPixel();
        mouseMoveSensitivity = config.getMouseMoveSensitivity();
        mouseFineMoveSensitivity = config.getMouseFineMoveSensitivity();
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
                // first finger pressed down status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                    mouseLeftClick();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    status = 2;
                    mouseMove(event);
                } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    status = 3;
                    accumulativeDeltaX = accumulativeDeltaY = 0;
                    Log.v("mouse", "Mouse scroll mode - Two finger");
                }
                break;
            case 2:
                // One finger moving status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                    updateXY(event);
                    if (consideredAsNotMoving()) {
                        mouseLeftClick();
                    }
                } else if (action == MotionEvent.ACTION_MOVE) {
                    mouseMove(event);
                } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    status = 3;
                    accumulativeDeltaX = accumulativeDeltaY = 0;
                    Log.v("mouse", "Mouse scroll mode - Two finger");
                }
                break;
            case 3:
                // Two fingers moving status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                    updateXY(event);
                    if (consideredAsNotMoving()) {
                        mouseRightClick();
                    }
                } else if (action == MotionEvent.ACTION_MOVE) {
                    scrollMove(event);
                }
                break;
        }
    }

    private void mouseLeftClick() {
        Log.v("mouse", "Mouse left click");
        controller.sendMouseClick(ServerController.MouseButton.LEFT);
    }

    private void mouseRightClick() {
        Log.v("mouse", "Mouse right click");
        controller.sendMouseClick(ServerController.MouseButton.RIGHT);
    }

    private void updateXY(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        deltaX = x - lastX;
        deltaY = y - lastY;
        deltaXTotal = x - downX;
        deltaYTotal = y - downY;
        lastX = x;
        lastY = y;
    }

    private boolean consideredAsNotMoving() {
        return Math.abs(deltaXTotal) < moveThresholdPixel && Math.abs(deltaYTotal) < moveThresholdPixel;
    }

    private void mouseMove(MotionEvent event) {
        updateXY(event);
        float sensitivity = fineMouseMovingButton.isChecked() ? mouseFineMoveSensitivity : mouseMoveSensitivity;
        Log.v("mouse", "Mouse move: (" + Math.round(deltaX / sensitivity) + "," + Math.round(deltaY / sensitivity) + ") sensitivity:" + +sensitivity);
        controller.sendMouseMove(Math.round(deltaX / sensitivity), Math.round(deltaY / sensitivity));
    }

    private void scrollMove(MotionEvent event) {
        updateXY(event);

        if (Math.abs(deltaY) > Math.abs(deltaX)) {
            // Vertical scroll
            accumulateDeltaForScroll(deltaY, true);
        } else {
            // horizontal scroll
            accumulateDeltaForScroll(deltaX, false);
        }
    }

    private void accumulateDeltaForScroll(float delta, boolean verticalScroll) {
        if (verticalScroll) {
            accumulativeDeltaY += delta;
            while (accumulativeDeltaY > scrollThresholdPixel) {
                accumulativeDeltaY -= scrollThresholdPixel;
                Log.v("mouse", "Scroll down");
                controller.sendMouseClick(ServerController.MouseButton.WHEEL_DOWN);
            }
            while (accumulativeDeltaY < -scrollThresholdPixel) {
                accumulativeDeltaY += scrollThresholdPixel;
                Log.v("mouse", "Scroll up");
                controller.sendMouseClick(ServerController.MouseButton.WHEEL_UP);
            }
        }

        else {
            accumulativeDeltaX += delta;
            controller.sendKeyEvent("shift", 1);
            while (accumulativeDeltaX > scrollThresholdPixel) {
                accumulativeDeltaX -= scrollThresholdPixel;
                Log.v("mouse", "Scroll right");
                controller.sendMouseClick(ServerController.MouseButton.WHEEL_DOWN);
            }
            while (accumulativeDeltaX < -scrollThresholdPixel) {
                accumulativeDeltaX += scrollThresholdPixel;
                Log.v("mouse", "Scroll left");
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
