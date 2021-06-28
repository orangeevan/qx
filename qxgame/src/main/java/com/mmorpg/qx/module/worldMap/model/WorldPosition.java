package com.mmorpg.qx.module.worldMap.model;

/**
 * 对象在世界的位置.用于对象位置定位
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public class WorldPosition implements Cloneable {

    /**
     * Map id.
     */
    private int mapId;

    /**
     * World position
     */
    private int gridId;

    /**
     * 地图实例ID
     */
    private int instanceId;


    /**
     * indicating if object is spawned or not.
     */
    //private AtomicBoolean isSpawned = new AtomicBoolean(false);

    /**
     * Return World map id.
     *
     * @return world map id
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * @param mapId the mapId to set
     */
    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    /**
     * Check if object is spawned.
     *
     * @return true if object is spawned.
     */
//	public boolean isSpawned() {
//		return isSpawned.get();
//	}

//	/**
//	 * Set isSpawned to given value.
//	 *
//	 * @param val
//	 */
////	public void setIsSpawned(boolean val) {
////		isSpawned.set(val);
////	}

//	public boolean compareAndSet(boolean expect, boolean update) {
////		return isSpawned.compareAndSet(expect, update);
////	}
    @Override
    public String toString() {
        return "WorldPosition [ instanceId=" + instanceId
                + ", gridId=" + gridId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + instanceId;
        //result = prime * result + ((isSpawned == null) ? 0 : isSpawned.hashCode());
        result = prime * result + mapId;
        result = prime * result + gridId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WorldPosition other = (WorldPosition) obj;
        if (instanceId != other.instanceId) {
            return false;
        }
//		if (isSpawned == null) {
//			if (other.isSpawned != null) {
//				return false;
//			}
//		} else if (!isSpawned.equals(other.isSpawned)) {
//			return false;
//		}
        if (mapId != other.mapId) {
            return false;
        }

        if (gridId != other.gridId) {
            return false;
        }

        return true;
    }

    @Override
    public WorldPosition clone() {
        WorldPosition pos = new WorldPosition();
        //pos.isSpawned = this.isSpawned;
        pos.mapId = this.mapId;
        pos.gridId = this.gridId;
        pos.instanceId = this.instanceId;
        return pos;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public static WorldPosition buildWorldPosition(WorldMapInstance worldMapInstance, int gridId) {
        if (worldMapInstance == null || worldMapInstance.getParent().getMapGrid(gridId) == null) {
            return null;
        }
        WorldPosition worldPosition = new WorldPosition();
        worldPosition.setGridId(gridId);
        worldPosition.setMapId(worldMapInstance.getParent().getMapId());
        worldPosition.setInstanceId(worldMapInstance.getInstanceId());
        return worldPosition;
    }

    //TODO 根据格子号获得x ，y ，z,暂时这样处理，为了兼顾老代码距离计算
    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }

    public int getZ() {
        return 0;
    }
}
