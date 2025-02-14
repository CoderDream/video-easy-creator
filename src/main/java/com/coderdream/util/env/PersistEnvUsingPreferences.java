package com.coderdream.util.env;

import java.util.prefs.Preferences;

public class PersistEnvUsingPreferences {
    public static void main(String[] args) {
        Preferences userPrefs = Preferences.userRoot().node("Environment");
        userPrefs.put("MY_PERSISTENT_PREF", "HelloWorld");
        System.out.println("已添加 MY_PERSISTENT_PREF 环境变量");
    }
}
