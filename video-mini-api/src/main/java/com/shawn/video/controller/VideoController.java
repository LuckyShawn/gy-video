package com.shawn.video.controller;


import com.shawn.video.pojo.Bgm;
import com.shawn.video.pojo.Users;
import com.shawn.video.pojo.vo.UsersVO;
import com.shawn.video.service.BgmService;
import com.shawn.video.service.UserService;
import com.shawn.video.utils.JSONResult;
import com.shawn.video.utils.MergeVideoMp3;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/11 0011
 */
@RestController
@Api(value = "视频相关业务接口", tags = {"视频相关业务接口controller"})
@RequestMapping("/video")
public class VideoController extends BasicController {


    @Autowired
    private UserService userService;

    @Autowired
    private BgmService bgmService;

    /**
     * 上传视频
     * @param userId
     * @param bgmId
     * @param duration  视频时长
     * @param width
     * @param height
     * @param desc
     * @param file
     * @return
     */
    @ApiOperation(value = "上传视频", notes = "上传视频的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频高度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form")
    }
    )

    @PostMapping(value = "/uploadVideo" ,headers = "content-type=multipart/form-data")
    public JSONResult uploadVideo(String userId, String bgmId,
                                  double duration,
                                  int width,
                                  int height,
                                  String desc,
                                  @ApiParam(value="短视频",required = true) MultipartFile file) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户id不能为空...");
        }
        //文件保存的命名空间
        //String fileSpace = "F:/WechatDev/javaworkspace/wechat_resource";
        //保存到数据库中的相对路径
        String uploadPathDB = "/gy_video_video/" + userId + "/video";
        String finalVideoPath = null;
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (file != null) {

                //获取文件名
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    finalVideoPath = FILE_SAPCE + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return JSONResult.errorMsg("上传错误...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //判断bgmId是否为空，如果不为空,查询bgm信息并合成
        if(StringUtils.isNotBlank(bgmId)){
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = FILE_SAPCE + bgm.getPath();

            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;

            String videoOutPutName = UUID.randomUUID().toString()+".mp4";
            uploadPathDB = "/gy_video_video/" + userId + "/video/"+videoOutPutName;
            finalVideoPath = FILE_SAPCE + uploadPathDB;
            tool.convertor(videoInputPath,mp3InputPath,duration,finalVideoPath);
        }
        System.out.println("uploadPathDB:"+uploadPathDB);
        System.out.println("finalVideoPath:"+finalVideoPath);

        //保存视频信息到数据库

        return JSONResult.ok();
    }



}
