package com.zearon.tvasistant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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
    }

    public void init() {
        sharedPreferences = activity.getSharedPreferences("tvassitant_config", Context.MODE_PRIVATE);
    }

    public String getServerHost() {
        return "172.23.120.8";
    }

    public int getServerPort() {
        return 8888;
    }
}
