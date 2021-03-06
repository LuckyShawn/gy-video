package com.shawn.video.controller;

import com.shawn.video.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/11 0011
 */
@RestController
public class BasicController {

    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    //文件保存命名空间
    //public static final String FILE_SAPCE = "F:/WechatDev/javaworkspace/wechat_resource";
    public static final String FILE_SAPCE = "/usr/local/wechatResource/gyResource";

    //ffmpeg所在目录
    //public static final String FFMPEG_EXE = "F:\\WechatDev\\Utils\\ffmpeg\\bin\\ffmpeg.exe";
    public static final String FFMPEG_EXE = "/monchickey/ffmpeg/bin/ffmpeg";

    public static final int PAGE_SIZE = 5;
}
