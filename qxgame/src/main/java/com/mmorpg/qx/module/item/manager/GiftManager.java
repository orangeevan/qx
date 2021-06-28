package com.mmorpg.qx.module.item.manager;

import com.haipaite.common.resource.ResourceReload;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.item.resource.GiftResource;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author zhang peng
 * @description
 * @since 14:42 2021/4/26
 */
@Component
public class GiftManager implements ResourceReload {

    private Logger logger = SysLoggerFactory.getLogger(GiftManager.class);

    @PostConstruct
    private void check() {
        for (GiftResource resource : giftStorage.getAll()) {
            List<Integer> itemIds = resource.getItemIds();
            List<Integer> nums = resource.getNums();
            List<Integer> weights = resource.getWeights();
            if (itemIds.size() != nums.size() || itemIds.size() != weights.size()) {
                logger.error("礼包配置表物品数量不一致，resourceId : %s", resource.getId());
                throw new RuntimeException();
            }
        }
    }

    @Static
    private Storage<Integer, GiftResource> giftStorage;

    public GiftResource getResource(int id) {
        return giftStorage.get(id, true);
    }

    @Override
    public void reload() {

    }

    @Override
    public Class<?> getResourceClass() {
        return GiftResource.class;
    }
}
