package com.mmorpg.qx.module.item.model.drop;

import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.DropItemCreature;
import com.mmorpg.qx.module.object.gameobject.enums.DropType;

/**
 * @author wang ke
 * @description: 掉落物使用
 * @since 14:22 2020-10-12
 */
public abstract class AbstractDropItemUse {

    /***
     * 使用掉落物
     * @param trainer
     * @param dropItem
     */
    public void useDropItem(AbstractTrainerCreature trainer, DropItemCreature dropItem){
        doUseDropItem(trainer, dropItem);
    }

    abstract boolean doUseDropItem(AbstractTrainerCreature trainer, DropItemCreature dropItem);

    abstract DropType getDropType();
}
