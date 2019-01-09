package com.sss.simpleDropMenu;

/**
 * Created by Administrator on 2018/12/20.
 */

public enum SimpleDropMenuGravity {
    center("center"),
    left("left"),
    right("right");


    private String gravity;

    SimpleDropMenuGravity(String gravity) {
        this.gravity = gravity;
    }

    public String getGravity() {
        return gravity;
    }
}
