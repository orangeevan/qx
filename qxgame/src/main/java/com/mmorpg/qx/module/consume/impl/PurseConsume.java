package com.mmorpg.qx.module.consume.impl;

import com.mmorpg.qx.common.moduletype.ModuleInfo;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.consume.AbstractConsume;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.purse.model.PurseType;

/**
 * 玩家钱包消耗
 *
 * @author wang ke
 * @since v1.0 2018年3月26日
 */
public class PurseConsume extends AbstractConsume<Player> {

    @Override
    protected Result doVerify(Player player, int multiple) {
        PurseType type = PurseType.typeOf(code);
        int amount = multiple * value;
        if (amount < 0) {
            throw new RuntimeException(String.format("消耗验证出现溢[%s]", amount));
        }
        if (!player.getPurse().isEnoughTotal(amount, type)) {
            return Result.valueOf(type.getErrorCode());
        }
        return Result.SUCCESS;
    }

    @Override
    public void consume(Player player, ModuleInfo moduleInfo, int amount) {
        PurseType type = PurseType.typeOf(code);
        player.getPurse().cost(type, amount);
        PlayerManager.getInstance().update(player);
    }

    @Override
    public AbstractConsume clone() {
        PurseConsume consume = new PurseConsume();
        consume.code = code;
        consume.value = value;
        consume.setType(getType());
        return consume;
    }

}
