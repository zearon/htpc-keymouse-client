package com.zearon.tvasistant;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhiyuangong on 17/5/10.
 */
public class TouchPadTouchListener implements View.OnTouchListener, View.OnClickListener {
    private ServerController controller;

    private int status = 0;
    private boolean hscrollOn = false;
    private boolean vscrollOn = false;
    private float downX,  downY;
    private float x,      y;
    private float lastX,  lastY;
    private float deltaX, deltaY;

    private View hscrollBtn, vscrollBtn;

    public TouchPadTouchListener() {
        controller = ServerController.getInstance();
    }

    public TouchPadTouchListener(View vscrollBtn, View hscrollBtn) {
        controller = ServerController.getInstance();
        this.vscrollBtn = vscrollBtn;
        this.hscrollBtn = hscrollBtn;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Log.v("mouse", "Mouse event:" + event.toString());
        updateStatus(event);

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == vscrollBtn) {
            setVScrollMode(null);
        } else if (v == hscrollBtn) {
            setHScrollMode(null);
        }
    }

    private void setVScrollMode(Boolean active) {
        if (active == null) {
            vscrollOn = ! vscrollOn;
        } else {
            vscrollOn = active;
        }
        if (vscrollOn) {
            setHScrollMode(false);
        }
        float buttonAlpha = vscrollOn ? 0.8f : 0.2f;
        vscrollBtn.setAlpha(buttonAlpha);
    }

    private void setHScrollMode(Boolean active) {
        if (active == null) {
            hscrollOn = ! hscrollOn;
        } else {
            hscrollOn = active;
        }
        if (hscrollOn) {
            setVScrollMode(false);
        }
        float buttonAlpha = hscrollOn ? 0.8f : 0.2f;
        hscrollBtn.setAlpha(buttonAlpha);
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
                    if (vscrollOn) {
                        status = 3;
                        verticalScroll(event);
                    } else if (hscrollOn) {
                        status = 4;
                        horizontalScroll(event);
                    } else {
                        status = 2;
                        mouseMove(event);
                    }
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
            case 3:
                // vertical scroll status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    verticalScroll(event);
                }
                break;
            case 4:
                // horizontal scroll status
                if (action == MotionEvent.ACTION_UP) {
                    status = 0;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    horizontalScroll(event);
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

    private void verticalScroll(MotionEvent event) {
        y = event.getY();
        deltaY = y - lastY;
        lastY = y;

        Log.v("mouse", "Vertically scroll: " + Math.round(deltaY) + ", total: " + (y-downY));
        if (deltaY > 0) {
            controller.sendMouseClick(ServerController.MouseButton.WHEEL_DOWN);
        } else {
            controller.sendMouseClick(ServerController.MouseButton.WHEEL_UP);
        }
    }

    private void horizontalScroll(MotionEvent event) {
        x = event.getX();
        deltaX = x - lastX;
        lastX = x;

        Log.v("mouse", "Horizontally scroll: " + Math.round(deltaX) + ", total: " + (x-downX));
        // Shift + mouse wheel
        controller.sendKeyEvent("shift", 1);
        if (deltaX > 0) {
            controller.sendMouseClick(ServerController.MouseButton.WHEEL_DOWN);
        } else {
            controller.sendMouseClick(ServerController.MouseButton.WHEEL_UP);
        }
        controller.sendKeyEvent("shift", 2);
    }
}
