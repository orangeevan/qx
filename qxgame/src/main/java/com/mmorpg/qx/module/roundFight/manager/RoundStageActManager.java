package com.mmorpg.qx.module.roundFight.manager;

import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.roundFight.enums.RoundStage;
import com.mmorpg.qx.module.roundFight.model.actSelector.AbstractRSActSelector;
import com.mmorpg.qx.module.roundFight.model.actSelector.impl.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wang ke
 * @description:AI回合行为管理者
 * @since 11:28 2020-08-15
 */
@Component
public class RoundStageActManager {
    /**
     * 每个回合对应行为
     */
    private Map<RoundStage, Map<ObjectType, AbstractRSActSelector>> aiRsActStages = new LinkedHashMap<>();


    @PostConstruct
    private void init() {
        for (RoundStage stage : RoundStage.values()) {
            switch (stage) {
                case Extract_Card_Before:
                    Map<ObjectType, AbstractRSActSelector> objectRsAct = new HashMap<>();
                    objectRsAct.put(ObjectType.ROBOT_TRAINER, new RobotTrainerRS1ActSelector());
                    objectRsAct.put(ObjectType.PLAYER_TRAINER, PlayerRSActManager.createRsActSelector(stage));
                    aiRsActStages.put(RoundStage.Extract_Card_Before, objectRsAct);
                    break;
                case Extract_Card:
                    Map<ObjectType, AbstractRSActSelector> objectRsActEC = new HashMap<>();
                    objectRsActEC.put(ObjectType.ROBOT_TRAINER, new RobotTrainerRS2ActSelector());
                    objectRsActEC.put(ObjectType.PLAYER_TRAINER, PlayerRSActManager.createRsActSelector(stage));
                    aiRsActStages.put(RoundStage.Extract_Card, objectRsActEC);
                    break;
                case Throw_Dice_Before:
                    Map<ObjectType, AbstractRSActSelector> objectRsActTDB = new HashMap<>();
                    objectRsActTDB.put(ObjectType.ROBOT_TRAINER, new RobotTrainerRS3ActSelector());
                    objectRsActTDB.put(ObjectType.PLAYER_TRAINER, PlayerRSActManager.createRsActSelector(stage));
                    aiRsActStages.put(RoundStage.Throw_Dice_Before, objectRsActTDB);
                    break;
                case Throw_Dice:
                    Map<ObjectType, AbstractRSActSelector> objectRsActTD = new HashMap<>();
                    objectRsActTD.put(ObjectType.ROBOT_TRAINER, new RobotTrainerRS4ActSelector());
                    objectRsActTD.put(ObjectType.PLAYER_TRAINER, PlayerRSActManager.createRsActSelector(stage));
                    aiRsActStages.put(RoundStage.Throw_Dice, objectRsActTD);
                    break;
                case Throw_Dice_After:
                    Map<ObjectType, AbstractRSActSelector> objectRsActTDA = new HashMap<>();
                    objectRsActTDA.put(ObjectType.ROBOT_TRAINER, new RobotTrainerRS45ActSelector());
                    objectRsActTDA.put(ObjectType.PLAYER_TRAINER, PlayerRSActManager.createRsActSelector(stage));
                    aiRsActStages.put(RoundStage.Throw_Dice_After, objectRsActTDA);
                    break;
                case MOVE:
                    Map<ObjectType, AbstractRSActSelector> objectRsActMove = new HashMap<>();
                    objectRsActMove.put(ObjectType.ROBOT_TRAINER, new RobotTrainerRS5ActSelector());
                    objectRsActMove.put(ObjectType.PLAYER_TRAINER, PlayerRSActManager.createRsActSelector(stage));
                    aiRsActStages.put(RoundStage.MOVE, objectRsActMove);
                    break;
                case MOVE_END:
                    Map<ObjectType, AbstractRSActSelector> objectRsActME = new HashMap<>();
                    objectRsActME.put(ObjectType.ROBOT_TRAINER, new RobotTrainerRS6ActSelector());
                    objectRsActME.put(ObjectType.PLAYER_TRAINER, PlayerRSActManager.createRsActSelector(stage));
                    aiRsActStages.put(RoundStage.MOVE_END, objectRsActME);
                    break;
                case Round_END:
                    Map<ObjectType, AbstractRSActSelector> objectRsActRE = new HashMap<>();
                    objectRsActRE.put(ObjectType.ROBOT_TRAINER, new RobotTrainerRS7ActSelector());
                    objectRsActRE.put(ObjectType.PLAYER_TRAINER, PlayerRSActManager.createRsActSelector(stage));
                    aiRsActStages.put(RoundStage.Round_END, objectRsActRE);
                    break;
                default:
                    break;
            }
        }
        instance = this;
    }

    public AbstractRSActSelector getRSActAutoSelector(RoundStage roundStage, ObjectType type) {
        Map<ObjectType, AbstractRSActSelector> objectTypeSelectors = aiRsActStages.get(roundStage);
        if (CollectionUtils.isEmpty(objectTypeSelectors)) {
            return null;
        }
        return objectTypeSelectors.get(type);
    }


    public static RoundStageActManager getInstance() {
        return instance;
    }

    private static RoundStageActManager instance;

}
