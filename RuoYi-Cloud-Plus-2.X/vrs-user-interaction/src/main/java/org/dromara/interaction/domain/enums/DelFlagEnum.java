package org.dromara.interaction.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 删除标志枚举
 */
@Getter
@AllArgsConstructor
public enum DelFlagEnum {

    /**
     * 有效
     */
    VALID(0, "有效"),

    /**
     * 已删除
     */
    DELETED(1, "已删除");

    private final Integer code;
    private final String info;
}
