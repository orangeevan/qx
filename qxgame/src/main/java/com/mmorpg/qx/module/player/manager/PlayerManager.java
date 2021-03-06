package com.mmorpg.qx.module.player.manager;

import com.haipaite.common.event.core.EventBusManager;
import com.haipaite.common.ramcache.anno.Inject;
import com.haipaite.common.ramcache.orm.Querier;
import com.haipaite.common.ramcache.service.EntityBuilder;
import com.haipaite.common.ramcache.service.EntityCacheService;
import com.haipaite.common.resource.Storage;
import com.haipaite.common.resource.anno.Static;
import com.haipaite.common.threadpool.AbstractDispatcherHashCodeRunable;
import com.haipaite.common.threadpool.IdentityEventExecutorGroup;
import com.haipaite.common.utility.collection.ConcurrentHashSet;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.rule.EntityOfPlayerUpdateRule;
import com.mmorpg.qx.common.session.SessionUtils;
import com.mmorpg.qx.common.socket.config.ServerConfigConstant;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.account.model.KickReason;
import com.mmorpg.qx.module.object.gameobject.AbstractVisibleObject;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.object.gameobject.update.CreatureUpdateType;
import com.mmorpg.qx.module.player.entity.PlayerEnt;
import com.mmorpg.qx.module.player.event.LogoutEvent;
import com.mmorpg.qx.module.player.event.PlayerHeartbeatEvent;
import com.mmorpg.qx.module.player.model.LoginPositionType;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.player.model.RoleType;
import com.mmorpg.qx.module.player.model.loginposition.AbstractLoginPositionHandle;
import com.mmorpg.qx.module.player.packet.vo.PlayerShortInfo;
import com.mmorpg.qx.module.player.resource.PlayerLevelResource;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ???????????????
 *
 * @author wang ke
 * @since v1.0 2019???2???7???
 */
@Component
public class PlayerManager implements ApplicationContextAware, EntityOfPlayerUpdateRule {

    private Logger logger = SysLoggerFactory.getLogger(PlayerManager.class);

    /**
     * ????????????????????????.<??????,<serverid,????????????>>
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<PlayerShortInfo>>> accountToPlayer = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, PlayerShortInfo> idToPlayer = new ConcurrentHashMap<>();
    /**
     * ??????????????????,?????????????????????
     */
    private ConcurrentHashSet<String> allNames = new ConcurrentHashSet<>();

    @Inject
    private EntityCacheService<Long, PlayerEnt> playerCacheService;

    @Autowired
    private Querier querier;

    @PostConstruct
    public void init() {
        self = this;
        List<Object[]> allPlayerShortInfo = querier.list(Object[].class, "PlayerEnt.playerShortInfo");
        for (Object[] objects : allPlayerShortInfo) {
            // playerId,account,serverId,name,role
            PlayerShortInfo shortInfo = new PlayerShortInfo();
            shortInfo.setPlayerId((long) objects[0]);
            shortInfo.setAccount((String) objects[1]);
            shortInfo.setServerId((int) objects[2]);
            shortInfo.setName((String) objects[3]);
            shortInfo.setRole((int) objects[4]);
            if (!accountToPlayer.containsKey(shortInfo.getAccount())) {
                accountToPlayer.put(shortInfo.getAccount(), new ConcurrentHashMap<Integer, List<PlayerShortInfo>>());
            }
            ConcurrentHashMap<Integer, List<PlayerShortInfo>> serverToPlayer = accountToPlayer
                    .get(shortInfo.getAccount());
            if (!serverToPlayer.containsKey(shortInfo.getServerId())) {
                serverToPlayer.put(shortInfo.getServerId(), new ArrayList<>());
            }
            serverToPlayer.get(shortInfo.getServerId()).add(shortInfo);
            idToPlayer.putIfAbsent(shortInfo.getPlayerId(), shortInfo);
            allNames.add(shortInfo.getName());
        }
        // ????????????????????????
        IdentityEventExecutorGroup.addScheduleAtFixedRate(new AbstractDispatcherHashCodeRunable() {
            @Override
            public String name() {
                return "initSystemPlayerHeartbeat";
            }

            @Override
            public int getDispatcherHashCode() {
                // ??????????????????
                return this.hashCode();
            }

            @Override
            public void doRun() {
                systemPlayerHeartbeat();
            }
        }, 1, ServerConfigConstant.PLAYER_HEAR_BEAT_PERIOD, TimeUnit.MILLISECONDS);
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<PlayerShortInfo>>> getAccountToPlayer() {
        return accountToPlayer;
    }

    /**
     * ???????????????????????????????????????
     * ???????????????session
     * ???2??????????????????????????????
     */
    private Object sessionLock = new Object();
    private ConcurrentHashMap<String, Wsession> accountToSession = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Wsession, Player> sessionToPlayer = new ConcurrentHashMap<>();

    private static PlayerManager self;

    public static PlayerManager getInstance() {
        return self;
    }

    public void sendPacket(Player player, Object message) {
        Wsession session = accountToSession.get(player.getPlayerEnt().getAccount());
        if (session != null) {
            session.sendPacket(message);
        }
    }

    /**
     * ??????????????????,??????????????????????????????????????????????????????
     *
     * @param sender  ?????????
     * @param message ?????????
     * @param toSelf  ??????????????????
     */
    public void braodcast2Server(AbstractVisibleObject sender, final Object message, boolean toSelf) {
        if (toSelf) {
            sessionToPlayer.keySet().forEach((wsession) ->
                    wsession.sendPacket(message)
            );
        } else {
            if (sender == null || !(sender instanceof PlayerTrainerCreature)) {
                return;
            }
            Player player = ((PlayerTrainerCreature) sender).getOwner();
            sessionToPlayer.entrySet().stream().filter(entry -> !player.equals(entry.getValue())).forEach(ent -> {
                if (ent.getKey() != null) {
                    ent.getKey().sendPacket(message);
                }
            });
        }
    }

    /**
     * ???????????????????????????session
     *
     * @param session
     * @param player
     */
    public void addSession(Wsession session, Player player) {
        // ??????????????????????????????
        //boolean needlogout = false;
        synchronized (sessionLock) {
            Wsession oldSession = accountToSession.get(player.getPlayerEnt().getAccount());
            if (oldSession != null) {
                // ?????????????????????,???????????????????????????
                //needlogout = true;
                // ?????????
                SessionUtils.kickPlayerAndClose(oldSession, KickReason.REPEATED_LOGIN);
                logger.info("?????????" + player.getPlayerEnt().getAccount() + " ??????????????????????????????");
            }
            accountToSession.put(player.getPlayerEnt().getAccount(), session);
            sessionToPlayer.put(session, player);
        }

//        if (needlogout) {
//            // ?????????????????????????????????
//            logout(player);
//        }
    }

    /**
     * channel??????????????????????????????
     */
    public void channelCloseSetSession(Wsession session) {
        String account = SessionUtils.getAccount(session);
        if (account == null) {
            // ???????????????????????????,?????????
            return;
        }

        // ?????????????????????????????????
        Player logoutPlayer = null;
        synchronized (sessionLock) {
            Wsession oldSession = accountToSession.get(account);
            // ?????????????????????????????????session
            if (oldSession == session) {
                logoutPlayer = removeOldSessionCache(account);
                System.err.println("??????session ??? " + logoutPlayer.getName());
            }
        }
        if (logoutPlayer != null) {
            logout(logoutPlayer);
        }

    }

    /**
     * ????????????
     */
    public void systemPlayerHeartbeat() {
        if (CollectionUtils.isEmpty(sessionToPlayer)) {
            return;
        }
        sessionToPlayer.values().forEach(player -> EventBusManager.getInstance().syncSubmit(PlayerHeartbeatEvent.valueOf(player)));
    }

    /**
     * ??????
     */
    //@Autowired
    //private WorldMap world;

    /**
     * ????????????,???????????????????????????????????????
     *
     * @param player
     */
    public void logout(Player player) {

        // ??????????????????????????????
        //world.despawn(player);

        // ????????????
        LogoutEvent event = LogoutEvent.valueOf(player);
        // ????????????
        EventBusManager.getInstance().syncSubmit(event);

        // TODO ????????????????????????????????????????????????,?????????????????????????????????????????????????????????,?????????????????????????????????
        Map<String, EntityOfPlayerUpdateRule> onThePlayersEntityUpdateRules = applicationContext.getBeansOfType(EntityOfPlayerUpdateRule.class);
        for (EntityOfPlayerUpdateRule rule : onThePlayersEntityUpdateRules.values()) {
            try {
                rule.logoutWriteBack(player);
            } catch (Exception e) {
                logger.error(String.format("%s ???????????????????????????!", rule.getClass()), e);
            }
        }
    }

    /**
     * ????????????
     *
     * @param name
     * @return
     */
    public boolean nameRegister(String name) {
        synchronized (allNames) {
            if (allNames.contains(name)) {
                return false;
            }
            allNames.add(name);
            return true;
        }
    }

    public PlayerEnt createPlayerEnt(long id, String account, String name, int role, int serverId, int sex) {
        PlayerEnt playerEnt = playerCacheService.create(id, new EntityBuilder<Long, PlayerEnt>() {
            @Override
            public PlayerEnt newInstance(Long id) {
                PlayerEnt playerEnt = new PlayerEnt();
                playerEnt.setAccount(account);
                playerEnt.setPlayerId(id);
                playerEnt.setName(name);
                playerEnt.setRole(role);
                playerEnt.setServerId(serverId);
                playerEnt.setSex(sex);

                // TODO ???????????? ?????????
                playerEnt.setPureKryptonGold(10000L);
                playerEnt.setKryptonGold(10000L);
                playerEnt.setGold(10000L);

                return playerEnt;
            }
        });
        PlayerShortInfo playerShortInfo = new PlayerShortInfo();
        playerShortInfo.setAccount(playerEnt.getAccount());
        playerShortInfo.setName(playerEnt.getName());
        playerShortInfo.setPlayerId(id);
        playerShortInfo.setRole(role);
        playerShortInfo.setServerId(serverId);
        addPlayerShortInfo(playerShortInfo);

        return playerEnt;
    }

    /**
     * ?????????
     *
     * @param playerShortInfo
     */
    private void addPlayerShortInfo(PlayerShortInfo playerShortInfo) {
        synchronized (accountToPlayer) {
            if (accountToPlayer.get(playerShortInfo.getAccount()) == null) {
                accountToPlayer.put(playerShortInfo.getAccount(), new ConcurrentHashMap<>());
            }
            if (accountToPlayer.get(playerShortInfo.getAccount()).get(playerShortInfo.getServerId()) == null) {
                accountToPlayer.get(playerShortInfo.getAccount()).put(playerShortInfo.getServerId(), new ArrayList<>());
            }
            accountToPlayer.get(playerShortInfo.getAccount()).get(playerShortInfo.getServerId()).add(playerShortInfo);
            idToPlayer.putIfAbsent(playerShortInfo.getPlayerId(), playerShortInfo);
        }
    }

    public PlayerShortInfo getPlayerShortInfo(String account, int serverId, long playerId) {
        if (accountToPlayer.get(account) != null) {
            if (accountToPlayer.get(account).get(serverId) != null) {
                for (PlayerShortInfo psi : accountToPlayer.get(account).get(serverId)) {
                    if (psi.getPlayerId() == playerId) {
                        return psi;
                    }
                }
            }
        }
        return null;
    }

    public PlayerShortInfo getPlayerShortInfo(long playerId) {
        return idToPlayer.get(playerId);
    }

    @Static
    private Storage<Integer, PlayerLevelResource> playerLeveltResources;

    public void addPlayerLevelAttr(PlayerTrainerCreature player) {
        int level = player.getOwner().getPlayerEnt().getLevel();
        PlayerLevelResource playerLeveltResource = playerLeveltResources.get(level, true);
        List<Attr> stats = playerLeveltResource.getRoleStat(RoleType.getById(player.getOwner().getPlayerEnt().getRole()));
        player.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Level_Base), stats);
    }

    public PlayerLevelResource getPlayerLeveltResources(int level) {
        return playerLeveltResources.get(level, true);
    }

    public PlayerEnt loadPlayerEnt(long playerId) {
        return playerCacheService.load(playerId);
    }

    private Map<LoginPositionType, AbstractLoginPositionHandle> loginPostionHandles = new ConcurrentHashMap<>();

    public void registerProvider(AbstractLoginPositionHandle postionHandle) {
        loginPostionHandles.put(postionHandle.getType(), postionHandle);
    }

    public Map<LoginPositionType, AbstractLoginPositionHandle> getLoginPostionHandles() {
        return loginPostionHandles;
    }

    public ConcurrentHashMap<Wsession, Player> getSessionToPlayer() {
        return sessionToPlayer;
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initPlayer(Player player) {
        // nothing
    }

    @Override
    public void update(Player player) {
        player.getUpdateTaskManager().addUpdateTaskDelayMinute(CreatureUpdateType.PLAYER_ENTITY_UPDATE, new AbstractDispatcherHashCodeRunable() {
            @Override
            public String name() {
                return "updatePlayerEntity";
            }

            @Override
            public int getDispatcherHashCode() {
                return player.getDispatcherHashCode();
            }

            @Override
            public void doRun() {
                logoutWriteBack(player);
            }
        }, 1);
    }

    @Override
    public void logoutWriteBack(Player player) {
        playerCacheService.writeBack(player.getObjectId(), player.getPlayerEnt());
    }


    public Player getPlayerBySession(Wsession wsession) {
        return sessionToPlayer.get(wsession);
    }

    public Wsession getWsessionByPlayer(Player player) {
        return accountToSession.get(player.getPlayerEnt().getAccount());
    }

    public List<Player> getAllOnlinePlayer() {
        return new ArrayList<>(sessionToPlayer.values());
    }

    private Player removeOldSessionCache(String account) {
        Wsession oldSession = accountToSession.get(account);
        // ?????????????????????????????????session
        if (oldSession != null) {
            accountToSession.remove(account);
            if (sessionToPlayer.containsKey(oldSession)) {
                return sessionToPlayer.remove(oldSession);
            }
        }
        return null;
    }
}