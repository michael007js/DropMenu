package com.sss.simpleDropMenu;

/**
 * Created by Administrator on 2018/9/22.
 */

public class TabMenuBean {
    public String title;//标题
    public boolean canDropDown;//是否可下拉

    public TabMenuBean(String title, boolean canDropDown) {
        this.title = title;
        this.canDropDown = canDropDown;
    }
}

