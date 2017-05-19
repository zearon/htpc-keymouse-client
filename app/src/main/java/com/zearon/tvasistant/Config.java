package com.zearon.tvasistant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Vector;

/**
 * Created by zhiyuangong on 17/5/9.
 */
public class Config {
    private Activity activity = null;
    SharedPreferences sharedPreferences = null;

    private static Config instance;

    public static synchronized Config initInstance(Activity activity) {
        if (instance == null) {
            instance = new Config(activity);
        }
        return instance;
    }

    public static synchronized Config getInstance() {
        if (instance == null) {
            throw new RuntimeException("Config is not initialized");
        }
        return instance;
    }

    public interface ConfigUpdateListener {
        void onUpdate();
    }

    public Config(Activity activity) {
        this.activity = activity;
        init();
    }

    private Vector<ConfigUpdateListener> updateListeners = new Vector<>();
    private String serverHostname;
    private int serverPort;
    private int soundCardIndex;
    private String outputSoundDeviceName;
    private int scrollThresholdPixel;
    private int moveThresholdPixel;
    private float mouseMoveSensitivity;
    private float mouseFineMoveSensitivity;

    private void init() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        serverHostname = sharedPreferences.getString("server_hostname", "");
        serverPort = Integer.parseInt(sharedPreferences.getString("server_port", ""));
        Log.v("config", "server port: " + serverPort);
        soundCardIndex = Integer.parseInt(sharedPreferences.getString("hardware_sound_card_index", ""));
        Log.v("config", "soundCardIndex: " + soundCardIndex);
        outputSoundDeviceName = sharedPreferences.getString("hardware_output_device_name", "");
        scrollThresholdPixel = Integer.parseInt(sharedPreferences.getString("mouse_scroll_threshold_pixel", ""));
        moveThresholdPixel = Integer.parseInt(sharedPreferences.getString("mouse_move_threshold_pixel", ""));
        mouseMoveSensitivity = Float.parseFloat(sharedPreferences.getString("mouse_move_sensitivity", ""));
        mouseMoveSensitivity = Float.parseFloat(sharedPreferences.getString("mouse_fine_move_sensitivity", ""));
    }

    public void addUpdateListener(ConfigUpdateListener listener) {
        updateListeners.add(listener);
    }

    private void notifyUpdateListeners() {
        for (ConfigUpdateListener listener : updateListeners) {
            listener.onUpdate();
        }
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public void setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
        notifyUpdateListeners();
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
        notifyUpdateListeners();
    }

    public int getSoundCardIndex() {
        return soundCardIndex;
    }

    public void setSoundCardIndex(int soundCardIndex) {
        this.soundCardIndex = soundCardIndex;
        notifyUpdateListeners();
    }

    public String getOutputSoundDeviceName() {
        return outputSoundDeviceName;
    }

    public void setOutputSoundDeviceName(String outputSoundDeviceName) {
        this.outputSoundDeviceName = outputSoundDeviceName;
        notifyUpdateListeners();
    }

    public int getScrollThresholdPixel() {
        return scrollThresholdPixel;
    }

    public void setScrollThresholdPixel(int scrollThreasholdPixel) {
        this.scrollThresholdPixel = scrollThreasholdPixel;
        notifyUpdateListeners();
    }

    public int getMoveThresholdPixel() {
        return moveThresholdPixel;
    }

    public void setMoveThresholdPixel(int moveThresholdPixel) {
        this.moveThresholdPixel = moveThresholdPixel;
        notifyUpdateListeners();
    }

    public float getMouseMoveSensitivity() {
        return mouseMoveSensitivity;
    }

    public void setMouseMoveSensitivity(float mouseMoveSensitivity) {
        this.mouseMoveSensitivity = mouseMoveSensitivity;
        notifyUpdateListeners();
    }

    public float getMouseFineMoveSensitivity() {
        return mouseFineMoveSensitivity;
    }

    public void setMouseFineMoveSensitivity(float mouseFineMoveSensitivity) {
        this.mouseFineMoveSensitivity = mouseFineMoveSensitivity;
        notifyUpdateListeners();
    }
}
