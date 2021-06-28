package com.mmorpg.qx.common;

/**
 * 所有的通信协议
 *
 * @author wang ke
 * @since v1.0 2018年2月18日
 */
public interface PacketId {

    /**
     * 错误码同意推送
     */
    short ERROR_PACKET = Short.MAX_VALUE;

    /**
     * 请求登陆验证
     */
    short LOGIN_AUTH_REQ = 1;
    /**
     * 登陆验证结果
     */
    short LOGIN_AUTH_RESP = 2;
    /**
     * 请求角色列表信息
     */
    short GET_PLAYER_LIST_REQ = 3;
    /**
     * 返回角色列表信息
     */
    short GET_PLAYER_LIST_RESP = 4;
    /**
     * 创建角色
     */
    short CREATE_PLAYER_REQ = 5;
    /**
     * 登陆角色
     */
    short LOGIN_PLAYER_REQ = 6;
    /**
     * 登陆角色返回
     */
    short LOGIN_PLAYER_RESP = 7;
    /**
     * 登陆完成以后进入世界
     */
    short PLAYER_QUIT_REQ = 8;
    /**
     * 移动请求
     */
    short TRAINER_MOVE_REQ = 9;
    /**
     * 修正客户端当前位置
     */
    short UPDATE_POSITION = 10;
    /**
     * 广播玩家的移动路径
     */
    short MOVE_RESP = 11;
    /**
     * 使用技能请求
     */
    short USE_SKILL_REQ = 12;
    /**
     * 使用技能广播
     */
    short USE_SKILL_RESP = 13;
    /**
     * 效果添加
     */
    short EFFECT_ADD_RESP = 14;
    /**
     * 效果移除
     */
    short EFFECT_REMOVE_RESP = 15;
    /**
     * 效果生效
     */
    short EFFECT_APPLY_RESP = 16;
    /**
     * 生物属性变化
     */
    short CREATURE_ATTR_CHANGE_RESP = 17;
    /**
     * 生物状态属性变化
     */
    short ABNORMAL_EFFECT_RESP = 18;
    /**
     * 背包更新
     */
    short PACK_UPDATE_RESP = 19;
    /**
     * 商场购买请求
     */
    short SHOP_BUY_REQ = 20;
    /**
     * 钱包更新
     */
    short PURSE_UPDATE_RESP = 21;
    /**
     * 看见玩家驯养师消息
     */
    short PLAYER_TRAINER_INFO_RESP = 22;
    /**
     * 看见怪物
     */
    short MWN_INFO_RESP = 23;
    /**
     * 接取任务
     */
    short ACCEPTQUEST_REQ = 24;
    /**
     * 完成任务
     */
    short COMPLETEQUEST_REQ = 25;
    /**
     * 领取任务奖励
     */
    short REWARDQUEST_REQ = 26;
    /**
     * 领取任务奖励返回
     */
    short REWARDQUEST_RESP = 27;
    /**
     * 可接取的任务添加
     */
    short ACCEPTABLEQUEST_ADD_RESP = 28;
    /**
     * 可接取的任务删除
     */
    short ACCEPTABLEQUEST_REMOVE_RESP = 29;
    /**
     * 任务状态更新
     */
    short QUEST_UPDATE_RESP = 30;
    /**
     * 模块开启
     */
    short MODULE_OPEN_RESP = 31;
    /**
     * 经验增加
     */
    short PLAYER_EXPADD_RESP = 32;
    /**
     * 创建地图
     */
    short CREATE_MAP_REQ = 33;
    /**
     * 效果触发
     */
    short EFFECT_TRIGGER_RESP = 34;
    /**
     * 装备出现
     */
    short EQUIP_INFO_RESP = 35;
    /**
     * 单位出视野
     */
    short NOT_SEE_RESP = 40;
    /**
     * 单位死亡
     */
    short CREATURE_DIE = 41;
    /**
     * 生物移除出场景
     */
    short REMOVE_CREATURE_FROM_MAP_RESP = 42;
    /**
     * 卡包更新
     */
    short CARD_BAGS_RESP = 43;
    /**
     * 请求使用背包物品
     */
    short BACKPACK_ITEM_USE = 46;
    /**
     * 踢人下线的原因
     */
    short KICK_REASON_RESP = 48;
    /**
     * 客户端请求心跳
     */
    short PLAYER_HEARTBEAT_REQ = 49;
    /**
     * 服务端返回心跳
     */
    short PLAYER_HEARTBEAT_RESP = 50;
    /**
     * 释放魔法
     */
    short PLAYER_USE_MAGIC_REQ = 51;
    /**
     * 请求抽卡
     */
    short PLAYER_EXTRACT_CARD_REQ = 52;
    /**
     * 战场中玩家卡牌变化
     */
    short PLAYER_CARD_UPDATE_RESP = 53;
    /**
     * 广播当前轮次信息
     */
    short BROADCAST_ROUND_INFO_RESP = 54;

    /**
     * 召唤魔物娘
     */
    short PLAYER_CALL_SUMMON_REQ = 55;

    /**
     * 请求投骰子
     */
    short PLAYER_THROW_DICE_REQ = 56;
    /**
     * 返回投骰子信息
     */
    short THROW_DICE_RESP = 57;
    /**
     * 请求魔物娘战斗
     */
    short MWN_FIGHT_REQ = 58;
    /**
     * 驯养师场景信息
     */
    short ROBOT_TRAINER_INFO = 59;
    /**
     * 玩家请求创建房间
     */
    short PLAYER_ROOM_CREATE_REQ = 60;
    /**
     * 玩家请求开启房间战斗
     */
    short PLAYER_ROUND_FIGHT_START_REQ = 61;

    /**
     * 请求进入房间
     */
    short PLAYER_JOIN_ROOM_REQ = 62;

    /**
     * 请求系统房间信息
     */
    short ROOM_INFO_REQ = 63;

    /**
     * 返回系统房间信息
     */
    short ROOM_INFO_RESP = 64;

    /**
     * 更新房间状态
     */
    short ROOM_UPDATE_RESP = 65;
    /**
     * 玩家请求查询指定房间
     */
    short PLAYER_SEARCH_ROOM_REQ = 66;
    /**
     * 创建魔物娘
     */
    short CREATE_MWN_REQ = 67;
    /**
     * 创建驯养师
     */
    short CREATE_TRAINER_REQ = 68;
    /**
     * 魔物娘回合战斗战报
     */
    short MWN_ROUND_FIGHT_DAMAGE_RESP = 69;
    /**
     * 魔物娘穿戴装备
     */
    short MWN_WEAR_EQUIP_REQ = 70;
    /**
     * 战斗中使用装备更新
     */
    short EQUIPMENT_UPDATE_RESP = 71;
    /**
     * 通知前端准备魔物娘战斗
     */
    short NOTIFY_MWN_FIGHT_READY_RESP = 72;
    /**
     * 魔物娘已经准备好战斗
     */
    short MWN_FIGHT_READY_REQ = 73;
    /**
     * 建筑信息
     */
    short BUILDING_INFO_RESP = 74;
    /**
     * 操作建筑技能
     */
    short USE_BUILDING_SKILL_REQ = 75;
    /**
     * 房间结果广播
     */
    short ROOM_FIGHT_RESULT_RESP = 76;
    /**
     * 战场生命值变化
     */
    short LIFE_CHANGE_RESP = 77;
    /**
     * buff效果信息
     */
    short EFFECT_INFO_RESP = 78;
    /**
     * 选择消耗魔物娘卡牌投骰子
     */
    short COST_CARD_THROW_DICE = 79;
    /**
     * 中途退出房间
     */
    short PLAYER_QUIT_ROOM_REQ = 80;

    /**
     * 通知结束当前回合
     */
    short NOTIFY_ROUND_END_REQ = 81;

    /**
     * 请求查看装备栏信息
     */
    short EQUIPMENT_INFO_REQ = 82;

    /**
     * 返回装备栏信息
     */
    short EQUIPMENT_INFO_RESP = 83;

    /**
     * 请求设置简易战斗模式
     */
    short MWN_SIMPLE_FIGHT_REQ = 84;

    /**
     * 广播魔物娘简易战斗模式
     */
    short MWN_SIMPLE_FIGHT_RESP = 85;

    /**
     * 同步玩家通用配置数据
     */
    short SYNC_COMMON_DATA_RESP = 86;

    /**
     * 查看自身对象或己方魔物娘身上buff
     */
    short CREATURE_EFFECT_INFO_REQ = 87;

    /**
     * 驯养师进入场景后加载完毕准备开启战斗
     */
    short TRAINER_FIGHT_READY_REQ = 88;

    /**
     * 同步正在使用卡包卡数量
     */
    short TRAINER_USECARD_NNUM_RESP = 89;

    /**
     * 请求死亡魔物娘
     */
    short DIED_MWN_REQ = 90;

    /**
     * 返回死亡魔物娘
     */
    short DIED_MWN_RESP = 91;

    /**
     * 单个技能cd
     */
    short SINGLE_SKILL_CD_RESP = 92;

    /**
     * 驯养师技能CD，建筑CD
     */
    short ALL_SKILL_BUILD_CD_RESP = 93;
    /**
     * 驯养师卡组和卡包信息
     */
    short TRAINER_ALL_CARDS_RESP = 94;

    /**
     * 驯养师所有卡牌信息
     */
    short TRAINER_ALL_CARDS_REQ = 95;

    /**
     * 玩家离线前后房间战斗记录
     */
    short PLAYER_OFF_LINE_ROUND_LOG_RESP = 96;

    /**
     * 玩家请求查看离线后战斗日志
     */
    short PLAYER_OFF_LINE_ROUND_LOG_REQ = 97;

    /**
     * 服务端像客户端查询离线后战斗日志
     */
    short SERVER_ASK_CLI_ROUND_LOG_RESP = 98;

    /**
     * 客户端返回收到所有回合战斗数据
     */
    short SERVER_ASK_CLI_ROUND_LOG_REQ = 99;

    /**
     * 魔物娘跳过战斗
     */
    short ESCAPCE_MWN_FIGHT = 100;

    /**
     * 返回积分更新数据
     */
    short INTEGRAL_UPDATE_RESP = 101;

    /**************** 魔物娘养成模块102-110 ****************/
    /**
     * 魔物娘列表请求
     */
    short MWN_LIST_REQ = 102;

    /**
     * 魔物娘列表返回
     */
    short MWN_LIST_RESP = 103;

    /**
     * 培养魔物娘请求
     */
    short DEVELOP_MWN_REQ = 104;

    /**
     * 培养魔物娘返回
     */
    short DEVELOP_MWN_RESP = 105;

    /**
     * 魔物娘更换皮肤请求
     */
    short MWN_CHANGE_SKIN_REQ = 106;

    /**
     * 魔物娘更换皮肤返回
     */
    short MWN_CHANGE_SKIN_RESP = 107;

    /**
     * 魔物娘技能buff效果表现完成
     */
    short MWN_SKILL_EFFECT_ACT_OVER_REQ = 111;

    /**
     * 驯养师操作顺序信息
     */
    short TRAINER_ORDER_INFO_RESP = 112;

    /**
     * 魔物娘技能进退化结果
     */
    short MWN_SKILL_EVO_RESP = 113;

    /**
     * 魔物娘援助
     */
    short MWN_SUPPORT_REQ = 114;

    /**
     * 驯养师列表请求
     */
    short TRAINER_LIST_REQ = 115;

    /**
     * 驯养师列表回复
     */
    short TRAINER_LIST_RESP = 116;

    /**
     * 驯养师解锁请求
     */
    short TRAINER_UNLOCK_REQ = 117;

    /**
     * 驯养师解锁回复
     */
    short TRAINER_UNLOCK_RESP = 118;

    /**
     * 驯养师更换皮肤请求
     */
    short TRAINER_CHANGE_SKIN_REQ = 119;

    /**
     * 驯养师更换皮肤回复
     */
    short TRAINER_CHANGE_SKIN_RESP = 120;

    /**
     * 驯养师出战请求
     */
    short TRAINER_GO_FIGHT_REQ = 121;

    /**
     * 驯养师出战回复
     */
    short TRAINER_GO_FIGHT_RESP = 122;

    /**
     * 解锁编队请求
     */
    short UNLOCK_TROOP_REQ = 123;

    /**
     * 修改编队请求
     */
    short ALTER_TROOP_REQ = 124;

    /**
     * 更新编队回复
     */
    short UPDATE_TROOP_RESP = 125;

    /**
     * 编辑编队名字请求
     */
    short EDIT_TROOP_NAME_REQ = 126;

    /**
     * 编辑编队名字回复
     */
    short EDIT_TROOP_NAME_RESP = 127;

    /******排行榜 130-139********/
    short RANKING_LIST_REQ = 130;
    short RANKING_LIST_RESP = 131;
    short RANKING_SELF_REQ = 132;
    short RANKING_SELF_RESP = 133;
    /******排行榜********/

    /**
     * 编队出战请求
     */
    short TROOP_GO_FIGHT_REQ = 134;

    /**
     * 编队出战回复
     */
    short TROOP_GO_FIGHT_RESP = 135;

    /**
     * 编队切换技能请求
     */
    short TROOP_CHANGE_SKILL_REQ = 136;

    /**
     * 编队切换技能回复
     */
    short TROOP_CHANGE_SKILL_RESP = 137;
    short RECONNECT_REQ = 138;
    /******GM******/
    /**
     * 踢人
     */
    short SERVER_KICK_OFF = 6000;

    /**
     * 后续所有GM相关命令供用这条协议
     */
    short GM_COMMAND_REQ = 6001;

    /**
     * GM指令错误，返回正确格式
     */
    short GM_COMMAND_ERROR_RESP = 6002;
}
