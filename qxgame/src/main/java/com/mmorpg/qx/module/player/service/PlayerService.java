package com.mmorpg.qx.module.player.service;

import com.haipaite.common.event.core.EventBusManager;
import com.haipaite.common.utility.StringUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.common.identify.manager.IdentifyManager.IdentifyType;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.SessionUtils;
import com.mmorpg.qx.common.socket.config.ServerConfigConstant;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.account.model.KickReason;
import com.mmorpg.qx.module.integral.manager.IntegralManager;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.moduleopen.manager.ModuleOpenManager;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.player.entity.PlayerEnt;
import com.mmorpg.qx.module.player.event.LoginEvent;
import com.mmorpg.qx.module.player.event.ReconnetEvent;
import com.mmorpg.qx.module.player.manager.PlayerCommonManager;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import com.mmorpg.qx.module.player.model.LoginPosition;
import com.mmorpg.qx.module.player.model.LoginPositionType;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.player.packet.*;
import com.mmorpg.qx.module.player.packet.vo.PlayerShortInfo;
import com.mmorpg.qx.module.purse.manager.PurseManager;
import com.mmorpg.qx.module.quest.service.QuestManager;
import com.mmorpg.qx.module.trainer.manager.TrainerManager;
import com.mmorpg.qx.module.troop.manager.TroopManager;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 角色模块业务处理,Service层是不允许其他业务模块调用的
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
@Component
public class PlayerService {

    private Logger logger = SysLoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private PlayerManager playerManager;
    @Autowired
    private ItemManager itemManager;
    @Autowired
    private PurseManager purseManager;
    @Autowired
    private QuestManager questManager;
    @Autowired
    private ModuleOpenManager moduleOpenManager;
    @Autowired
    private IntegralManager integralManager;
    @Autowired
    private TroopManager troopManager;

    public GetPlayerListResp getPlayerList(Wsession session, GetPlayerListReq req) {
        // 取session验证时的account和serverId防止玩家登陆其它人的账号
        String account = SessionUtils.getAccount(session);
        int serverId = SessionUtils.getServerId(session);
        GetPlayerListResp resp = new GetPlayerListResp();
        ConcurrentHashMap<Integer, List<PlayerShortInfo>> serverToPlayer = playerManager.getAccountToPlayer().get(account.trim());
        if (serverToPlayer == null) {
            return resp;
        }
        List<PlayerShortInfo> playerShortInfos = serverToPlayer.get(serverId);
        if (playerShortInfos == null) {
            return resp;
        }
        for (PlayerShortInfo p : playerShortInfos) {
            resp.getPlayerItems().add(p);
        }
        return resp;
    }

    /**
     * 创建角色
     *
     * @param session
     * @param req
     */
    public void createPlayer(Wsession session, CreatePlayerReq req) {
        // TODO 参数合法性验证
        // TODO 角色数量是否大于3
        // TODO 姓名合法性验证
        if (StringUtils.isBlank(req.getName())) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        boolean success = playerManager.nameRegister(req.getName());
        if (!success) {
            throw new ManagedException(ManagedErrorCode.NAME_REPEAT);
        }

        // ID生成器生成玩家全服唯一id
        long playerId = IdentifyManager.getInstance().getNextIdentify(IdentifyType.PLAYER);
        PlayerEnt playerEnt = playerManager.createPlayerEnt(playerId, SessionUtils.getAccount(session), req.getName(),
                req.getRole(), SessionUtils.getServerId(session), req.getSex());
        Player player = initPlayer(playerEnt);
        player.setDispatchId(session.selectDispatcherHashCode());
        // 登陆
        doLogin(session, player);
    }

    /**
     * 重连
     *
     * @param session
     * @param req
     */
    public void reconnect(Wsession session, ReconnectReq req) {
        long playerId = req.getPlayerId();
        PlayerEnt ent = playerManager.loadPlayerEnt(playerId);
        if (null == ent || ent.getPlayer() == null) {
            SessionUtils.kickPlayerAndClose(session, KickReason.QUIT_GAME);
            return;
        }
        Player player = ent.getPlayer();
        doReconnect(session, player);

    }

    /**
     * 初始化玩家
     *
     * @param playerEnt
     * @return
     */
    private Player initPlayer(PlayerEnt playerEnt) {
        Player player = new Player();
        player.setPlayerEnt(playerEnt);
        playerEnt.setPlayer(player);
        if (playerEnt.getLoginPositionType() != 0) {
            LoginPosition loginPosition = new LoginPosition(LoginPositionType.valueOf(playerEnt.getLoginPositionType()));
            loginPosition.setMapId(playerEnt.getMapId());
            loginPosition.setInstanceId(playerEnt.getInstanceId());
            loginPosition.setGridId(playerEnt.getGridId());
            player.setLoginPosition(loginPosition);
        }
        //后续模块设置都在这里,由于模块之间的初始化可能涉及时序问题.所以这里不能采用监听器或者事件模式解耦.必须显示的表达初始化顺序
        itemManager.initPlayer(player);
        purseManager.initPlayer(player);
        questManager.initPlayer(player);
        moduleOpenManager.initPlayer(player);
        integralManager.initPlayer(player);

        /** 驯养师模块初始化*/
        TrainerManager.getInstance().initPlayer(player);
        /** 魔物娘模块初始化*/
        MWNManager.getInstance().initPlayer(player);
        /** 玩家通用数据*/
        PlayerCommonManager.getInstance().initPlayer(player);
        troopManager.initPlayer(player);
        //playerManager.addPlayerLevelAttr(player);

        // 设置完属性以后.设置100%血量与蓝量
        //player.getLifeStats().setCurrentHpPercent(100);
        //player.getLifeStats().setCurrentMpPercent(100);
        return player;
    }

    public void login(Wsession session, long playerId) {

        String account = SessionUtils.getAccount(session);
        int serverId = SessionUtils.getServerId(session);

        PlayerShortInfo playerShortInfo = playerManager.getPlayerShortInfo(account, serverId, playerId);
        if (playerShortInfo == null) {
            throw new ManagedException(ManagedErrorCode.PLAYER_NOT_FOUND);
        }
        PlayerEnt playerEnt = playerManager.loadPlayerEnt(playerId);
        Player player = playerEnt.getPlayer();
        if (player == null) {
            // 登陆初始化player
            player = initPlayer(playerEnt);
            player.setDispatchId(session.selectDispatcherHashCode());
        }
        doLogin(session, player);
    }

    /**
     * 登陆
     *
     * @param player
     */
    private void doLogin(Wsession session, Player player) {
        if (player.getLoginPosition() == null) {
            // 这个为空,设计默认的出生点.
            LoginPosition defaultLoginPosition = new LoginPosition(LoginPositionType.DEFAULT);
            player.setLoginPosition(defaultLoginPosition);
        }

        // 设置session关联
        playerManager.addSession(session, player);
        //登录后，重置心跳时间
        player.updateHeartbeat(System.currentTimeMillis());
        // 登陆坐标控制,这里面的模块必须去设置玩家身上的worldPosition TODO 卡招游戏不需要设置玩家出生点和坐标，只有进入棋盘才有场景坐标概念
        //playerManager.getLoginPostionHandles().get(player.getLoginPosition().getType()).loginPosition(player);

        // TODO 推送玩家消息
        LoginPlayerResp resp = LoginPlayerResp.valueOf(player);
        //TODO 装备数据 移除
        //resp.setEquipmentStorage(BackPackUpdateResp.valueOf(player.getEquipmentStorage().getEquipments(), PackType.EQUIPMENTSTORAGE.getType()));
        //resp.setMapId(player.getMapId());
        //resp.setGridId(player.getGridId());
        //resp.setInstanceId(player.getInstanceId());
        // 登陆码
        // 进入世界sign.
        // 1.客户端登陆.服务端完成登陆生成sign返回客户端.
        // 2.客户端完成初始化以后,通过该sign确定自己要进入世界.
        // 3.服务端对比该sign如果正确那么允许客户端登陆
        //int enterSign = RandomUtils.betweenInt(1, Integer.MAX_VALUE - 1, false);
        //player.getEnterWorldSign().set(enterSign);
        //resp.setEnterSign(enterSign);
        session.sendPacket(resp);
        // 登陆事件通知,这里是异步的
        EventBusManager.getInstance().submit(LoginEvent.valueOf(player));
        logger.info("玩家【{}】登录成功, 更新心跳时间【{}】", player.getName(), player.getLastHeartbeat());
    }

    /**
     * 重连
     *
     * @param session
     * @param player
     */
    private void doReconnect(Wsession session, Player player) {
        Wsession old = playerManager.getWsessionByPlayer(player);
        if (null == old) {
            SessionUtils.kickPlayerAndClose(session, KickReason.QUIT_GAME);
            return;
        }
        session.copy(old);
        playerManager.addSession(session, player);
        player.updateHeartbeat(System.currentTimeMillis());
        LoginPlayerResp resp = LoginPlayerResp.valueOf(player);
        session.sendPacket(resp);
        // 重连
        EventBusManager.getInstance().submit(ReconnetEvent.valueOf(player));
    }

    public void enterWorld(Player player, EnterWorldReq req) {
        if (player.getEnterWorldSign().compareAndSet(req.getSign(), 0)) {
            //World.getInstance().spawn(player);
        } else {
            logger.warn(String.format("player [%s] enterSgin error. expect[%s] req[%s]", player.getName(),
                    player.getEnterWorldSign().get(), req.getSign()));
        }
    }

    /***
     * 心跳处理，上次心跳时间与当前时间有两次心跳间隔或以上，认为客户端已经断开
     * @param player
     * @param sysHeartbeat
     */
    public void heartbeatEventHandle(Player player, long sysHeartbeat) {
        long latestHeartbeat = sysHeartbeat - ServerConfigConstant.PLAYER_HEAR_BEAT_PERIOD * 2;
        //两次心跳以内正常或客户端还未开启心跳
        if (player.getLastHeartbeat() == 0 || player.getLastHeartbeat() > latestHeartbeat) {
            return;
        }

        //断开连接,业务退出，这里没有线程安全，session可能已经没了
        Wsession wsbyPlayer = playerManager.getWsessionByPlayer(player);
        //由于GM踢人和顶号是先关闭链接再异步移除，可能session已经关了，player对象还未从内存移除
        //这里直接返回就行了，player和session会异步移除
        if (wsbyPlayer == null) {
            return;
        }
        SessionUtils.kickPlayerAndClose(wsbyPlayer, KickReason.HEARTBEAT_DELAY);
        System.err.println("用户 " + player.getName() + " 检查出心跳延迟，关闭连接");
        logger.info("用户 【{}】 检查出心跳延迟 上次心跳 【{}】，当前系统系统 【{}】，相差【{}】秒，关闭连接", player.getName(), player.getLastHeartbeat(), sysHeartbeat, (sysHeartbeat - player.getLastHeartbeat()) / 1000);
    }

    /***
     * 更新玩家心跳时间戳
     * @param player
     */
    public void updatePlayerHeartBeat(Player player) {
        //直接更新心跳
        if (Objects.nonNull(player)) {
            player.updateHeartbeat(System.currentTimeMillis());
        }
    }

    public void robotLogin(Wsession session, long playerId) {
        PlayerEnt playerEnt = playerManager.loadPlayerEnt(playerId);
        Player player = playerEnt.getPlayer();
        if (player == null) {
            // 登陆初始化player
            player = initPlayer(playerEnt);
            player.setDispatchId(session.selectDispatcherHashCode());
        }
        doLogin(session, player);
    }

    /**
     * 客户端请求退出游戏
     *
     * @param player
     */
    public void quitGame(Player player) {
        Wsession session = playerManager.getWsessionByPlayer(player);
        //这里直接返回就行了，player和session会异步移除
        if (session == null) {
            return;
        }
        SessionUtils.kickPlayerAndClose(session, KickReason.QUIT_GAME);
    }

}
