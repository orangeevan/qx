package com.mmorpg.qx.module.skill.model.effect;

import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.controllers.observer.ActionObserver;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 技能效果,效果业务对象
 *
 * @author wang ke
 * @since v1.0 2018年3月3日
 */
public class Effect {

    /**
     * 技能配置表
     */
    private int skillResourceId;

    /**
     * 效果配置表
     */
    private int effectResourceId;

    /**
     * 该效果处理器
     */
    private AbstractEffectTemplate effectTemplate;

    /**
     * 承受者(效果在承受者身上)
     */
    private AbstractCreature effected;

    /**
     * 施法者
     */
    private AbstractCreature effector;

    /**
     * 结束时间
     */
    private int endRound;

    /**
     * 下一次执行的时间
     */
    private int periodRound;

    /**
     * 开始执行时间
     */
    private int startRound;

    /**
     * 周期类技能最后一次触发时间
     */
    private int lastPeriodRound;

    /**
     * 延迟生效回合
     */
    private int delayRound;

    /**
     * 计算值
     */
    private int value;

    /**
     * 可以扩展的值
     */
    private transient Map<String, Integer> values;

    /**
     * 是否已经添加到技能承受者的effectController上
     */
    private volatile boolean addedToController;

    /**
     * 魔法响应
     */
    private ActionObserver actionObserver;

    /** 客户端传过来的目标 */
    private Target clientTarget;

    /** 当前次数 */
    private int currentTimes;

    /**
     * 效果触发者
     */
    private AbstractCreature trigger;

    /**
     * 效果生效后参数，比如召唤魔物娘，对应召唤格子
     */
    private List<Integer> effectParam = new ArrayList<>();

    /**
     * 效果目标，根据效果配置表目标类型选择
     */
    private Target effectTarget;

    /**
     * 添加回合阶段
     */
    private RoundStage addRS;

    /**
     * 效果释放者归属驯养师，用于统计回合结束
     */
    public AbstractTrainerCreature addInRSTrainer;

    /***
     *
     * @param effector
     * @param effected
     * @param effectTemplate
     * @param skillId
     * @param effectId
     * @param target
     */
    public Effect(AbstractCreature effector, AbstractCreature effected, AbstractEffectTemplate effectTemplate, int skillId, int effectId, Target target) {
        this.effector = effector;
        this.effected = effected;
        this.effectTemplate = effectTemplate;
        this.skillResourceId = skillId;
        this.effectResourceId = effectId;
        this.effectTarget = target;
        this.delayRound = SkillManager.getInstance().getEffectResource(effectId).getDelayRound();
        this.initialize();
        addInRSTrainer = effector.getMaster().getRoom().getCurrentTurn();
        addRS = addInRSTrainer.getRoom().getRoundStage();
    }

    public boolean isDeadRemove() {
        return getEffectResource().isDeadRemove();
    }

    public EffectResource getEffectResource() {
        return SkillManager.getInstance().getEffectResource(effectResourceId);
    }

    /**
     * @return the effected
     */
    public AbstractCreature getEffected() {
        return effected;
    }

    public void addEndTime(int time) {
        this.setEndRound(this.getEndRound() + time);
    }

    public void replace(Effect newEffect) {
        effectTemplate.replace(this, newEffect);
    }

    /**
     * @return the effector
     */
    public AbstractCreature getEffector() {
        return effector;
    }

    public void initialize() {
        // TODO 是否暴击
        //this.damageType = GameMathUtil.calculateDamageType(this).getValue();
        EffectResource resource = SkillManager.getInstance().getEffectResource(effectResourceId);
        effectTemplate.init(resource);
        // 计算效果
        effectTemplate.calculate(this);
    }

    /**
     * 效果开始生效,加强条件处理，处理完后恢复旧值
     */
    public synchronized boolean applyEffect() {
        int beforeValue = this.getValue();
        this.getEffectTemplate().processStrength(this);
        boolean result = effectTemplate.applyEffect(this);
        this.setValue(beforeValue);
        return result;
    }

    public void startEffect(EffectController controller) {
        effectTemplate.startEffect(this, controller);
    }

    public boolean isPeriodicEffect() {
        return effectTemplate instanceof AbstractPeriodicEffect;
    }

    synchronized public void periodicAction() {
        ((AbstractPeriodicEffect) effectTemplate).onPeriodicAction(this);
    }

    /**
     * 添加到效果处理器
     */
    synchronized public void addToEffectedController() {
        if (!addedToController) {
            effected.getEffectController().addEffect(this);
            addedToController = true;
        }
    }

    private final AtomicBoolean STOP = new AtomicBoolean(false);

    public void endEffect() {
        if (STOP.compareAndSet(false, true)) {
            effectTemplate.endEffect(this, effected.getEffectController());
        }
    }

    /**
     * 效果结束，目前是按效果添加着对应阶段下一回合这个阶段才算一个完整回合
     *
     * @return
     */
    public boolean isEndRound() {
        //return onAddRS() && endRound <= getRoom().getRound();
        return endRound <= getRoom().getRound();
    }

    public Map<String, Integer> getValues() {
        return values;
    }

    public void setValues(Map<String, Integer> values) {
        this.values = values;
    }

    public void setEndRound(int endRound) {
        this.endRound = endRound;
    }

    public ActionObserver getActionObserver() {
        return actionObserver;
    }

    public void setActionObserver(ActionObserver actionObserver) {
        this.actionObserver = actionObserver;
    }

    public int getSkillResourceId() {
        return skillResourceId;
    }

    public void setSkillResourceId(int skillResourceId) {
        this.skillResourceId = skillResourceId;
    }


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getEffectResourceId() {
        return effectResourceId;
    }

    public Room getRoom() {
        return effected.getMaster().getRoom();
    }

    public int getCurrentTimes() {
        return currentTimes;
    }

    public void addUseTime() {
        currentTimes++;
    }

    public int getEndRound() {
        return endRound;
    }

    public int getPeriodRound() {
        return periodRound;
    }

    public void setPeriodRound(int periodRound) {
        this.periodRound = periodRound;
    }

    public int getStartRound() {
        return startRound;
    }

    public void setStartRound(int startRound) {
        this.startRound = startRound;
    }

    public int getLastPeriodRound() {
        return lastPeriodRound;
    }

    public void setLastPeriodRound(int lastPeriodRound) {
        this.lastPeriodRound = lastPeriodRound;
    }

    public boolean isAddedToController() {
        return addedToController;
    }

    public void setAddedToController(boolean addedToController) {
        this.addedToController = addedToController;
    }

    public boolean isUsedOver() {
        return currentTimes >= getEffectResource().getMaxTimes();
    }

    public EffectType getType() {
        return effectTemplate.getEffectType();
    }

    public AbstractEffectTemplate getEffectTemplate() {
        return effectTemplate;
    }

    public AbstractCreature getTrigger() {
        return trigger;
    }

    public void setTrigger(AbstractCreature trigger) {
        this.trigger = trigger;
        //设置触发者为默认目标
        if (Objects.isNull(effectTarget) && Objects.nonNull(trigger)) {
            this.effectTarget = Target.valueOf(trigger.getGridId(), trigger.getObjectId());
        }
    }

    public int getDelayRound() {
        return delayRound;
    }

    public void setDelayRound(int delayRound) {
        this.delayRound = delayRound;
    }

    /**
     * 延迟生效类型
     *
     * @return
     */
    public boolean isDelayType() {
        return delayRound > 0;
    }

    /**
     * 到达延迟生效回合
     *
     * @return
     */
    public boolean onDelayEffectRound() {
        return getRoom().getRound() >= (getStartRound() + getDelayRound());
    }

    public List<Integer> getEffectParam() {
        return effectParam;
    }

    public void setEffectParam(List<Integer> effectParam) {
        this.effectParam = effectParam;
    }

    public boolean isTriggerType() {
        return StringUtils.isNotEmpty(getEffectResource().getTriggerCondition());
    }

    public Target getEffectTarget() {
        return effectTarget;
    }

    public void setEffectTarget(Target effectTarget) {
        this.effectTarget = effectTarget;
    }

    public RoundStage getAddRS() {
        return addRS;
    }

    /**
     * 是否处于对应驯养师自身回合阶段
     *
     * @return
     */
    public boolean onAddRS() {
        //指定阶段移除必定不会处于添加阶段去做操作
        if (isFixRSRemove()) {
            return false;
        }
        return getRoom().getCurrentTurn() == addInRSTrainer && getRoom().getRoundStage() == addRS;
    }

    /**
     * 是否是配置指定阶段移除
     *
     * @return
     */
    public boolean isFixRSRemove() {
        return Objects.nonNull(this.getEffectResource().getRemoveStage());
    }


    /**
     * 是否处于周期性触发回合
     *
     * @return
     */
    public boolean onPeriodRound() {
        return getRoom().getRound() >= getPeriodRound() && getRoom().getRound() > getLastPeriodRound();
    }

    public boolean hasTargets() {
        return effectTarget != null && !CollectionUtils.isEmpty(effectTarget.getTargetIds());
    }

}