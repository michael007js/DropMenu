package com.sss.dropmenu;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sss.simpleDropMenu.SimpleDropMenu;
import com.sss.simpleDropMenu.bean.TabMenuBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/22.
 */

public class Example1 extends Activity {
    private SimpleDropMenu dropMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example1);
        dropMenu = findViewById(R.id.dropDownMenu);

        //第一项菜单View,这里用图方便用了TextView,你可以替换成RecyclerVie或其他View
        TextView textView = new TextView(this);
        textView.setBackgroundColor(getResources().getColor(R.color.white));
        textView.setText("我是标签,你可以把我替换成你想要的一切View");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShortToast(getApplicationContext(),"标签被点击");
            }
        });


        //第二项菜单View,这里用图方便用了ImageView,你可以替换成RecyclerView或其他View
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher);
        List<View> views = new ArrayList<>();
        views.add(textView);
        views.add(imageView);
        views.add(dropMenu.createHolderView());
        List<TabMenuBean> tabMenuBeanList = new ArrayList<>();


        tabMenuBeanList.add(new TabMenuBean("标签下拉", true));//是否可下拉,如果不可下拉将触发点击事件
        tabMenuBeanList.add(new TabMenuBean("图片下拉", true));
        tabMenuBeanList.add(new TabMenuBean("我没有下拉", false));
        dropMenu.setDropDownMenu(tabMenuBeanList, views, null);
        dropMenu.setOnDropDownMenuCallBack(new SimpleDropMenu.OnDropDownMenuCallBack() {
            @Override
            public void onTabClick(int position, String title) {
                dropMenu.setMenuTabColor(position,getResources().getColor(R.color.app_red));
                ToastUtils.showShortToast(getApplicationContext(), title + "---" + position);
            }

            @Override
            public void onTabMenuStatusChanged(int position, boolean isMenuOpen) {
               ToastUtils.showShortToast(getApplicationContext(), "第" + position + "项菜单被" + (isMenuOpen ? "打开" : "关闭"));
            }
        });

    }




}
