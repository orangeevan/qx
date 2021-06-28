package com.mmorpg.qx.common.identify.manager;

import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.mmorpg.qx.common.identify.entity.IdentifyEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdentifyManager {

    public enum IdentifyType {
        /**
         * 玩家id
         */
        PLAYER(true, "00"),
        /**
         * 道具id
         */
        ITEM(true, "01"),
        /**
         * 系统邮件id
         */
        MAILGROUP(true, "02"),
        /**
         * 邮件id
         */
        MAIL(true, "03"),
        /**
         * 怪物id
         */
        MONSTER(false, "04"),
        /**
         * 玩家驯养师
         */
        TRAINER(true, "05"),
        /**
         * 魔物娘
         */
        MOWUNIANG(true, "06"),
        /**
         * 线程取模Id
         */
        DISPATCH(false, "07"),
        /**
         * 地图对象
         */
        MAP_CREATURE(false, "08"),
        ;

        private final boolean save;
        private String typeSign;

        private IdentifyType(boolean save, String typeSign) {
            this.save = save;
            this.typeSign = typeSign;
        }

        public boolean isSave() {
            return save;
        }

    }

    private static IdentifyManager self;

    public static IdentifyManager getInstance() {
        return self;
    }

    @Inject
    private EntityCacheService<String, IdentifyEntity> identifyEntityService;

    private static final AtomicInteger INDEX = new AtomicInteger();

    @PostConstruct
    protected final void init() {
        self = this;
    }

    public synchronized long getNextIdentify(IdentifyType type) {
        if (!type.isSave()) {
            return INDEX.incrementAndGet();
        }

        // 这里需要根据规则计算一个id的初始值
        final long initValue = createInitValue(type);
        IdentifyEntity identifyEntity = identifyEntityService.loadOrCreate(type.name(), id -> IdentifyEntity.valueOf(id, initValue));
        long result = identifyEntity.getNextIdentify();
        identifyEntityService.writeBack(identifyEntity.getId(), identifyEntity);
        return result;
    }

    private long createInitValue(IdentifyType type) {
        // 配置服务器ID
        StringBuilder serverId = new StringBuilder(ServerConfigValue.getInstance().getServerId()+"");
        while (serverId.length() < 5) {
            serverId.append("0");
        }
        String id = serverId.toString() + type.typeSign + ID_SIGN;
        return Long.valueOf(id);
    }

    private static final String ID_SIGN = "000000000000";
}
