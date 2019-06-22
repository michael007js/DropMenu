 ** **DropMenu** ** 

No picture u say a j8!

少啰嗦，先看效果

![闭嘴看图](https://github.com/michael007js/DropMenu/blob/master/images/dropMenu.gif "闭嘴看图")


 **项目介绍** 
 一个使用比较方便的下拉选择菜单，魔改于原版的DropDownMenu下拉条件筛选菜单，对原有的一些使用不方便的方法做了一堆扩展，增加了一些比较实用的扩展


 **使用说明** 
    
  导入本库依赖
  
  implementation 'com.michael007js:SimpleDropMenu:1.0.4'
  
  
  本库有两种使用方式，第一种为正常的XML可见模式，第二种为代码addView模式，两种使用方式的唯一区别就在于dropMenu.setDropDownMenu(tabMenuBeanList, views, null)这个方法，addView模式不需要画XML，只需要new出你想要的控件add进去即可，
   [详情请参考](https://github.com/michael007js/DropMenu/blob/master/app/src/main/java/com/sss/dropmenu/Example2.java)
 
  由于两种使用方式代码几乎一样，这里以第一种常用的XML模式介绍：
     
     1.在画出你的布局： 

       <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">


        <include layout="@layout/layout_list"
            android:layout_marginTop="41dp"
            android:layout_height="match_parent"
            android:layout_width="match_parent" />

        <com.sss.simpleDropMenu.SimpleDropMenu
            android:layout_marginTop="1dp"
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
            app:dropMenuTabIsWrapContent="false"
            app:dropMenuTabGravity="center"/>
    </FrameLayout>

     
     
     2. 代码中关联各种配置
     第一项菜单View,这里用图方便用了TextView,你可以替换成RecyclerVie或其他View
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
        
        //设置下拉选择菜单
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
  
你没看错，就这样搞定了


属性介绍：

        dropMenuBackgroundColor="@color/white"                            tab背景
        dropMenuDistanceOfTabAndImage="5dp"                               tab与图片的距离(如果>-1将tab设置成match_parent并设置weight=1且正常设置margin,否则将tab设置成wrap_content)
        dropMenuDividerColor="#333333"                                    tab之间分割线的颜色，如不需要可以设置透明
        dropMenuMaskColor="#cc333333"                                     遮罩的颜色（类似dialiog弹出时灰色背景的那一层）
        dropMenuMenuHeightPercent="0.5"                                   下拉菜单的高度（取的屏幕高度乘以这个数值，推荐0.5，也就是屏幕高度的一半）
        dropMenuMenuParentHeight="40dp"                                   顶部菜单布局高度（包括下划线）
        dropMenuSelectedIcon="@mipmap/item_play_xia"                      被选中的菜单图标
        dropMenuTextSelectedColor="@color/app_red"                        被选中的菜单文字颜色
        dropMenuTextSize="13sp"                                           tab字体大小
        dropMenuTextUnselectedColor="@color/text_666666"                  未选中的菜单文字颜色
        dropMenuUnderLineHeight="1dp"                                     tab菜单下的一条下划线高度
        dropMenuUnderlineColor="@color/gray"                              tab菜单下的一条下划线颜色
        dropMenuUnselectedIcon="@mipmap/item_play_you"                    未选中的菜单图标
        app:dropMenuTabIsWrapContent="false"                              顶部tab中一个个tab是否需要WrapContent模式,如果是，自适应菜单按钮中文字或图标的宽度，如果不是，则顶部的一个个菜单按钮按权重=1排列
        app:dropMenuTabGravity="center"                                   顶部tab中一个个tab的位置
        app:dropMenuTabChildGravity="center"                              顶部tab中一个个tab中子tab的位置
        
     

            



**混淆添加**

-keep class com.sss.simpleDropMenu.bean**{*;}


**历史版本**

V1.0.4：调整菜单关闭效果

V1.0.3：修复dropMenuTabIsWrapContent在代码中未调用的bug

V1.0.2：加入三个XML属性dropMenuTabGravity、dropMenuTabChildGravity、dropMenuTabIsWrapContent

V1.0.1：新增公开方法

V1.0.0：初始版本上线

 over

 By SSS

 [原版地址](https://github.com/dongjunkun/DropDownMenu)



