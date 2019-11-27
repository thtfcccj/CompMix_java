package com.cofdet.dap8600.CompMix_java;
//描述UI显示字符串时的相关信息

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import java.util.ArrayList;

public class StringDesc {
    //字符串,直接操作
    public StringBuilder string = new StringBuilder(); //直接操作

    //颜色的RGB值,每两个为一组，"结束位置,颜色", 0xARGB, A值越小越透明，0时使用默认色
    public ArrayList<Integer> color = new ArrayList<>();
    //其它相关标志，"结束位置,标志"
    public ArrayList<Integer> flag = new ArrayList<>();//按位定义为:
    public static final int FLAG_STRIKE_THROUGH = 0x01;//删除线
    public static final int FLAG_UNDER_LINE   = 0x02;//下划线
    public static final int FLAG_BOLD           = 0x04;//加粗
    public static final int FLAG_ITALIC         = 0x08;//斜体
    public static final int FLAG_ITALIC_BOLD    = 0x0C;//加粗 + 斜体

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

    //更新TextView
    public void updateTextView(TextView tv, int colorDefault) {
        //标准显示方式
        if ((color.size() == 0) && (flag.size() == 0)) {
            tv.setTextColor(colorDefault);
            tv.setText(string.toString());
            return;
        }
        //更新字体局部颜色或样式等
        Spannable sp = new SpannableString(string.toString());
        //=========================颜色局部更新处理========================
        int start = 0;
        int end;
        for (int pos = 0; pos < color.size(); pos++) {
            end = color.get(pos++);
            int curColor = color.get(pos);
            if (curColor == 0) curColor = colorDefault;
            sp.setSpan(new ForegroundColorSpan(curColor),
                    start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//含start但不含end
            start = end;
        }
        //=================================标志更新处理======================
        start = 0;
        for (int pos = 0; pos < flag.size(); pos++) {
            end = flag.get(pos++);
            int curFlag = flag.get(pos);
            //删除线
            if ((curFlag & StringDesc.FLAG_STRIKE_THROUGH) != 0) {
                sp.setSpan(new StrikethroughSpan(), start, end,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//含start但不含end
            }
            //下划线
            if ((curFlag & StringDesc.FLAG_UNDER_LINE) != 0) {
                sp.setSpan(new UnderlineSpan(), start, end,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//含start但不含end
            }
            //加粗与斜体
            int typeFace;
            if ((curFlag & StringDesc.FLAG_ITALIC_BOLD) == FLAG_ITALIC_BOLD) //加粗 + 斜体
                typeFace = android.graphics.Typeface.BOLD_ITALIC;
            else if ((curFlag & StringDesc.FLAG_BOLD) != 0)//加粗
                typeFace = android.graphics.Typeface.BOLD;
            else if ((curFlag & StringDesc.FLAG_ITALIC) != 0)//斜体
                typeFace = android.graphics.Typeface.ITALIC;
            else typeFace = 0;
            if(typeFace != 0){
                sp.setSpan(new StyleSpan(typeFace), start, end,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//含start但不含end
            }
            start = end;
        }
        tv.setText(sp);
    }
}
