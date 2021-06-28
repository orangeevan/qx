package com.mmorpg.qx.module.integral.service;

import com.alibaba.fastjson.JSON;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.integral.enums.IntegralType;
import com.mmorpg.qx.module.integral.manager.IntegralManager;
import com.mmorpg.qx.module.integral.packet.resp.IntegralUpdateResp;
import com.mmorpg.qx.module.integral.packet.vo.IntegralVo;
import com.mmorpg.qx.module.player.model.Player;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhang peng
 * @description:
 * @since 16:59 2021/3/10
 */
@Component
public class IntegralService {

    private Logger logger = SysLoggerFactory.getLogger(IntegralService.class);

    @Autowired
    private IntegralManager integralManager;

    /**
     * 添加积分
     *
     * @param player
     * @param code   积分类型
     * @param num    增加值
     */
    public void addIntegral(Player player, int code, int num) {
        IntegralType integralType = IntegralType.typeOf(code);
        if (integralType == null) {
            throw new IllegalArgumentException("无效的积分类型, code:" + code);
        }

        Map<Integer, Integer> stores = player.getIntegralStore().getStores();
        Integer before = stores.computeIfAbsent(code, k -> 0);
        stores.merge(code, num, (a, b) -> before + b);
        Integer after = stores.get(code);

        // 保存数据
        player.getIntegralEntity().setIntegralStore(JSON.toJSONString(player.getIntegralStore()));
        integralManager.update(player);

        // 发送消息
        List<IntegralVo> integralVos = player.getIntegralStore().getStores().entrySet().stream()
                .map(e -> IntegralVo.valueOf(e.getKey(), e.getValue())).collect(Collectors.toList());
        PacketSendUtility.sendPacket(player, IntegralUpdateResp.valueOf(integralVos));

        logger.info("玩家添加积分, 玩家ID:{}, 积分类型:{}, 操作前数量:{}, 增加数量{} ,操作后数量:{}",
                player.getObjectId(), integralType, before, num, after);
    }
}
