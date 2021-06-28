package com.mmorpg.qx.module.object.gameobject.building;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.building.manager.BuildingResourceManager;
import com.mmorpg.qx.module.building.resource.BuildingResource;
import com.mmorpg.qx.module.object.controllers.BuildingController;
import com.mmorpg.qx.module.object.controllers.packet.BuildingInfoResp;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.manager.ObjectManager;
import com.mmorpg.qx.module.object.spawn.resource.MapCreatureResource;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

import java.util.List;

/**
 * @author wang ke
 * @description: 建筑,特殊生物，有技能
 * @since 14:32 2020-08-31
 */
public abstract class AbstractBuilding extends AbstractVisibleObject {

    private int resourceId;

    /**
     * Constructor.
     *
     * @param objId
     * @param controller
     * @param position
     */
    public AbstractBuilding(long objId, BuildingController controller, WorldPosition position) {
        super(objId, controller, position);
        controller.setOwner(this);
    }

    @Override
    public String getName() {
        return ObjectManager.getInstance().getObjectResource(this.getObjectKey()).getName();
    }

    public List<Integer> getSkills() {
        return BuildingResourceManager.getInstance().getResource(resourceId).getSkills();
    }

    public BuildingInfoResp getBuildingInfo(){
        BuildingInfo info = BuildingInfo.valueOf(this);
        return BuildingInfoResp.valueOf(info);
    }

    public void handleTrigger(TriggerType triggerType, AbstractCreature trigger){
//        if(triggerType == TriggerType.Move_Pass){
//            Optional<Object> configValue = Optional.of(ConfigValueManager.getInstance().getConfigValue(BuildingResourceManager.Add_Gold));
//            if (configValue.isPresent()) {
//                trigger.getLifeStats().increaseGold((int)configValue.get(), Reason.Building, true);
//                //PacketSendUtility.broadcastPacket(trigger, LifeChangeResp.valueOf(Reason.Building, trigger), true);
//            }
//
//        }
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public void init(MapCreatureResource mapResource, BuildingResource resource, WorldMapInstance instance){
        setDir(mapResource.createDir());
        setObjectKey(resource.getId());
        setResourceId(resource.getId());
    }

    public BuildingResource getResource(){
        return BuildingResourceManager.getInstance().getResource(resourceId);
    }
}