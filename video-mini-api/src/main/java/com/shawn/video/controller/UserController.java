package com.shawn.video.controller;


import com.shawn.video.pojo.Users;
import com.shawn.video.pojo.vo.PublisherVideo;
import com.shawn.video.pojo.vo.UsersVO;
import com.shawn.video.service.UserService;
import com.shawn.video.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


/**
 * @Description TODO
 * @Author shawn
 * @create 2019/4/11 0011
 */
@RestController
@Api(value = "用户相关业务接口",tags = {"用户相关业务controller"})
@RequestMapping("/user")
public class UserController extends BasicController {


    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像接口")
    @ApiImplicitParam(name="userId",value = "用户id",required = true,dataType = "String",paramType = "query")
    @PostMapping("/uploadFace")
    public JSONResult register(String userId, @RequestParam("file") MultipartFile[] files){

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户id不能为空...");
        }
        //文件保存的命名空间
        String fileSpace = "F:/WechatDev/javaworkspace/wechat_resource";
        //保存到数据库中的相对路径
        String uploadPathDB = "/gy_video_face/" + userId + "/face";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if(files != null && files.length > 0){

                //获取文件名
                String fileName = files[0].getOriginalFilename();
                if(StringUtils.isNotBlank(fileName)){
                    //文件上传的最终保存路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" +fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    File outFile = new File(finalFacePath);
                    if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()){
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                }
            }else {
                return JSONResult.errorMsg("上传错误...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错...");
        }finally{
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);
        return JSONResult.ok(uploadPathDB);

    }

    @ApiOperation(value = "查询用户信息", notes = "查询用户信息的接口 ")
    @ApiImplicitParam(name="userId",value = "用户id",required = true,dataType = "String",paramType = "query")
    @PostMapping("/query")
    public JSONResult query(String userId){
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户id不能为空...");
        }
        if("undefined".equals(userId)){
            return JSONResult.errorMsg("请登录！");
        }
        Users user = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        return JSONResult.ok(usersVO);
    }

    @ApiOperation(value = "查询发布者信息", notes = "查询发布者信息的接口 ")
    @ApiImplicitParams({@ApiImplicitParam(name="loginUserId",value = "用户id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name="videoId",value = "视频id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name="publishId",value = "发布者id",required = true,dataType = "String",paramType = "query")
            })
    @PostMapping("/queryPublisher")
    public JSONResult queryPublisher(String loginUserId,String videoId,String publishId){
        //不可为空
         if (StringUtils.isBlank(loginUserId) || StringUtils.isBlank(videoId) || StringUtils.isBlank(publishId)) {
            return JSONResult.errorMsg("");
        }
        //1.查询视频发布者的信息
        Users userInfo = userService.queryUserInfo(publishId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo,publisher);
        //2.查询当前登陆者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId,videoId);
        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);
        return JSONResult.ok(bean);
    }

    /**
     * 关注
     * @param userId
     * @param fansId
     * @return
     */
    @PostMapping("/beYourFans")
    public JSONResult beYourFans(String userId,String fansId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fansId)){
            return JSONResult.errorMsg("");
        }
        //保存用户关系
        userService.saveUserFanRelation(userId,fansId);

        return JSONResult.ok("关注成功!");
    }

    /**
     * 取关
     * @param userId
     * @param fansId
     * @return
     */
    @PostMapping("/notBeYourFans")
    public JSONResult notBeYourFans(String userId,String fansId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fansId)){
            return JSONResult.errorMsg("");
        }
        //删除用户关系
        userService.deleteUserFanRelation(userId,fansId);

        return JSONResult.ok("取消关注成功...");
    }
}
