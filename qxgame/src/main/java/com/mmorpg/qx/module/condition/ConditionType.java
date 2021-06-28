package com.mmorpg.qx.module.condition;

import com.mmorpg.qx.module.condition.impl.creature.*;
import com.mmorpg.qx.module.condition.impl.effectTrigger.*;
import com.mmorpg.qx.module.condition.impl.player.PlayerLevelCondition;
import com.mmorpg.qx.module.condition.impl.skill.*;
import com.mmorpg.qx.module.condition.resource.ConditionResource;
import com.mmorpg.qx.module.quest.condition.QuestCompleteCondition;
import com.mmorpg.qx.module.quest.condition.QuestNoCompleteCondition;
import com.mmorpg.qx.module.quest.condition.QuestTodayCompleteCondition;
import com.mmorpg.qx.module.quest.condition.QuestTodayNoCompleteCondition;

public enum ConditionType {

    /**
     * 玩家等级
     */
    PLAYER_LEVEL {
        @Override
        public PlayerLevelCondition create() {
            PlayerLevelCondition playerLevelCondition = new PlayerLevelCondition();
            playerLevelCondition.setType(this);
            return playerLevelCondition;
        }

        @Override
        public PlayerLevelCondition create(ConditionResource resource) {
            PlayerLevelCondition condition = this.create();
            condition.init(resource);
            condition.setLow(resource.getMin());
            condition.setHigh(resource.getMax());
            condition.setType(this);
            return condition;
        }

    },
    /**
     * 任务未完成
     */
    QUEST_NOCOMPLETE {
        @Override
        public QuestNoCompleteCondition create() {
            QuestNoCompleteCondition questNoCompleteCondition = new QuestNoCompleteCondition();
            questNoCompleteCondition.setType(this);
            return questNoCompleteCondition;
        }

        @Override
        public QuestNoCompleteCondition create(ConditionResource resource) {
            QuestNoCompleteCondition condition = new QuestNoCompleteCondition();
            condition.init(resource);
            condition.setType(this);
            return condition;
        }

    },
    /**
     * 任务完成
     */
    QUEST_COMPLETE {
        @Override
        public QuestCompleteCondition create() {
            QuestCompleteCondition condition = new QuestCompleteCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public QuestCompleteCondition create(ConditionResource resource) {
            QuestCompleteCondition condition = this.create();
            condition.init(resource);
            condition.setType(this);
            return condition;
        }

    },
    /**
     * 日常任务完成
     */
    QUEST_TODAY_COMPLETE {
        @Override
        public QuestTodayCompleteCondition create() {
            QuestTodayCompleteCondition condition = new QuestTodayCompleteCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public QuestTodayCompleteCondition create(ConditionResource resource) {
            QuestTodayCompleteCondition condition = this.create();
            condition.init(resource);
            condition.setType(this);
            return condition;
        }

    },
    /**
     * 日常任务未完成
     */
    QUEST_TODAY_NO_COMPLETE {
        @Override
        public QuestTodayNoCompleteCondition create() {
            QuestTodayNoCompleteCondition condition = new QuestTodayNoCompleteCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public QuestTodayNoCompleteCondition create(ConditionResource resource) {
            QuestTodayNoCompleteCondition condition = this.create();
            condition.init(resource);
            condition.setType(this);
            return condition;
        }
    },


    /************************************************  creature类条件开始  *************************************/
    /**
     * 对象存在于场上
     */
    IN_MAP {
        @Override
        public InMapCondition create() {
            InMapCondition condition = new InMapCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public InMapCondition create(ConditionResource resource) {
            InMapCondition condition = new InMapCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 属性条件
     */
    ATTR {
        @Override
        public AttrCondition create() {
            AttrCondition condition = new AttrCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public AttrCondition create(ConditionResource resource) {
            AttrCondition condition = new AttrCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 生命值变化条件
     */
    HP_ALTER {
        @Override
        public HpAlterCondition create() {
            HpAlterCondition condition = new HpAlterCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public HpAlterCondition create(ConditionResource resource) {
            HpAlterCondition condition = new HpAlterCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 生存条件
     */
    IN_LIVING {
        @Override
        public InLivingCondition create() {
            InLivingCondition condition = new InLivingCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public InLivingCondition create(ConditionResource resource) {
            InLivingCondition condition = new InLivingCondition();
            condition.init(resource);
            return condition;
        }
    },

    /************************************************  creature类条件结束  *************************************/


    /************************************************  技能类条件开始  *************************************/
    /**
     * 回合条件
     */
    SKILL_ROUND {
        @Override
        public SkillRoundCondition create() {
            SkillRoundCondition condition = new SkillRoundCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public SkillRoundCondition create(ConditionResource resource) {
            SkillRoundCondition condition = new SkillRoundCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 魔物娘平均等级
     */
    MWN_LV_AVG {
        @Override
        public MWNLvAvgCondition create() {
            MWNLvAvgCondition condition = new MWNLvAvgCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public MWNLvAvgCondition create(ConditionResource resource) {
            MWNLvAvgCondition condition = new MWNLvAvgCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 被施法者生命值百分比
     */
    SKILL_EFFECTED_HP_PERCENT {
        @Override
        public SkillEffectedHpPercentCondition create() {
            SkillEffectedHpPercentCondition condition = new SkillEffectedHpPercentCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public SkillEffectedHpPercentCondition create(ConditionResource resource) {
            SkillEffectedHpPercentCondition condition = new SkillEffectedHpPercentCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 施法者生命值百分比
     */
    SKILL_EFFECTOR_HP_PERCENT {
        @Override
        public EffectorHpPercentCondition create() {
            EffectorHpPercentCondition condition = new EffectorHpPercentCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public EffectorHpPercentCondition create(ConditionResource resource) {
            EffectorHpPercentCondition condition = new EffectorHpPercentCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 被施法者生命值
     */
    SKILL_EFFECTED_HP {
        @Override
        public SkillEffectedHpCondition create() {
            SkillEffectedHpCondition condition = new SkillEffectedHpCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public SkillEffectedHpCondition create(ConditionResource resource) {
            SkillEffectedHpCondition condition = new SkillEffectedHpCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 施法者生命值
     */
    SKILL_EFFECTOR_HP {
        @Override
        public SkillEffectorHpCondition create() {
            SkillEffectorHpCondition condition = new SkillEffectorHpCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public SkillEffectorHpCondition create(ConditionResource resource) {
            SkillEffectorHpCondition condition = new SkillEffectorHpCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 魔物娘数量
     */
    SKILL_MWN_NUM {
        @Override
        public MWNNumsCondition create() {
            MWNNumsCondition condition = new MWNNumsCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public MWNNumsCondition create(ConditionResource resource) {
            MWNNumsCondition condition = new MWNNumsCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 技能回合后
     */
    SKILL_AFTER_ROUND {
        @Override
        public AfterRoundCondition create() {
            return new AfterRoundCondition();
        }

        @Override
        public AfterRoundCondition create(ConditionResource resource) {
            AfterRoundCondition condition = new AfterRoundCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 技能属性条件
     */
    SKILL_ATTR {
        @Override
        public SkillAttrCondition create() {
            SkillAttrCondition condition = new SkillAttrCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public SkillAttrCondition create(ConditionResource resource) {
            SkillAttrCondition condition = new SkillAttrCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 处于某个阶段使用技能
     */
    SKILL_ROUND_STAGE {
        @Override
        public SkillRoundStageCondition create() {
            SkillRoundStageCondition condition = new SkillRoundStageCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public SkillRoundStageCondition create(ConditionResource resource) {
            SkillRoundStageCondition condition = new SkillRoundStageCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 穿戴装备触发
     */
    SKILL_WEAR_EQUIP {
        @Override
        public WearEquipCondition create() {
            WearEquipCondition condition = new WearEquipCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public WearEquipCondition create(ConditionResource resource) {
            WearEquipCondition condition = new WearEquipCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 装备数量达到多少件不能释放
     */
    SKILL_EQUIP_NUM_LIMIT {
        @Override
        public EquipNumLimitSkillCondition create() {
            EquipNumLimitSkillCondition condition = new EquipNumLimitSkillCondition();
            condition.setType(this);
            return condition;
        }

        @Override
        public EquipNumLimitSkillCondition create(ConditionResource resource) {
            EquipNumLimitSkillCondition condition = new EquipNumLimitSkillCondition();
            condition.init(resource);
            return condition;
        }
    },
    /************************************************  技能类条件结束  *************************************/


    /************************************************  效果触发类条件开始  *************************************/
    /**
     * 被攻击
     */
    TRIGGER_ATTACKED {
        @Override
        public AfterAttackedCondition create() {
            return new AfterAttackedCondition();
        }

        @Override
        public AfterAttackedCondition create(ConditionResource resource) {
            AfterAttackedCondition condition = new AfterAttackedCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 新回合开始
     */
    TRIGGER_ROUND_BEGIN {
        @Override
        public NewRoundCondition create() {
            return new NewRoundCondition();
        }

        @Override
        public NewRoundCondition create(ConditionResource resource) {
            NewRoundCondition condition = new NewRoundCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 死亡
     */
    TRIGGER_DIE {
        @Override
        public AfterDieCondition create() {
            return new AfterDieCondition();
        }

        @Override
        public AfterDieCondition create(ConditionResource resource) {
            AfterDieCondition condition = new AfterDieCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 受攻击
     */
    TRIGGER_Attacked {
        @Override
        public AfterAttackedCondition create() {
            return new AfterAttackedCondition();
        }

        @Override
        public AfterAttackedCondition create(ConditionResource resource) {
            AfterAttackedCondition condition = new AfterAttackedCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 使用技能后触发
     */
    TRIGGER_Use_Skill {
        @Override
        public AfterUseSkillCondition create() {
            return new AfterUseSkillCondition();
        }

        @Override
        public AfterUseSkillCondition create(ConditionResource resource) {
            AfterUseSkillCondition condition = new AfterUseSkillCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 属性变化触发
     */
    TRIGGER_Attr_Alter {
        @Override
        public AttrAlterCondition create() {
            return new AttrAlterCondition();
        }

        @Override
        public AttrAlterCondition create(ConditionResource resource) {
            AttrAlterCondition condition = new AttrAlterCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 骰子点数触发
     */
    TRIGGER_DICE_POINT {
        @Override
        public DicePointCondition create() {
            return new DicePointCondition();
        }

        @Override
        public DicePointCondition create(ConditionResource resource) {
            DicePointCondition condition = new DicePointCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 魔物娘之间战斗死亡触发
     */
    TRIGGER_DIE_MWN_MWN {
        @Override
        public DieWithMwnFightCondition create() {
            return new DieWithMwnFightCondition();
        }

        @Override
        public DieWithMwnFightCondition create(ConditionResource resource) {
            DieWithMwnFightCondition condition = new DieWithMwnFightCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 技能效果死亡触发
     */
    TRIGGER_DIE_WITH_SKILL_EFFECT {
        @Override
        public DieWithSkillEffectCondition create() {
            return new DieWithSkillEffectCondition();
        }

        @Override
        public DieWithSkillEffectCondition create(ConditionResource resource) {
            DieWithSkillEffectCondition condition = new DieWithSkillEffectCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * buff生效后触发其他buff
     */
    TRIGGER_EFFECT_APPLY {
        @Override
        public EffectApplyTriggerCondition create() {
            return new EffectApplyTriggerCondition();
        }

        @Override
        public EffectApplyTriggerCondition create(ConditionResource resource) {
            EffectApplyTriggerCondition condition = new EffectApplyTriggerCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 路过或停留格子
     */
    TRIGGER_MOVE_PASS {
        @Override
        public MovePassCondition create() {
            return new MovePassCondition();
        }

        @Override
        public MovePassCondition create(ConditionResource resource) {
            MovePassCondition condition = new MovePassCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 魔物娘之间战斗获胜
     */
    TRIGGER_MWN_FIGHT_WIN {
        @Override
        public MwnFightWinCondition create() {
            return new MwnFightWinCondition();
        }

        @Override
        public MwnFightWinCondition create(ConditionResource resource) {
            MwnFightWinCondition condition = new MwnFightWinCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 新回合动作触发
     */
    TRIGGER_NEW_ROUND {
        @Override
        public NewRoundCondition create() {
            return new NewRoundCondition();
        }

        @Override
        public NewRoundCondition create(ConditionResource resource) {
            NewRoundCondition condition = new NewRoundCondition();
            condition.init(resource);
            return condition;
        }
    },

    /**
     * 驯养师主动魔法
     */
    TRIGGER_TRAINER_ACTIVE_SKILL {
        @Override
        public TrainerActiveSkillCondition create() {
            return new TrainerActiveSkillCondition();
        }

        @Override
        public TrainerActiveSkillCondition create(ConditionResource resource) {
            TrainerActiveSkillCondition condition = new TrainerActiveSkillCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 召唤出新魔物娘
     */
    Trigger_SPAWN {
        @Override
        public AfterSpawnCondition create() {
            return new AfterSpawnCondition();
        }

        @Override
        public AfterSpawnCondition create(ConditionResource resource) {
            AfterSpawnCondition condition = new AfterSpawnCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 回合结束阶段触发效果
     */
    TRIGGER_RS_END {
        @Override
        public AfterRSEndCondition create() {
            return new AfterRSEndCondition();
        }

        @Override
        public AfterRSEndCondition create(ConditionResource resource) {
            AfterRSEndCondition condition = new AfterRSEndCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 具体魔物娘死亡
     */
    TRIGGER_DIE_MWN_FIX_ATTR {
        @Override
        public DieMwnFixAttrCondition create() {
            return new DieMwnFixAttrCondition();
        }

        @Override
        public DieMwnFixAttrCondition create(ConditionResource resource) {
            DieMwnFixAttrCondition condition = new DieMwnFixAttrCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 连袭
     */
    LIAN_XI {
        @Override
        public LianXiCondition create() {
            return new LianXiCondition();
        }

        @Override
        public LianXiCondition create(ConditionResource resource) {
            LianXiCondition condition = new LianXiCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 召唤魔物娘，卡牌中的位置
     */
    CALL_MWN_INDEX {
        @Override
        public CallMwnIndexCondition create() {
            return new CallMwnIndexCondition();
        }

        @Override
        public CallMwnIndexCondition create(ConditionResource resource) {
            CallMwnIndexCondition condition = new CallMwnIndexCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 场上位置
     */
    CALL_MWN_GRID_POS {
        @Override
        public CallMwnGridPosCondition create() {
            return new CallMwnGridPosCondition();
        }

        @Override
        public CallMwnGridPosCondition create(ConditionResource resource) {
            CallMwnGridPosCondition condition = new CallMwnGridPosCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 感应
     */
    CALL_MWN_SENSE {
        @Override
        public SenseCondition create() {
            return new SenseCondition();
        }

        @Override
        public SenseCondition create(ConditionResource resource) {
            SenseCondition condition = new SenseCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 换阵
     */
    REPLACE {
        @Override
        public ReplaceCondition create() {
            return new ReplaceCondition();
        }

        @Override
        public ReplaceCondition create(ConditionResource resource) {
            ReplaceCondition condition = new ReplaceCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 对战前
     */
    MWN_BEFORE_FIGHT {
        @Override
        public MwnBeforeFightCondition create() {
            return new MwnBeforeFightCondition();
        }

        @Override
        public MwnBeforeFightCondition create(ConditionResource resource) {
            MwnBeforeFightCondition condition = new MwnBeforeFightCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 魔物娘对战前作为攻击方
     */
    MWN_BEFORE_FIGHT_ATTACKER {
        @Override
        public AttackerMwnBeforeFightCondition create() {
            return new AttackerMwnBeforeFightCondition();
        }

        @Override
        public AttackerMwnBeforeFightCondition create(ConditionResource resource) {
            AttackerMwnBeforeFightCondition condition = new AttackerMwnBeforeFightCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 魔物娘对战前作为防守方
     */
    MWN_BEFORE_FIGHT_DEFENDER {
        @Override
        public DefenderMwnBeforeFightCondition create() {
            return new DefenderMwnBeforeFightCondition();
        }

        @Override
        public DefenderMwnBeforeFightCondition create(ConditionResource resource) {
            DefenderMwnBeforeFightCondition condition = new DefenderMwnBeforeFightCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 魔物娘对战前敌方
     */
    MWN_BEFORE_FIGHT_ENEMY {
        @Override
        public EnemyMwnBeforeFightCondition create() {
            return new EnemyMwnBeforeFightCondition();
        }

        @Override
        public EnemyMwnBeforeFightCondition create(ConditionResource resource) {
            EnemyMwnBeforeFightCondition condition = new EnemyMwnBeforeFightCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 魔物娘战斗胜利
     */
    MWN_FIGHT_WIN {
        @Override
        public MwnFightWinCondition create() {
            return new MwnFightWinCondition();
        }

        @Override
        public MwnFightWinCondition create(ConditionResource resource) {
            MwnFightWinCondition condition = new MwnFightWinCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 魔物娘战斗失败
     */
    MWN_FIGHT_LOSE {
        @Override
        public MwnFightLoseCondition create() {
            return new MwnFightLoseCondition();
        }

        @Override
        public MwnFightLoseCondition create(ConditionResource resource) {
            MwnFightLoseCondition condition = new MwnFightLoseCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 回合内使用技能
     */
    MWN_USE_SKILL_TIME_ROUND {
        @Override
        public MwnUseSkillTimeRoundCondition create() {
            return new MwnUseSkillTimeRoundCondition();
        }

        @Override
        public MwnUseSkillTimeRoundCondition create(ConditionResource resource) {
            MwnUseSkillTimeRoundCondition condition = new MwnUseSkillTimeRoundCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 召唤魔物娘
     */
    CALL_MWN {
        @Override
        public CallMwnCondition create() {
            return new CallMwnCondition();
        }

        @Override
        public CallMwnCondition create(ConditionResource resource) {
            CallMwnCondition condition = new CallMwnCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 回合情况
     */
    ROUND {
        @Override
        public RoundCondition create() {
            return new RoundCondition();
        }

        @Override
        public RoundCondition create(ConditionResource resource) {
            RoundCondition condition = new RoundCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 敌方进入指定阶段
     */
    ENEMY_ENTER_ROUND_STAGE {
        @Override
        public EnemyEnterRoundStageCondition create() {
            return new EnemyEnterRoundStageCondition();
        }

        @Override
        public EnemyEnterRoundStageCondition create(ConditionResource resource) {
            EnemyEnterRoundStageCondition condition = new EnemyEnterRoundStageCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 指定阶段
     */
    ENTER_ROUND_STAGE {
        @Override
        public EnterRoundStageCondition create() {
            return new EnterRoundStageCondition();
        }

        @Override
        public EnterRoundStageCondition create(ConditionResource resource) {
            EnterRoundStageCondition condition = new EnterRoundStageCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 鼓励
     */
    ENCOURAGE {
        @Override
        public EncourageCondition create() {
            return new EncourageCondition();
        }

        @Override
        public EncourageCondition create(ConditionResource resource) {
            EncourageCondition condition = new EncourageCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 回合能使用技能次数
     */
    USE_SKILL_ROUND {
        @Override
        public UseSkillRoundCondition create() {
            return new UseSkillRoundCondition();
        }

        @Override
        public UseSkillRoundCondition create(ConditionResource resource) {
            UseSkillRoundCondition condition = new UseSkillRoundCondition();
            condition.init(resource);
            return condition;
        }
    },
    /**
     * 回合内消耗魔法值
     */
    CONSUME_MP_ROUND {
        @Override
        public ConsumeMpRoundCondition create() {
            return new ConsumeMpRoundCondition();
        }

        @Override
        public ConsumeMpRoundCondition create(ConditionResource resource) {
            ConsumeMpRoundCondition condition = new ConsumeMpRoundCondition();
            condition.init(resource);
            return condition;
        }
    }
    /************************************************  触发类条件结束  *************************************/


    ;

    public abstract <T extends AbstractCondition> T create();

    public abstract <T extends AbstractCondition> T create(ConditionResource resource);
}
