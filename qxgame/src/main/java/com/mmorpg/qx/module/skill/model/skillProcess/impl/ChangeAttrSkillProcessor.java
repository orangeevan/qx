package com.mmorpg.qx.module.skill.model.skillProcess.impl;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.mwn.service.MWNService;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.SkillType;
import com.mmorpg.qx.module.skill.model.skillProcess.AbstractSkillProcessor;
import com.mmorpg.qx.module.skill.model.skillResult.ChangeAttrResult;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author zhang peng
 * @description ：属性变动 对目标对象的基础属性进行一次性的变化或者调整
 * @since 14:17 2021/4/10
 */
@Component
public class ChangeAttrSkillProcessor extends AbstractSkillProcessor<ChangeAttrResult, Map<String, Integer>> {

    @Override
    public SkillType getSkillType() {
        return SkillType.Change_Attr;
    }

    @Override
    public List<ChangeAttrResult> process(Skill skill, int roundIndex) {
        List<AbstractCreature> denfenders = skill.getDenfendersList();
        if (CollectionUtils.isEmpty(denfenders)) {
            return null;
        }
        List<ChangeAttrResult> results = new ArrayList<>();
        Map<String, Integer> param = initParam(skill.getResource());
        ChangeAttrType attrType = ChangeAttrType.valueOf(param.get("attr"));
        for (AbstractCreature defender : denfenders) {
            int oldValue = attrType.getValue(defender);
            int newValue = calAttrValue(oldValue, param, skill);
            attrType.setValue(newValue - oldValue, skill);
            ChangeAttrResult result = new ChangeAttrResult(skill.getSkillCaster().getObjectId(),
                    defender.getObjectId(), roundIndex, skill, oldValue, newValue);
            results.add(result);
            String format = String.format("属性变动技能, 技能ID:%s, 属性类型:%s, 属性旧值:%s, 属性新值:%s",
                    skill.getResource().getSkillId(), attrType, oldValue, newValue);
            System.err.println(format);
        }
        return results;
    }

    @Override
    public Map<String, Integer> initParam(SkillResource resource) {
        return JsonUtils.string2Map(resource.getParam(), String.class, Integer.class);
    }

    /**
     * 计算变动后属性值
     * 公式：若value为值 attr + var * value, 若value为万分比 attr + var * attr * value / 10000
     *
     * @param attr 旧值
     * @param param
     * @param skill
     * @return 新值
     */
    private int calAttrValue(int attr, Map<String, Integer> param, Skill skill) {
        int valueType = skill.getResource().getValueType();
        AbstractTrainerCreature my = skill.getSkillCaster().getMaster();
        AbstractTrainerCreature enemy = my.getRoom().getTrainers().stream().filter(t -> t != my).findFirst().orElse(null);
        int var = VarType.valueOf(param.get("var")).getValue(my, enemy);
        int value = param.get("value");
        if (valueType == 1) {
            return attr + var * value;
        } else if (valueType == 2) {
            return attr + var * attr * value / 10000;
        } else {
            throw new ManagedException(ManagedErrorCode.SYS_ERROR);
        }
    }

    /**
     * 属性类型
     */
    public enum ChangeAttrType {

        /** 魔物娘最大血量 */
        MWN_MAX_HP(1) {
            MWNCreature mwn;
            @Override
            public int getValue(AbstractCreature defender) {
                mwn = RelationshipUtils.toMWNCreature(defender);
                return mwn.getAttrController().getCurrentAttr(AttrType.Max_Hp);
            }

            @Override
            public void setValue(int value, Skill skill) {
                mwn.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Change_Attr_Skill),
                        Attr.valueOf(AttrType.Max_Hp, value), true);
                int currentHp = mwn.getLifeStats().getCurrentHp();
                int currentAttr = mwn.getAttrController().getCurrentAttr(AttrType.Max_Hp);
                if (currentHp > currentAttr) {
                    mwn.getLifeStats().reduceHp(currentHp - currentAttr, skill.getSkillCaster(),
                           Reason.Change_Attr_Skill, true);
                }
            }
        },

        /** 魔物娘攻击 */
        MWN_ATTACK(2) {
            MWNCreature mwn;
            @Override
            public int getValue(AbstractCreature defender) {
                mwn = RelationshipUtils.toMWNCreature(defender);
                return mwn.getAttrController().getCurrentAttr(AttrType.Attack);
            }

            @Override
            public void setValue(int value, Skill skill) {
                mwn.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Change_Attr_Skill),
                        Attr.valueOf(AttrType.Attack, value), true);
            }
        },

        /** 魔物娘对驯养师伤害 */
        MWN_TO_TRAINER_HARM(3) {
            MWNCreature mwn;
            @Override
            public int getValue(AbstractCreature defender) {
                mwn = RelationshipUtils.toMWNCreature(defender);
                return mwn.getAttrController().getCurrentAttr(AttrType.Trainer_Harm);
            }

            @Override
            public void setValue(int value, Skill skill) {
                mwn.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Change_Attr_Skill),
                        Attr.valueOf(AttrType.Trainer_Harm, value), true);
            }
        },

        /** 魔物娘元素点 */
        MWN_ELE_VALUE(4) {
            MWNCreature mwn;
            @Override
            public int getValue(AbstractCreature defender) {
                mwn = RelationshipUtils.toMWNCreature(defender);
                return MWNService.getInstance().getEleAttrType(mwn).getAttrValue(mwn);
            }

            @Override
            public void setValue(int value, Skill skill) {
                AttrType eleAttrType = MWNService.getInstance().getEleAttrType(mwn);
                mwn.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Change_Attr_Skill),
                        Attr.valueOf(eleAttrType, value), true);
            }
        },

        /** 驯养师血量 */
        TRAINER_MAX_HP(5) {
            AbstractTrainerCreature trainer;
            @Override
            public int getValue(AbstractCreature defender) {
                trainer = RelationshipUtils.toTrainer(defender);
                return trainer.getAttrController().getCurrentAttr(AttrType.Max_Hp);
            }

            @Override
            public void setValue(int value, Skill skill) {
                trainer.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Change_Attr_Skill),
                        Attr.valueOf(AttrType.Max_Hp, value), true);
                int currentHp = trainer.getLifeStats().getCurrentHp();
                int currentAttr = trainer.getAttrController().getCurrentAttr(AttrType.Max_Hp);
                if (currentHp > currentAttr) {
                    trainer.getLifeStats().reduceHp(currentHp - currentAttr, skill.getSkillCaster(),
                            Reason.Change_Attr_Skill, true);
                }
            }
        };

        private final int type;

        ChangeAttrType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public abstract int getValue(AbstractCreature defender);

        public abstract void setValue(int value, Skill skill);

        public static ChangeAttrType valueOf(int type) {
            for (ChangeAttrType attrType : ChangeAttrType.values()) {
                if (attrType.getType() == type) {
                    return attrType;
                }
            }
            return null;
        }
    }

    /**
     * 变量类型
     */
    public enum VarType {

        /** 默认类型 */
        DEFAULT(0) {
            @Override
            public int getValue(AbstractTrainerCreature my, AbstractTrainerCreature enemy) {
                return 1;
            }
        },
        /** 当前回合数 */
        CUR_ROUND(1) {
            @Override
            public int getValue(AbstractTrainerCreature my, AbstractTrainerCreature enemy) {
                return my.getRoom().getRound();
            }
        },
        /** 我方战力值 */
        MY_POWER(2) {
            @Override
            public int getValue(AbstractTrainerCreature my, AbstractTrainerCreature enemy) {
                return my.getCardsTotalForce();
            }
        },
        /** 我方手牌数量 */
        MY_CARD_NUM(3) {
            @Override
            public int getValue(AbstractTrainerCreature my, AbstractTrainerCreature enemy) {
                return my.getUseCardStorage().getSize();
            }
        },
        /** 敌方手牌数量 */
        ENEMY_CARD_NUM(4) {
            @Override
            public int getValue(AbstractTrainerCreature my, AbstractTrainerCreature enemy) {
                return enemy.getUseCardStorage().getSize();
            }
        },
        /** 我方牌库剩余卡牌数量 */
        MY_TROOP_CARD_NUM(5) {
            @Override
            public int getValue(AbstractTrainerCreature my, AbstractTrainerCreature enemy) {
                return my.getSourceCardStorage().getSize();
            }
        },
        /** 敌方牌库剩余卡牌数量 */
        ENEMY_TROOP_CARD_NUM(6) {
            @Override
            public int getValue(AbstractTrainerCreature my, AbstractTrainerCreature enemy) {
                return enemy.getSourceCardStorage().getSize();
            }
        };

        private final int type;

        VarType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public abstract int getValue(AbstractTrainerCreature my, AbstractTrainerCreature enemy);

        public static VarType valueOf(int type) {
            for (VarType varType : VarType.values()) {
                if (varType.getType() == type) {
                    return varType;
                }
            }
            return null;
        }
    }

}
