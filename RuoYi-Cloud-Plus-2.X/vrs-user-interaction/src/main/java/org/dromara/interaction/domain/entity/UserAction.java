package org.dromara.interaction.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户行为对象 user_action
 */
@Data
@TableName("user_action")
public class UserAction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Long actionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 行为类型
     */
    private Integer actionType;

    /**
     * 行为发生时间
     */
    private LocalDateTime time;
}
