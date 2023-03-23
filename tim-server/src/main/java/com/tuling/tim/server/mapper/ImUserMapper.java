package com.tuling.tim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tuling.tim.server.pojo.ImUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lys
 * @since 2023-03-01
 */
@Mapper
public interface ImUserMapper extends BaseMapper<ImUser> {

}
