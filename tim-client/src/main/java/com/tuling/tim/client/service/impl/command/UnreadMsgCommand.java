package com.tuling.tim.client.service.impl.command;

import com.tuling.tim.client.config.AppConfiguration;
import com.tuling.tim.client.service.InnerCommand;
import com.tuling.tim.client.service.InnerCommandContext;
import com.tuling.tim.client.service.RouteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UnreadMsgCommand implements InnerCommand {

    private final static Logger LOGGER = LoggerFactory.getLogger(UnreadMsgCommand.class);
    @Autowired
    private RouteRequest routeRequest ;
    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void process(String msg) {
        String[] split = msg.split(" ");
        if (split.length < 2){
            //TODO 获取总消息数
            String allUnreadCount = routeRequest.getAllUnreadCount(appConfiguration.getUserId());
            if(allUnreadCount == null || allUnreadCount.length() == 0) {
                allUnreadCount = "0";
            }
            System.out.println("总未读数:" + allUnreadCount);
        } else if(split.length == 2) {
            try {
                //TODO 获取userId用户的消息数
                Long userId = Long.parseLong(split[1]);
                String count = routeRequest.getUnreadCountByUserId(appConfiguration.getUserId(), userId);
                if(count == null || count.length() == 0) {
                    count = "0";
                }
                System.out.println("用户[" + userId + "]的未读数:" + count);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.error("命令不合法");
            return;
        }
    }
}
