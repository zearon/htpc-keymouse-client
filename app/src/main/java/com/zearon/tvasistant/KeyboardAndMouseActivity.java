package com.zearon.tvasistant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class KeyboardAndMouseActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
//    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
//            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private Config config = null;
    private ServerController controller = null;
    private SoftKeyboard softKeyboard = null;
    private final StringRingQueue echoLogQueue = new StringRingQueue(35, "服务端回显：\n");

    private View mouseView = null;
    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_keyboard_and_mouse);

        initPreferences();

        /*Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        findViewById(R.id.fullscreen_container).setPadding(0, height, 0, 0);*/

        mVisible = true;
//        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        softKeyboard.initWithKeyButtons(this);

//        View hScrollBtn = findViewById(R.id.toggleHscrollButton);
//        View vScrollBtn = findViewById(R.id.toggleVscrollButton);
//        TouchPadTouchListener touchPadTouchListener = new TouchPadTouchListener(vScrollBtn, hScrollBtn);
//        hScrollBtn.setOnClickListener(touchPadTouchListener);
//        vScrollBtn.setOnClickListener(touchPadTouchListener);

        mouseView = findViewById(R.id.logTextView);
        mouseView.setOnTouchListener(new TouchPadTouchListener(this));

        findViewById((R.id.mouseLeftButton)).setOnTouchListener(new MouseButtonTouchListener(ServerController.MouseButton.LEFT));
        findViewById((R.id.mouseMiddleButton)).setOnTouchListener(new MouseButtonTouchListener(ServerController.MouseButton.MIDDLE));
        findViewById((R.id.mouseRightButton)).setOnTouchListener(new MouseButtonTouchListener(ServerController.MouseButton.RIGHT));

        logTextView = (TextView) findViewById(R.id.logTextView);
        controller.addOnResponseListener(msg -> logToLogTextView(msg));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void initPreferences() {
        // Load default preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_server, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_hardware, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_mouse, true);

        // Init fields
        config = Config.initInstance(this);
        controller = ServerController.getInstance();
        softKeyboard = new SoftKeyboard();
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void logToLogTextView(String msg) {
//        Log.v("main", msg);
        echoLogQueue.add(msg);
        logTextView.post( () -> logTextView.setText(echoLogQueue.toString()) );
//        Log.v("main", msg);
//        Log.v("main", echoLogQueue.toString());
    }

    public void onSettingsBtnClicked(View v) {
        Log.v("main", "Setting button clicked");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onStartBrowserBtnClicked(View v) {
        controller.startBrowser();
    }

    public void onStopBrowserBtnClicked(View v) {
        controller.stopBrowser();
    }

    public void onChangeSoundOutputDeviceBtnClicked(View v) { controller.changeSoundOutputDevice(); }

    public void onOtherCommandBtnClicked(View v) {
        // Not implemented yet.
    }

    public void onTypeTextBtnClicked(View v) {
        EditText editText = (EditText) findViewById(R.id.typeContentEditText);
        String text = editText.getText().toString();
//        editText.setText("");
        Log.v("main", "Type text: " + text);
        controller.sendTextInput(text);
    }

    public void onEnterPressBtnClicked(View v) {
        controller.sendKeyPress("Return");
    }

    public void onClearTextButtonClicked(View v) {
        EditText edit = (EditText) findViewById(R.id.typeContentEditText);
        edit.setText("");
    }

    public void onEscButtonClicked(View v) {
        controller.sendKeyPress("Escape");
    }

}
