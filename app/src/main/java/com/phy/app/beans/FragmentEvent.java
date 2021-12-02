package com.phy.app.beans;

public class FragmentEvent {
    private static final int EVENT_INT = 1;
    private static final int EVENT_STRING = 2;
    private static final int EVENT_LIST = 3;
    private static final int EVENT_MAP = 4;
    private static final int EVENT_BYTE_ARRAY = 5;

    public FragmentEvent() {
    }

    public int eventType;//可能类型有很多种，数据也不一样
    public Object data;//数据对象
}
