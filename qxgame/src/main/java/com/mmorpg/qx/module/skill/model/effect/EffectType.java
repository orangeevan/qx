package com.mmorpg.qx.module.skill.model.effect;

import com.mmorpg.qx.module.skill.model.effect.impl.*;

/**
 * 技能效果类型
 *
 * @author wang ke
 * @since v1.0 2018年3月3日
 */
public enum EffectType {
    /**
     * 添加或降低属性值
     */
    Alter_ATTR {
        @Override
        public AlterAttrEffect create() {
            return new AlterAttrEffect();
        }
    },


    /**
     * 伤害增加
     */
    Damage_Add {
        @Override
        public DamageAddEffect create() {
            return new DamageAddEffect();
        }
    },
    /**
     * 伤害减少
     */
    Damage_Reduce {
        @Override
        public DamageReduceEffect create() {
            return new DamageReduceEffect();
        }
    },
    /**
     * 恶魔火
     */
    Fire {
        @Override
        public FireEffect create() {
            return new FireEffect();
        }
    },
    /**
     * 从当前手牌扣除魔物娘
     */
    MWN_Reduce {
        @Override
        public FixMwnReduceEffect create() {
            return new FixMwnReduceEffect();
        }
    },
    /**
     * 获得装备
     */
    Equip_Gain {
        @Override
        public FixEquipGainEffect create() {
            return new FixEquipGainEffect();
        }
    },
    /**
     * 改变生命值
     */
    Hp_Alter {
        @Override
        public HpAlterEffect create() {
            return new HpAlterEffect();
        }
    },
    /**
     * 判胜
     */
    Judge_Winner {
        @Override
        public JudgeWinnerEffect create() {
            return new JudgeWinnerEffect();
        }
    },
    /**
     * 限制移动
     */
    Limit_Move {
        @Override
        public LimitMoveEffect create() {
            return new LimitMoveEffect();
        }
    },
    /**
     * 限制骰子
     */
    Limit_Throw_Dice {
        @Override
        public LimitThrowDiceEffect create() {
            return new LimitThrowDiceEffect();
        }
    },
    /**
     * 用于标识的buff
     */
    Mark_Buff {
        @Override
        public MarkBuffEffect create() {
            return new MarkBuffEffect();
        }
    },
    /**
     * 操作费用加倍
     */
    Operate_Cost {
        @Override
        public OperateCostEffect create() {
            return new OperateCostEffect();
        }
    },
    /**
     * 随机装备
     */
    Random_Equip {
        @Override
        public RandomEquipEffect create() {
            return new RandomEquipEffect();
        }
    },
    /**
     * 分担伤害
     */
    Share_Damage {
        @Override
        public ShareDamageEffect create() {
            return new ShareDamageEffect();
        }
    },
    /**
     * 护盾
     */
    Shield {
        @Override
        public ShieldEffect create() {
            return new ShieldEffect();
        }
    },

    /**
     * 溅射
     */
    Splash_Damage {
        @Override
        public SplashDamageEffect create() {
            return new SplashDamageEffect();
        }
    },
    /**
     * 禁止操作建筑
     */
    Limit_OP_Build {
        @Override
        public LimitOpBuildEffect create() {
            return new LimitOpBuildEffect();
        }
    },

    /**
     * 死亡后对攻击者造成伤害
     */
    Die_Damage {
        @Override
        public DieDamageEffect create() {
            return new DieDamageEffect();
        }
    },

    /**
     * 强袭
     */
    Assault_Attack {
        @Override
        public AssaultAttackEffect create() {
            return new AssaultAttackEffect();
        }
    },
    /**
     * 投骰子点数为偶数造成伤害
     */
    EVEN_DICE_DAMAGE {
        @Override
        public EvenDiceDamageEffect create() {
            return new EvenDiceDamageEffect();
        }
    },

    /**
     * 投骰子后，点数为指定几个数字，到达位置的前后格子为未被占领的魔物娘格，可在此地格额外召唤一张手牌中的魔物娘
     */
    Fix_Dice_Call_Mwn {
        @Override
        public FixDicePointCallMwn create() {
            return new FixDicePointCallMwn();
        }
    },

    /**
     * 移动后至回合结束期间，召唤的魔物娘武力值+1。
     */
    Move_End_Add_Trainer_Harm {
        @Override
        public MoveAddTrainerHarm create() {
            return new MoveAddTrainerHarm();
        }
    },

    /**
     * 改变投骰子点数
     */
    Dice_Points_Change {
        @Override
        public DicePointsChangeEffect create() {
            return new DicePointsChangeEffect();
        }
    },
    /**
     * 随机地格召唤魔物娘
     */
    Call_Mwn_Random_Grid {
        @Override
        public CallMwnRandomGridEffect create() {
            return new CallMwnRandomGridEffect();
        }
    },
    /**
     * 偷金币
     */
    Steal_Trainer_Gold {
        @Override
        public StealGoldEffect create() {
            return new StealGoldEffect();
        }
    },
    /**
     * 偷魔法
     */
    Steal_Trainer_Mp {
        @Override
        public StealMpEffect create() {
            return new StealMpEffect();
        }
    },
    /**
     * 改变魔物娘武力值
     */
    Alter_Mwn_Trainer_Harm {
        @Override
        public AlterMwnTrainerHarmEffect create() {
            return new AlterMwnTrainerHarmEffect();
        }
    },

    /**
     * 免除建筑操作额外费用
     */
    Build_Op_Reduce_Cost {
        @Override
        public BuildOpReduceCostEffect create() {
            return new BuildOpReduceCostEffect();
        }
    },

    /**
     * 魔物强度降低伤害效果
     */
    Magicstren_Dec_Harm {
        @Override
        public MagicstrenDecHarmEffect create() {
            return new MagicstrenDecHarmEffect();
        }
    },

    /**
     * 魔法强度增加恢复
     */
    Magicstren_Inc_Rec {
        @Override
        public MagicstrenIncRecEffect create() {
            return new MagicstrenIncRecEffect();
        }
    },

    /**
     * 新回合初扣除受魔法强度影响血量
     */
    New_Round_Ms_Reduce_Hp {
        @Override
        public NewRoundMsReduceHpEffect create() {
            return new NewRoundMsReduceHpEffect();
        }
    },
    /**
     * 新回合初扣除血量
     */
    New_Round_Reduce_Hp {
        @Override
        public NewRoundReduceHpEffect create() {
            return new NewRoundReduceHpEffect();
        }
    },
    /**
     * 提升金币获取数量
     */
    Inc_Gold {
        @Override
        public IncGoldEffect create() {
            return new IncGoldEffect();
        }
    },
    /**
     * 清除型buff
     */
    Clear {
        @Override
        public ClearEffect create() {
            return new ClearEffect();
        }
    },
    /**
     * 死亡后返回手牌
     */
    Mwn_Dead_To_Card {
        @Override
        public MwnDeadToCardEffect create() {
            return new MwnDeadToCardEffect();
        }
    },
    /**
     * 加强别的buff
     */
    Strength_Other {
        @Override
        public StrengthOtherEffect create() {
            return new StrengthOtherEffect();
        }
    },
    /**
     * 死亡后随机失去卡牌抽取卡牌
     */
    Gain_Loss_Card_After_Die {
        @Override
        public GainLossCardAfterDieEffect create() {
            return new GainLossCardAfterDieEffect();
        }
    },
    /**
     * 随机获取一张或数张卡牌
     */
    Random_Gain_Mwn {
        @Override
        public RandomGainMwnEffect create() {
            return new RandomGainMwnEffect();
        }
    },
    /**
     * 随机丢失一张或数张卡牌
     */
    Random_Reduce_Mwn {
        @Override
        public RandomReduceMwnEffect create() {
            return new RandomReduceMwnEffect();
        }
    },
    /**
     * 随机从牌库丢失一张或多张卡牌
     */
    Random_Reduce_Mwn_Source {
        @Override
        public RandomReduceMwnSourceEffect create() {
            return new RandomReduceMwnSourceEffect();
        }
    },

    /**
     * 添加新效果
     */
    Add_Effects {
        @Override
        public AddNewEffect create() {
            return new AddNewEffect();
        }
    },

    /**
     * 禁止使用装备
     */
    Limit_Use_Equip {
        @Override
        public LimitUseEqupEffect create() {
            return new LimitUseEqupEffect();
        }
    },

    /**
     * 禁止使用技能
     */
    Limit_Use_Skill {
        @Override
        public LimitUseSkillEffect create() {
            return new LimitUseSkillEffect();
        }
    },
    /**
     * 禁止召唤
     */
    Limit_Call_Mwn {
        @Override
        public LimitCallMwnEffect create() {
            return new LimitCallMwnEffect();
        }
    },
    /**
     * 获得金币
     */
    Add_Gold {
        @Override
        public AddGoldEffect create() {
            return new AddGoldEffect();
        }
    },
    /**
     * 投骰子对敌方驯养师造成等同点子数伤害
     */
    Throw_Dice_Damage {
        @Override
        public ThrowDiceDamageEffect create() {
            return new ThrowDiceDamageEffect();
        }
    },
    /**
     * 挑衅
     */
    Provocation {
        @Override
        public ProvocationEffect create() {
            return new ProvocationEffect();
        }
    },
    /**
     * 反噬
     */
    Bite_Back {
        @Override
        public BiteBackEffect create() {
            return new BiteBackEffect();
        }
    },
    /**
     * 闪避
     */
    Miss {
        @Override
        public MissEffect create() {
            return new MissEffect();
        }
    },
    /**
     * 封锁
     */
    Block {
        @Override
        public BlockEffect create() {
            return new BlockEffect();
        }
    },
    /**
     * 瘫痪
     */
    Paralysis {
        @Override
        public ParalysisEffect create() {
            return new ParalysisEffect();
        }
    },
    /**
     * 冲锋
     */
    Charge {
        @Override
        public ChargeEffect create() {
            return new ChargeEffect();
        }
    },
    /**
     * 施咒
     */
    Curse {
        @Override
        public CurseEffect create() {
            return new CurseEffect();
        }
    },
    /**
     * 魅惑
     */
    ConfuseEffect {
        @Override
        public ConfuseEffect create() {
            return new ConfuseEffect();
        }
    },
    /**
     * 模仿
     */
    ImitateEffect {
        @Override
        public ImitateEffect create() {
            return new ImitateEffect();
        }
    },
    /**
     * 伴生
     */
    BanshengEffect {
        @Override
        public BanshengEffect create() {
            return new BanshengEffect();
        }
    },
    /**
     *连袭
     */
    Lianxi {
        @Override
        public LianXiEffect create() {
            return new LianXiEffect();
        }
    },
    /**
     * 沉默
     */
    Silence{
        @Override
        public SilenceEffect create() {
            return new SilenceEffect();
        }
    },
    /**
     * 隐身
     */
    Invsible{
        @Override
        public InvsibleEffect create() {
            return new InvsibleEffect();
        }
    };


    public abstract <T extends AbstractEffectTemplate> T create();
}
