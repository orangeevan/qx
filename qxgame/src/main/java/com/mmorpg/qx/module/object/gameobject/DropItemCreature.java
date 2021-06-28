package com.mmorpg.qx.module.object.gameobject;

import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.threadpool.IdentityEventExecutorGroup;
import com.haipaite.common.utility.New;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.AbstractVisibleObjectController;
import com.mmorpg.qx.module.object.gameobject.enums.DropType;
import com.mmorpg.qx.module.reward.model.RewardItem;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 掉落物
 */
public class DropItemCreature extends AbstractVisibleObject {

    /**
     * 物品归属
     */
    private List<Long> owner;
    /**
     * 掉落物品消失任务
     **/
    private Future<?> clearTask;

    private AtomicBoolean pick = new AtomicBoolean(false);

    private int clearTime = 10000;

    private final long createTime = System.currentTimeMillis();

    private String fromNpcSpawnKey;

    private DropType dropType;

    public DropItemCreature(long objId, AbstractVisibleObjectController<AbstractVisibleObject> controller,
                            WorldPosition position) {
        super(objId, controller, position);
    }

    public static DropItemCreature valueOf(long objId, AbstractVisibleObjectController<AbstractVisibleObject> controller,
                                           WorldPosition position, RewardItem rewardItem, int clearTime, String sourcerSpawnKey) {
        final DropItemCreature obj = new DropItemCreature(objId, controller, position);
        obj.clearTime = clearTime;
        obj.fromNpcSpawnKey = sourcerSpawnKey;
        return obj;
    }

    public void addOwner(long playerId) {
        if (owner == null) {
            owner = New.arrayList();
        }
        owner.add(playerId);
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.ITEM;
    }

    public boolean isOwner(long playerId) {
        if (owner == null || owner.isEmpty()) {
            return true;
        }
        return owner.contains(playerId);
    }

    public boolean anyCanPick() {
        return owner == null || owner.isEmpty();
    }

    public List<Long> getOwner() {
        return owner;
    }

    public void setOwner(List<Long> owner) {
        this.owner = owner;
    }

    public boolean canPick() {
        return pick.compareAndSet(false, true);
    }

    public void startClearTask() {
        if (clearTask == null) {
            final AbstractVisibleObject obj = this;
            clearTask = IdentityEventExecutorGroup.addScheduleTask(new AbstractDispatcherHashCodeRunable() {

                @Override
                public long timeoutNanoTime() {
                    return TimeUnit.MINUTES.toNanos(1);
                }

                @Override
                public String name() {
                    return "DropObjectClear";
                }

                @Override
                public int getDispatcherHashCode() {
                    return getWorldMapInstance().getInstanceId();
                }

                @Override
                public void doRun() {
                    //WorldMapService.getInstance().despawn(this);

                }
            }, clearTime, TimeUnit.MILLISECONDS);
        }
    }

    public void cancelAllTask() {
        if (clearTask != null && !clearTask.isCancelled()) {
            clearTask.cancel(false);
            this.clearTask = null;
        }
    }

    public boolean isNew() {
        return (System.currentTimeMillis() - createTime) < 1000;
    }

    public String getFromNpcSpawnKey() {
        return fromNpcSpawnKey;
    }

    public void setFromNpcSpawnKey(String fromNpcSpawnKey) {
        this.fromNpcSpawnKey = fromNpcSpawnKey;
    }

    @Override
    public String getName() {
        return getResource().getName();
    }

    public DropType getDropType() {
        return dropType;
    }

    public void setDropType(DropType dropType) {
        this.dropType = dropType;
    }

    public ItemResource getResource(){
        return ItemManager.getInstance().getResource(getObjectKey());
    }
}
