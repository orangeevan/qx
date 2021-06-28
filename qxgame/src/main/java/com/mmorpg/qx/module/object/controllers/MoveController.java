package com.mmorpg.qx.module.object.controllers;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.controllers.move.Road;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.building.AbstractBuilding;
import com.mmorpg.qx.module.roundFight.packet.MoveResp;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 移动管理器
 *
 * @author wang ke
 * @since v1.0 2018年3月15日
 */
public final class MoveController {
    private static final Logger logger = SysLoggerFactory.getLogger(MoveController.class);
    private AbstractCreature owner;

    //玩家移动标志，一次完整移动
    private AtomicBoolean isStopped = new AtomicBoolean(Boolean.TRUE);

    //一小阶段移动目标完成标志
    private AtomicBoolean stageOver = new AtomicBoolean(true);


    //移动步数
    private volatile int step;

    private boolean needUpdate;

    private long beginMoveTime;

    //移动方向
    private DirType moveDir;
    /**
     * 最后一次移动的时间
     */
    private long lastMoveTime = System.currentTimeMillis();

    private double cost = 0;

    private Road road;

    /**
     * @param owner
     */
    public MoveController(AbstractCreature owner) {
        this.owner = owner;
    }

    /**
     * 对controller发移动指令入口,移动前需要判断对象是否处于移动状态
     */
    synchronized public boolean startMove(int step, DirType dirType, boolean needCheckGrid) {
        //移动步数判断
        if (step <= 0) {
            //特殊处理，预防意外
            stopMoving();
            return false;
        }
        //上次移动未结束，先等上次移动结束
        if (!stageOver.get() || !isMoveOver()) {
            System.err.println("驯养师: " + owner.getObjectId() + " 开始移动，由于上次移动未完成，取消移动请求");
            return false;
        }
        Road road = GameUtil.generateRoadByStep(owner, step, dirType, needCheckGrid);
        //判断移动路径及格子数
        //|| road.getRoads().length < step
        if (road == null) {
            throw new ManagedException(ManagedErrorCode.DIR_ERROR);
        }

        this.road = road;
        PacketSendUtility.broadcastInWorldMap(owner.getWorldMapInstance(), MoveResp.valueOf(owner, owner.getGridId(), road.getLeftRoads()), null);
        startMoving();
        beginMoveTime = System.currentTimeMillis();
        cost = doMoveCostTime(road);
        moveDir = dirType;
        //设置移动步数
        incStep(this.road.getRoads().length);
        //广播完将目标放置对应格子上
        owner.setPosition(road.getTargetPosition());
        owner.setDir(DirType.indexOfType(this.road.getLastRoad()));
        String log = "驯养师： " + owner.getObjectId() + " 寻路完成，设置最终格子方向: " + owner.getDir() + " 第一个格子方向：" + DirType.indexOfType(road.getRoads()[0]).name() + " 当前格子号：" + owner.getGridId();
        logger.info(log);
        //后续移动
        //schedule();
        //进入移动状态
        this.owner.getController().onMove();
        this.owner.getController().onMeet();
        return true;
    }

    /**
     * 路过或终点有停留
     */
    public void handleMovePass() {
        //处于移动中，当前时间点已过移动时间点
//        if (isStopped.get() || isStageOver()) {
//            return;
//        }
//        //设置阶段移动目标完成
//        moveStageOver();
        Collection<AbstractVisibleObject> objects = owner.getWorldMapInstance().findObjects(owner.getGridId());
        if (!CollectionUtils.isEmpty(objects)) {
            // 路过生物、建筑时触发效果，路过敌方驯养师时偷取金币
            objects.stream().filter(object -> object instanceof AbstractCreature).map(object -> (AbstractCreature) object).forEach(creature -> creature.handleEffect(TriggerType.Move_Pass, this.owner));
            objects.stream().filter(object -> object instanceof AbstractBuilding).map(object -> (AbstractBuilding) object).forEach(building -> building.handleTrigger(TriggerType.Move_Pass, this.owner));
            objects.stream().filter(object -> object instanceof AbstractTrainerCreature).map(object -> (AbstractTrainerCreature) object).forEach(trainer -> {
                if (RelationshipUtils.judgeRelationship(this.owner.getMaster(), trainer, RelationshipUtils.Relationships.ENEMY_TRAINER_TRAINER) && this.owner.getEffectController().isInStatus(EffectStatus.Steal_Gold)) {
                    int stealGold = trainer.getLifeStats().getCurrentGold() / 2;
                    trainer.getLifeStats().reduceGold(stealGold, Reason.Trainer_Skill_Effect, false);
                    owner.getLifeStats().increaseGold(stealGold, Reason.Trainer_Skill_Effect, true);
                }
            });
        }

    }

    private void moveStageOver() {
        resetMoveState();
    }

    private boolean isStageOver() {
        return stageOver.get();
    }

    public byte[] getLeftRoads() {
        return isStopped.get() ? null : road.getLeftRoads();
    }

    public boolean isMoving() {
        return !isStopped.get() && !isMoveOver();
    }

    /***
     * 是否能走，如果已经有路径在走需要等旧路径走完
     * @return
     */
    public boolean canMove() {
        //正在移动中
        if (isMoving()) {
            return false;
        }
        if (!owner.canPerformMove() || owner.isCasting()) {
            return false;
        }
        return true;
    }


    /**
     * 统计移动过程中总耗费时间
     */
    private int doMoveCostTime(Road road) {
        int cost = 0;
        byte dir = 0;
        while (!road.isOver()) {
            dir = road.poll();
            DirType direction = DirType.values()[dir];
            cost += direction.getCost();
        }
        return cost;
    }

    /**
     * 在没有移动完前不能做其他动作
     */
    public boolean isStopped() {
        return isStopped.get() || isMoveOver();
    }

    /**
     * 每次投骰子后，移动可能被中断，如有中断，表示目前小段移动目标完成
     *
     * @return
     */
    private boolean isMoveOver() {
        return System.currentTimeMillis() > (beginMoveTime + cost);
    }

    /**
     * 开始移动
     */
    public void startMoving() {
        resetMoveState();
        isStopped.compareAndSet(Boolean.TRUE, Boolean.FALSE);
        stageOver.compareAndSet(true, false);
    }


    /**
     * 停止或中断移动
     */
    public void stopMoving() {
        if (isStopped.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
            owner.getController().stopMoving();
            owner.getController().onStopMove();
            resetMoveState();
            this.road = null;
            this.step = 0;
        }
    }

    /***
     * 清除移动状态数据
     */
    private void resetMoveState() {
        beginMoveTime = 0;
        cost = 0;
        stageOver.compareAndSet(false, true);
    }

    public Road getRoad() {
        return road;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    synchronized public void incStep(int step) {
        this.step += step;
    }
}
