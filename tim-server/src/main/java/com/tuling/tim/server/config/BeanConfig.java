package com.tuling.tim.server.config;

import com.tuling.tim.common.constant.Constants;
import com.tuling.tim.common.protocol.TIMReqMsg;
import com.tuling.tim.server.model.MsgPack;
import okhttp3.OkHttpClient;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @since JDK 1.8
 */
@Configuration
public class BeanConfig implements ApplicationContextAware {

    @Autowired
    private AppConfiguration appConfiguration;

    public Map<String, MsgPack> map;

    public Map<String, MsgPack> getMap() {
        return map;
    }

    @Bean
    public ZkClient buildZKClient() {
        return new ZkClient(appConfiguration.getZkAddr(), appConfiguration.getZkConnectTimeout());
    }

    /**
     * http client
     *
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }


    /**
     * 创建心跳单例
     *
     * @return
     */
    @Bean(value = "heartBeat")
    public TIMReqMsg heartBeat() {
        TIMReqMsg heart = new TIMReqMsg(0L, "pong", Constants.CommandType.PING);
        return heart;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, MsgPack> map = applicationContext.getBeansOfType(MsgPack.class);
        if (!map.isEmpty()) {
            this.map = map.values().stream()
                    .collect(Collectors.toMap(service-> service.getType(), service -> service));
        }
    }
}
