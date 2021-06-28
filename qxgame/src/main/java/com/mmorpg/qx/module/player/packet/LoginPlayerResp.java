package com.mmorpg.qx.module.player.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.PacketId;
import com.mmorpg.qx.common.socket.annotation.SocketPacket;
import com.mmorpg.qx.module.integral.packet.vo.IntegralVo;
import com.mmorpg.qx.module.item.enums.ItemUpdateReason;
import com.mmorpg.qx.module.item.enums.PackType;
import com.mmorpg.qx.module.item.model.PackItem;
import com.mmorpg.qx.module.item.packet.resp.BackPackUpdateResp;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.packet.vo.MoWuNiangVo;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import com.mmorpg.qx.module.trainer.packet.vo.PlayerTrainerVo;
import com.mmorpg.qx.module.troop.model.Troop;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 登陆请求回复包含玩家的大量信息
 *
 * @author wang ke
 * @since v1.0 2019年2月8日
 */
@SocketPacket(packetId = PacketId.LOGIN_PLAYER_RESP)
public class LoginPlayerResp {

    @Protobuf(description = "角色ID", order = 1)
    private long playerId;

    @Protobuf(description = "姓名", order = 2)
    private String name;

    @Protobuf(description = "头像", order = 3)
    private int head;

    @Protobuf(description = "等级", order = 4)
    private int level;

    @Protobuf(description = "经验", order = 5)
    private long exp;

    @Protobuf(description = "提纯氪金", order = 6)
    private long pureKryptonGold;

    @Protobuf(description = "氪金", order = 7)
    private long kryptonGold;

    @Protobuf(description = "金币", order = 8)
    private long gold;

    @Protobuf(description = "仓库", order = 9)
    private BackPackUpdateResp itemStorge;

    @Protobuf(description = "驯养师组", order = 10)
    private List<PlayerTrainerVo> trainers;

    @Protobuf(description = "积分", order = 11)
    private List<IntegralVo> integrals;

    @Protobuf(description = "当前时间", order = 12)
    private long curTime;

    @Protobuf(description = "魔物娘组", order = 13)
    private List<MoWuNiangVo> mwns;

    @Protobuf(description = "编队", order = 14)
    private List<Troop> troops;

    public static LoginPlayerResp valueOf(Player player) {
        LoginPlayerResp resp = new LoginPlayerResp();
        resp.setPlayerId(player.getObjectId());
        resp.setName(player.getName());
        resp.setHead(player.getPlayerEnt().getHead());
        resp.setLevel(player.getPlayerEnt().getLevel());
        resp.setExp(player.getPlayerEnt().getExp());
        resp.setPureKryptonGold(player.getPlayerEnt().getPureKryptonGold());
        resp.setKryptonGold(player.getPlayerEnt().getKryptonGold());
        resp.setGold(player.getPlayerEnt().getGold());
        // 仓库
        Map<Long, PackItem> items = player.getWarehouse().getItems();
        if (!CollectionUtils.isEmpty(items)) {
            resp.setItemStorge(BackPackUpdateResp.valueOf(new ArrayList<>(items.values()),
                    PackType.WAREHOUSE.getType(), ItemUpdateReason.INIT.getReason()));
        }
        // 驯养师
        List<PlayerTrainerVo> trainers = new ArrayList<>();
        List<PlayerTrainer> playerTrainers = player.getPlayerTrainers();
        if (!CollectionUtils.isEmpty(playerTrainers)) {
            playerTrainers.forEach(trainer -> trainers.add(PlayerTrainerVo.valueOf(trainer)));
        }
        // 积分
        Map<Integer, Integer> stores = player.getIntegralStore().getStores();
        resp.integrals = CollectionUtils.isEmpty(stores) ? new ArrayList<>() : stores.entrySet().stream()
                .map(e -> IntegralVo.valueOf(e.getKey(), e.getValue())).collect(Collectors.toList());
        // 魔物娘组
        Map<Long, MoWuNiang> mwnMap = player.getPlayerMoWuNiang();
        resp.mwns = CollectionUtils.isEmpty(mwnMap) ? new ArrayList<>() : mwnMap.values().stream()
                .map(MoWuNiangVo::valueOf).collect(Collectors.toList());
        // 编队
        List<Troop> troops = player.getTroopStorage().getTroops();
        resp.troops = CollectionUtils.isEmpty(troops) ? new ArrayList<>() : troops;
        resp.setTrainers(trainers);
        resp.setCurTime(System.currentTimeMillis());
        return resp;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getPureKryptonGold() {
        return pureKryptonGold;
    }

    public void setPureKryptonGold(long pureKryptonGold) {
        this.pureKryptonGold = pureKryptonGold;
    }

    public long getKryptonGold() {
        return kryptonGold;
    }

    public void setKryptonGold(long kryptonGold) {
        this.kryptonGold = kryptonGold;
    }

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public BackPackUpdateResp getItemStorge() {
        return itemStorge;
    }

    public void setItemStorge(BackPackUpdateResp itemStorge) {
        this.itemStorge = itemStorge;
    }

    public List<PlayerTrainerVo> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<PlayerTrainerVo> trainers) {
        this.trainers = trainers;
    }

    public List<IntegralVo> getIntegrals() {
        return integrals;
    }

    public void setIntegrals(List<IntegralVo> integrals) {
        this.integrals = integrals;
    }

    public long getCurTime() {
        return curTime;
    }

    public void setCurTime(long curTime) {
        this.curTime = curTime;
    }

    public List<MoWuNiangVo> getMwns() {
        return mwns;
    }

    public void setMwns(List<MoWuNiangVo> mwns) {
        this.mwns = mwns;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }
}
