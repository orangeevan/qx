package com.mmorpg.qx.common.session;

import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.RobotTrainerCreature;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 发包工具类
 *
 * @author wang ke
 * @since v1.0 2018年3月9日
 */
public class PacketSendUtility {
    @SuppressWarnings("unused")
    private static final Logger logger = SysLoggerFactory.getLogger(PacketSendUtility.class);

    /**
     * 发给该玩家,机器人消息全地图广播
     *
     * @param abstractVisibleObject
     * @param packet
     */
    public static void sendPacket(AbstractVisibleObject abstractVisibleObject, Object packet) {
        if (abstractVisibleObject.getObjectType() == ObjectType.PLAYER_TRAINER) {
            PlayerTrainerCreature playerTrainerCreature = (PlayerTrainerCreature) abstractVisibleObject;
            Player player = playerTrainerCreature.getOwner();
            if (Objects.nonNull(player)) {
                PlayerManager.getInstance().sendPacket(player, packet);
            }
        } else if (abstractVisibleObject instanceof RobotTrainerCreature) {
            broadcastInWorldMap(abstractVisibleObject, packet, false);
        }
    }

    public static void sendPacket(Player player, Object packet) {
        PlayerManager.getInstance().sendPacket(player, packet);
    }

    /**
     * 视野广播
     * 广播给改对象周围的玩家
     *
     * @param visibleObject
     * @param packet
     */
    public static void broadcastInWorldMap(AbstractVisibleObject visibleObject, Object packet, boolean toSelf) {
        if (toSelf && visibleObject.getObjectType() == ObjectType.PLAYER_TRAINER) {
            sendPacket(visibleObject, packet);
        }
        broadcastInWorldMap(visibleObject.getWorldMapInstance(), packet, Stream.of(visibleObject).collect(Collectors.toList()));
    }

    public static void broadcastInWorldMap(WorldMapInstance worldMapInstance, final Object packet, Collection<AbstractVisibleObject> filters) {
        if (Objects.nonNull(worldMapInstance)) {
            WorldMapService.getInstance().broadcastInWorldMap(worldMapInstance, packet, filters);
        }
    }

    /**
     * 全服广播
     */
    public static void broadcast2Server(AbstractVisibleObject visibleObject, Object packet, boolean toSelf) {
        PlayerManager.getInstance().braodcast2Server(visibleObject, packet, toSelf);
    }

    public static void sendErrorMessage(Player player, int errorCode) {
        PlayerManager.getInstance().sendPacket(player, ErrorPacketUtil.getErrorPacket(errorCode));
    }

    public static void broadcastInWorldMap(List<AbstractCreature> objectList, Object packet) {
        if (CollectionUtils.isEmpty(objectList)) {
            return;
        }
        objectList.stream().forEach(object -> sendPacket(object, packet));
    }
}
