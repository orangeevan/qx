package com.mmorpg.qx.module.moduleopen.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.moduleopen.manager.ModuleOpenManager;
import com.mmorpg.qx.module.moduleopen.packet.ModuleOpenResp;
import com.mmorpg.qx.module.moduleopen.resource.ModuleOpenResource;
import com.mmorpg.qx.module.player.model.Player;

@Component
public class ModuleOpenService {

    @Autowired
    private ModuleOpenManager moduleOpenManager;

    public void refresh(Player player) {
        List<Integer> adds = new ArrayList<>();
        for (ModuleOpenResource resource : moduleOpenManager.getModuleOpenResources().getAll()) {
            if (isModuleOpen(player, resource)) {
                player.getModuleOpenBox().getOpened().add(resource.getId());
                adds.add(resource.getId());
            }
        }
        if (!adds.isEmpty()) {
            ModuleOpenResp resp = new ModuleOpenResp();
            resp.setOpens(adds);
            PacketSendUtility.sendPacket(player, resp);
        }

    }

    /**
     * 判断模块是否开启
     * @param player
     * @param resource
     * @return
     */
    public boolean isModuleOpen(Player player, ModuleOpenResource resource) {
        if (player.getModuleOpenBox().getOpened().contains(resource.getId())) {
            return true;
        }
        if (resource.getLevel() > player.getPlayerEnt().getLevel()) {
            return false;
        }
        if (resource.getBarrier() > player.getPlayerEnt().getBarrier()) {
            return false;
        }
        return true;
    }
}
