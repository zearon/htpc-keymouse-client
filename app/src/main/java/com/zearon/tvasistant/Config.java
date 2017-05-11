package com.zearon.tvasistant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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

    public Config(Activity activity) {
        this.activity = activity;
        init();
    }

    private String serverHostname;
    private int serverPort;
    private int soundCardIndex;
    private String outputSoundDeviceName;
    private int scrollThresholdPixel;

    private void init() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        serverHostname = sharedPreferences.getString("server_hostname", "");
        serverPort = Integer.parseInt(sharedPreferences.getString("server_port", ""));
        Log.v("config", "server port: " + serverPort);
        soundCardIndex = Integer.parseInt(sharedPreferences.getString("hardware_sound_card_index", ""));
        Log.v("config", "soundCardIndex: " + soundCardIndex);
        outputSoundDeviceName = sharedPreferences.getString("hardware_output_device_name", "");
        scrollThresholdPixel = Integer.parseInt(sharedPreferences.getString("mouse_scroll_threshold_pixel", ""));
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public void setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getSoundCardIndex() {
        return soundCardIndex;
    }

    public void setSoundCardIndex(int soundCardIndex) {
        this.soundCardIndex = soundCardIndex;
    }

    public String getOutputSoundDeviceName() {
        return outputSoundDeviceName;
    }

    public void setOutputSoundDeviceName(String outputSoundDeviceName) {
        this.outputSoundDeviceName = outputSoundDeviceName;
    }

    public int getScrollThresholdPixel() {
        return scrollThresholdPixel;
    }

    public void setScrollThresholdPixel(int scrollThreasholdPixel) {
        this.scrollThresholdPixel = scrollThreasholdPixel;
    }
}
