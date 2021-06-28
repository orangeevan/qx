package com.mmorpg.qx.module.skill.model.effect.impl;

import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.RandomUtils;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import com.mmorpg.qx.module.worldMap.enums.SelectGridType;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 当某魔物娘存在于场上时，一回合限定一次，其他游侠职业魔物娘在占据地格之后，如果场上存在可被放置的空白地格，可以选择一张手牌中的魔物娘，消耗与之相匹配的魔法值，将之召唤到地图上的随机空白地格
 * @since 18:02 2020-10-26
 */
public class CallMwnRandomGridEffect extends AbstractEffectTemplate {
    private CallMwnParam param;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        param = JsonUtils.string2Object(resource.getParam(), CallMwnParam.class);
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (!CollectionUtils.isEmpty(creatures)) {
            for (AbstractCreature creature : creatures) {
                AbstractTrainerCreature master = creature.getMaster();
                if (master.getRoom() == null || master.getRoom().getCurrentTurn() != master || master.isCallMwnRound()) {
                    //从目标中移除
                    getTargets().remove(creature);
                    return false;
                }
                master.getEffectController().setStatus(EffectStatus.Set_Mwn, false);
                SelectGridType gridType = param.getGridType();
                AbstractCreature referTo = effect.getEffected().getMaster();
                //如果参照物是魔物娘
                if (param.getReferTo() == 1) {
                    referTo = effect.getTrigger();
                }
                List<Integer> callMwnGrids = new ArrayList<>();
                if (gridType == SelectGridType.Random) {
                    if (param.getGridRange() == GridRange.Around) {
                        List<MapGrid> aroundGrids = GameUtil.findAroundGrid(referTo.getWorldMapInstance().getParent(), referTo.getGridId(), false);
                        aroundGrids = aroundGrids.stream().filter(mapGrid -> master.getWorldMapInstance().isEmpty(mapGrid.getId())).collect(Collectors.toList());
                        int index = RandomUtils.betweenInt(0, aroundGrids.size() - 1, true);
                        effect.getEffectParam().add(aroundGrids.get(index).getId());
                        callMwnGrids.add(aroundGrids.get(index).getId());
                    } else if (param.getGridRange() == GridRange.All) {
                        int gridId = WorldMapService.getInstance().randomEmptyGrid(master.getWorldMapInstance()).getGridId();
                        effect.getEffectParam().add(gridId);
                        callMwnGrids.add(gridId);
                    }
                } else {
                    if (param.getGridRange() == GridRange.Before) {
                        WorldPosition grid = GameUtil.getNeighborGridByDir(referTo.getWorldMapInstance(), referTo.getPosition(), referTo.getDir());
                        effect.getEffectParam().add(grid.getGridId());
                        callMwnGrids.add(grid.getGridId());
                    } else if (param.getGridRange() == GridRange.After) {
                        WorldPosition grid = GameUtil.getNeighborGridByDir(referTo.getWorldMapInstance(), referTo.getPosition(), referTo.getDir().getOpposite());
                        effect.getEffectParam().add(grid.getGridId());
                        callMwnGrids.add(grid.getGridId());
                    } else if (param.getGridRange() == GridRange.Around) {
                        List<MapGrid> aroundGrid = GameUtil.findAroundGrid(referTo.getWorldMapInstance().getParent(), referTo.getGridId(), false);
                        if (!CollectionUtils.isEmpty(aroundGrid)) {
                            aroundGrid.stream().forEach(grid -> effect.getEffectParam().add(grid.getId()));
                            callMwnGrids.addAll(aroundGrid.stream().map(MapGrid::getId).collect(Collectors.toList()));
                        }
                    }
                }
                master.addCallMwnGrids(callMwnGrids);
            }
        }
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        Set<AbstractCreature> targets = getTargets();
        if (!CollectionUtils.isEmpty(targets)) {
            targets.stream().forEach(target -> {
                if (!target.getMaster().getEffectController().isInStatus(EffectStatus.Set_Mwn)) {
                    return;
                }
                target.getMaster().getEffectController().unsetStatus(EffectStatus.Set_Mwn, false);
                target.getMaster().resetCallMwnGrids();
            });
        }
    }

    /**
     * 召唤魔物娘参数解析类
     */
    public static class CallMwnParam {
        /**
         * 选择格子类型，随机或指定
         */
        private SelectGridType gridType;
        /**
         * 参照对象 0：驯养师  1：魔物娘
         */
        private int referTo;
        /**
         * 格子前后
         */
        private GridRange gridRange;

        public SelectGridType getGridType() {
            return gridType;
        }

        public void setGridType(SelectGridType gridType) {
            this.gridType = gridType;
        }

        public int getReferTo() {
            return referTo;
        }

        public void setReferTo(int referTo) {
            this.referTo = referTo;
        }

        public GridRange getGridRange() {
            return gridRange;
        }

        public void setGridRange(GridRange gridRange) {
            this.gridRange = gridRange;
        }
    }

    public static enum GridRange {
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

    public static void main(String[] args) {
        CallMwnParam callMwnParam = new CallMwnParam();
        callMwnParam.setGridRange(GridRange.After);
        callMwnParam.setGridType(SelectGridType.Random);
        callMwnParam.setReferTo(0);
        System.err.println(JsonUtils.toJson(callMwnParam, false));
    }
}
