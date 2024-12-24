package org.dromara.vrsauth.domain.convert;

import io.github.linpeilie.BaseMapper;
import org.dromara.vrsauth.domain.vo.TenantListVo;
import org.dromara.system.api.domain.vo.RemoteTenantVo;
import org.dromara.vrsauth.domain.vo.TenantListVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 租户vo转换器
 * @author zhujie
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TenantVoConvert extends BaseMapper<RemoteTenantVo, TenantListVo> {

}
