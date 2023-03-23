package com.tuling.tim.client.scanner;

import com.tuling.tim.client.config.AppConfiguration;
import com.tuling.tim.client.service.EchoService;
import com.tuling.tim.client.service.MsgHandle;
import com.tuling.tim.client.service.MsgLogger;
import com.tuling.tim.client.util.SpringBeanFactory;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * @since JDK 1.8
 */
public class Scan implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Scan.class);

    /**
     * 系统参数
     */
    private AppConfiguration configuration;

    private MsgHandle msgHandle;

    private MsgLogger msgLogger;

    private EchoService echoService;

    public Scan() {
        this.configuration = SpringBeanFactory.getBean(AppConfiguration.class);
        this.msgHandle = SpringBeanFactory.getBean(MsgHandle.class);
        this.msgLogger = SpringBeanFactory.getBean(MsgLogger.class);
        this.echoService = SpringBeanFactory.getBean(EchoService.class);
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String msg = sc.nextLine();

            //检查消息
            if (msgHandle.checkMsg(msg)) {
                continue;
            }

            //系统内置命令
            if (msgHandle.innerCommand(msg)) {
                continue;
            }

            //真正的发送消息
            msgHandle.sendMsg(msg);

            //写入聊天记录
            //msgLogger.log(msg);

            echoService.echo(EmojiParser.parseToUnicode(msg));
        }
    }

}
