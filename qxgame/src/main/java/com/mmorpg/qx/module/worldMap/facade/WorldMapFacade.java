package com.mmorpg.qx.module.worldMap.facade;


import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.worldMap.enums.DirType;
import com.mmorpg.qx.module.worldMap.manager.MapResourceManager;
import com.mmorpg.qx.module.worldMap.model.WorldMap;
import com.mmorpg.qx.module.worldMap.packet.CreateWorldMapReq;
import com.mmorpg.qx.module.worldMap.service.WorldMapService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wang ke
 * @description:
 * @since 13:32 2020-08-03
 */
@Component
@SocketClass
public class WorldMapFacade {
    private Logger logger = SysLoggerFactory.getLogger(WorldMapFacade.class);

    @Autowired
    private PlayerManager playerManager;

    @Autowired
    private WorldMapService service;

    @Autowired
    private MapResourceManager mapResourceManager;

//    /***
//     * 玩家请求移动
//     * @param wsession
//     * @param moveReq
//     */
//    @SocketMethod
//    public void playerMove(Wsession wsession, PlayerMoveReq moveReq){
//        try{
//            Player player = playerManager.getPlayerBySession(wsession);
//            PlayerTrainerCreature trainer = player.getTrainerCreature();
//            //检查玩家当前格子前后端是否一致
//           if(!trainer.isInPosition(moveReq.getCurrGrid())){
//               ErrorPacketUtil.sendError(wsession, ManagedErrorCode.GRID_ERROR);
//               //前后端格子不一致，修正前端格子
//               trainer.sendUpdatePosition();
//               return;
//           }
//            //检验方向是否合法
//            DirType dirType = moveReq.getDir();
//            service.move(trainer, moveReq.getStep(), dirType);
//        }catch (ManagedException e) {
//            ErrorPacketUtil.sendError(wsession, e.getCode());
//        }catch(Exception e){
//            ErrorPacketUtil.sendError(wsession,  ManagedErrorCode.ERROR_MSG);
//            logger.error("", e);
//        }
//    }

    @SocketMethod
    public void createMap(Wsession wsession, CreateWorldMapReq req){
        try{
            Player player = playerManager.getPlayerBySession(wsession);
            WorldMap worldMap = mapResourceManager.getWorldMap(req.getMapId());
            if (worldMap == null) {
                return;
            }
            service.createWorldMapInstance(worldMap, true);
        }catch(Exception e){
            logger.error("", e);
        }
    }
}
