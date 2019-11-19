package com.cofdet.dap8600.utils;
//描述UI显示字符串时的相关信息

import java.util.ArrayList;

public class StringDesc {
    //字符串,直接操作
    public StringBuilder string; //直接操作

    //颜色的RGB值,每两个为一组，"结束位置,颜色"
    private ArrayList<Integer> color = new ArrayList<>();
    //其它相关标志，如字体，加粗，倾斜等(暂不支持),每两个为一组，"结束位置,标志"
    private ArrayList<Integer> flag = new ArrayList<>();

    //清除所有
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
