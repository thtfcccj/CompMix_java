package com.cofdet.dap8600.CompMix_java;

//事件管理器,事件阵列按时间顺序从前向后排列,此类与应用无关

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class EventMng{
    private int idInc;        //id累加器
    private int rdyWrInc;     //待写入事件条数
    private int newEventPos;  //新事件位置

    //每条事件信息的格式组成, 设计原则为可不依赖系统存在。
    private class Element{
        private int id;           //* 记录ID号,累加,数据库用
        private int eventType;    //* 事件类型，用于数据库快速查找等,见EventType定义
        private int zipTime;      //* 压缩时间格式
        private String pos;       //* 设备名称及其位置,如“我的家->厨房->厨下可燃”
        private String eventInfo; //* 事件及相关信息,用于快速显示

        @Override
        public String toString() { //去掉标识识符，直接用","号以节省空间
            return id + "," + eventType + "," + zipTime + "," + pos + '\'' +
                    ",'" + eventInfo + '\'';
        }
    }
    private static final int ELEMENT_LEN = 5; //元素个数

    private static List<Element> elementAry  = new ArrayList<>();  //事件队列

    //--------------------------------以下为公共接口-------------------------------------
    //创建, 并从事件输入流中恢复事件队列,读取最大条数
    public EventMng(Context context, int rdMaxCount){
        InputStream in = null;
        try {
            in = context.openFileInput("Event");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(in == null) return; //没有事件输入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder string = new StringBuilder();
        String line = null;
        for(; rdMaxCount > 0; rdMaxCount--) {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                break;//忽略
            }
            if (line == null) break; //读完了
            String ary[] = line.split(",");
            if (ary.length != ELEMENT_LEN) continue; //长度不对,忽略
            //入队
            Element element = new Element();
            element.id = Integer.valueOf(ary[0]);
            element.eventType = Integer.valueOf(ary[1]);
            element.zipTime = Integer.valueOf(ary[2]);
            element.pos = ary[3];
            element.eventInfo = ary[4];
            elementAry.add(element);
            //准备累加器
            if (element.id > idInc) idInc = element.id;
        }
        newEventPos = -1; //开机无新位置信息
    }

    //得到新事件起始位置ID,负值表示无新事件
    public int getNewEventPos(){return newEventPos; }
    //得到事件总数
    public int getEventCount(){return elementAry.size(); }


    //更新新事件位置，形参为从前到后已读取新事件总数
    public void updateNewEventPos(int readedCount){
        if(newEventPos < 0) return;
        newEventPos += readedCount;
        if(newEventPos >= elementAry.size())
            newEventPos = -1; //全部读取完成了，无新位置可重新统计
    }

    //压入事件
    public void pushEvent(int eventType, String pos, String eventInfo){
        Element element = new Element();
        element.id = idInc++;//id计数
        element.eventType = eventType;
        element.zipTime = ZipTime.getCurTime();
        element.pos = pos;
        element.eventInfo = eventInfo;
        elementAry.add(element);
        rdyWrInc++; //回写计数
        if(newEventPos < 0 ) newEventPos = elementAry.size() - 1;//新事件位置
    }

    //追加写入文件,即将未写入的数据写入
    public void appendWrite(Context context) {
        if(rdyWrInc <= 0) return; //无需写入
        Writer writer = null;
        try {
            OutputStream out = context.openFileOutput("Event", Context.MODE_APPEND);//追加方式
            writer = new OutputStreamWriter(out);
            StringBuilder stringBuilder = new StringBuilder();
            int aryPos = elementAry.size() - rdyWrInc;
            for(; rdyWrInc > 0; rdyWrInc--, aryPos++) {
                stringBuilder.append(elementAry.get(aryPos).toString());
            }
            writer.write(stringBuilder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //得到事件字符串,返回是否成功
    //flagAry的ID号定义为： “0日期 ,1位置，2事件信息”,日期时，最高8bit表示日期格式
    public boolean getEventString(int eventPos, //指定倒序位置
                                  int flagAry[],   //以顺序表示：0-7bit: 长度(0时不指定)， 17bit以上，项号
                                  StringBuilder stringBuilder){
        int aryPos = elementAry.size() - eventPos - 1;
        if(aryPos < 0) return false;
        Element element = elementAry.get(aryPos);
        for(int flagPos = 0; flagPos < flagAry.length; flagPos++){
            int flag = flagAry[flagPos];
            int prvPos = stringBuilder.length();
            switch((flag >> 8) & 0xff){
                case 0: //0序号
                    stringBuilder.append(eventPos + 1);
                    break;
                case 1: //1时间
                    ZipTime.toStringCh(stringBuilder, element.zipTime, (flag >> 24) & 0x7f);
                    break;
                case 2: //2位置
                    stringBuilder.append(element.pos);
                    break;
                case 3: //3事件信息
                    stringBuilder.append(element.eventInfo);
                    break;
            }
            //对齐处理:
            int len = stringBuilder.length();
            int curLen = len - prvPos;
            int Max = flag & 0xff;
            if(Max == 0){
                stringBuilder.append(" ");//空格间隔
            }
            else if(curLen < Max){//不够，补齐空格
                for(; curLen < Max; curLen++) stringBuilder.append(" ");
            }
            else if(curLen > Max){//强制截断
                stringBuilder.delete(len - (curLen - Max), len - 1);
                stringBuilder.append(" ");//空格间隔
            }
        }//end for
        return true;
    }

}