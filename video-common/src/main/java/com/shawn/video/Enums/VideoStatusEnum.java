package com.shawn.video.Enums;

/**
 * @Description 视频状态枚举
 * @Author shawn
 * @create 2019/4/16 0016
 */
public enum VideoStatusEnum {
    SUCCESS(1), //发布成功
    FORBID(2);  //禁止播放，管理员操作

    public final int value;

    VideoStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
