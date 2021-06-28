package com.mmorpg.qx.module.player.model;

import com.mmorpg.qx.module.integral.entity.IntegralEntity;
import com.mmorpg.qx.module.integral.model.IntegralStore;
import com.mmorpg.qx.module.item.entity.PackEntity;
import com.mmorpg.qx.module.item.model.EquipmentStorage;
import com.mmorpg.qx.module.item.model.ItemStorage;
import com.mmorpg.qx.module.moduleopen.model.ModuleOpenBox;
import com.mmorpg.qx.module.mwn.entity.PlayerMwnEntity;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateTaskManager;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.player.entity.PlayerCommonEnt;
import com.mmorpg.qx.module.player.entity.PlayerEnt;
import com.mmorpg.qx.module.purse.model.Purse;
import com.mmorpg.qx.module.quest.model.QuestBox;
import com.mmorpg.qx.module.trainer.entity.PlayerTrainerEntity;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import com.mmorpg.qx.module.troop.entity.TroopEntity;
import com.mmorpg.qx.module.troop.model.CardBag;
import com.mmorpg.qx.module.troop.model.TroopStorage;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 玩家
 *
 * @author wang ke
 * @since v1.0 2019年3月6日
 */
public class Player {

    /** 玩家存储的实例对象 */
    private PlayerEnt playerEnt;

    //TODO 卡招后面如果pvp玩法玩家在场景退出，下次登录需要恢复坐标
    /** 下次登陆时登陆位置标记 */
    private LoginPosition loginPosition;

    /**  背包存储的实例对象 */
    private PackEntity packEntity;

    /** 仓库 */
    private ItemStorage warehouse;

    /** 装备栏 */
    private EquipmentStorage equipmentStorage;

    /** 编队实例对象 */
    private TroopEntity troopEntity;

    /** 编队 */
    private TroopStorage troopStorage;

    /** 钱包 */
    private Purse purse;

    /** 任务 */
    private QuestBox questBox;

    /** 模块开启 */
    private ModuleOpenBox moduleOpenBox;

    /** 驯养师实体 */
    private PlayerTrainerEntity trainerEntity;

    /** 玩家驯养师对象 */
    private List<PlayerTrainer> playerTrainers;

    /** 魔物娘实体 */
    private PlayerMwnEntity playerMwnEntity;

    /** 魔物娘 */
    private Map<Long, MoWuNiang> playerMoWuNiang;

    /** 玩家最近心跳时间戳 */
    private long lastHeartbeat;

    /** 进入回合战斗选择卡包 */
    private CardBag selectedCardBag;

    /** 进入回合选择驯养师 */
    private PlayerTrainer selectedTrainer;

    /** 积分实例对象 */
    private IntegralEntity integralEntity;

    /** 积分 */
    private IntegralStore integralStore;

    /**
     * 进入世界sign.
     * 1.客户端登陆.服务端完成登陆生成sign返回客户端.
     * 2.客户端完成初始化以后,通过该sign确定自己要进入世界.
     * 3.服务端对比该sign如果正确那么运行客户端登陆
     */
    private final AtomicInteger enterWorldSign = new AtomicInteger(0);

    private int dispatchId;

    private PlayerCommonEnt commonEnt;

    /**
     * 任务状态
     */
    private CreatureUpdateTaskManager updateTaskManager = new CreatureUpdateTaskManager(CreatureUpdateType.values());

    public PlayerEnt getPlayerEnt() {
        return playerEnt;
    }

    public void setPlayerEnt(PlayerEnt playerEnt) {
        this.playerEnt = playerEnt;
    }

    public int getDispatcherHashCode() {
        //return playerEnt.getAccount().hashCode();
        //采用连续ID防止线程业务分配不均
        return getDispatchId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((playerEnt == null) ? 0 : playerEnt.hashCode());
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
        Player other = (Player) obj;
        if (playerEnt == null) {
            if (other.playerEnt != null) {
                return false;
            }
        } else if (!playerEnt.equals(other.playerEnt)) {
            return false;
        }
        return true;
    }

    public LoginPosition getLoginPosition() {
        return loginPosition;
    }

    public void setLoginPosition(LoginPosition loginPosition) {
        this.loginPosition = loginPosition;
    }

    public ItemStorage getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(ItemStorage warehouse) {
        this.warehouse = warehouse;
    }

    public PackEntity getPackEntity() {
        return packEntity;
    }

    public void setPackEntity(PackEntity packEntity) {
        this.packEntity = packEntity;
    }

    public EquipmentStorage getEquipmentStorage() {
        return equipmentStorage;
    }

    public void setEquipmentStorage(EquipmentStorage equipmentStorage) {
        this.equipmentStorage = equipmentStorage;
    }

    public Purse getPurse() {
        return purse;
    }

    public void setPurse(Purse purse) {
        this.purse = purse;
    }

    public AtomicInteger getEnterWorldSign() {
        return enterWorldSign;
    }

    public QuestBox getQuestBox() {
        return questBox;
    }

    public void setQuestBox(QuestBox questBox) {
        this.questBox = questBox;
    }

    public ModuleOpenBox getModuleOpenBox() {
        return moduleOpenBox;
    }

    public void setModuleOpenBox(ModuleOpenBox moduleOpenBox) {
        this.moduleOpenBox = moduleOpenBox;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void updateHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }


    public long getObjectId() {
        return playerEnt.getPlayerId();
    }

    public String getName() {
        return playerEnt.getName();
    }

    /**
     * 队伍id
     **/
    private int teamId;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    /**
     * 当前正在进入回合战斗驯养师
     */
    private PlayerTrainerCreature trainerCreature;

    public PlayerTrainerCreature getTrainerCreature() {
        return trainerCreature;
    }

    public void setTrainerCreature(PlayerTrainerCreature trainerCreature) {
        this.trainerCreature = trainerCreature;
    }

    public CreatureUpdateTaskManager getUpdateTaskManager() {
        return updateTaskManager;
    }

    public PlayerTrainerEntity getTrainerEntity() {
        return trainerEntity;
    }

    public void setTrainerEntity(PlayerTrainerEntity trainerEntity) {
        this.trainerEntity = trainerEntity;
    }

    public List<PlayerTrainer> getPlayerTrainers() {
        return playerTrainers;
    }

    public void setPlayerTrainers(List<PlayerTrainer> playerTrainers) {
        this.playerTrainers = playerTrainers;
    }

    public CardBag getSelectedCardBag() {
        return selectedCardBag;
    }

    public void setSelectedCardBag(CardBag selectedCardBag) {
        this.selectedCardBag = selectedCardBag;
    }

    public PlayerTrainer getSelectedTrainer() {
        return selectedTrainer;
    }

    public void setSelectedTrainer(PlayerTrainer selectedTrainer) {
        this.selectedTrainer = selectedTrainer;
    }

    public int getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(int dispatchId) {
        this.dispatchId = dispatchId;
    }

    public boolean hasTrainer(long trainerId) {
        if (CollectionUtils.isEmpty(playerTrainers)) {
            return false;
        }
        return playerTrainers.stream().filter(trainer -> trainer.getTrainerId() == trainerId).count() == 1;
    }

    public PlayerTrainer getTrainerCreature(long trainerId) {
        if(!hasTrainer(trainerId)){
           return null;
        }
        return playerTrainers.stream().filter(trainer -> trainer.getTrainerId() == trainerId).findFirst().get();
    }

    /***
     * 玩家是否处于房间战斗
     * @return
     */
    public boolean isInRoomFight() {
        return trainerCreature != null && trainerCreature.getRoom() != null;
    }

    public Map<Long, MoWuNiang> getPlayerMoWuNiang() {
        return playerMoWuNiang;
    }

    public void setPlayerMoWuNiang(Map<Long, MoWuNiang> playerMoWuNiang) {
        this.playerMoWuNiang = playerMoWuNiang;
    }

    public PlayerMwnEntity getPlayerMwnEntity() {
        return playerMwnEntity;
    }

    public void setPlayerMwnEntity(PlayerMwnEntity playerMwnEntity) {
        this.playerMwnEntity = playerMwnEntity;
    }

    public PlayerCommonEnt getCommonEnt() {
        return commonEnt;
    }

    public void setCommonEnt(PlayerCommonEnt commonEnt) {
        this.commonEnt = commonEnt;
    }

    public IntegralEntity getIntegralEntity() {
        return integralEntity;
    }

    public void setIntegralEntity(IntegralEntity integralEntity) {
        this.integralEntity = integralEntity;
    }

    public IntegralStore getIntegralStore() {
        return integralStore;
    }

    public void setIntegralStore(IntegralStore integralStore) {
        this.integralStore = integralStore;
    }

    public TroopStorage getTroopStorage() {
        return troopStorage;
    }

    public void setTroopStorage(TroopStorage troopStorage) {
        this.troopStorage = troopStorage;
    }

    public TroopEntity getTroopEntity() {
        return troopEntity;
    }

    public void setTroopEntity(TroopEntity troopEntity) {
        this.troopEntity = troopEntity;
    }
}
