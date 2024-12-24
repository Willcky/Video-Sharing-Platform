package org.dromara.video.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.video.domain.ShardingOrderItem;

@Mapper
@DS("sharding")
public interface ShardingOrderItemMapper extends BaseMapper<ShardingOrderItem> {


}
