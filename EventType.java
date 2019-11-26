package com.cofdet.dap8600.CompMix_java;

//事件类型按位, 常量定义
public class EventType{

    //最高位置位表示取消(负值)
    public static final int UN_MASK = 0x800000;
    //主类型定义（63种）
    public static final int MAIN_SHIFT = 25;

    //事件以"主类型 + 类型(32种) + ID(1024k)"时的ID定义(如：报警,设备ID)
    public static final int TYPE_SHIFT = 20;
    public static final int TYPE_MASK =  (0x1f << TYPE_SHIFT);
    public static final int ID_MASK =    (0xfffff);

    //事件以"主类型 + 子类型(256种) + 类型码(128k)"时的ID定义(如:系统->登录事件->用户ID)
    public static final int SUB_SHIFT = 17;
    public static final int SUB_MASK =  (0xff << SUB_SHIFT);
    public static final int CODE_MASK =  (0x1ffff);

};