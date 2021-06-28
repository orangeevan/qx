package com.mmorpg.qx.common.fight.action.impl;

import com.mmorpg.qx.common.fight.IBoard;
import com.mmorpg.qx.common.fight.action.IAction;
import com.mmorpg.qx.common.fight.handler.impl.MwnFightHandler;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.skillResult.AbstractSkillResult;
import com.mmorpg.qx.module.skill.model.target.Target;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: yuanchengyan
 * @description:
 * @since 10:29 2021/4/28
 */
public class MwnRoundAction extends IAction<MwnRoundAction.Board, MwnFightHandler.BoardBox> {

    @Override
    public boolean isEnd() {
        if (box.getDefender().isAlreadyDead()) {
            return true;
        }
        if (box.getAttacker().isAlreadyDead()) {
            return true;
        }
        return board.round >= 100;
    }

    @Override
    public void circleEnd() {
        super.circleEnd();
        board.round++;
    }

    @Override
    public void end() {

    }

    @Override
    public void action() {
        MWNCreature attacker = box.getAttacker();
        MWNCreature defender = box.getDefender();
        /** a的攻击目标*/
        Target targetA = Target.valueOf(defender.getGridId(), defender.getObjectId());
        /**b的攻击目标*/
        Target targetB = Target.valueOf(attacker.getGridId(), attacker.getObjectId());
        /**循环战斗，直到一方死亡,设置100次防止没有伤害技能造成循环*/

        Skill skillA = null;
        Skill skillB = null;
        /** a挑选技能*/
        skillA = attacker.getSkillController().selectSkill(targetA);
        /**b挑选技能*/
        skillB = defender.getSkillController().selectSkill(targetB);
        if (skillA != null) {
            List<AbstractSkillResult> damages = skillA.useSkill(board.round);
            String format = String.format("魔物娘进攻方A id【%s】【%s】，使用技能：【%s】  敌方剩余血量:【%s】", attacker.getObjectId(), attacker.getName(), skillA.getResource().getSkillId(), defender.getCurrentHp());
            System.err.println(format);
            if (!CollectionUtils.isEmpty(damages)) {
                for (AbstractSkillResult result : damages) {
                    System.err.println("添加进攻方技能战报： " + result);
                }
            }
            if (!CollectionUtils.isEmpty(damages)) {
                box.getResults().addAll(damages);
            }
            attacker.getSkillController().afterUseSkill(skillA);
        }
        if (skillB != null) {
            List<AbstractSkillResult> damages = skillB.useSkill(board.round);
            String format = String.format("魔物娘防守方B id【%s】【%s】，使用技能：【%s】  敌方剩余血量:【%s】", defender.getObjectId(), defender.getName(), skillB.getResource().getSkillId(), attacker.getCurrentHp());
            System.err.println(format);
            if (!CollectionUtils.isEmpty(damages)) {
                for (AbstractSkillResult result : damages) {
                    System.err.println("添加防守方技能战报： " + result);
                }
            }
            if (!CollectionUtils.isEmpty(damages)) {
                box.getResults().addAll(damages);
            }
            defender.getSkillController().afterUseSkill(skillB);
        }
    }

    public static class Board implements IBoard<MwnFightHandler.BoardBox> {
        int round = 0;

        @Override
        public void initBoard(MwnFightHandler.BoardBox box) {

        }
    }

}

