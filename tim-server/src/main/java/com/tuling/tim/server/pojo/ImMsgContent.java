package com.tuling.tim.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 消息内容表
 * </p>
 *
 * @author lys
 * @since 2023-03-01
 */
@ApiModel(value="ImMsgContent对象", description="消息内容表")
public class ImMsgContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "消息id")
      @TableId(value = "mid", type = IdType.AUTO)
    private Long mid;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "发送消息用户id")
    private Long senderId;

    @ApiModelProperty(value = "接收消息用户id")
    private Long recipientId;

    @ApiModelProperty(value = "消息类型：1-文本消息，2-红包消息，3-语音消息，4-视频消息")
    private Integer msgType;

    @ApiModelProperty(value = "消息是否已被接收：0-未接收，1-已接收")
    private Integer isReceived;

    private Date createTime;


    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Integer getIsReceived() {
        return isReceived;
    }

    public void setIsReceived(Integer isReceived) {
        this.isReceived = isReceived;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ImMsgContent{" +
        "mid=" + mid +
        ", content=" + content +
        ", senderId=" + senderId +
        ", recipientId=" + recipientId +
        ", msgType=" + msgType +
        ", isReceived=" + isReceived +
        ", createTime=" + createTime +
        "}";
    }
}
