package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.equipment.manager.EquipmentManager;
import com.mmorpg.qx.module.equipment.model.EquipItem;
import com.mmorpg.qx.module.equipment.resource.EquipmentResource;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.roundFight.packet.EquipmentUpdateResp;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author wang ke
 * @description: 获取新物品
 * @since 11:06 2020-09-07
 */
public class FixEquipGainEffect extends AbstractEffectTemplate {
    //物品类型
    private int equipId;
    //数量
    private int num;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        num = resource.getValue();
        equipId = Integer.valueOf(resource.getParam());
    }

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(creature);
                if (trainer.getEquipmentStorage().isFull()) {
                    return false;
                }
                EquipmentResource resource = EquipmentManager.getInstance().getEquipResource(equipId);
                EquipItem equipItem = EquipmentManager.getInstance().genEquipItem(resource);
                //工匠职业驯养师穿戴耐久度不同,在此处处理驯养师职业点会根据魔物娘改变
                if (trainer.getAttrController().hasAttr(AttrType.Job_Artizan)) {
                    equipItem.setDurability(equipItem.getResource().getSpecialDurability());
                }
                trainer.getEquipmentStorage().addEquip(equipItem);
                PacketSendUtility.sendPacket(trainer, EquipmentUpdateResp.valueOf(trainer, equipItem, true));
            }
            return true;
        }
        return false;
    }
}
