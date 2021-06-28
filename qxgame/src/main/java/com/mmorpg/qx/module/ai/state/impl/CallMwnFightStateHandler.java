package com.mmorpg.qx.module.ai.state.impl;

import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.module.ai.state.AIState;
import com.mmorpg.qx.module.ai.state.IStateHandler;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description:召唤魔物娘战斗
 * @since 16:48 2020-08-18
 */
public class CallMwnFightStateHandler implements IStateHandler {
    @Override
    public AIState getState() {
        return AIState.Call_Fight;
    }

    @Override
    public void handleState(AbstractTrainerCreature owner, Object... params) {

        //TODO 后续指定条件抽取卡牌

        if (CollectionUtils.isEmpty(owner.getUseCardStorage().getMwns())) {
            return;
        }
        List<MWNCreature> mwnList = owner.getWorldMapInstance().getMWNList(owner.getGridId());
        if (!CollectionUtils.isEmpty(mwnList)) {
            if (mwnList.stream().anyMatch(mwnCreature -> RelationshipUtils.judgeRelationship(owner, mwnCreature, RelationshipUtils.Relationships.SELF_TRAINER_MWN))) {
                return;
            }
        }
        //抽取手牌卡
        List<MoWuNiang> filterMwns = owner.getUseCardStorage().getMwns().stream().filter(moWuNiang -> moWuNiang.getResource().getCostMp() <= owner.getLifeStats().getCurrentMp()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterMwns)) {
            return;
        }
        MoWuNiang moWuNiang = filterMwns.stream().sorted(Comparator.comparingInt(o -> o.getResource().getCostMp())).limit(1).findFirst().get();
        //走正常召唤流程
        try {
            RoundFightService.getInstance().callMWN(owner, moWuNiang.getId(), owner.getGridId());
        } catch (ManagedException e) {

        }
    }
}
