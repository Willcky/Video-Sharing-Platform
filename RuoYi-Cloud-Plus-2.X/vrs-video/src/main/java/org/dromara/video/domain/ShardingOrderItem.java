package org.dromara.video.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_order_item")
@Data
public class ShardingOrderItem {

    private Long orderItemId;

    private Long orderId;

    private Long userId;

    private int totalMoney;
}