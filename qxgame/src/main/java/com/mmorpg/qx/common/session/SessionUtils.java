package com.mmorpg.qx.common.session;

import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.account.model.KickReason;
import com.mmorpg.qx.module.account.packet.KickReasonResp;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * session超过相关工具类
 *
 * @author wang ke
 * @since v1.0 2018年2月24日
 */
public class SessionUtils {

    private static String LOGIN_AUTH = "LOGIN_AUTH";
    private static String SERVER_OPERATE_PRI = "SERVER_OP_PRI";

    /**
     * 验证该session是否通过验证
     *
     * @param session
     * @return
     */
    public static boolean isLoginAuth(Wsession session) {
        if (session.getAttributes().containsKey(LOGIN_AUTH)) {
            return true;
        }
        return false;
    }

    /**
     * 设置验证标记
     *
     * @param session
     */
    public static void setLoginAuth(Wsession session) {
        session.getAttributes().put(LOGIN_AUTH, new Object());
    }

    public static void setAccount(Wsession session, String account) {
        session.getAttributes().put("account", account);
    }

    public static String getAccount(Wsession session) {
        return (String) session.getAttributes().get("account");
    }

    public static void setServerId(Wsession session, int serverId) {
        session.getAttributes().put("serverId", serverId);
    }

    public static int getServerId(Wsession session) {
        return (int) session.getAttributes().get("serverId");
    }

    /**
     * 提玩家下线并且提供原因
     *
     * @param session
     */
    public static void kickPlayerAndClose(Wsession session, KickReason reason) {
        KickReasonResp resp = new KickReasonResp();
        resp.setReason(reason.getValue());
        ChannelFuture future = session.sendPacket(resp);
        future.addListener(ChannelFutureListener.CLOSE);
        PlayerManager.getInstance().channelCloseSetSession(session);
    }

    /**
     * 设置服务器级别的操作权限
     */
    public static final void setServerOpPrivilege(Wsession session) {
        session.getAttributes().put(SERVER_OPERATE_PRI, new Object());
    }


    /**
     * 验证服务器级别操作权限
     */
    public static final boolean hasServerOpPri(Wsession wsession) {
        if (wsession.getAttributes().get(SERVER_OPERATE_PRI) != null) {
            return true;
        }
        return false;
    }
}
