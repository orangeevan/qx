package com.mmorpg.qx.module.skill.model.target;

import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.skill.model.target.chooser.AbstractTargetChooser;
import com.mmorpg.qx.module.worldMap.model.WorldMap;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 每一种类型需要添加对应的检查规则
 *
 * @author wang ke
 * @since v1.0 2019年3月2日
 */
public enum TargetType {
    /**
     * 驯养师自己
     */
    SELF_TRAINER {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.SELF_TRAINER;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (caster.getMaster().getObjectId().equals(target.getTargetIds().get(0))) {
                        return Result.SUCCESS;
                    }
                    return Result.FAILURE;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    if (RelationshipUtils.isTrainer(skillCaster)) {
                        return Target.valueOf(skillCaster.getGridId(), skillCaster.getObjectId());
                    }
                    if (RelationshipUtils.isMWN(skillCaster)) {
                        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(skillCaster);
                        AbstractTrainerCreature trainer = mwnCreature.getMaster();
                        return Target.valueOf(trainer.getGridId(), trainer.getObjectId());
                    }
                    return null;
                }
            };
        }
    },
    /**
     * 魔物娘自己
     */
    MYSELF_MWN {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {

                @Override
                public TargetType getTargetType() {
                    return TargetType.MYSELF_MWN;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (caster.getObjectId().equals(target.getTargetIds().get(0))) {
                        return Result.SUCCESS;
                    }
                    return Result.FAILURE;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    if (RelationshipUtils.isTrainer(skillCaster)) {
                        AbstractTrainerCreature trainerCreature = RelationshipUtils.toTrainer(skillCaster);
                        Collection<MWNCreature> mwnCreatures = trainerCreature.getMWN(true);
                        List<Long> targets = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(mwnCreatures)) {
                            targets = mwnCreatures.stream().map(MWNCreature::getObjectId).collect(Collectors.toList());
                        }
                        return Target.valueOf(skillCaster.getGridId(), targets);
                    }
                    if (RelationshipUtils.isMWN(skillCaster)) {
                        MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(skillCaster);
                        return Target.valueOf(mwnCreature.getGridId(), mwnCreature.getObjectId());
                    }
                    return null;
                }
            };
        }

    },

    /**
     * 己方魔物娘
     */
    SELF_MWN {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return SELF_MWN;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (Objects.isNull(target) || target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    for (long targetId : target.getTargetIds()) {
                        AbstractTrainerCreature trainerCreature = RelationshipUtils.toTrainer(caster);
                        AbstractCreature mwn = trainerCreature.getMwn(targetId);
                        if (!RelationshipUtils.judgeRelationship(caster, mwn, RelationshipUtils.Relationships.SELF_TRAINER_MWN)) {
                            return Result.FAILURE;
                        }
                    }
                    return Result.SUCCESS;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    if (RelationshipUtils.isMWN(skillCaster)) {
                        return Target.valueOf(skillCaster.getGridId(), skillCaster.getObjectId());
                    }

                    if (!RelationshipUtils.isTrainer(skillCaster)) {
                        return null;
                    }
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(skillCaster);
                    Collection<MWNCreature> mwnCreatures = trainer.getMWN(true);
                    if (Objects.isNull(mwnCreatures)) {
                        return null;
                    }
                    return Target.valueOf(0, mwnCreatures.stream().map(MWNCreature::getObjectId).collect(Collectors.toList()));
                }
            };
        }
    },
    /**
     * 友方驯养师
     */
    FRIEND_TRAINER {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.FRIEND_TRAINER;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    for (long targetId : target.getTargetIds()) {
                        AbstractCreature targetCreature = caster.getWorldMapInstance().getCreatureById(targetId);
                        if (!RelationshipUtils.judgeRelationship(caster, targetCreature, RelationshipUtils.Relationships.FRIEND_TRAINER_TRAINER) &&
                                !RelationshipUtils.judgeRelationship(targetCreature, caster, RelationshipUtils.Relationships.FRIEND_TRAINER_MWN)) {
                            return Result.FAILURE;
                        }

                    }
                    return Result.SUCCESS;
                }
            };
        }
    },
    /**
     * 友方魔物娘
     */
    FRIEND_MWN {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.FRIEND_TRAINER;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    for (long targetId : target.getTargetIds()) {
                        AbstractCreature targetCreature = caster.getWorldMapInstance().getCreatureById(targetId);
                        if (!RelationshipUtils.judgeRelationship(caster, targetCreature, RelationshipUtils.Relationships.FRIEND_MWN_MWN) && !RelationshipUtils.judgeRelationship(caster, targetCreature, RelationshipUtils.Relationships.FRIEND_TRAINER_MWN)) {
                            return Result.FAILURE;
                        }

                    }
                    return Result.SUCCESS;
                }
            };
        }
    },
    /**
     * 敌方驯养师
     */
    ENEMY_TRAINER {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.ENEMY_TRAINER;
                }

                @Override
                public Result verifyTarget(AbstractCreature skillCaster, Target target) {
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    for (long targetId : target.getTargetIds()) {
                        AbstractCreature targetCreature = skillCaster.getWorldMapInstance().getCreatureById(targetId);
                        if (!RelationshipUtils.judgeRelationship(skillCaster, targetCreature, RelationshipUtils.Relationships.ENEMY_TRAINER_TRAINER) && !RelationshipUtils.judgeRelationship(targetCreature, skillCaster, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)) {
                            return Result.FAILURE;
                        }
                    }
                    return Result.SUCCESS;
                }

                @Override
                public Target chooseTarget(AbstractCreature caster) {
                    if (caster == null || caster.getWorldMapInstance() == null) {
                        return null;
                    }
                    AbstractTrainerCreature master = caster.getMaster();
                    Collection<AbstractTrainerCreature> allTrainers = caster.getWorldMapInstance().getAllTrainer();
                    List<AbstractTrainerCreature> enemyTrainers = allTrainers.stream().filter(trainer -> trainer != master).filter(trainer -> RelationshipUtils.judgeRelationship(master, trainer, RelationshipUtils.Relationships.ENEMY_TRAINER_TRAINER)).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(enemyTrainers)) {
                        return null;
                    }
                    Target target = new Target();
                    List<Long> targetIds = enemyTrainers.stream().map(AbstractTrainerCreature::getObjectId).collect(Collectors.toList());
                    target.setTargetIds(targetIds);
                    return target;
                }
            };
        }
    },
    /**
     * 敌方魔物娘
     */
    ENEMY_MWN {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.ENEMY_MWN;
                }

                @Override
                public Result verifyTarget(AbstractCreature skillCaster, Target target) {
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    for (long targetId : target.getTargetIds()) {
                        AbstractCreature targetCreature = skillCaster.getWorldMapInstance().getCreatureById(targetId);
                        if (Objects.nonNull(targetCreature) && !RelationshipUtils.judgeRelationship(skillCaster, targetCreature, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN) && !RelationshipUtils.judgeRelationship(skillCaster, targetCreature, RelationshipUtils.Relationships.ENEMY_MWN_MWN)) {
                            return Result.FAILURE;
                        }
                    }
                    return Result.SUCCESS;
                }

                @Override
                public Target chooseTarget(AbstractCreature creature) {
                    AbstractTrainerCreature trainer = null;
                    if (RelationshipUtils.isMWN(creature)) {
                        trainer = creature.getMaster();
                    }
                    if (RelationshipUtils.isTrainer(creature)) {
                        trainer = RelationshipUtils.toTrainer(creature);
                    }
                    List<Long> results = new ArrayList<>();
                    for (AbstractTrainerCreature trainerCreature : trainer.getRoom().getTrainers()) {
                        if (RelationshipUtils.judgeRelationship(trainer, trainerCreature, RelationshipUtils.Relationships.ENEMY_TRAINER_TRAINER)) {
                            Collection<MWNCreature> aliveMwns = trainerCreature.getMWN(true);
                            if (!CollectionUtils.isEmpty(aliveMwns)) {
                                results.addAll(aliveMwns.stream().map(MWNCreature::getObjectId).collect(Collectors.toList()));
                            }

                        }
                    }
                    if (!CollectionUtils.isEmpty(results)) {
                        return Target.valueOf(0, results);
                    }
                    return null;
                }
            };
        }
    },


    /**
     * 以某个格子为中心的相邻敌方魔物娘
     */
    ENEMY_MWN_POINT_RANGE {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.ENEMY_MWN_POINT_RANGE;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    List<MWNCreature> creaturesAroundGrid = caster.getWorldMapInstance().getMWNAroundGrid(target.getGridId(), true);
                    if (CollectionUtils.isEmpty(creaturesAroundGrid)) {
                        return Result.FAILURE;
                    }
                    //先验证目标是否在周围格子上
                    for (long targetId : target.getTargetIds()) {
                        Optional<MWNCreature> first = creaturesAroundGrid.stream().filter(creature -> creature.getObjectId() == targetId).findFirst();
                        if (!first.isPresent()) {
                            return Result.FAILURE;
                        }
                        MWNCreature enemy = first.get();
                        if (!RelationshipUtils.judgeRelationship(caster, enemy, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN) &&
                                !RelationshipUtils.judgeRelationship(caster, enemy, RelationshipUtils.Relationships.ENEMY_MWN_MWN)) {
                            return Result.FAILURE;
                        }
                    }
                    return Result.SUCCESS;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    WorldMapInstance mapInstance = skillCaster.getWorldMapInstance();
                    List<MWNCreature> creaturesAround = mapInstance.getMWNAroundGrid(skillCaster.getGridId(), true);
                    if (CollectionUtils.isEmpty(creaturesAround)) {
                        return null;
                    }
                    List<Long> targets = null;
                    if (RelationshipUtils.isMWN(skillCaster)) {
                        targets = creaturesAround.stream().filter(mwn -> RelationshipUtils.judgeRelationship(skillCaster, mwn, RelationshipUtils.Relationships.ENEMY_MWN_MWN)).map(MWNCreature::getObjectId).collect(Collectors.toList());
                    } else if (RelationshipUtils.isTrainer(skillCaster)) {
                        targets = creaturesAround.stream().filter(mwn -> RelationshipUtils.judgeRelationship(skillCaster, mwn, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)).map(MWNCreature::getObjectId).collect(Collectors.toList());
                    }
                    if (CollectionUtils.isEmpty(targets)) {
                        return null;
                    }
                    return Target.valueOf(skillCaster.getGridId(), targets);
                }
            };
        }
    },

    /**
     * 以某个格子为中心的相邻敌方魔物娘,不包括中心格子
     */
    ENEMY_MWN_WITHOUT_POINT_RANGE {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            AbstractTargetChooser abstractTargetChooser = new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.ENEMY_MWN_WITHOUT_POINT_RANGE;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    List<MWNCreature> creaturesAroundGrid = caster.getWorldMapInstance().getMWNAroundGrid(target.getGridId(), false);
                    if (CollectionUtils.isEmpty(creaturesAroundGrid)) {
                        return Result.FAILURE;
                    }
                    //先验证目标是否在周围格子上
                    for (long targetId : target.getTargetIds()) {
                        Optional<MWNCreature> first = creaturesAroundGrid.stream().filter(creature -> creature.getObjectId() == targetId).findFirst();
                        if (!first.isPresent()) {
                            return Result.FAILURE;
                        }
                        MWNCreature enemy = first.get();
                        if (!RelationshipUtils.judgeRelationship(caster, enemy, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN) &&
                                !RelationshipUtils.judgeRelationship(caster, enemy, RelationshipUtils.Relationships.ENEMY_MWN_MWN)) {
                            return Result.FAILURE;
                        }
                    }
                    return Result.SUCCESS;
                }


            };
            return abstractTargetChooser;
        }
    },

    /**
     * 以某个格子为中心的相邻友方魔物娘
     */
    FRIEND_MWN_POINT_RANGE {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.FRIEND_MWN_POINT_RANGE;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    List<MWNCreature> creaturesAroundGrid = caster.getWorldMapInstance().getMWNAroundGrid(target.getGridId(), true);
                    if (CollectionUtils.isEmpty(creaturesAroundGrid)) {
                        return Result.FAILURE;
                    }
                    //先验证目标是否在周围格子上
                    for (long targetId : target.getTargetIds()) {
                        Optional<MWNCreature> first = creaturesAroundGrid.stream().filter(creature -> creature.getObjectId() == targetId).findFirst();
                        if (!first.isPresent()) {
                            return Result.FAILURE;
                        }
                        MWNCreature enemy = first.get();
                        if (!RelationshipUtils.judgeRelationship(caster, enemy, RelationshipUtils.Relationships.FRIEND_TRAINER_MWN) &&
                                !RelationshipUtils.judgeRelationship(caster, enemy, RelationshipUtils.Relationships.FRIEND_MWN_MWN)) {
                            return Result.FAILURE;
                        }
                    }
                    return Result.SUCCESS;
                }
            };
        }
    },

    /**
     * 以某个格子为中心的相邻友方魔物娘,不包括中心格子
     */
    FRIEND_MWN_WITHOUT_POINT_RANGE {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.FRIEND_MWN_POINT_RANGE;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    List<MWNCreature> creaturesAroundGrid = caster.getWorldMapInstance().getMWNAroundGrid(target.getGridId(), false);
                    if (CollectionUtils.isEmpty(creaturesAroundGrid)) {
                        return Result.FAILURE;
                    }
                    //先验证目标是否在周围格子上
                    for (long targetId : target.getTargetIds()) {
                        Optional<MWNCreature> first = creaturesAroundGrid.stream().filter(creature -> creature.getObjectId() == targetId).findFirst();
                        if (!first.isPresent()) {
                            return Result.FAILURE;
                        }
                        MWNCreature enemy = first.get();
                        if (!RelationshipUtils.judgeRelationship(caster, enemy, RelationshipUtils.Relationships.FRIEND_TRAINER_MWN) &&
                                !RelationshipUtils.judgeRelationship(caster, enemy, RelationshipUtils.Relationships.FRIEND_MWN_MWN)) {
                            return Result.FAILURE;
                        }
                    }
                    return Result.SUCCESS;
                }
            };
        }
    },
    /**
     * 自己卡牌
     */
    SELF_CARD {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.SELF_CARD;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    AbstractCreature skillCaster = caster;
                    if (!RelationshipUtils.isTrainer(skillCaster)) {
                        return Result.FAILURE;
                    }
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(skillCaster);
                    for (long cardId : target.getTargetIds()) {
                        if (!trainer.getUseCardStorage().hasMwn(cardId)) {
                            return Result.FAILURE;
                        }
                    }
                    return Result.SUCCESS;
                }
            };
        }
    },

    /**
     * 友方血量最少魔物娘
     */
    SELF_MIN_HP_MWN {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return SELF_MIN_HP_MWN;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (!RelationshipUtils.isTrainer(caster)) {
                        return Result.FAILURE;
                    }
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(caster);
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    AbstractCreature creature = caster.getWorldMapInstance().getCreatureById(target.getTargetIds().get(0));
                    if (!RelationshipUtils.isMWN(creature)) {
                        return Result.FAILURE;
                    }
                    MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(creature);
                    Collection<MWNCreature> mwnCreatures = trainer.getMWN(true);
                    if (CollectionUtils.isEmpty(mwnCreatures)) {
                        return Result.FAILURE;
                    }
                    MWNCreature minHp = mwnCreatures.stream().sorted(Comparator.comparingInt(MWNCreature::getCurrentHp)).findFirst().get();
                    if (minHp == mwnCreature) {
                        return Result.SUCCESS;
                    }
                    return Result.FAILURE;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainerOrNull(skillCaster);
                    if (trainer == null) {
                        return null;
                    }
                    Collection<MWNCreature> mwn = trainer.getMWN(true);
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> min = mwn.stream().filter(Objects::nonNull).min(Comparator.comparingInt(MWNCreature::getCurrentHp));
                    if (min.isPresent()) {
                        MWNCreature moWuNiangCreature = min.get();
                        return Target.valueOf(moWuNiangCreature.getGridId(), moWuNiangCreature.getObjectId());
                    }
                    return null;
                }
            };
        }
    },

    /**
     * 友方血量比例最低魔物娘
     */
    SELF_MIN_HP_PERCENT_MWN {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return SELF_MIN_HP_PERCENT_MWN;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    if (!RelationshipUtils.isTrainer(caster)) {
                        return Result.FAILURE;
                    }
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(caster);
                    if (target.getTargetIds() == null || CollectionUtils.isEmpty(target.getTargetIds())) {
                        return Result.FAILURE;
                    }
                    AbstractCreature creature = caster.getWorldMapInstance().getCreatureById(target.getTargetIds().get(0));
                    if (!RelationshipUtils.isMWN(creature)) {
                        return Result.FAILURE;
                    }
                    MWNCreature mwnCreature = RelationshipUtils.toMWNCreature(creature);
                    Collection<MWNCreature> mwnCreatures = trainer.getMWN(true);
                    if (CollectionUtils.isEmpty(mwnCreatures)) {
                        return Result.FAILURE;
                    }
                    MWNCreature minHp = mwnCreatures.stream().sorted(Comparator.comparingInt(MWNCreature::getHpPercent)).findFirst().get();
                    if (minHp == mwnCreature) {
                        return Result.SUCCESS;
                    }
                    return Result.FAILURE;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainerOrNull(skillCaster);
                    if (trainer == null) {
                        return null;
                    }
                    Collection<MWNCreature> mwn = trainer.getMWN(true);
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> min = mwn.stream().filter(Objects::nonNull).min(Comparator.comparingInt(MWNCreature::getHpPercent));
                    if (min.isPresent()) {
                        MWNCreature moWuNiangCreature = min.get();
                        return Target.valueOf(moWuNiangCreature.getGridId(), moWuNiangCreature.getObjectId());
                    }
                    return null;
                }
            };
        }
    },
    /**
     * 敌方血量最少魔物娘
     */
    ENEMY_MIN_HP_MWN {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return ENEMY_MIN_HP_MWN;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    List<AbstractCreature> denfendersList = new ArrayList<>();
                    target.getTargetIds().stream().forEach(id -> denfendersList.add(caster.getWorldMapInstance().getCreatureById(id)));
                    if (CollectionUtils.isEmpty(denfendersList)) {
                        return Result.FAILURE;
                    }
                    if (RelationshipUtils.isMWN(denfendersList.get(0))) {
                        return Result.FAILURE;
                    }
                    MWNCreature moWuNiangCreature = RelationshipUtils.toMWNCreature(denfendersList.get(0));
                    AbstractTrainerCreature trainer = moWuNiangCreature.getMaster();
                    WorldMapInstance worldMap = trainer.getWorldMapInstance();
                    Collection<MWNCreature> mwn = worldMap.findMWN();
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> min = mwn.stream().filter(m -> RelationshipUtils.judgeRelationship(trainer, m, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)).min(Comparator.comparingInt(MWNCreature::getCurrentHp));
                    if (min.isPresent()) {
                        MWNCreature minHp = min.get();
                        if (minHp == moWuNiangCreature) {
                            return Result.SUCCESS;
                        }
                    }
                    return Result.FAILURE;
                }

                @Override
                public Target chooseTarget(AbstractCreature trainerCreature) {
                    final AbstractTrainerCreature trainer = RelationshipUtils.toTrainerOrNull(trainerCreature);
                    if (trainer == null) {
                        return null;
                    }
                    WorldMapInstance worldMap = trainer.getWorldMapInstance();
                    Collection<MWNCreature> mwn = worldMap.findMWN();
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> min = mwn.stream().filter(m -> RelationshipUtils.judgeRelationship(trainer, m, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)).min(Comparator.comparingInt(MWNCreature::getCurrentHp));
                    if (min.isPresent()) {
                        return Target.valueOf(min.get().getGridId(), min.get().getObjectId());
                    }
                    return null;
                }
            };
        }
    },

    /**
     * 敌方血量比例最少魔物娘
     */
    ENEMY_MIN_HP_PER_MWN {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return ENEMY_MIN_HP_MWN;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    List<AbstractCreature> denfendersList = new ArrayList<>();
                    target.getTargetIds().stream().forEach(id -> denfendersList.add(caster.getWorldMapInstance().getCreatureById(id)));
                    if (CollectionUtils.isEmpty(denfendersList)) {
                        return Result.FAILURE;
                    }
                    if (RelationshipUtils.isMWN(denfendersList.get(0))) {
                        return Result.FAILURE;
                    }
                    MWNCreature targetMwn = RelationshipUtils.toMWNCreature(denfendersList.get(0));
                    AbstractTrainerCreature targetTrainer = targetMwn.getMaster();
                    Collection<MWNCreature> mwn = targetTrainer.getMWN(true);
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> min = mwn.stream().min(Comparator.comparingInt(MWNCreature::getHpPercent));
                    if (min.isPresent()) {
                        MWNCreature minHp = min.get();
                        if (minHp == targetMwn) {
                            return Result.SUCCESS;
                        }
                    }
                    return Result.FAILURE;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    final AbstractTrainerCreature trainer = RelationshipUtils.toTrainerOrNull(skillCaster);
                    if (trainer == null) {
                        return null;
                    }
                    WorldMapInstance worldMap = trainer.getWorldMapInstance();
                    Collection<MWNCreature> mwn = worldMap.findMWN();
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> min = mwn.stream().filter(m -> RelationshipUtils.judgeRelationship(trainer, m, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)).min(Comparator.comparingInt(MWNCreature::getHpPercent));
                    if (min.isPresent()) {
                        return Target.valueOf(min.get().getGridId(), min.get().getObjectId());
                    }
                    return null;
                }
            };
        }
    },

    /**
     * 敌方血量最多魔物娘
     */
    ENEMY_MAX_HP_MWN {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return ENEMY_MAX_HP_MWN;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    List<AbstractCreature> denfendersList = new ArrayList<>();
                    target.getTargetIds().stream().forEach(id -> denfendersList.add(caster.getWorldMapInstance().getCreatureById(id)));
                    if (CollectionUtils.isEmpty(denfendersList)) {
                        return Result.FAILURE;
                    }
                    if (RelationshipUtils.isMWN(denfendersList.get(0))) {
                        return Result.FAILURE;
                    }
                    MWNCreature targetMwn = RelationshipUtils.toMWNCreature(denfendersList.get(0));
                    AbstractTrainerCreature trainer = targetMwn.getMaster();
                    Collection<MWNCreature> mwn = trainer.getMWN(true);
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> max = mwn.stream().max(Comparator.comparingInt(MWNCreature::getCurrentHp));
                    if (max.isPresent()) {
                        MWNCreature minHp = max.get();
                        if (minHp == targetMwn) {
                            return Result.SUCCESS;
                        }
                    }
                    return Result.FAILURE;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainerOrNull(skillCaster);
                    if (trainer == null) {
                        return null;
                    }
                    WorldMapInstance worldMap = trainer.getWorldMapInstance();
                    Collection<MWNCreature> mwn = worldMap.findMWN();
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> max = mwn.stream().filter(m -> RelationshipUtils.judgeRelationship(trainer, m, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)).max(Comparator.comparingInt(MWNCreature::getCurrentHp));
                    if (max.isPresent()) {
                        return Target.valueOf(max.get().getGridId(), max.get().getObjectId());
                    }
                    return null;
                }
            };
        }
    },

    /**
     * 空格
     */
    EMPTY_POSITION {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return EMPTY_POSITION;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    int gridId = target.getGridId();
                    WorldMapInstance mapInstance = caster.getWorldMapInstance();
                    WorldMap worldMap = mapInstance.getParent();
                    MapGrid mapGrid = worldMap.getMapGrid(gridId);
                    if (mapGrid == null) {
                        return Result.FAILURE;
                    }
                    if (CollectionUtils.isEmpty(mapInstance.findObjects(gridId))) {
                        return Result.SUCCESS;
                    }
                    return Result.FAILURE;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainerOrNull(skillCaster);
                    if (trainer == null) {
                        return null;
                    }
                    WorldMapInstance worldMap = trainer.getWorldMapInstance();
                    Collection<MWNCreature> mwn = worldMap.findMWN();
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> min = mwn.stream().filter(m -> RelationshipUtils.judgeRelationship(trainer, m, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)).min(Comparator.comparingInt(MWNCreature::getCurrentHp));
                    if (min.isPresent()) {
                        return Target.valueOf(min.get().getGridId(), min.get().getObjectId());
                    }
                    return null;
                }
            };
        }
    },
    /**
     * 己方魔物娘占领格子
     */
    SELF_MWN_POSITION {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return SELF_MWN_POSITION;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    int gridId = target.getGridId();
                    WorldMapInstance mapInstance = caster.getWorldMapInstance();
                    WorldMap worldMap = mapInstance.getParent();
                    MapGrid mapGrid = worldMap.getMapGrid(gridId);
                    if (mapGrid == null) {
                        return Result.FAILURE;
                    }
                    if (!RelationshipUtils.isTrainer(caster)) {
                        return Result.FAILURE;
                    }
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(caster);
                    MWNCreature mwnByGridId = mapInstance.getMWNByGridId(gridId);
                    if (mwnByGridId == null) {
                        return Result.FAILURE;
                    }
                    Collection<MWNCreature> aliveMwns = trainer.getMWN(true);
                    if (aliveMwns != null && aliveMwns.stream().filter(mwn -> mwnByGridId == mwn).findAny().isPresent()) {
                        return Result.SUCCESS;
                    }
                    return Result.FAILURE;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    if (!RelationshipUtils.isTrainer(skillCaster)) {
                        return null;
                    }
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(skillCaster);
                    WorldMapInstance worldMap = trainer.getWorldMapInstance();
                    Collection<MWNCreature> mwn = worldMap.findMWN();
                    if (CollectionUtils.isEmpty(mwn)) {
                        return null;
                    }
                    Optional<MWNCreature> min = mwn.stream().filter(m -> RelationshipUtils.judgeRelationship(trainer, m, RelationshipUtils.Relationships.ENEMY_TRAINER_MWN)).min(Comparator.comparingInt(MWNCreature::getCurrentHp));
                    if (min.isPresent()) {
                        return Target.valueOf(min.get().getGridId(), min.get().getObjectId());
                    }
                    return null;
                }
            };
        }
    },

    /**
     * 击败释放对象的魔物娘
     */
    ENEMY_MWN_BEAT_SELF {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.ENEMY_MWN_BEAT_SELF;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    List<AbstractCreature> denfendersList = new ArrayList<>();
                    target.getTargetIds().stream().forEach(id -> denfendersList.add(caster.getWorldMapInstance().getCreatureById(id)));
                    if (CollectionUtils.isEmpty(denfendersList)) {
                        return Result.FAILURE;
                    }
                    AbstractCreature creature = denfendersList.get(0);
                    if (!RelationshipUtils.isMWN(creature)) {
                        return Result.FAILURE;
                    }
                    MWNCreature enemy = RelationshipUtils.toMWNCreature(creature);
                    if (RelationshipUtils.isMWN(caster)) {
                        MWNCreature selfMWN = RelationshipUtils.toMWNCreature(caster);
                        MWNCreature killer = selfMWN.getKiller();
                        if (killer == enemy) {
                            return Result.SUCCESS;
                        } else {
                            return Result.FAILURE;
                        }
                    }
                    if (RelationshipUtils.isTrainer(caster)) {
                        AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(caster);
                        Collection<MWNCreature> aliveMwns = trainer.getMWN(true);
                        if (CollectionUtils.isEmpty(aliveMwns)) {
                            return Result.FAILURE;
                        }
                        Optional<MWNCreature> any = aliveMwns.stream().filter(mwn -> mwn.isAlreadyDead() && enemy == mwn.getKiller()).findAny();
                        if (any.isPresent()) {
                            return Result.SUCCESS;
                        }
                        return Result.FAILURE;
                    }
                    return Result.FAILURE;
                }
            };
        }
    },

    /***
     * 己方魔物娘拥有职业属性
     */
    SELF_MWN_HAS_JOB_ELE_ATTR {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.SELF_MWN_HAS_JOB_ELE_ATTR;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    Result result = TargetManager.getInstance().getTargetChooser(TargetType.SELF_MWN).verifyTarget(caster, target);
                    if (result == Result.FAILURE) {
                        return result;
                    }
                    List<MWNCreature> mwns = new ArrayList<>();
                    target.getTargetIds().stream().forEach(id -> mwns.add((MWNCreature) caster.getWorldMapInstance().getCreatureById(id)));
                    if (CollectionUtils.isEmpty(mwns)) {
                        return Result.FAILURE;
                    }
                    for (MWNCreature creature : mwns) {
                        if (!creature.hasJobAttr() && !creature.hasEleAttr()) {
                            return Result.FAILURE;
                        }
                    }
                    return Result.SUCCESS;
                }
            };
        }
    },

    /**
     * 满足某个属性点
     */
    HAS_FIX_ATTRTYPE {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return TargetType.HAS_FIX_ATTRTYPE;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    throw new UnsupportedOperationException("HAS_FIX_ATTRTYPE  must use verifyTarget(AbstractCreature caster, Target target, Object params)");
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target, Object params) {
                    List<AbstractCreature> creatures = new ArrayList<>();
                    target.getTargetIds().stream().forEach(id -> creatures.add(caster.getWorldMapInstance().getCreatureById(id)));
                    if (CollectionUtils.isEmpty(creatures)) {
                        return Result.FAILURE;
                    }
                    List<String> attrs = JsonUtils.string2List((String) params, String.class);
                    if (!CollectionUtils.isEmpty(attrs)) {
                        for (String s : attrs) {
                            AttrType attrType = AttrType.valueOf(s);
                            for (AbstractCreature creature : creatures) {
                                if (!creature.hasAttr(attrType)) {
                                    return Result.FAILURE;
                                }
                            }
                        }
                    }
                    return Result.SUCCESS;
                }
            };
        }
    },


    /**
     * 任意目标
     */
    ANY {
        @Override
        public AbstractTargetChooser createTargetChooser() {
            return new AbstractTargetChooser() {
                @Override
                public TargetType getTargetType() {
                    return ANY;
                }

                @Override
                public Result verifyTarget(AbstractCreature caster, Target target) {
                    return Result.SUCCESS;
                }

                @Override
                public Target chooseTarget(AbstractCreature skillCaster) {
                    if (!RelationshipUtils.isTrainer(skillCaster)) {
                        return null;
                    }
                    AbstractTrainerCreature trainer = RelationshipUtils.toTrainer(skillCaster);
                    WorldMapInstance worldMap = trainer.getWorldMapInstance();
                    AbstractVisibleObject object = worldMap.objectIterator().next();
                    return Target.valueOf(object.getGridId(), object.getObjectId());
                }
            };
        }
    },
    ;

    public abstract AbstractTargetChooser createTargetChooser();

    public static void main(String[] args) {
        Arrays.stream(TargetType.values()).forEach(targetType -> System.err.println(targetType.name()));
    }
}
