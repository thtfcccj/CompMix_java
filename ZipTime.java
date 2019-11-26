package com.cofdet.dap8600.CompMix_java;
//压缩时间格式模块实现-java版,全部为静态实现

import java.util.Date;

public class ZipTime{
    //压缩时间格式将时间压缩到一个32位变量中,各位定义为:
    //年范围:
    public static final int YEAR_START = 2019;            //定义起始年
    public static final int YEAR_END = (YEAR_START + 63); //定义结束年

    //秒: 范围0-59
    private static final int SEC_SHIFT = 0;
    private static final int SEC_MASK =    (0x3f << SEC_SHIFT);
    //分: 范围0-59
    private static final int MIN_SHIFT =    6;
    private static final int MIN_MASK =    (0x3f << MIN_SHIFT);
    //时: 范围0-23
    private static final int HOUR_SHIFT =   12;
    private static final int HOUR_MASK =    (0x1f << HOUR_SHIFT);
    //日: 范围1-31
    private static final int DATE_SHIFT =   17;
    private static final int DATE_MASK =    (0x1f << DATE_SHIFT);
    //月: 范围1-12
    private static final int MOUTH_SHIFT =   22;
    private static final int MOUTH_MASK =    (0x0f << MOUTH_SHIFT);
    //年：为相对值，范围0-64,起始年由回调函数决定
    private static final int YEAR_SHIFT =   26;
    private static final int YEAR_MASK =    (0x3f << YEAR_SHIFT);

    //绝对年转换为相对年
    private static int yearA2R(int absoluteData){
        if(absoluteData < YEAR_START) return YEAR_START;
        if(absoluteData > YEAR_END) return YEAR_END;
        return absoluteData - YEAR_START;
    }

    //相对年转换为绝对年
    private static int yearR2A(int  relativeData){
        if(relativeData < 0 ) return YEAR_START;
        if(relativeData > (YEAR_END - YEAR_START)) return YEAR_END;
        return relativeData + YEAR_START;
    }

    //得到当前时间
    public static int getCurTime(){
        Date date =  new Date();
        int zipTime = date.getSeconds() << SEC_SHIFT;
        zipTime |= date.getMinutes() << MIN_SHIFT;
        zipTime |= date.getHours() << HOUR_SHIFT;
        zipTime |= date.getDay() << DATE_SHIFT;
        zipTime |= date.getMonth() << MOUTH_SHIFT;
        zipTime |= yearA2R(date.getYear()) << YEAR_SHIFT;
        return zipTime;
    }

    //---------------------------压缩时间转换为中文格式字符------------------------
    //格式为XXXX-XX-XX XX:XX:XX的掩码,即年-月-日 时:分:秒
    //标志按位定义为:0bit:显示年前两位, 1bit:显示年后两位,2bit:打印月
    //3bit:打印日, 4bit:打印时, 5bit,打印分, 6bit:允许显示秒, 7bit:是否含结束字符
    public static void toStringCh(StringBuilder stringBuilder, int  ZipTime, int Flag) {
        int data;
        if((Flag & 0x03) != 0) {//有年份定义时
            data = yearR2A(ZipTime >> YEAR_SHIFT);
            if ((Flag & 0x01) != 0) {
                stringBuilder.append((data / 1000) + '0');
                data %= 1000;
                stringBuilder.append((data / 100) + '0');
                Flag |= 0x02;//强制
            }
            data %= 100;
            if ((Flag & 0x02) != 0) {
                stringBuilder.append((data / 10) + '0');
                stringBuilder.append((data % 10) + '0');
                stringBuilder.append('-');
            }
        }
        if((Flag & 0x04) != 0){//月
            data = (ZipTime & MOUTH_MASK) >> MOUTH_SHIFT;
            stringBuilder.append((data / 10) + '0');
            stringBuilder.append((data % 10) + '0');
            stringBuilder.append('-');
        }
        if((Flag & 0x08) != 0){//日
            data = (ZipTime & DATE_MASK) >> DATE_SHIFT;
            stringBuilder.append((data / 10) + '0');
            stringBuilder.append((data % 10) + '0');
            stringBuilder.append(' ');
        }
        if((Flag & 0x10) != 0){//时
            data = (ZipTime & HOUR_MASK) >> HOUR_SHIFT;
            stringBuilder.append((data / 10) + '0');
            stringBuilder.append((data % 10) + '0');
            stringBuilder.append(':');
        }
        if((Flag & 0x20) != 0){//分
            data = (ZipTime & MIN_MASK) >> MOUTH_SHIFT;
            stringBuilder.append((data / 10) + '0');
            stringBuilder.append((data % 10) + '0');
            stringBuilder.append(':');
        }
        if((Flag & 0x40) != 0){//秒
            data = (ZipTime & SEC_MASK);
            stringBuilder.append((data / 10) + '0');
            stringBuilder.append((data % 10) + '0');
        }
        if((Flag & 0x80) != 0) {//结束字符
            stringBuilder.append('\0');
        }
    }
}

