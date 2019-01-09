package com.sss.simpleDropMenu;

import android.util.Log;
import android.view.Gravity;

/**
 * Created by Administrator on 2018/12/20.
 */

public class GravityType {

    public static int getGravity(String gravity) {
        if (SimpleDropMenuGravity.center.getGravity().equals(gravity)) {
            Log.e("tabGravity","center");
            return Gravity.CENTER;
        } else if (SimpleDropMenuGravity.left.getGravity().equals(gravity)) {
            Log.e("tabGravity","left");
            return Gravity.LEFT;
        } else if (SimpleDropMenuGravity.right.getGravity().equals(gravity)) {
            Log.e("tabGravity","right");
            return Gravity.RIGHT;
        }
        Log.e("tabGravity","default");
        return Gravity.LEFT;
    }
}
