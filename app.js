//app.js
App({

 //serverUrl:"http://267e5cf2.ngrok.io",
  serverUrl: "http://47.107.183.79:8082",
  //serverUrl: "http://luckyshawn.xyz:8081",
  userInfo: null,
  setGlobalUserInfo:function(user){
    wx.setStorageSync("userInfo", user)
  },
  getGlobalUserInfo:function(){
    return wx.getStorageSync("userInfo");
  },
  reportReasonArray: [
    "色情低俗",
    "政治敏感",
    "涉嫌诈骗",
    "辱骂谩骂",
    "广告垃圾",
    "诱导分享",
    "引人不适",
    "过于暴力",
    "违法违纪",
    "其它原因"
  ]
})