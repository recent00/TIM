package com.tuling.tim.client.service.impl.command;

import com.tuling.tim.client.config.AppConfiguration;
import com.tuling.tim.client.service.EchoService;
import com.tuling.tim.client.service.InnerCommand;
import com.tuling.tim.client.service.MsgLogger;
import com.tuling.tim.client.service.RouteRequest;
import com.tuling.tim.client.vo.res.ChatRecordResVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @since JDK 1.8
 */
@Service
public class QueryHistoryCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(QueryHistoryCommand.class);


    @Autowired
    private RouteRequest routeRequest ;
    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void process(String msg) {
        String[] split = msg.split(" ");
        if (split.length < 2){
            return;
        }
        Long otherId = Long.parseLong(split[1]);
        List<ChatRecordResVo.DataBodyBean> msgList = routeRequest.getChatRecordsByUserId(appConfiguration.getUserId(), otherId);
        if(msgList == null || msgList.size() == 0) {
            return;
        }
        for (ChatRecordResVo.DataBodyBean bodyBean : msgList) {
            System.out.println("用户[" + bodyBean.getUserId() + "]的说:" + bodyBean.getMsg());
        }
    }
}
