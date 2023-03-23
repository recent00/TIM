package com.tuling.tim.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tuling.tim.server.mapper.ImUserMapper;
import com.tuling.tim.server.pojo.ImUser;
import com.tuling.tim.server.service.IImUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lys
 * @since 2023-03-01
 */
@Service
public class ImUserServiceImpl extends ServiceImpl<ImUserMapper, ImUser> implements IImUserService {

}
