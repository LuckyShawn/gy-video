package com.shawn.video;

import com.shawn.video.Enums.BGMOperatorTypeEnum;
import com.shawn.video.Enums.VideoStatusEnum;
import com.shawn.video.cofig.ResourceConfig;
import com.shawn.video.service.BgmService;
import com.shawn.video.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @Description zk客户端
 * @Author shawn
 * @create 2019/5/5 0005
 */
@Component
public class ZKCuratorClient {
    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

    //public static final String ZOOKEEPER_SERVER = "47.107.183.79:2181";

    @Autowired
    private BgmService bgmService;

    @Autowired
    private ResourceConfig resourceConfig;

    public void init(){
        if(client != null){
            return;
        }

        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,5);
        //创建zk客户端
        client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();
        // 启动客户端
        client.start();

        try {
			String testNodeData = new String(client.getData().forPath("/bgm/190429CSKS83TD8H"));
			log.info("测试的节点数据为： {}", testNodeData);
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加监听节点
    public void addChildWatch(String nodePath) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(client,nodePath,true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                    log.info("监听到事件：CHILD_ADDED");
                    //1. 从数据库查询bgm对象，获取路径path
                    String path = event.getData().getPath();
                     String operatorObjStr = new String (event.getData().getData());
                    //json转成map
                    Map<String,String> map = JsonUtils.jsonToPojo(operatorObjStr,Map.class);
                    String operatorType = map.get("operType");
                    String songPath = map.get("path"); //相对路径

//                    String arr[] = path.split("/");
//                    String bgmId = arr[arr.length - 1];
//                    Bgm bgm = bgmService.queryBgmById(bgmId);
//                    if(bgm == null){
//                        return;
//                    }
                    //String songPath = bgm.getPath(); //相对路径

                    //2. 定义保存到本地的bgm路径
                    //String filePath = "F:\\WechatDev\\javaworkspace\\wechat_resource\\gy_video_admin_bgm" + songPath;
                    String filePath = resourceConfig.getFileSpace() + songPath;
                    //3. 定义下载的路径（播放的url）
                    String arrPath[] = songPath.split("\\\\");
                    String finalPath = "";
                    //3.1 处理url的斜杠以及编码
                    for(int i = 0;i < arrPath.length; i++){
                        if(StringUtils.isNotBlank(arrPath[i])){
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i],"UTF-8");
                        }
                    }

                    //admin路径
                    //String bgmUrl = "http://127.0.0.1:8081/mvc" + finalPath;
                    String bgmUrl = resourceConfig.getBgmServer() + finalPath;
                    if(operatorType.equals(BGMOperatorTypeEnum.ADD.type)){
                        //下载bgm到spring boot服务器
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url,file);
                        client.delete().forPath(path);
                    }else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)){
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }

                }
            }
        });
    }

}
