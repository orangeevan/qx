package com.mmorpg.qx.module.mwn.manager;

import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.utility.JsonUtils;
import com.haipaite.common.utility.SelectRandom;
import com.haipaite.common.utility.StringUtils;
import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.configValue.ConfigValueManager;
import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.module.mwn.entity.PlayerMwnEntity;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.mwn.service.MWNService;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.player.model.Player;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description:魔物娘相关业务
 * @since 10:34 2020-08-18
 */
@Component
public class MWNManager implements ResourceReload, EntityOfPlayerUpdateRule {

    @Static
    private Storage<Integer, MWNResource> mwnResourceStorage;

    /**
     * 魔物娘缓存
     */
    @Inject
    private EntityCacheService<Long, PlayerMwnEntity> mwnEntityCache;

    @PostConstruct
    private void init() {
        initResource();
    }

    private void initResource() {
        mwnResourceStorage.getAll().forEach(MWNResource::initAttr);
    }

    @Override
    public void initPlayer(Player player) {
        PlayerMwnEntity entity = mwnEntityCache.loadOrCreate(player.getPlayerEnt().getPlayerId(), id -> {
            PlayerMwnEntity pe = new PlayerMwnEntity();
            pe.setPlayerId(id);
            return pe;
        });
        player.setPlayerMwnEntity(entity);
        // 初始化魔物娘
        if (!StringUtils.isBlank(entity.getMwnJson())) {
            Map<Long, MoWuNiang> playerMwn = JsonUtils.string2Map(entity.getMwnJson(), Long.class, MoWuNiang.class);
            player.setPlayerMoWuNiang(playerMwn);
        } else {
            Map<Long, MoWuNiang> playerMwn = new HashMap<>();
            for (MWNResource resource : getAllMwnResource()) {
                MoWuNiang moWuNiang = MWNService.getInstance().createMwn(player, resource.getId());
                playerMwn.put(moWuNiang.getId(), moWuNiang);
            }
            player.setPlayerMoWuNiang(playerMwn);
        }
    }

    @Override
    public void update(Player player) {
        if (player.getPlayerMwnEntity() == null) {
            return;
        }
        player.getUpdateTaskManager().addUpdateTaskDelayMinute(CreatureUpdateType.PLAYER_MWN, new AbstractDispatcherHashCodeRunable() {
            @Override
            public int getDispatcherHashCode() {
                return player.getDispatcherHashCode();
            }

            @Override
            public String name() {
                return "UpdatePlayerMWN";
            }

            @Override
            public void doRun() {
                logoutWriteBack(player);
            }
        }, 1);
    }

    @Override
    public void logoutWriteBack(Player player) {
        if (player.getPlayerMwnEntity() == null) {
            return;
        }
        player.getPlayerMwnEntity().setMwnJson(JsonUtils.map2String(player.getPlayerMoWuNiang()));
        mwnEntityCache.writeBack(player.getPlayerEnt().getPlayerId(), player.getPlayerMwnEntity());
    }

    public Collection<MWNResource> getAllMwnResource() {
        return mwnResourceStorage.getAll();
    }

    public MWNResource getMWNResource(int resourceId) {
        return mwnResourceStorage.get(resourceId, true);
    }

    public static MWNManager getInstance() {
        return BeanService.getBean(MWNManager.class);
    }

    /***
     * 随机魔物娘
     * @param type
     * @param trainer
     * @return
     */
    public MWNResource randomResource(AttrType type, AbstractTrainerCreature trainer, boolean needDistinct, List<MWNResource> filterResources) {
        if (CollectionUtils.isEmpty(filterResources)) {
            filterResources = new ArrayList<>(mwnResourceStorage.getAll());
        }
        if (type != null) {
            filterResources = filterResources.stream().filter(resource -> GameUtil.hasAttrType(type, resource.getBaseAttrsList()) && resource.canRandom()).collect(Collectors.toList());
        }
        if (needDistinct) {
            for (MoWuNiang mwn : trainer.getUseCardStorage().getMwns()) {
                filterResources.remove(mwn.getResource());
            }
        }
        if (CollectionUtils.isEmpty(filterResources)) {
            return null;
        }
        SelectRandom<MWNResource> selector = new SelectRandom<>();
        filterResources.forEach(resource -> selector.addElement(resource, 1));
        List<MWNResource> randomResourceList = selector.run(1);
        return randomResourceList.get(0);
    }

    /**
     * 魔物娘技能进化临界点
     *
     * @return
     */
    public int getEvoPoint(AttrType eleType) {
        String evolutionPoint = ConfigValueManager.getInstance().getStrConfigValue("EvolutionPoint");
        Map<AttrType, Integer> attrTypeIntegerMap = JsonUtils.string2Map(evolutionPoint, AttrType.class, Integer.class);
        return attrTypeIntegerMap.getOrDefault(eleType, Integer.MAX_VALUE);
    }

    @Override
    public void reload() {
        initResource();
    }

    @Override
    public Class<?> getResourceClass() {
        return MWNResource.class;
    }
}
