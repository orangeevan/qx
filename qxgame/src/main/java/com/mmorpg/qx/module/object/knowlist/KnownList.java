package com.mmorpg.qx.module.object.knowlist;

import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.AbstractObject;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 观察者列表
 *
 * @author wang ke
 * @since v1.0 2018年2月24日
 */
public class KnownList implements Iterable<AbstractVisibleObject> {

    @SuppressWarnings("unused")
    private static final Logger logger = SysLoggerFactory.getLogger(KnownList.class);

    protected final AbstractVisibleObject owner;

    protected final ConcurrentHashMap<Long, AbstractVisibleObject> knownObjects = new ConcurrentHashMap<>();

    private long lastUpdate;

    private static final int VIS_ROWDIS = 40;
    private static final int VIS_COLDIS = 20;

    public KnownList(AbstractVisibleObject owner) {
        this.owner = owner;
    }

    public void doUpdate() {
        doUpdate(true);
    }

    public void doUpdate(boolean force) {
        if (!force) {
            if ((System.currentTimeMillis() - lastUpdate) < 1000) {
                return;
            }
        }
        forgetObjects();
        findVisibleObjects();
        lastUpdate = System.currentTimeMillis();
    }

    public void clear() {
        Iterator<AbstractVisibleObject> knownIt = iterator();
        while (knownIt.hasNext()) {
            AbstractVisibleObject obj = knownIt.next();
            knownIt.remove();
            owner.getController().notSee(obj, false);
            //obj.getKnownList().del(owner, false);
        }
    }

    public boolean knowns(AbstractObject object) {
        return knownObjects.containsKey(object.getObjectId());
    }

    public boolean knownObjectType(ObjectType objectType) {
        Iterator<AbstractVisibleObject> knownIt = iterator();
        while (knownIt.hasNext()) {
            AbstractVisibleObject obj = knownIt.next();
            if (obj.getObjectType() == objectType) {
                return true;
            }
        }
        return false;
    }

    public boolean knownObjectKey(int objectkey) {
        Iterator<AbstractVisibleObject> knownIt = iterator();
        while (knownIt.hasNext()) {
            AbstractVisibleObject obj = knownIt.next();
            if (obj.getObjectKey() == objectkey) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<AbstractVisibleObject> iterator() {
        return knownObjects.values().iterator();
    }

    public int getVisibleSize() {
        return knownObjects.size();
    }

    /**
     * Add VisibleObject to this KnownList.
     *
     * @param object
     */
    protected void add(AbstractVisibleObject object) {
        /**
         * object is not known.
         */
        if (knownObjects.put(object.getObjectId(), object) == null) {
            owner.getController().see(object);
        }
    }

    /**
     * Delete VisibleObject from this KnownList.
     *
     * @param object
     */
    private void del(AbstractVisibleObject object, boolean isOutOfRange) {
        /**
         * object was known.
         */
        if (knownObjects.remove(object.getObjectId()) != null) {
            owner.getController().notSee(object, isOutOfRange);
        }
    }

    /**
     * forget out of distance objects.
     */
    private void forgetObjects() {
        Iterator<AbstractVisibleObject> knownIt = iterator();
        while (knownIt.hasNext()) {
            AbstractVisibleObject obj = knownIt.next();
            if (!checkObjectInRange(owner, obj)) {
                knownIt.remove();
                owner.getController().notSee(obj, true);
                //obj.getKnownList().del(owner, true);
            }
        }
    }

    /**
     * Find objects that are in visibility range.
     */
//	protected void findVisibleObjects() {
//		if (owner == null || !owner.isSpawned()) {
//			return;
//		}
//
//		List<MapRegion> list = owner.getActiveRegion().getNeighbours();
//		for (MapRegion r : list) {
//			Map<Long, AbstractVisibleObject> objects = r.getObjects();
//			for (Entry<Long, AbstractVisibleObject> e : objects.entrySet()) {
//				AbstractVisibleObject newObject = e.getValue();
//				if (newObject == owner || newObject == null) {
//					continue;
//				}
//
//				if (!checkObjectInRange(owner, newObject)) {
//					continue;
//				}
//
//				add(newObject);
//				newObject.getKnownList().add(owner);
//			}
//		}
//	}
    protected boolean checkObjectInRange(AbstractVisibleObject owner, AbstractVisibleObject newObject) {
        //return GameMathUtil.isInRange(owner, newObject, VIS_ROWDIS, VIS_COLDIS);
        return true;
    }

    /***
     * 卡招里面全地图都是视野
     */
    public void findVisibleObjects() {
        if (owner == null || owner.getPosition() == null) {
            return;
        }
        WorldMapInstance worldMapInstance = this.owner.getWorldMapInstance();
        if (worldMapInstance == null) {
            return;
        }
        Iterator<AbstractVisibleObject> objectIterator = worldMapInstance.objectIterator();
//		while (objectIterator.hasNext()){
//			this.owner.getKnownList().add(objectIterator.next());
//		}
    }
}
