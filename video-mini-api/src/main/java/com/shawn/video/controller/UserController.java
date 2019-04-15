package com.shawn.video.controller;


import com.shawn.video.pojo.Users;
import com.shawn.video.pojo.vo.UsersVO;
import com.shawn.video.service.UserService;
import com.shawn.video.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

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
        String fileSpace = "F:/WechatDev/javaworkspace/wechat_resource/gy_video_face";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";
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
        Users user = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        return JSONResult.ok(usersVO);
    }



}
