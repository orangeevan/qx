package com.mmorpg.qx.module.skill.model.effect.impl;

import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.object.controllers.effect.EffectController;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.roundFight.utils.RoundFightUtils;
import com.mmorpg.qx.module.skill.model.effect.AbstractEffectTemplate;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author wang ke
 * @description: 投骰子后，点数为指定几个数字，到达位置的前后一个为未被占领的魔物娘格，可在此地格额外召唤一张手牌中的魔物娘
 * @since 13:56 2020-10-26
 */
public class FixDicePointCallMwn extends AbstractEffectTemplate {

    private List<Integer> points;

    @Override
    public void init(EffectResource resource) {
        super.init(resource);
        points = new ArrayList<>();
        String[] pointStr = resource.getParam().split(",");
        Arrays.stream(pointStr).forEach(ps -> points.add(Integer.valueOf(ps)));
    }

    @Override
    public boolean applyEffect(Effect effect) {
        chooseTargets(effect);
        Set<AbstractCreature> creatures = RoundFightUtils.getTargetCreatures(effect.getEffectTarget(), effect.getEffected().getWorldMapInstance());
        if (CollectionUtils.isEmpty(creatures)) {
            return false;
        }
        AbstractCreature effected = creatures.iterator().next();
        AbstractTrainerCreature master = effected.getMaster();
        if (master.getRoom() == null || master.getRoom().getCurrentTurn() != master) {
            return false;
        }

        int point = master.getDicePoint().calcPoints();
        if (!points.contains(point)) {
            return false;
        }
        EffectStatus status = EffectStatus.Arround_Blank_Grid_Set_Mwn;
        List<Integer> grids = new ArrayList<>(2);
        if (effect.getValue() == 1) {//召唤前面一个格子
            //status = EffectStatus.Beofre_Blank_Grid_Set_Mwn;
            WorldPosition position = GameUtil.getNeighborGridByDir(master.getWorldMapInstance(), master.getPosition(), master.getDir());
            grids.add(position.getGridId());
        } else if (effect.getValue() == 2) {//召唤后面一个格子
            //status = EffectStatus.After_Blank_Grid_Set_Mwn;
            WorldPosition position = GameUtil.getNeighborGridByDir(master.getWorldMapInstance(), master.getPosition(), master.getDir().getOpposite());
            grids.add(position.getGridId());
        } else {
            WorldPosition beforeGrid = GameUtil.getNeighborGridByDir(master.getWorldMapInstance(), master.getPosition(), master.getDir());
            grids.add(beforeGrid.getGridId());
            WorldPosition afterGrid = GameUtil.getNeighborGridByDir(master.getWorldMapInstance(), master.getPosition(), master.getDir().getOpposite());
            grids.add(afterGrid.getGridId());
        }
        master.getEffectController().setStatus(status, false);
        effect.setEffectParam(grids);
        return true;
    }

    @Override
    public void endEffect(Effect effect, EffectController controller) {
        super.endEffect(effect, controller);
        effect.getEffected().getMaster().getEffectController().unsetStatus(EffectStatus.Arround_Blank_Grid_Set_Mwn, false);
    }
}
