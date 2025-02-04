package org.dromara.vrsauth.enums;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import org.dromara.vrsauth.captcha.UnsignedMathGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.vrsauth.captcha.UnsignedMathGenerator;

/**
 * 验证码类型
 *
 * @author Lion Li
 */
@Getter
@AllArgsConstructor
public enum CaptchaType {

    /**
     * 数字
     */
    MATH(UnsignedMathGenerator.class),

    /**
     * 字符
     */
    CHAR(RandomGenerator.class);

    private final Class<? extends CodeGenerator> clazz;
}
