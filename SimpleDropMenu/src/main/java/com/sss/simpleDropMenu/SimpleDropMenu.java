package com.sss.simpleDropMenu;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sss.simpleDropMenu.bean.TabMenuBean;

import java.util.List;


/**
 * 魔改原版的下拉条件筛选菜单，使用更方便，对外暴露了事件回调及增加了一堆方法,且扩展了一种使用方式
 * 原版地址https://github.com/dongjunkun/DropDownMenu
 * Created by Administrator on 2018/9/22.
 */

/*
    XML:
      <com.sss.simpleDropMenu.SimpleDropMenu
            android:id="@+id/dropDownMenu"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:dropMenuBackgroundColor="@color/white"
            app:dropMenuDistanceOfTabAndImage="3dp"
            app:dropMenuDividerColor="#00000000"
            app:dropMenuMaskColor="#cc333333"
            app:dropMenuMenuHeightPercent="0.5"
            app:dropMenuMenuParentHeight="40dp"
            app:dropMenuSelectedIcon="@mipmap/arrow_down"
            app:dropMenuTextSelectedColor="@color/app_red"
            app:dropMenuTextSize="13sp"
            app:dropMenuTextUnselectedColor="@color/text_666666"
            app:dropMenuUnderLineHeight="1dp"
            app:dropMenuUnderlineColor="@color/gray_background_line"
            app:dropMenuUnselectedIcon="@mipmap/arrow_right"
            app:dropMenuTabIsWrapContent="true"/>


    代码固定使用方式，可直接copy,之后将下拉时要显示出来的控件即可修改为你想要的控件即可
    RecyclerView recyclerView = new RecyclerView(getContext());//下拉的时候要显示出来的控件
    List<View> views = new ArrayList<>();
    views.add(recyclerView);
    views.add(dropDownMenu.createHolderView());
    views.add(dropDownMenu.createHolderView());
    List<TabMenuBean> tabMenuBeanList = new ArrayList<>();
    tabMenuBeanList.add(new TabMenuBean("全部", true));
    tabMenuBeanList.add(new TabMenuBean("最新", false));
    tabMenuBeanList.add(new TabMenuBean("最热", false));
    dropDownMenu.setDropDownMenu(tabMenuBeanList, views,null);
    dropDownMenu.setOnDropDownMenuCallBack(new SimpleDropMenu.OnDropDownMenuCallBack() {
        @Override
        public void onTabClick(int position, String title) {

        }

        @Override
        public void onTabMenuStatusChanged(int position, boolean isMenuOpen) {

        }
    });
 */


@SuppressWarnings("ALL")
public class SimpleDropMenu extends LinearLayout {
    //顶部tab中一个个子tab的位置
    private String tabGravity = "left";
    //顶部tab中一个个子tab中的内容位置
    private String tabChildGravity = "center";
    //顶部菜单布局
    private LinearLayout tabMenuView;
    //底部容器，包含popupMenuViews，maskView
    private FrameLayout containerView;
    //弹出菜单父布局
    private FrameLayout popupMenuViews;
    //遮罩半透明View，点击可关闭DropDownMenu
    private View maskView;
    //tabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;
    //分割线颜色
    private int dividerColor = 0xffcccccc;
    //tab选中颜色
    private int textSelectedColor = 0xff890c85;
    //tab未选中颜色
    private int textUnselectedColor = 0xff111111;
    //遮罩颜色
    private int maskColor = 0x88888888;
    //顶部菜单布局高度（包括下划线）
    private int menuParentHeight = 40;
    //tab字体大小
    private int menuTextSize = 14;
    //下划线高度
    private int underLineHeight = 1;
    //tab与图片的距离(如果>-1将tab设置成match_parent并设置weight=1且正常设置margin,否则将tab设置成wrap_content)
    private int distanceOfTabAndImage = 5;
    //tab选中图标
    private int menuSelectedIcon;
    //tab未选中图标
    private int menuUnselectedIcon;
    //菜单视图区域高度百分比
    private float menuHeighPercent = 0.5f;
    //顶部菜单内容父布局是否需要WrapContent模式,true将采用match_parent
    private boolean dropMenuTabIsWrapContent = false;

    private OnDropDownMenuCallBack onDropDownMenuCallBack;

    public void setOnDropDownMenuCallBack(OnDropDownMenuCallBack onDropDownMenuCallBack) {
        this.onDropDownMenuCallBack = onDropDownMenuCallBack;
    }

    public LinearLayout getTabMenuView() {
        return tabMenuView;
    }

    public SimpleDropMenu(Context context) {
        super(context, null);
    }

    public SimpleDropMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleDropMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.VERTICAL);

        //为DropDownMenu添加自定义属性
        int menuBackgroundColor = 0xffffffff;
        int underlineColor = 0xffcccccc;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        underlineColor = a.getColor(R.styleable.DropDownMenu_dropMenuUnderlineColor, underlineColor);//下划线颜色
        dividerColor = a.getColor(R.styleable.DropDownMenu_dropMenuDividerColor, dividerColor);// //分割线颜色
        textSelectedColor = a.getColor(R.styleable.DropDownMenu_dropMenuTextSelectedColor, textSelectedColor);//tab选中颜色
        textUnselectedColor = a.getColor(R.styleable.DropDownMenu_dropMenuTextUnselectedColor, textUnselectedColor);//tab未选中颜色
        menuBackgroundColor = a.getColor(R.styleable.DropDownMenu_dropMenuBackgroundColor, menuBackgroundColor);//tab 背景颜色
        maskColor = a.getColor(R.styleable.DropDownMenu_dropMenuMaskColor, maskColor); //遮罩颜色，一般是半透明
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_dropMenuTextSize, menuTextSize); //tab字体大小
        menuSelectedIcon = a.getResourceId(R.styleable.DropDownMenu_dropMenuSelectedIcon, menuSelectedIcon);//tab选中状态图标
        menuUnselectedIcon = a.getResourceId(R.styleable.DropDownMenu_dropMenuUnselectedIcon, menuUnselectedIcon);//tab未选中状态图标
        menuHeighPercent = a.getFloat(R.styleable.DropDownMenu_dropMenuMenuHeightPercent, menuHeighPercent);//菜单的最大高度，根据屏幕高度的百分比设置
        underLineHeight = a.getDimensionPixelSize(R.styleable.DropDownMenu_dropMenuUnderLineHeight, underLineHeight);//下划线高度
        menuParentHeight = a.getDimensionPixelSize(R.styleable.DropDownMenu_dropMenuMenuParentHeight, menuParentHeight);//顶部菜单布局高度（包括下划线）
        distanceOfTabAndImage = a.getDimensionPixelSize(R.styleable.DropDownMenu_dropMenuDistanceOfTabAndImage, distanceOfTabAndImage);//tab与图片的距离(如果>-1将tab设置成match_parent并设置weight=1且正常设置margin,否则将tab设置成wrap_content)
        dropMenuTabIsWrapContent = a.getBoolean(R.styleable.DropDownMenu_dropMenuTabIsWrapContent, dropMenuTabIsWrapContent);//顶部tab中一个个tab是否需要WrapContent模式,如果是，自适应菜单按钮中文字或图标的宽度，如果不是，则顶部的一个个菜单按钮按权重=1排列
        tabGravity = a.getString(R.styleable.DropDownMenu_dropMenuTabGravity) == null ? tabGravity : a.getString(R.styleable.DropDownMenu_dropMenuTabGravity);//顶部tab中一个个tab的位置
        tabChildGravity = a.getString(R.styleable.DropDownMenu_dropMenuTabChildGravity) == null ? tabChildGravity : a.getString(R.styleable.DropDownMenu_dropMenuTabChildGravity);//顶部tab中一个个tab中子tab的位置
        a.recycle();

        //为tabMenuView添加上划线
        View upLine = new View(getContext());
        upLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, underLineHeight));
        upLine.setBackgroundColor(underlineColor);
        addView(upLine, 0);

        //初始化tabMenuView并添加到tabMenuView
        tabMenuView = new LinearLayout(context);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setGravity(GravityType.getGravity(tabGravity));
        tabMenuView.setBackgroundColor(menuBackgroundColor);
        tabMenuView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, menuParentHeight));
        addView(tabMenuView, 1);

        //为tabMenuView添加下划线
        View underLine = new View(getContext());
        underLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, underLineHeight));
        underLine.setBackgroundColor(underlineColor);
        addView(underLine, 2);

        //初始化containerView并将其添加到DropDownMenu
        containerView = new FrameLayout(context);
        containerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(containerView, 3);

    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts
     * @param popupViews
     * @param contentView
     */
    public void setDropDownMenu(List<TabMenuBean> tabTexts, List<View> popupViews, View contentView) {
        if (tabTexts.size() != popupViews.size()) {
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal popupViews.size()");
        }

        for (int i = 0; i < tabTexts.size(); i++) {
            //添加Tab
            addTab(tabTexts.get(i), i);
            //添加分割线
            if (i < tabTexts.size() - 1) {
                View view = new View(getContext());
                view.setLayoutParams(new LayoutParams(dpTpPx(0.5f), ViewGroup.LayoutParams.MATCH_PARENT));
                view.setBackgroundColor(dividerColor);
                tabMenuView.addView(view);
            }
        }
        if (contentView != null) {
            containerView.addView(contentView, 0);
        }
        maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(maskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu(current_tab_position);
            }
        });
        containerView.addView(maskView, contentView == null ? 0 : 1);
        maskView.setVisibility(GONE);
        if (containerView.getChildAt(contentView == null ? 1 : 2) != null) {
            containerView.removeViewAt(contentView == null ? 1 : 2);
        }

        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getScreenSize(getContext()).y * menuHeighPercent)));
        popupMenuViews.setVisibility(GONE);
        popupMenuViews.setBackgroundColor(Color.TRANSPARENT);
        containerView.addView(popupMenuViews, contentView == null ? 1 : 2);
        for (int i = 0; i < popupViews.size(); i++) {
            popupViews.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            popupMenuViews.addView(popupViews.get(i), i);
        }

    }


    /**
     * 添加顶部菜单tab
     *
     * @param tabMenuBean
     * @param i
     */
    private void addTab(final TabMenuBean tabMenuBean, final int i) {
        //tab父容器（包含TextView和ImageView）
        LinearLayout tabParent = new LinearLayout(getContext());
        tabParent.setOrientation(HORIZONTAL);
        tabParent.setGravity(GravityType.getGravity(tabChildGravity));
        tabParent.setTag(i);//绑定索引
        //添加tab
        TextView tab = new TextView(getContext());
        tab.setSingleLine();
        tab.setEllipsize(TextUtils.TruncateAt.END);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        tab.setTextColor(textUnselectedColor);
        tab.setText(tabMenuBean.title);
        tab.setPadding(dpTpPx(5), dpTpPx(12), dpTpPx(5), dpTpPx(12));
        tabParent.addView(tab);
        //添加点击事件
        tabParent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabMenuBean.canDropDown) {
                    switchMenu(i);
                } else {
                    if (onDropDownMenuCallBack != null) {
                        onDropDownMenuCallBack.onTabClick(i, tabMenuBean.title);
                    }
                }
            }
        });
        //添加图标
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(menuUnselectedIcon);
        imageView.setPadding(0, 0, dpTpPx(0), 0);
        tabParent.addView(imageView);
        tabMenuView.addView(tabParent);
        if (dropMenuTabIsWrapContent) {
            tabParent.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            tabParent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
        }

        //设置位置参数
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(distanceOfTabAndImage, 3, 0, 0);
        imageView.setLayoutParams(layoutParams);
        if (tabMenuBean.canDropDown) {
            if (distanceOfTabAndImage > -1) {
                tab.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            } else {
                tab.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            }
        } else {
            tab.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            imageView.setVisibility(GONE);
        }
    }


    /**
     * 获取tab父容器
     *
     * @param position
     * @return
     */
    private LinearLayout getTabParent(int position) {
        if (position > -1 && position < tabMenuView.getChildCount() - 1) {
            for (int i = 0; i < tabMenuView.getChildCount(); i++) {
                if (tabMenuView.getChildAt(i).getTag() != null) {//判断当前view是否被绑定过索引（分割线不会绑定索引）
                    if (position == (Integer) tabMenuView.getChildAt(i).getTag()) {//获取之前绑定的索引
                        return (LinearLayout) tabMenuView.getChildAt(i);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 创建一个占位View,只有一个功能，就是用来分辨哪一个Tab是启用状态
     *
     * @return
     */
    public View createHolderView() {
        View view = new View(getContext());
        view.setEnabled(false);
        return view;
    }

    /**
     * 菜单是否处于打开状态
     *
     * @return
     */
    public boolean isMenuOpen() {
        return current_tab_position != -1;
    }

    /**
     * 处于打开状态菜单的索引
     *
     * @return
     */
    public int openMenuPosition() {
        return current_tab_position;
    }

    /**
     * 改变tab文字颜色
     *
     * @param position
     * @param color
     */
    public void setMenuTabColor(int position, int color) {
        LinearLayout linearLayout = getTabParent(position);
        if (linearLayout != null) {
            ((TextView) linearLayout.getChildAt(0)).setTextColor(color);
        }
    }

    /**
     * 重置tab文字颜色
     */
    public void resetMenuTabColor() {
        for (int i = 0; i < tabMenuView.getChildCount(); i++) {
            setMenuTabColor(i, textUnselectedColor);
        }
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setMenuTabText(int position, String text) {
        LinearLayout linearLayout = getTabParent(position);
        if (linearLayout != null) {
            ((TextView) linearLayout.getChildAt(0)).setText(text);
        }
    }

    /**
     * DP2PX
     *
     * @param value
     * @return
     */
    public int dpTpPx(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, Resources.getSystem().getDisplayMetrics());
    }


    /**
     * 关闭所有菜单
     */
    public void closeAllMenu() {
        for (int i = 0; i < tabMenuView.getChildCount(); i++) {
            closeMenu(i);
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu(int position) {
        LinearLayout linearLayout = getTabParent(position);
        if (linearLayout != null) {
            ((TextView) linearLayout.getChildAt(0)).setTextColor(textUnselectedColor);
            ((ImageView) linearLayout.getChildAt(1)).setImageResource(menuUnselectedIcon);
        }
        Animation animation=AnimationUtils.loadAnimation(getContext(), R.anim.menu_out);
        popupMenuViews.startAnimation(animation);
        Animation maskAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.mask_out);
        maskAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                maskView.setVisibility(View.GONE);
                closePopmenuView();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        maskView.startAnimation(maskAnimation);
        if (onDropDownMenuCallBack != null) {
            onDropDownMenuCallBack.onTabMenuStatusChanged(current_tab_position, false);
        }
        current_tab_position = -1;
    }


    /**
     * 打开菜单
     */
    public void openMenu(int position) {
        current_tab_position = position;
        LinearLayout linearLayout = getTabParent(position);
        if (linearLayout != null) {
            ((TextView) linearLayout.getChildAt(0)).setTextColor(textSelectedColor);
            ((ImageView) linearLayout.getChildAt(1)).setImageResource(menuSelectedIcon);
        }
        popupMenuViews.setVisibility(View.VISIBLE);
        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.menu_in));
        maskView.setVisibility(VISIBLE);
        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.mask_in));
        openPopmenuView(position);
        if (onDropDownMenuCallBack != null) {
            onDropDownMenuCallBack.onTabMenuStatusChanged(current_tab_position, true);
        }
    }


    /**
     * 打开popMenuView
     *
     * @param position
     */
    private void openPopmenuView(int position) {
        closePopmenuView();
        popupMenuViews.getChildAt(position).setVisibility(View.VISIBLE);
    }

    /**
     * 关闭某一项popMenuView
     *
     * @param position
     */
    @Deprecated
    private void closePopmenuViewByPosition(int position) {
        closePopmenuView();
        popupMenuViews.getChildAt(position).setVisibility(View.GONE);
    }

    /**
     * 关闭所有popMenuView
     *
     * @param position
     */
    private void closePopmenuView() {
        for (int i = 0; i < popupMenuViews.getChildCount(); i++) {
            popupMenuViews.getChildAt(i).setVisibility(GONE);
        }
    }

    /**
     * 切换菜单
     *
     * @param position
     */
    public void switchMenu(int position) {
        if (current_tab_position == -1) {
            openMenu(position);
        } else {
            if (current_tab_position == position) {
                closeMenu(position);
            } else {
                closeAllMenu();
                openMenu(position);

            }


        }
    }

    /**
     * 获取屏幕尺寸
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return new Point(display.getWidth(), display.getHeight());
        } else {
            Point point = new Point();
            display.getSize(point);
            return point;
        }
    }


    public interface OnDropDownMenuCallBack {

        /**
         * 顶部菜单被点击（此功能是在没有下拉的情况下，换句话说就是顶部的菜单直接当做了一个tab来用）
         *
         * @param position 当前点击菜单的索引
         * @param title    当前点击菜单的标题
         */
        void onTabClick(int position, String title);

        /**
         * 菜单状态被改变（打开或关闭）
         *
         * @param position   当前点击菜单的索引
         * @param isMenuOpen 当前点击菜单的状态
         */
        void onTabMenuStatusChanged(int position, boolean isMenuOpen);
    }

}
