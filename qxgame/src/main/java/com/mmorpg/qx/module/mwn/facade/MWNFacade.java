package com.mmorpg.qx.module.mwn.facade;

import com.haipaite.common.event.anno.ReceiverAnno;
import com.mmorpg.qx.common.exception.ErrorPacketUtil;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.socket.annotation.SocketClass;
import com.mmorpg.qx.common.socket.annotation.SocketMethod;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.packet.CreateMwnReq;
import com.mmorpg.qx.module.mwn.packet.DevelopMwnReq;
import com.mmorpg.qx.module.mwn.packet.MwnChangeSkinReq;
import com.mmorpg.qx.module.mwn.packet.MwnListReq;
import com.mmorpg.qx.module.mwn.service.MWNService;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.object.gameobject.event.TrainerJobOrEleAlterEvent;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.Player;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wang ke
 * @description: 魔物娘对外门面
 * @since 13:36 2020-08-19
 */
@Component
@SocketClass
public class MWNFacade {

    private static final Logger logger = SysLoggerFactory.getLogger(MWNFacade.class);

    @Autowired
    private MWNService mwnService;

    @SocketMethod
    public void createMwn(Wsession wsession, CreateMwnReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            mwnService.createMwn(player, req.getMwnId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(wsession, e.getCode());
        } catch (Exception e) {
            logger.error("createPlayerTrainer", e);
        }
    }

    @SocketMethod
    public void mwnList(Wsession wsession, MwnListReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            mwnService.mwnList(player);
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(wsession, e.getCode());
        } catch (Exception e) {
            logger.error("魔物娘列表异常", e);
        }
    }

    @SocketMethod
    public void developMwn(Wsession wsession, DevelopMwnReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            mwnService.developMwn(player, req.getMwnId(), req.getType());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(wsession, e.getCode());
        } catch (Exception e) {
            logger.error("培养魔物娘异常", e);
        }
    }

    @SocketMethod
    public void mwnChangeSkin(Wsession wsession, MwnChangeSkinReq req) {
        try {
            Player player = PlayerManager.getInstance().getPlayerBySession(wsession);
            mwnService.mwnChangeSkin(player, req.getMwnId(), req.getSkinId());
        } catch (ManagedException e) {
            ErrorPacketUtil.sendError(wsession, e.getCode());
        } catch (Exception e) {
            logger.error("魔物娘更换皮肤异常", e);
        }
    }

    @ReceiverAnno
    public void onTrainerJobOrEleChange(TrainerJobOrEleAlterEvent event) {
        AbstractTrainerCreature trainerCreature = event.getCreature();
        int currentAttr = trainerCreature.getAttrController().getCurrentAttr(event.getEleAttrType());
        int evoPoint = MWNManager.getInstance().getEvoPoint(event.getEleAttrType());
        List<MWNCreature> mwnEvoList;
        boolean isEvo = true;
        int evo = 0;
        //当前比临界点低
        if (currentAttr < evoPoint) {
            if (event.getEleAttrBefore() >= evoPoint) {
                evo = 2;
            }
            isEvo = false;
            mwnEvoList = mwnService.getMwnHasEleEvo(trainerCreature, event.getEleAttrType());
        } else {
            evo = 1;
            mwnEvoList = mwnService.getMwnHasEleNeedEvo(trainerCreature, event.getEleAttrType());
        }
        AttrType attrType = event.getEleAttrType();
        mwnService.mwnSKillEvo(mwnEvoList, isEvo);
        mwnService.sendMwnEvoOrOcc(mwnEvoList, evo, trainerCreature, event.getReason(), Attr.valueOf(attrType, event.getEleAttrBefore()));
    }
}
