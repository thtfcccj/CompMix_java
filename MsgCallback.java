package com.cofdet.dap8600.CompMix_java;

//定义消息回调,可用于线程间通讯
import android.os.Handler;
import android.os.Message;

public class MsgCallback {
    private Handler handler;

    public MsgCallback(Handler handler) {
        this.handler = handler;
    }

    public void sendMsg(int messageWhat) {
        Message message = new Message();
        message.what = messageWhat;
        handler.sendMessage(message);
    }

    public void sendMsg(int messageWhat, int arg1) {
        Message message = new Message();
        message.what = messageWhat;
        message.arg1 = arg1;
        handler.sendMessage(message);
    }
}