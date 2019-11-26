package com.cofdet.dap8600.CompMix_java;
//描述UI显示字符串时的相关信息

import java.util.ArrayList;

public class StringDesc {
    //字符串,直接操作
    public StringBuilder string = new StringBuilder(); //直接操作

    //颜色的RGB值,每两个为一组，"结束位置,颜色", 0xARGB, A值越小越透明，0时使用默认色
    public ArrayList<Integer> color = new ArrayList<>();
    //其它相关标志，"结束位置,标志"
    public ArrayList<Integer> flag = new ArrayList<>();//按位定义为:
    public static final int FLAG_STRIKE_THROUGH = 0x01;//删除线
    //public static final int FLAG_UNDER_LINE     = 0x02;//下划线
    //清除所有Underline
    public void clear() {
        string.delete(0, string.length());
        color.clear();
        flag.clear();
    }
    //附加颜色
    public void appendColor(int curColor) {
        color.add(string.length());
        color.add(curColor);
    }
    //附加标志
    public void appendFlag(int curColor) {
        flag.add(string.length());
        flag.add(curColor);
    }
}
