package com.mmorpg.qx.module.purse.service;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.common.moduletype.ModuleType;
import com.mmorpg.qx.common.moduletype.SubModuleType;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.consume.ConsumeType;
import com.mmorpg.qx.module.consume.impl.PurseConsume;
import com.mmorpg.qx.module.consume.resource.ConsumeResource;
import com.mmorpg.qx.module.player.model.Player;
import org.springframework.stereotype.Component;

/**
 * @author zhang peng
 * @since 17:26 2021/5/11
 */
@Component
public class PurseService {

    public static PurseService getInstance() {
        return BeanService.getBean(PurseService.class);
    }

    /**
     * 消耗货币
     *
     * @param player
     * @param code
     * @param value
     */
    public void consumePurse(Player player, String code, int value) {
        ConsumeResource resource = ConsumeResource.valueOf(ConsumeType.PURSE, code, value);
        PurseConsume consume = resource.getType().create(resource);
        consume.verify(player, 1);
        Result result = consume.verify(player, 1);
        if (!result.isSuccess()) {
            throw new ManagedException(result.getCode());
        }
        consume.consume(player, ModuleInfo.valueOf(ModuleType.TROOP, SubModuleType.TROOP_UNLOCK), value);
        // 通知前端更新货币数量
        PacketSendUtility.sendPacket(player, player.getPurse().update(Integer.parseInt(code)));
    }
}
