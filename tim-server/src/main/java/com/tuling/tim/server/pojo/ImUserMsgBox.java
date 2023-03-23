package com.tuling.tim.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户信箱消息索引表
 * </p>
 *
 * @author lys
 * @since 2023-03-01
 */
@ApiModel(value="ImUserMsgBox对象", description="用户信箱消息索引表")
public class ImUserMsgBox implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "信箱拥有用户id")
      @TableId(value = "owner_uid", type = IdType.ASSIGN_ID)
    private Long ownerUid;

    @ApiModelProperty(value = "消息参与的另一方")
    private Long otherUid;

    @ApiModelProperty(value = "消息id")
    private Long mid;

    @ApiModelProperty(value = "信箱类型：1-发件箱，2-收件箱")
    private Integer boxType;

    private Date createTime;


    public Long getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(Long ownerUid) {
        this.ownerUid = ownerUid;
    }

    public Long getOtherUid() {
        return otherUid;
    }

    public void setOtherUid(Long otherUid) {
        this.otherUid = otherUid;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Integer getBoxType() {
        return boxType;
    }

    public void setBoxType(Integer boxType) {
        this.boxType = boxType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ImUserMsgBox{" +
        "ownerUid=" + ownerUid +
        ", otherUid=" + otherUid +
        ", mid=" + mid +
        ", boxType=" + boxType +
        ", createTime=" + createTime +
        "}";
    }
}
