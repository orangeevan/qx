package com.mmorpg.qx.module.skill.model.effect.impl;

import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.RandomUtils;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.creater.CurseCreater;
import com.mmorpg.qx.module.object.creater.FireCreater;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.CurseCreature;
import com.mmorpg.qx.module.object.gameobject.FireCreature;
import com.mmorpg.qx.module.object.manager.ObjectManager;
import com.mmorpg.qx.module.object.resource.ObjectResource;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import com.mmorpg.qx.module.worldMap.enums.SelectGridType;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 施咒
 *
 * @author: yuanchengyan
 * @description:
 * @since 17:23 2021/4/13
 */
public class CurseEffect extends AbstractEffectTemplate {
    private Param param;
    private long objectId;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        param = JsonUtils.string2Object(resource.getParam(), Param.class);
    }

    @Override
    public boolean applyEffect(Effect effect) {

        AbstractCreature creature = effect.getEffector();

        SelectGridType gridType = param.getGridType();
        WorldPosition pos = null;
        if (gridType == SelectGridType.Random) {
            if (param.getGridRange() == GridRange.Around) {
                List<MapGrid> aroundGrids = GameUtil.findAroundGrid(creature.getWorldMapInstance().getParent(), creature.getGridId(), false);
                aroundGrids = aroundGrids.stream().filter(mapGrid -> creature.getWorldMapInstance().isEmpty(mapGrid.getId())).collect(Collectors.toList());
                int index = RandomUtils.betweenInt(0, aroundGrids.size() - 1, true);
                int gridId = aroundGrids.get(index).getId();
                pos = creature.getWorldMapInstance().getPosition(gridId);
            } else if (param.getGridRange() == GridRange.All) {
                int gridId = WorldMapService.getInstance().randomEmptyGrid(creature.getWorldMapInstance()).getGridId();
                pos = creature.getWorldMapInstance().getPosition(gridId);
            }
        } else {
            if (param.getGridRange() == GridRange.Before) {
                pos = GameUtil.getNeighborGridByDir(creature.getWorldMapInstance(), creature.getPosition(), creature.getDir());

            } else if (param.getGridRange() == GridRange.After) {
                pos = GameUtil.getNeighborGridByDir(creature.getWorldMapInstance(), creature.getPosition(), creature.getDir().getOpposite());
            }
        }

        ObjectResource fireResource = ObjectManager.getInstance().getObjectResource(param.objectId);
        WorldMapInstance mapInstance = creature.getWorldMapInstance();
        CurseCreature curseCreature = CurseCreater.create(creature, param.hurt, fireResource, mapInstance, effect, pos);
        objectId = curseCreature.getObjectId();
        WorldMapService.getInstance().spawn(curseCreature);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        AbstractCreature curseCreature = effect.getEffected().getWorldMapInstance().getCreatureById(objectId);
        curseCreature.getController().delete();
    }

    /**
     * 参数解析类
     */
    public static class Param {
        /**
         * 选择格子类型，随机或指定
         */
        private SelectGridType gridType;

        private GridRange gridRange;
        private int objectId;
        private int hurt;

        public SelectGridType getGridType() {
            return gridType;
        }

        public void setGridType(SelectGridType gridType) {
            this.gridType = gridType;
        }


        public GridRange getGridRange() {
            return gridRange;
        }

        public void setGridRange(GridRange gridRange) {
            this.gridRange = gridRange;
        }

        public int getObjectId() {
            return objectId;
        }

        public void setObjectId(int objectId) {
            this.objectId = objectId;
        }
    }

    public enum GridRange {
        /**
         * 前一个格子
         */
        Before,
        /**
         * 后一个格子
         */
        After,
        /**
         * 周围两侧
         */
        Around,
        /**
         * 全图
         */
        All,
        ;
    }


}

