const app = getApp()

Page({
    data: {
      redirectUrl:''
    },

  onLoad: function (params){
    var me = this;
    var redirectUrl = params.redirectUrl;
    if (redirectUrl != null && redirectUrl != '' && redirectUrl!=undefined){
      redirectUrl = redirectUrl.replace(/#/g, "?");
      redirectUrl = redirectUrl.replace(/@/g, "=");
    }
    me.redirectUrl = redirectUrl;
    },

    doLogin: function(e) {
      var me = this;
      var formObject = e.detail.value;
      var username = formObject.username;
      var password = formObject.password;

      // 简单验证
      if (username.length == 0 || password.length == 0) {
        wx.showToast({
          title: '用户名或密码不能为空',
          icon: 'none',
          duration: 3000
        })
      } else {
        var serverUrl = app.serverUrl;
        //显示加载动态图，增加体验
        wx.showLoading({
          title: '请等待...',
        });
        //调用后端
        wx.request({
          url: serverUrl + '/login',
          method: "POST",
          data: {
            username: username,
            password: password
          },
          header: {
            'content-type': 'application/json' // 默认值
             //验证参数
          },
          success: function(res) {
            console.log(res.data);
            wx.hideLoading(); //隐藏加载动态图
            var status = res.data.status;
            console.log("status:"+status)
            if (status == 200) {
              wx.showToast({
                title: "用户登录成功！",
                icon: 'success',
                duration: 2000
               })
              //app.userInfo = res.data.data;
              //fixme 修改原有的全局对象为本地缓存
              app.setGlobalUserInfo(res.data.data);
               //页面跳转
               var redirectUrl = me.redirectUrl;
              if (redirectUrl != null && redirectUrl != '' && redirectUrl != undefined){
                wx.redirectTo({
                  url: redirectUrl,
                })
               }else{
                wx.redirectTo({
                  url: '../mine/mine',
                })
               }
                
            } else if (status == 500) {
              wx.showToast({
                title: res.data.msg,
                icon: 'none',
                duration: 2000
              })
            }
          }
        })
      }
    },

  goRegister:function() {
      wx.navigateTo({
        url: '../userRegister/register',
      })
    }
})