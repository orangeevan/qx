package com.mmorpg.qx.module.skin.manager;

import com.mmorpg.qx.module.skin.enums.SkinGetType;
import com.mmorpg.qx.module.skin.resource.MWNSkinResource;
import com.mmorpg.qx.module.skin.resource.PlayerTrainerSkinResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: 驯养师&魔物娘皮肤管理
 * @since 19:24 2020-09-27
 */
@Component
public class SkinResourceManager {

    @Autowired
    private TrainerSkinManager trainerSkinManager;

    @Autowired
    private MwnSkinManager mwnSkinManager;

    private static SkinResourceManager instance;

    @PostConstruct
    private void init() {
        instance = this;
    }

    public static SkinResourceManager getInstance() {
        return instance;
    }

    public List<PlayerTrainerSkinResource> getTrainerSkin(int trainerId) {
        return trainerSkinManager.getAllResouces().stream().filter(resource -> resource.getPlayerTrainerId() == trainerId)
                .collect(Collectors.toList());
    }

    public PlayerTrainerSkinResource getTrainerOriginalSkin(int trainerId) {
        return trainerSkinManager.getAllResouces().stream().filter(resource -> resource.getPlayerTrainerId() == trainerId
                && resource.getGetWay() == SkinGetType.Original.getId()).findAny().get();
    }

    public List<MWNSkinResource> getMwnSkin(int mwnId) {
        return mwnSkinManager.getAllResources().stream().filter(resource -> resource.getMwnId() == mwnId).collect(Collectors.toList());
    }

    public MWNSkinResource getMwnOriginalSkin(int mwnId) {
        return mwnSkinManager.getAllResources().stream().filter(resource -> resource.getMwnId() == mwnId
                && resource.getGetWay() == SkinGetType.Original.getId()).findAny().get();
    }

}
