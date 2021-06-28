package com.mmorpg.qx.module.moduleopen.manager;

import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.utility.JsonUtils;
import com.mmorpg.qx.common.moduletype.ModuleType;
import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.module.moduleopen.entity.ModuleOpenEntity;
import com.mmorpg.qx.module.moduleopen.model.ModuleOpenBox;
import com.mmorpg.qx.module.moduleopen.resource.ModuleOpenResource;
import com.mmorpg.qx.module.player.model.Player;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;

@Component
public class ModuleOpenManager implements ResourceReload, EntityOfPlayerUpdateRule {

    @Inject
    private EntityCacheService<Long, ModuleOpenEntity> moduleOpenCacheService;

    @Static
    private Storage<Integer, ModuleOpenResource> moduleOpenResources;

    public ModuleOpenResource getResource(int moduleId) {
        return moduleOpenResources.get(moduleId, true);
    }

    @PostConstruct
    private void init() {
        initResources();
    }

    private void initResources() {
        moduleOpenResources.getAll().forEach(module -> module.setMoType(ModuleType.valueOf(module.getType())));
    }


    @Override
    public void initPlayer(Player player) {
        ModuleOpenEntity openEntity = moduleOpenCacheService.loadOrCreate(player.getObjectId(),
                id -> {
                    ModuleOpenEntity oe = new ModuleOpenEntity();
                    oe.setModuleOpen("[]");
                    oe.setPlayerId(id);
                    return oe;
                });
        ModuleOpenBox openBox = new ModuleOpenBox();
        openBox.setOpened(new HashSet<>(JsonUtils.string2List(openEntity.getModuleOpen(), Integer.class)));
        openBox.setModuleOpenEntity(openEntity);
        player.setModuleOpenBox(openBox);

    }

    @Override
    public void update(Player player) {
        // 数据量少即时存储
        logoutWriteBack(player);
    }

    @Override
    public void logoutWriteBack(Player player) {
        ModuleOpenBox openBox = player.getModuleOpenBox();
        openBox.getModuleOpenEntity().setModuleOpen(JsonUtils.object2String(openBox.getOpened()));
        moduleOpenCacheService.writeBack(player.getObjectId(), openBox.getModuleOpenEntity());
    }

    public Storage<Integer, ModuleOpenResource> getModuleOpenResources() {
        return moduleOpenResources;
    }

    @Override
    public void reload() {
        initResources();
    }

    @Override
    public Class<?> getResourceClass() {
        return ModuleOpenResource.class;
    }
}
