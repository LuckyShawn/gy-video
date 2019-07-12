var videoUtil = require('../../utils/util.js')

const app = getApp()

Page({
  data: {
    cover: "cover",
    videoId: '',
    src: '',
    videoInfo: [],
    userLikeVideo: false,

    commentsPage: 1,
    commentsTotalPage: 1,
    commentsList: [],

    placeholder: "说点什么..."
  },

  videoCtx: {},

  onLoad: function(params) {
    var me = this;
    var serverUrl = app.serverUrl;
    me.videoCtx = wx.createVideoContext("myVideo", this)
    console.log(params.videoInfo)
    //获取上一个页面传入的参数
    var videoInfo = JSON.parse(params.videoInfo);
    //判断视频是横向还是纵向
    var height = videoInfo.videoHeight;
    var width = videoInfo.videoWidth;
    me.cover = "cover";
    if (width >= height) {
      me.cover = '';
    }

    me.setData({
      videoId: videoInfo.id,
      src: app.serverUrl + videoInfo.videoPath,
      videoInfo: videoInfo
    });

    var user = app.getGlobalUserInfo();
    var loginUserId = "";
    if (user != null || user != undefined || user != '') {
      loginUserId = user.id;
    }

    wx.request({
      url: serverUrl + '/user/queryPublisher?loginUserId=' + loginUserId + "&videoId=" + videoInfo.id + "&publishId=" + videoInfo.userId,
      method: "POST",
      success: function(res) {
        console.log(res.data);
        var publisher = res.data.data.publisher;
        var userLikeVideo = res.data.data.userLikeVideo;
        me.setData({
          serverUrl: serverUrl,
          publisher: publisher,
          userLikeVideo: userLikeVideo
        })
        me.getCommentsList(1);
      }
    })

  },

  onShow: function() {
    var me = this;
    me.videoCtx.play();
  },
  onHide: function() {
    var me = this;
    me.videoCtx.pause();
  },

  showSearch: function() {
    wx.navigateTo({
      url: '../searchVideo/searchVideo',
    })
  },


  showPublisher: function() {
    var me = this;
    var user = app.getGlobalUserInfo();

    var videoInfo = me.data.videoInfo;
    var realUrl = '../mine/mine#publisherId@' + videoInfo.userId;


    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/userLogin?redirectUrl=' + realUrl,
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine?publisherId=' + videoInfo.userId,
      })
    }
  },

  upload: function() {
    var me = this;
    var user = app.getGlobalUserInfo();

    var videoInfo = JSON.stringify(me.data.videoInfo);
    var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;


    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/userLogin?redirectUrl=' + realUrl,
      })
    } else {
      videoUtil.uploadVideo();
    }
  },
  showIndex: function() {
    wx.redirectTo({
      url: '../index/index',
    })
  },
  showMine: function() {
    var user = app.getGlobalUserInfo();
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/userLogin',
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine',
      })
    }

  },
  //视频点赞和取消赞
  likeVideoOrNot: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    var videoInfo = me.data.videoInfo;
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/userLogin',
      })
    } else {
      //与后端交互
      var userLikeVideo = me.data.userLikeVideo;
      var url = "/video/userLike?userId=" + user.id +
        "&videoId=" + videoInfo.id + "&videoCreaterId=" + videoInfo.userId;
      if (userLikeVideo) {
        var url = "/video/userUnLike?userId=" + user.id +
          "&videoId=" + videoInfo.id + "&videoCreaterId=" + videoInfo.userId;
      }

      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '...',
      })
      wx.request({
        url: serverUrl + url,
        method: "POST",
        header: {
          'content-type': 'application/json',
          //验证参数
          "headerUserId": user.id,
          "headerUserToken": user.userToken
        },
        success: function(res) {
          wx.hideLoading();
          me.setData({
            userLikeVideo: !userLikeVideo
          })
        }
      })
    }
  },
  shareMe: function() {
    var user = app.getGlobalUserInfo();
    var me = this;
    wx.showActionSheet({
      itemList: ['下载到本地', '举报用户', '分享到朋友圈', '分享到QQ', '分享到微博'],
      success: function(res) {
        if (res.tapIndex == 0) {
          //下载视频到本地
          wx.showLoading({
            title: '下载中...',
          })
          wx.downloadFile({
            url: app.serverUrl + me.data.videoInfo.videoPath,
            success: function(res) {
              //只要服务器有响应的数据，就会把响应的内容写入文件并进入success回调，业务需要自行判断是否下载到了想要的内容
              console.log(res.tempFilePath)
              wx.saveVideoToPhotosAlbum({
                filePath: res.tempFilePath,
                success: function(res) {
                  console.log(res.errMsg)
                  wx.hideLoading();
                },
                //只要调用结束就执行（比如取消保存）
                complete: function() {
                  wx.hideLoading();
                }
              })
            }
          })

        } else if (res.tapIndex == 1) {
          //举报
          var videoInfo = JSON.stringify(me.data.videoInfo);
          var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
          if (user == null || user == undefined || user == '') {
            wx.navigateTo({
              url: '../userLogin/login?redirectUrl=' + realUrl,
            })
          } else {
            var publishUserId = me.data.videoInfo.userId;
            var videoId = me.data.videoInfo.id;
            var currentUserId = user.id;
            wx.navigateTo({
              url: '../report/report?videoId=' + videoId + "&publishUserId=" + publishUserId
            })
          }

        } else {
          wx.showToast({
            title: '官方暂未开放...'
          })
        }
      }
    })
  },

  onShareAppMessage: function(res) {
    var me = this;
    var videoInfo = me.data.videoInfo;
    return {
      title: '视频内容',
      path: 'pages/videoInfo/videoInfo?videoInfo=' +
        JSON.stringify(videoInfo)
    }
  },
  //获取评论焦点
  leaveComment: function() {
    this.setData({
      commentFocus: true
    })
  },

  //回复
  replyFocus: function(e) {
    //fathercommentid 小写
    var fatherCommentId = e.currentTarget.dataset.fathercommentid;
    var toUserId = e.currentTarget.dataset.touserid;
    var toNickname = e.currentTarget.dataset.tonickname;
    this.setData({
      placeholder: "回复" + toNickname,
      replyFatherCommentId: fatherCommentId,
      replyToUserId: toUserId,
      commentFocus: true
    })
  },

  //留言评论
  saveComment: function(e) {
    //获取评论回复的fatherCommentId和toUserId
    var fatherCommentId = e.currentTarget.dataset.replyfathercommentid; //小写
    var toUserId = e.currentTarget.dataset.replytouserid;

    var me = this;
    var serverUrl = app.serverUrl;
    var content = e.detail.value;
    var user = app.getGlobalUserInfo();
    var videoInfo = JSON.stringify(me.data.videoInfo);
    var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login?redirectUrl=' + realUrl,
      })
    } else {
      wx.showLoading({
        title: '发布中...',
      })
      wx.request({
        url: serverUrl + '/video/saveComment?fatherCommentId=' + fatherCommentId + "&toUserId=" + toUserId,
        method: "POST",
        header: {
          'content-type': 'application/json',
          //验证参数
          "headerUserId": user.id,
          "headerUserToken": user.userToken
        },
        data: {
          fromUserId: user.id,
          videoId: me.data.videoInfo.id,
          comment: content
        },
        success: function(res) {
          console.log(res.data);
          wx.hideLoading();
          me.setData({
            contentValue: "",
            commentsList: []
          })
          me.getCommentsList(1);
        }
      })
    }

  },

  //获取评论
  getCommentsList: function(page) {
    var me = this;
    var videoId = me.data.videoInfo.id;

    wx.request({
      url: app.serverUrl + '/video/getVideoComments?videoId=' + videoId + '&page=' + page + '&pageSize=5',
      method: "POST",
      success: function(res) {
        console.log(res.data);
        var commentsList = res.data.data.rows;
        var newCommentsList = me.data.commentsList;
        me.setData({
          commentsList: newCommentsList.concat(commentsList),
          commentsPage: page,
          commentsTotalPage: res.data.data.total
        })
      }
    })
  },
  //触底事件
  onReachBottom: function() {
    var me = this;
    var currentPage = me.data.commentsPage;
    var totalPage = me.data.commentsTotalPage;
    if (currentPage === totalPage) {
      return;
    }
    var page = currentPage + 1;
    me.getCommentsList(page);
  }

})