package org.dromara.interaction.service;

import org.dromara.interaction.domain.dto.UserActionDTO;
import org.dromara.interaction.domain.vo.UserActionVo;

import java.util.List;

/**
 * 用户行为服务接口
 */
public interface IUserActionService {

    /**
     * 执行用户行为
     *
     * @param actionDTO 用户行为信息
     */
    void doAction(UserActionDTO actionDTO);

    /**
     * 获取用户对视频的所有操作
     */
    List<UserActionVo> getUserActions(Long videoId);
} 