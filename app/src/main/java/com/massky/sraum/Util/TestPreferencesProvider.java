package com.massky.sraum.Util;

import com.zlm.libs.preferences.PreferencesProvider;

/**
 * Created by zhangliangming on 2018-04-29.
 */

public class TestPreferencesProvider extends PreferencesProvider {
    @Override
    public String getAuthorities() {
        return "com.massky.sraum.TestPreferencesProvider";
    }
}
