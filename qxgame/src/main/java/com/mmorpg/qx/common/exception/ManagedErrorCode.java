package com.mmorpg.qx.common.exception;

/**
 * 所有异常的错误码在这里登记
 *
 * @author wang ke
 * @since v1.0 2018年2月24日
 */
public interface ManagedErrorCode {

    /** 系统未知错误 */
    int SYS_ERROR = -1;

    /** no error */
    int OKAY = 0;

    /** 非法请求，所有不和情理的请求，都返回这个错误码 */
    int ERROR_MSG = 1;

    /** 参数非法 */
    int PARAMETER_ILLEGAL = 2;

    /** 角色不存在 */
    int PLAYER_NOT_FOUND = 3;

    /** 角色名字重复 */
    int NAME_REPEAT = 4;

    /** 等级不够 */
    int LEVEL = 5;

    /** 没有足够的提纯氪金 */
     int NOT_ENOUGH_PURE_KRYPTON = 6;

    /** 没有足够的金币 */
     int NOT_ENOUGH_GOLD = 7;

    /** 没有足够的氪金 */
     int NOT_ENOUGH_KRYPTON_GOLD = 8;

    /** 格子错误 */
     int GRID_ERROR = 9;

    /** 指定移动方向错误 */
     int DIR_ERROR = 10;

    /** 技能处于CD无法释放 */
     int SKILL_CD = 11;

    /** 玩家已经在房间 */
     int PLAYER_IN_ROOM = 12;

    /** 房间号不存在 */
     int ROOM_NOT_EXISTS = 13;

    /** 房间已经进入战斗 */
     int ROOM_IN_FIGHT = 14;

    /** 房间人数已满 */
     int ROOM_FULL = 15;

    /** 技能释放条件不足 */
     int SKILL_CONDITIONS_LIMIT = 16;

    /** 技能释放魔法不足 */
     int SKILL_MAGIC_LACK = 17;

    /** 技能目标选择错误 */
     int SKILL_TARGET_ERROR = 18;

    /** 技能释放者不能移动 */
     int SKILL_NOT_MOVE = 19;

    /** 指定物品使用次数已达上限，无法再使用 */
     int ITEM_USE_LIMIT = 20;

    /** 魔物娘不存在 */
     int MWN_NOT_EXISTS = 21;

    /** 驯养师当前回合不支持该操作 */
     int TRAINER_OP_LIMIT = 22;

    /** GM指令方法不存在 */
     int GM_COMMAND_NOT_EXISTS = 23;

    /** 施法者不具备对应技能 */
     int SKILL_NOT_OWE = 24;

    /** 技能释放金币不足 */
     int SKILL_GOLD_LACK = 25;

    /** 魔法不足 */
     int SKILL_LACK_MAGIC = 26;

    /** 玩家无法操作，处于禁止状态 */
     int LIMIT_OP = 27;

    /** 血量不足 */
     int NOT_ENOUGH_HP = 28;

    /** 已经死亡 */
     int DEAD_ERROR = 29;

    /** 房间结束 */
     int ROOM_STATE_END = 30;

    /** 该任务不能接取 */
     int QUEST_CANNOT_ACCEPT = 31;

    /** 该任务不能客户端接取 */
     int QUEST_NOT_CLIENT_COMPLETE = 32;

    /** 该任务不能完成 */
     int QUEST_NOT_ACCEPTED_OR_COMPLETE = 33;

    /** 任务没有找到 */
     int QUEST_NOT_FOUND = 34;

    /** 任务没有完成 */
     int QUEST_INCOMPLETE = 35;

    /** 玩家处于定身状态 */
     int PLAYER_STUN = 36;

    /** 房间状态错误 */
     int ROOM_STATE_ERROR = 37;

    /** 中了敌人的魔法，这回合召唤不了魔物娘 */
     int CALL_MWN_LIMIT_EFFECT = 38;

    /** 所在位置有建筑，召唤不了魔物娘 */
     int CALL_MWN_LIMIT_BUILD = 39;

    /** 魔法值不足，召唤不了 */
     int CALL_MWN_LIMIT_MP = 40;

    /** 没有可穿戴装备 */
     int MO_EQUIPS = 41;

    /** 穿戴错误，进攻方穿进攻装备，防守方穿防守装备 */
     int EQUIP_WEAR_ERROR = 42;

    /** 手牌已满,获得魔物娘无法进入手牌 */
     int USE_STORAGE_FULL = 43;

    /** 敌方回合,无法进行操作 */
     int NOT_TURN_OPERATOR = 44;

    /** 道具不存在 */
    int ITEM_NOT_EXIST = 45;

    /** 道具过期 */
    int ITEM_OVERDUE = 46;

    /** 道具无法出售 */
    int ITEM_CAN_NOT_SALE = 47;

    /** 没有足够的道具 */
    int NOT_ENOUGH_ITEM = 48;

    /** 消耗道具数量错误 */
    int USE_ITEM_NUM_ERROR = 49;

    /** 魔物娘升级阶数不足 */
    int MWN_UPLEVEL_CASTE_LIMIT = 50;

    /** 魔物娘已达到最大等级 */
    int MWN_REACH_MAX_LEVEL = 51;

    /** 魔物娘已达到最大阶数 */
    int MWN_REACH_MAX_CASTE = 52;

    /** 魔物娘升阶等级不足 */
    int MWN_UPCASTE_LEVEL_LIMIT = 53;

    /**
     * 魔物娘已经投骰子，不能再次投骰子
     */
    int MWN_HAS_THROW_DICE = 54;

    /**
     * 技能不在施法范围
     */
    int SKILL_NOT_IN_RANGE = 55;

    /**
     * 投骰子前不能再召唤魔物娘
     */
    int HAS_ENEMY_MWN = 56;

    /** 魔物娘已达到最大星数 */
    int MWN_REACH_MAX_STAR = 57;

    /** 驯养师解锁碎片错误 */
    int TRAINER_UNLOCK_CHIP_ERROR = 58;

    /** 驯养师已解锁 */
    int TRAINER_HAS_UNLOCK = 59;

    /** 驯养师不存在 */
    int TRAINER_NOT_EXIST = 60;

    /** 驯养师已穿戴该皮肤 */
    int TRAINER_HAS_WEAR_SKIN = 61;

    /** 驯养师没有该皮肤 */
    int TRAINER_NOT_HAVE_SKIN = 62;

    /** 驯养师已出战 */
    int TRAINER_IS_IN_FIGHT = 63;

    /** 编队数量限制 */
    int TROOP_NUM_LIMIT = 64;

    /** 编队卡牌数量限制 */
    int TROOP_CARD_NUM_LIMIT = 65;

    /** 编队名称长度限制 */
    int TROOP_NAME_LENGTH_LIMIT = 66;

    /** 编队卡牌不存在 */
    int TROOP_CARD_NOT_EXIST = 67;

    /** 编队卡牌重复 */
    int TROOP_CARD_REPEATED = 68;

    /** 编队名字未修改 */
    int TROOP_NAME_NOT_ALTER = 69;

    /** 编队不存在 */
    int TROOP_NOT_EXIST = 70;

    /** 魔物娘羁绊等级不足 */
    int MWN_FETTER_LV_LIMIT = 71;

    /** 魔物娘已穿戴该皮肤 */
    int MWN_HAS_WEAR_SKIN = 72;

    /** 魔物娘没有该皮肤 */
    int MWN_NOT_HAVE_SKIN = 73;

    /** 同一个回合一个地格只能进行一次魔物娘战斗 */
    int SAME_GRID_ONCE_MWN_FIGHT = 74;

    /** 编队已出战 */
    int TROOP_IS_IN_FIGHT = 75;

    /** 编队已解锁 */
    int TROOP_IS_UNLOCK = 76;

    /** 编队已选中该技能 */
    int TROOP_HAS_CHOOSE_SKILL = 77;

    /** 没有选择出战编队 */
    int NO_TROOP_IN_FIGHT = 78;

    /** 出战编队卡牌数量错误 */
    int TROOP_IN_FIGHT_NUM_ERROR = 79;

    /** 没有指定的卡牌 */
    int NO_FIXED_CARD = 80;

    /** 卡组已空 */
    int SOURCE_CARD_IS_EMPTY = 81;

}