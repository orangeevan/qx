package com.mmorpg.qx.module.trainer.service;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.item.model.PackItem;
import com.mmorpg.qx.module.item.service.ItemService;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.skin.manager.SkinResourceManager;
import com.mmorpg.qx.module.skin.resource.PlayerTrainerSkinResource;
import com.mmorpg.qx.module.trainer.manager.TrainerManager;
import com.mmorpg.qx.module.trainer.module.PlayerTrainer;
import com.mmorpg.qx.module.trainer.packet.TrainerChangeSkinResp;
import com.mmorpg.qx.module.trainer.packet.TrainerGoFightResp;
import com.mmorpg.qx.module.trainer.packet.TrainerListResp;
import com.mmorpg.qx.module.trainer.packet.TrainerUnlockResp;
import com.mmorpg.qx.module.trainer.packet.vo.PlayerTrainerVo;
import com.mmorpg.qx.module.trainer.resource.PlayerTrainerResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 玩家驯养师业务处理
 * @since 15:07 2020-08-19
 */
@Component
public class PlayerTrainerService {

    /**
     * 创建玩家驯养师
     *
     * @param player
     * @param resourceId
     */
    public PlayerTrainer createTrainer(Player player, int resourceId) {
        //TODO 后面根据具体业务添加创建条件判断
        PlayerTrainerResource trainerResource = TrainerManager.getInstance().getPlayerTrainerResource(resourceId);
        PlayerTrainer playerTrainer = new PlayerTrainer();
        playerTrainer.setTrainerId(IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.TRAINER));
        playerTrainer.setResourceId(resourceId);
        //playerTrainer.setAttrList(trainerResource.getBaseAttrsList());
        //元素点职业点从皮肤获取
        PlayerTrainerSkinResource trainerOriginalSkin = SkinResourceManager.getInstance().getTrainerOriginalSkin(resourceId);
        playerTrainer.setExp(0L);
        playerTrainer.setName(trainerResource.getName());
        playerTrainer.setSkinResourceId(trainerOriginalSkin.getId());
        if (playerTrainer.getSkinIdList() == null) {
            playerTrainer.setSkinIdList(new ArrayList<>());
        }
        playerTrainer.getSkinIdList().add(trainerOriginalSkin.getId());
        playerTrainer.setOwner(player);
        if (player.getPlayerTrainers() == null) {
            player.setPlayerTrainers(new ArrayList<>());
        }
        player.getPlayerTrainers().add(playerTrainer);
        System.err.println("创建驯养师: " + resourceId);
        TrainerManager.getInstance().update(player);
        return playerTrainer;
    }

    /**
     * 驯养师列表
     *
     * @param player
     */
    public void trainerList(Player player) {
        TrainerListResp resp = new TrainerListResp();
        List<PlayerTrainerVo> trainerList = player.getPlayerTrainers().stream()
                .map(PlayerTrainerVo::valueOf).collect(Collectors.toList());
        resp.setTrainerList(trainerList);
        PacketSendUtility.sendPacket(player, resp);
    }

    /**
     * 驯养师解锁
     *
     * @param player
     * @param resourceId 驯养师配置ID
     * @param chipId     驯养师碎片ID
     */
    public void trainerUnlock(Player player, int resourceId, long chipId) {
        // 判断驯养师是否已解锁
        List<PlayerTrainer> trainers = player.getPlayerTrainers();
        if (!CollectionUtils.isEmpty(trainers) && trainers.stream().anyMatch(t -> t.getResourceId() == resourceId)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_HAS_UNLOCK);
        }
        PlayerTrainerResource resource = TrainerManager.getInstance().getPlayerTrainerResource(resourceId);
        PackItem packItem = ItemService.getInstance().getOrThrowItem(player, chipId);
        // 驯养师碎片配置ID验证
        if (packItem.getKey() != resource.getChipKey()) {
            throw new ManagedException(ManagedErrorCode.TRAINER_UNLOCK_CHIP_ERROR);
        }
        // 消耗驯养师碎片
        ItemService.getInstance().consumeItem(player, chipId, "trainer", resource.getChipNum());
        // 创建驯养师
        PlayerTrainer trainer = createTrainer(player, resourceId);
        PacketSendUtility.sendPacket(player, TrainerUnlockResp.valueOf(trainer));
    }

    /**
     * 驯养师更换皮肤
     *
     * @param player
     * @param trainerId
     * @param skinId
     */
    public void trainerChangeSkin(Player player, long trainerId, int skinId) {
        PlayerTrainer trainer = player.getPlayerTrainers().stream()
                .filter(t -> t.getTrainerId() == trainerId).findFirst().orElse(null);
        if (trainer == null) {
            throw new ManagedException(ManagedErrorCode.TRAINER_NOT_EXIST);
        }
        if (skinId == trainer.getSkinResourceId()) {
            throw new ManagedException(ManagedErrorCode.TRAINER_HAS_WEAR_SKIN);
        }
        if (!trainer.getSkinIdList().contains(skinId)) {
            throw new ManagedException(ManagedErrorCode.TRAINER_NOT_HAVE_SKIN);
        }
        trainer.setSkinResourceId(skinId);
        TrainerManager.getInstance().update(player);
        PacketSendUtility.sendPacket(player, TrainerChangeSkinResp.valueOf(trainerId, skinId));
    }

    /**
     * 驯养师出战
     *
     * @param player
     * @param trainerId
     */
    public void trainerGoFight(Player player, long trainerId) {
        PlayerTrainer trainer = player.getPlayerTrainers().stream()
                .filter(t -> t.getTrainerId() == trainerId).findFirst().orElse(null);
        if (trainer == null) {
            throw new ManagedException(ManagedErrorCode.TRAINER_NOT_EXIST);
        }
        if (trainer.isInFight()) {
            throw new ManagedException(ManagedErrorCode.TRAINER_IS_IN_FIGHT);
        }
        player.getPlayerTrainers().forEach(t -> {
            if (t.isInFight()) {
                t.setInFight(false);
            }
        });
        trainer.setInFight(true);
        TrainerManager.getInstance().update(player);
        PacketSendUtility.sendPacket(player, TrainerGoFightResp.valueOf(trainerId));
    }

    public static PlayerTrainerService getInstance() {
        return BeanService.getBean(PlayerTrainerService.class);
    }

}
