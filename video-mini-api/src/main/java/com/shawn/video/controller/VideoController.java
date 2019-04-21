package com.shawn.video.controller;


import com.shawn.video.Enums.VideoStatusEnum;
import com.shawn.video.pojo.Bgm;
import com.shawn.video.pojo.Users;
import com.shawn.video.pojo.Videos;
import com.shawn.video.pojo.vo.UsersVO;
import com.shawn.video.service.BgmService;
import com.shawn.video.service.UserService;
import com.shawn.video.service.VideoService;
import com.shawn.video.utils.FetchVideoCover;
import com.shawn.video.utils.JSONResult;
import com.shawn.video.utils.MergeVideoMp3;
import com.shawn.video.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    /**
     * 上传视频
     *
     * @param userId
     * @param bgmId
     * @param duration 视频时长
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

    @PostMapping(value = "/uploadVideo", headers = "content-type=multipart/form-data")
    public JSONResult uploadVideo(String userId, String bgmId,
                                  double duration,
                                  int width,
                                  int height,
                                  String desc,
                                  @ApiParam(value = "短视频", required = true) MultipartFile file) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户id不能为空...");
        }
        //文件保存的命名空间
        //String fileSpace = "F:/WechatDev/javaworkspace/wechat_resource";
        //保存到数据库中的相对路径
        String uploadPathDB = "/gy_video_video/" + userId + "/video";
        String coverPathDB = "/gy_video_video/" + userId + "/video";
        String finalVideoPath = null;
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (file != null) {

                //获取文件名
                String fileName = file.getOriginalFilename();
                //ab.mp4
                String fileNamePrefix = fileName.split("\\.")[0];

                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    finalVideoPath = FILE_SAPCE + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";
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
        if (StringUtils.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = FILE_SAPCE + bgm.getPath();

            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;

            String videoOutPutName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/gy_video_video/" + userId + "/video/" + videoOutPutName;
            finalVideoPath = FILE_SAPCE + uploadPathDB;
            tool.convertor(videoInputPath, mp3InputPath, duration, finalVideoPath);
        }
        System.out.println("uploadPathDB:" + uploadPathDB);
        System.out.println("finalVideoPath:" + finalVideoPath);

        //对视频进行截图
        // 获取视频信息。
        FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
        try {
            videoInfo.getCover(finalVideoPath,FILE_SAPCE + coverPathDB);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) duration);
        video.setVideoWidth(width);
        video.setVideoHeight(height);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCoverPath(coverPathDB);
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);
        return JSONResult.ok(videoId);
    }


    @ApiOperation(value = "上传封面", notes = "上传封面的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form")
    }
    )
    @PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
    public JSONResult upload(String videoId,
                             String userId,
                             @ApiParam(value = "视频封面", required = true) MultipartFile file) throws Exception {

        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("视频id和用户id不能为空...");
        }
        //文件保存的命名空间
        //String fileSpace = "F:/WechatDev/javaworkspace/wechat_resource";
        //保存到数据库中的相对路径
        String uploadPathDB = "/gy_video_video/" + userId + "/video";
        String finalCoverPath = null;
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (file != null) {
                //获取文件名
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    finalCoverPath = FILE_SAPCE + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    File outFile = new File(finalCoverPath);
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
        //保存封面信息
        videoService.updateVideo(videoId,uploadPathDB);
        return JSONResult.ok(videoId);
    }

    /**
     * 查询所有视频
     * @param page
     * @return
     */
    @ApiOperation(value = "查询所有视频", notes = "查询所有视频的接口")
    @PostMapping("/showAll")
    public JSONResult showAll(@RequestBody Videos video,Integer isSaveRecord, Integer page){
        if(page == null){
            page = 1;
        }

        PagedResult pagedResult = videoService.getAllVideo(video,isSaveRecord,page,PAGE_SIZE);
        return JSONResult.ok(pagedResult);
    }

    /**
     * 热搜词获取
     * @return
     */
    @ApiOperation(value = "查询所有视频", notes = "查询所有视频的接口")
    @PostMapping("/hot")
    public JSONResult showAll(){
        return JSONResult.ok(videoService.getHotWords());
    }

    /**
     * 点赞
     * @return
     */
    @ApiOperation(value = "点赞", notes = "点赞接口")
    @PostMapping("/userLike")
    public JSONResult userLike(String userId, String videoId, String videoCreaterId){
        videoService.userLikeVideo(userId,videoId,videoCreaterId);

        return JSONResult.ok(videoService.getHotWords());
    }

    /**
     * 取消点赞
     * @return
     */
    @ApiOperation(value = "取消点赞", notes = "取消点赞接口")
    @PostMapping("/userUnLike")
    public JSONResult userUnLike(String userId, String videoId, String videoCreaterId){
        videoService.userUnLikeVideo(userId,videoId,videoCreaterId);
        return JSONResult.ok(videoService.getHotWords());
    }


}
