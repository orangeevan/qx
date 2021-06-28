package com.mmorpg.qx.module.skill.model.effect.impl;

import com.haipaite.common.utility.SelectRandom;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.equipment.manager.EquipmentManager;
import com.mmorpg.qx.module.equipment.model.EquipItem;
import com.mmorpg.qx.module.equipment.resource.EquipmentResource;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author wang ke
 * @description: 随机获取装备
 * @since 18:55 2020-09-04
 */
public class RandomEquipEffect extends AbstractEffectTemplate {
    private int num;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        num = resource.getValue();
    }

    @Override
    public void calculate(Effect effect) {
        super.calculate(effect);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        //随机物品
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                if (!RelationshipUtils.isTrainer(creature)) {
                    return false;
                }
                AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(creature);
                if (trainer.getEquipmentStorage().isFull()) {
                    return false;
                }
                int gainNum = trainer.getMaxStorageSize() - trainer.getUseCardStorage().getCurrentSize();
                gainNum = gainNum > num ? num : gainNum;
                Collection<EquipmentResource> items = EquipmentManager.getInstance().getAllEquips();
                if (CollectionUtils.isEmpty(items)) {
                    return false;
                }
                SelectRandom<EquipmentResource> random = new SelectRandom<>();
                items.stream().forEach(item -> random.addElement(item, 1));
                List<EquipmentResource> results = random.run(gainNum, true);
                results.stream().forEach(item -> {
                    EquipItem equipItem = EquipmentManager.getInstance().genEquipItem(item);
                    //工匠职业驯养师穿戴耐久度不同,在此处处理驯养师职业点会根据魔物娘改变
                    if (trainer.getAttrController().hasAttr(AttrType.Job_Artizan)) {
                        equipItem.setDurability(equipItem.getResource().getSpecialDurability());
                    }
                    trainer.getEquipmentStorage().addEquip(equipItem);
                });
            }
            return true;
        }
        return false;
    }
}
