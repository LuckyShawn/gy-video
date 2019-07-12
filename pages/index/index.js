const app = getApp()

Page({
  data: {
    screenWidth: 350,
    //用于分页的属性
    totalPage: 1,
    page: 1,
    videoList: [],
    serverUrl: "",
    searchContent: ""
  },

  onLoad: function(params) {
    var me = this;
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    me.setData({
      screenWidth: screenWidth,
    });

    var searchContent = params.search;
    var isSaveRecord = params.isSaveRecord;
    if (isSaveRecord == null || isSaveRecord == '' || isSaveRecord == undefined) {
      isSaveRecord = 0;
    }
    if (searchContent != null && searchContent != undefined &&
      searchContent != '') {
      me.setData({
        searchContent: searchContent
      })
    }


    //获取当前分页数
    var page = me.data.page;
    me.getAllVideoList(page, isSaveRecord);
  },

  //上拉刷新事件
  onReachBottom: function() {
    var me = this;
    var currentPage = me.data.page;
    var totalPage = me.data.totalPage;
    //判断当前页和总页数是否相等，相等则无需刷新
    if (currentPage === totalPage) {
      wx.showToast({
        title: '视频已经刷完啦！',
        icon: "none"
      })
      return;
    }
    var page = currentPage + 1;
    me.getAllVideoList(page, 0);
  },

  onPullDownRefresh: function() {
    wx.showNavigationBarLoading();
    this.getAllVideoList(1, 0);
  },

  //获取视频共用方法
  getAllVideoList: function(page, isSaveRecord) {
    var me = this;
    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '请等待，加载中...',
    })

    var searchContent = me.data.searchContent;
    wx.request({
      url: serverUrl + '/video/showAll?page=' + page + "&isSaveRecord=" + isSaveRecord,
      method: "POST",
      data: {
        videoDesc: searchContent
      },
      success: function(res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh(); //停止下拉刷新动画
        console.log(res.data);

        //判断当前页page是否是第一页，如果是第一页，那么设置videoList为空
        if (page === 1) {
          me.setData({
            videoList: []
          })

        }
        //后台数据设置到前端this对象
        var videoList = res.data.data.rows;
        var newVideoList = me.data.videoList;

        me.setData({
          videoList: newVideoList.concat(videoList),
          page: page,
          totalPage: res.data.data.total,
          serverUrl: serverUrl
        });

      }
    })
  },
  showVideoInfo: function(e) {
    var me = this;
    var videoList = me.data.videoList;
    var arrIndex = e.target.dataset.arrindex;
    //返回字符串传入下一个页面，对象是无法传入的
    var videoInfo = JSON.stringify(videoList[arrIndex]);

    wx.redirectTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo,
    })
  }

})