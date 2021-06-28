package com.mmorpg.qx.module.object.gameobject;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.controllers.NpcController;
import com.mmorpg.qx.module.object.controllers.packet.MWNInfoResp;
import com.mmorpg.qx.module.skill.resource.SkillResource;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;

import java.util.List;
import java.util.stream.Collectors;

public class MonsterCreature extends NpcCreature {

	public MonsterCreature(long objId, NpcController controller, WorldPosition position) {
		super(objId, controller, position);
		controller.setOwner(this);
	}

	@Override
	public ObjectType getObjectType() {
		return ObjectType.MONSTER;
	}



}
