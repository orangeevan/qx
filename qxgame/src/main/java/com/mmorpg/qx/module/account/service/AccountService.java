package com.mmorpg.qx.module.account.service;

import com.haipaite.common.utility.CryptUtils;
import com.mmorpg.qx.common.identify.manager.ServerConfigValue;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.common.session.SessionUtils;
import com.mmorpg.qx.common.socket.core.Wsession;
import com.mmorpg.qx.module.account.packet.LoginAuthReq;
import com.mmorpg.qx.module.account.packet.LoginAuthResp;
import com.mmorpg.qx.module.player.manager.PlayerManager;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账号
 *
 * @author wang ke
 * @since v1.0 2018年3月8日
 */
@Component
public class AccountService {

    private Logger logger = SysLoggerFactory.getLogger(AccountService.class);

    @Autowired
    private PlayerManager playerManager;

    @Autowired
    ServerConfigValue serverConfigValue;

    public LoginAuthResp loginAuth(Wsession session, LoginAuthReq req) throws Exception {
        LoginAuthResp resp = new LoginAuthResp();
        String token = serverConfigValue.getServerMsgToken();
        StringBuilder str = new StringBuilder();
        str.append(req.getAccount());
        str.append(req.getServerId() + "");
        str.append(token);
        String expectMD5 = CryptUtils.md5(str.toString());
        //String expectMD5 = Base64.getEncoder().encodeToString(str.toString().getBytes());
        //TODO 暂不检查验证码
        if (!expectMD5.equals(req.getMd5check())) {
            //throw new ManagedException(ManagedErrorCode.MD5_TOKEN_ERROR);
        }
        resp.setResult(0);
        // 设置验证通过标记
        SessionUtils.setLoginAuth(session);
        SessionUtils.setAccount(session, req.getAccount());
        SessionUtils.setServerId(session, req.getServerId());
        logger.info("账号: [{}] 登录，时间[{}]", req.getAccount(), System.currentTimeMillis());
        return resp;
    }

}
