package com.tuling.tim.client.service;

import com.tuling.tim.client.vo.req.GroupReqVO;
import com.tuling.tim.client.vo.req.P2PReqVO;

/**
 * 消息处理器
 *
 * @since JDK 1.8
 */
public interface MsgHandle {

    /**
     * 统一的发送接口，包含了 groupChat p2pChat
     * @param msg
     */
    void sendMsg(String msg) ;

    /**
     * 回复收到消息的包
     */
    void ensureMsg(Long msgId) throws Exception;

    /**
     * 群聊
     * @param groupReqVO 群聊消息 其中的 userId 为发送者的 userID
     * @throws Exception
     */
    void groupChat(GroupReqVO groupReqVO) throws Exception ;

    /**
     * 私聊
     * @param p2PReqVO 私聊请求
     * @throws Exception
     */
    void p2pChat(P2PReqVO p2PReqVO) throws Exception;


    // TODO: 2018/12/26 后续对消息的处理可以优化为责任链模式
    /**
     * 校验消息
     * @param msg
     * @return 不能为空，后续可以加上一些敏感词
     * @throws Exception
     */
    boolean checkMsg(String msg) ;

    /**
     * 执行内部命令
     * @param msg
     * @return 是否应当跳过当前消息（包含了":" 就需要跳过）
     */
    boolean innerCommand(String msg) ;


    /**
     * 关闭系统
     */
    void shutdown() ;

    /**
     * 开启 AI 模式
     */
    void openAIModel() ;

    /**
     * 关闭 AI 模式
     */
    void closeAIModel() ;
}
