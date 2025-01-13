package org.dromara.interaction.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.interaction.domain.entity.UserAction;
import org.dromara.interaction.domain.vo.UserActionVo;

import java.util.List;

/**
 * 用户行为 数据层
 */
public interface UserActionMapper extends BaseMapperPlus<UserAction, UserAction> {

    /**
     * 获取用户对视频的所有操作
     */
    List<UserActionVo> selectUserActions(@Param("userId") Long userId, @Param("videoId") Long videoId);
} 