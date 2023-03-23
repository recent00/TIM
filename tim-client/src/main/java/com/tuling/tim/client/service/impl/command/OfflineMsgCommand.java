package com.tuling.tim.client.service.impl.command;

import com.tuling.tim.client.config.AppConfiguration;
import com.tuling.tim.client.service.InnerCommand;
import com.tuling.tim.client.service.RouteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OfflineMsgCommand implements InnerCommand {
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

        routeRequest.queryOfflineMsg(appConfiguration.getUserId(), Long.parseLong(split[1]));
    }
}
