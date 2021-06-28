package com.mmorpg.qx.common;

import com.mmorpg.qx.module.object.gameobject.*;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.team.manager.TeamManager;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author wang ke
 * @description: 对象关系工具类
 * @since 14:48 2020-08-24
 */
public final class RelationshipUtils {

    /**
     * 是否是驯养师
     *
     * @param visibleObject
     * @return
     */
    public static boolean isTrainer(AbstractVisibleObject visibleObject) {
        if (visibleObject == null || !(visibleObject instanceof AbstractCreature)) {
            return false;
        }
        AbstractCreature creature = (AbstractCreature) visibleObject;
        return isTrainer(creature);
    }

    /***
     * 是否是驯养师
     * @param creature
     * @return
     */
    public static boolean isTrainer(AbstractCreature creature) {
        if (creature != null && creature instanceof AbstractTrainerCreature) {
            return true;
        }
        return false;
    }

    /***
     * 是否是玩家驯养师
     * @param creature
     * @return
     */
    public static boolean isPlayerTrainer(AbstractCreature creature) {
        if (creature != null && creature instanceof PlayerTrainerCreature) {
            return true;
        }
        return false;
    }

    /***
     * 验证机器人驯养师
     * @param creature
     * @return
     */
    public static boolean isRobotTrainer(AbstractCreature creature) {
        if (Objects.nonNull(creature) && creature instanceof RobotTrainerCreature) {
            return true;
        }
        return false;
    }


    public static boolean isMWN(AbstractVisibleObject creature) {
        if (creature != null && creature instanceof MWNCreature) {
            return true;
        }
        return false;
    }

    public static AbstractTrainerCreature toTrainer(AbstractCreature creature) {
        if (isTrainer(creature)) {
            return (AbstractTrainerCreature) creature;
        } else {
            throw new IllegalArgumentException("无法转换成驯养师");
        }
    }

    public static AbstractTrainerCreature toTrainerOrNull(AbstractCreature creature) {
        if (isMWN(creature)) {
            return toMWNCreature(creature).getMaster();
        }
        if (isTrainer(creature)) {
            return toTrainer(creature);
        }
        return null;
    }

    public static RobotTrainerCreature toRobotTrainer(AbstractCreature creature) {
        if (isTrainer(creature) && creature instanceof RobotTrainerCreature) {
            return (RobotTrainerCreature) creature;
        }
        return null;
    }

    public static MWNCreature toMWNCreature(AbstractVisibleObject creature) {
        if (isMWN(creature)) {
            return (MWNCreature) creature;
        } else {
            throw new IllegalArgumentException("无法转换成魔物娘");
        }
    }

    public static PlayerTrainerCreature toPlayerTrainerCreature(AbstractTrainerCreature trainer) {
        if (!RelationshipUtils.isPlayerTrainer(trainer)) {
            throw new IllegalArgumentException("无法转换成玩家驯养师");
        }
        return (PlayerTrainerCreature) trainer;
    }

    /***
     * 判断二者关系
     * @param a
     * @param b
     * @param relationships 期望关系类型
     * @return
     */
    public static boolean judgeRelationship(AbstractCreature a, AbstractCreature b, Relationships relationships) {
        return relationships.judgeRelationship(a, b);
    }

    /**
     * 魔物娘是否独居
     *
     * @param creature
     * @return
     */
    public static boolean isMwnLiveAlone(AbstractCreature creature) {
        if (!isMWN(creature)) {
            return false;
        }
        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(creature);
        List<MWNCreature> mwnCreatureList = mwnCreature.getWorldMapInstance().getMWNAroundGrid(mwnCreature.getGridId(), false);
        if (CollectionUtils.isEmpty(mwnCreatureList)) {
            return true;
        }
        for (MWNCreature mwn : mwnCreatureList) {
            if (Relationships.FRIEND_MWN_MWN.judgeRelationship(mwnCreature, mwn)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断魔物娘是否群居
     *
     * @param creature
     * @return
     */
    public static boolean isMwnLiveGroups(AbstractCreature creature) {
        if (!isMWN(creature)) {
            return false;
        }
        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(creature);
        List<MWNCreature> mwnCreatureList = mwnCreature.getWorldMapInstance().getMWNAroundGrid(mwnCreature.getGridId(), false);
        if (CollectionUtils.isEmpty(mwnCreatureList)) {
            return false;
        }
        for (MWNCreature mwn : mwnCreatureList) {
            if (!Relationships.FRIEND_MWN_MWN.judgeRelationship(mwnCreature, mwn)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 魔物娘旁边有特殊元素属性魔物娘
     *
     * @param creature
     * @param attrTypes
     * @return
     */
    public static boolean hasSpecialAttrMwnAround(AbstractCreature creature, AttrType... attrTypes) {
        if (!isMWN(creature)) {
            return false;
        }
        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(creature);
        List<MWNCreature> mwnCreatureList = mwnCreature.getWorldMapInstance().getMWNAroundGrid(mwnCreature.getGridId(), false);
        if (CollectionUtils.isEmpty(mwnCreatureList)) {
            return false;
        }
        for (MWNCreature mwn : mwnCreatureList) {
            if (Arrays.stream(attrTypes).anyMatch(mwn::hasAttr)) {
                return true;
            }
        }
        return false;
    }


    /***
     * 关系枚举
     */
    public enum Relationships {
        /**
         * 己方驯养师和魔物娘
         */
        SELF_TRAINER_MWN {
            @Override
            public boolean judgeRelationship(AbstractCreature a, AbstractCreature b) {
                if (!isTrainer(a) || !isMWN(b)) {
                    return false;
                }
                AbstractTrainerCreature trainer = toTrainer(a);
                if (trainer.hasMwn(b.getObjectId())) {
                    return true;
                }
                return false;
            }
        },
        /**
         * 友方魔物娘
         */
        FRIEND_MWN_MWN {
            @Override
            public boolean judgeRelationship(AbstractCreature a, AbstractCreature b) {
                if (!isMWN(a) || !isMWN(b)) {
                    return false;
                }
                if (a.getMaster() == b.getMaster()) {
                    return true;
                }
                return false;
            }
        },
        /**
         * 友方驯养师
         */
        FRIEND_TRAINER_TRAINER {
            @Override
            public boolean judgeRelationship(AbstractCreature a, AbstractCreature b) {
                if (!isTrainer(a) || !isTrainer(b)) {
                    return false;
                }
                AbstractTrainerCreature atrainer = toTrainer(a);
                AbstractTrainerCreature bTrainer = toTrainer(b);
                if (atrainer.getWorldMapInstance() != bTrainer.getWorldMapInstance()) {
                    return false;
                }
                /**目前认定队友是友方驯养师*/
                if (atrainer == bTrainer || TeamManager.getInstance().isTeammate(atrainer, bTrainer)) {
                    return true;
                }
                return false;
            }
        },
        /**
         * 友方驯养师跟魔物娘
         */
        FRIEND_TRAINER_MWN {
            @Override
            public boolean judgeRelationship(AbstractCreature a, AbstractCreature b) {
                if (!isTrainer(a) || !isMWN(b)) {
                    return false;
                }
                AbstractTrainerCreature atrainer = toTrainer(a);
                MWNCreature bMwn = toMWNCreature(b);
                if (atrainer.getWorldMapInstance() != bMwn.getWorldMapInstance()) {
                    return false;
                }
                /**目前认定队友是友方驯养师*/
                if (SELF_TRAINER_MWN.judgeRelationship(a, b) || TeamManager.getInstance().isTeammate(atrainer, bMwn.getMaster())) {
                    return true;
                }
                return false;
            }
        },

        /**
         * 敌对驯养师
         */
        ENEMY_TRAINER_TRAINER {
            @Override
            public boolean judgeRelationship(AbstractCreature a, AbstractCreature b) {
                if (!isTrainer(a) || !isTrainer(b)) {
                    return false;
                }
                AbstractTrainerCreature aTrainer = toTrainer(a);
                AbstractTrainerCreature bTrainer = toTrainer(b);
                if (aTrainer.getWorldMapInstance() != bTrainer.getWorldMapInstance()) {
                    return false;
                }
                /**目前认定队友是友方驯养师*/
                if (!TeamManager.getInstance().isTeammate(aTrainer, bTrainer)) {
                    return true;
                }
                return false;
            }
        },
        /**
         * 敌对驯养师和魔物娘
         */
        ENEMY_TRAINER_MWN {
            @Override
            public boolean judgeRelationship(AbstractCreature a, AbstractCreature b) {
                if (!isTrainer(a) || !isMWN(b)) {
                    return false;
                }
                AbstractTrainerCreature bMaster = b.getMaster();
                AbstractTrainerCreature aTrainer = toTrainer(a);
                if (bMaster != aTrainer && !TeamManager.getInstance().isTeammate(aTrainer, bMaster)) {
                    return true;
                }
                return false;
            }
        },
        /**
         * 敌对魔物娘和魔物娘
         */
        ENEMY_MWN_MWN {
            @Override
            public boolean judgeRelationship(AbstractCreature a, AbstractCreature b) {
                if (!isMWN(a) || !isMWN(b)) {
                    return false;
                }
                AbstractTrainerCreature aMaster = a.getMaster();
                AbstractTrainerCreature bMaster = b.getMaster();
                if (bMaster != aMaster && !TeamManager.getInstance().isTeammate(aMaster, bMaster)) {
                    return true;
                }
                return false;
            }
        },
        ;

        public abstract boolean judgeRelationship(AbstractCreature a, AbstractCreature b);
    }
}
