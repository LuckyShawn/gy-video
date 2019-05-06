package com.shawn.video.Enums;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/5/5 0005
 */
public enum AAABBB {

    SUCCESS(1), //发布成功
    FORBID(2);  //禁止播放，管理员操作

    public final int value;

    AAABBB(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
