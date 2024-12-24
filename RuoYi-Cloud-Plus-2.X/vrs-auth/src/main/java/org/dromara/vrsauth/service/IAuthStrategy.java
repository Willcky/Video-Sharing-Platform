package org.dromara.vrsauth.service;

import org.dromara.vrsauth.domain.vo.LoginVo;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.dromara.vrsauth.form.PasswordLoginBody;

/**
 * 授权策略
 *
 * @author Michelle.Chung
 */
public interface IAuthStrategy {

    String BASE_NAME = "AuthStrategy";

    /**
     * 登录
     *
     * @param body      登录对象
     * @param grantType 授权类型
     * @return 登录验证信息
     */
    static LoginVo login(PasswordLoginBody body, String grantType) {
        // 授权类型和客户端id
        String beanName = grantType + BASE_NAME;
        if (!SpringUtils.containsBean(beanName)) {
            throw new ServiceException("授权类型不正确!");
        }
        IAuthStrategy instance = SpringUtils.getBean(beanName);
        return instance.login(body);
    }

    /**
     * 登录
     *
     * @param body   登录对象
     * @return 登录验证信息
     */
    LoginVo login(PasswordLoginBody body);

}
