package com.mmorpg.qx.module.object.gameobject.enums;

import com.mmorpg.qx.module.item.model.drop.AbstractDropItemUse;
import com.mmorpg.qx.module.item.model.drop.AddEquipItemUse;
import com.mmorpg.qx.module.item.model.drop.EffectDropItemUse;

/**
 * @author wang ke
 * @description: 掉落物类型
 * @since 15:26 2020-10-12
 */
public enum DropType {
    Effect {
        @Override
        public EffectDropItemUse createDropItemUse() {
            return new EffectDropItemUse();
        }
    },
    Equip {
        @Override
        public AddEquipItemUse createDropItemUse() {
            return new AddEquipItemUse();
        }
    },
    ;

    public abstract <T extends AbstractDropItemUse> T createDropItemUse();
}
