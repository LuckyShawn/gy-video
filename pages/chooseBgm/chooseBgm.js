const app = getApp()

Page({
    
    data:{
      bgmList:[], //设置空数组接收bgm列表
      serverUrl: "",
      videoParams: {}
    },
    onLoad:function(params){
      console.log(params);
      var me = this;
      console.log(this);
      me.setData({
        videoParams: params
      })
      var user = app.getGlobalUserInfo();
      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '请等待...',
      })
      var serverUrl = app.serverUrl;
      //调用后端
      wx.request({
        url: serverUrl + '/bgm/list',
        method: "POST",
        header: {
          'content-type': 'application/json' //默认值
        },
        success: function (res) {
          console.log(res.data);
          wx.hideLoading();
          if (res.data.status == 200) {
            var bgmList = res.data.data;
            me.setData({
              bgmList : bgmList,
              serverUrl:serverUrl
            })
          }
        }
      })
   
    },
    // 实现不同时播放？
  bindplay:function(e){
    console.log(e)
    var id = e.currentTarget.id;
    var arr = this.data.bgmList;
    // 使用 wx.createInnerAudioContext 获取 audio 上下文 context
    for (var a = 0; a < arr.length; a++) {
      if (id == arr[a].id){
        const innerAudioContext = wx.createInnerAudioContext();
        innerAudioContext.play();
      }else{
        const innerAudioContext = wx.createInnerAudioContext();
        innerAudioContext.stop();
      }
    }
  },
  upload:function(e){
    var me = this;
    var bgmId = e.detail.value.bgmId;
    var desc = e.detail.value.desc;
    console.log(bgmId+"=========="+desc);
    console.log("me.data.videoParams:"+me.data.videoParams);
    var duration = me.data.videoParams.duration;
    var height = me.data.videoParams.height;
    var width = me.data.videoParams.width;
    console.log(height);
    console.log(width);
    var tempFilePath = me.data.videoParams.tempFilePath;
    var thumbTempFilePath = me.data.videoParams.thumbTempFilePath;
    var userInfo = app.getGlobalUserInfo();
    //上传短视频
    var serverUrl = app.serverUrl;
    wx.uploadFile({
      url: serverUrl + '/video/uploadVideo',
      formData:{
        userId:userInfo.id,
        bgmId:bgmId,
        desc:desc,
        duration: duration,
        height: height,
        width: width
      },
      filePath: tempFilePath,
      name: 'file',
      header: {
        'content-type': 'application/json'
      },
      //此处有问题需要解决！为什么取不到值！
      success: function (res) {
        console.log("res返回值:" + res);
        console.log("typeof (res):" + typeof (res)) //object
        console.log("typeof (res.data):" + typeof (res.data)); //string
        const data = JSON.parse(res.data); //string转换成JSON对象
        wx.hideLoading();
        if (data.status == 200) {

          wx.showToast({
            title: '上传中...',
          })
          wx.navigateBack({
                   delta:1,
                 })
          //后台返回的videoId
          // var videoId = data.data;
          // //上传封面
          // wx.uploadFile({
          //   url: serverUrl + '/video/uploadCover',
          //   formData: {
          //     userId: app.userInfo.id,
          //     videoId: videoId
          //   },
          //   filePath: thumbTempFilePath,
          //   name: 'file',
          //   header: {
          //     'content-type': 'application/json'
          //   },
          //   //此处有问题需要解决！为什么取不到值！
          //   success: function (res) {
          //     var data = JSON.parse(res.data);
          //     wx.hideLoading();
          //     if (data.status == 200) {
          //       wx.showToast({
          //         title: "上传成功！",
          //         icon: "success",
          //          duration: 2000
          //       });
          //       wx.navigateBack({
          //         delta:1,
          //       })
          //     }else{
          //       wx.showToast({
          //         title: '上传失败！',
          //         duration: 2000
          //       })
          //     }
          //   }
          // })

        } 

      }
    })
  
  }
})

