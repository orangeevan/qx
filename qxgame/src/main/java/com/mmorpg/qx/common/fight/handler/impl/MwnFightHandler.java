package com.mmorpg.qx.common.fight.handler.impl;

import com.haipaite.common.event.core.EventBusManager;
import com.mmorpg.qx.common.fight.IBoardBox;
import com.mmorpg.qx.common.fight.Node;
import com.mmorpg.qx.common.fight.action.impl.MwnRoundAction;
import com.mmorpg.qx.common.fight.handler.AFightHandler;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.roundFight.event.MwnFightAfterEvent;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.packet.MwnRoundFightReportResp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yuanchengyan
 * @description:
 * @since 10:37 2021/4/28
 */
public class MwnFightHandler extends AFightHandler<MwnFightHandler.BoardBox> {

    {
        root.setNext(Node.Builder.build(MwnRoundAction.class));
    }

    @Override
    public void end() {
        super.end();

        Target targetA = Target.valueOf(box.defender.getGridId(), box.defender.getObjectId());
        /**b的攻击目标*/
        Target targetB = Target.valueOf(box.attacker.getGridId(), box.attacker.getObjectId());
        /**循环战斗，直到一方死亡,设置100次防止没有伤害技能造成循环*/
        Skill skillA = null;
        Skill skillB = null;
        /** a挑选技能*/
        skillA = box.attacker.getSkillController().selectSkill(targetA);
        /**b挑选技能*/
        skillB = box.defender.getSkillController().selectSkill(targetB);
        int attSupporter = box.attacker.getSupporter() == 0 ? 0 : box.attacker.getMaster().getMwnCreatureByCardId(box.attacker.getSupporter()).getMwn().getSkinResourceId();
        int defSupporter = box.defender.getSupporter() == 0 ? 0 : box.defender.getMaster().getMwnCreatureByCardId(box.defender.getSupporter()).getMwn().getSkinResourceId();
        long winnerId = box.defender.isAlreadyDead() ? box.attacker.getObjectId() : box.defender.getObjectId();
        MwnRoundFightReportResp reportResp = MwnRoundFightReportResp.valueOf(box.getResults(), winnerId, box.attacker, box.defender, attSupporter, defSupporter);
        PacketSendUtility.broadcastInWorldMap(box.attacker.getMaster().getWorldMapInstance(), reportResp, null);
        /**战斗完后处理*/
        box.attacker.afterFightRound(box.defender, skillB == null ? 0 : skillB.getResource().getSkillId(), 0);
        box.defender.afterFightRound(box.attacker, skillA == null ? 0 : skillA.getResource().getSkillId(), 0);
        System.err.println("魔物娘战斗战报： " + reportResp.getReports().size());
        // logger.info(String.format("魔物娘战斗结束，进攻方魔物娘 【%s】 血量:【%s】， 防守方魔物娘 【%s】 血量:【%s】", attacker.getResourceId() + "|" + attacker.getObjectId(), attacker.getCurrentHp(), defender.getResourceId() + "|" + defender.getObjectId(), defender.getCurrentHp()));
        //对战后
        AbstractCreature winner = box.defender.isAlreadyDead() ? box.attacker : box.defender;
        AbstractCreature loser = box.defender.isAlreadyDead() ? box.attacker : box.defender;
        EventBusManager.getInstance().syncSubmit(MwnFightAfterEvent.valueOf(winner, loser));
    }

    public static class BoardBox extends IBoardBox {
        protected MWNCreature attacker;
        protected MWNCreature defender;
        protected List<AbstractSkillResult> results = new ArrayList<>();

        public BoardBox() {
        }

        public List<AbstractSkillResult> getResults() {
            return results;
        }

        public MWNCreature getAttacker() {
            return attacker;
        }

        public void setAttacker(MWNCreature attacker) {
            this.attacker = attacker;
        }

        public MWNCreature getDefender() {
            return defender;
        }

        public void setDefender(MWNCreature defender) {
            this.defender = defender;
        }
    }
}

