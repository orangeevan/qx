package com.mmorpg.qx.module.purse.model;

import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.module.player.entity.PlayerEnt;

public enum PurseType {

    /**
     * 提纯氪金 (1)
     */
    PURE_KRYPTON_GOLD(1, ManagedErrorCode.NOT_ENOUGH_PURE_KRYPTON) {
        @Override
        void setValue(PlayerEnt playerEnt, long value) {
            playerEnt.setPureKryptonGold(value);
        }

        @Override
        long getValue(PlayerEnt playerEnt) {
            return playerEnt.getPureKryptonGold();
        }
    },

    /**
     * 氪金 (2)
     */
    KRYPTON_GOLD(2, ManagedErrorCode.NOT_ENOUGH_KRYPTON_GOLD) {
        @Override
        void setValue(PlayerEnt playerEnt, long value) {
            playerEnt.setKryptonGold(value);
        }

        @Override
        long getValue(PlayerEnt playerEnt) {
            return playerEnt.getKryptonGold();
        }
    },

    /**
     * 金币 (3)
     */
    GOLD(3, ManagedErrorCode.NOT_ENOUGH_GOLD) {
        @Override
        void setValue(PlayerEnt playerEnt, long value) {
            playerEnt.setGold(value);
        }

        @Override
        long getValue(PlayerEnt playerEnt) {
            return playerEnt.getGold();
        }
    },
    /**
     * 代金币，后台发放（4）
     */
    INSTEAD_GOLD(4, ManagedErrorCode.NOT_ENOUGH_GOLD) {
        @Override
        void setValue(PlayerEnt playerEnt, long value) {
            playerEnt.setIntsteadGold(value);
        }

        @Override
        long getValue(PlayerEnt playerEnt) {
            return playerEnt.getIntsteadGold();
        }
    },
    ;

    private final int code;

    private final int errorCode;

    PurseType(int code, int errorCode) {
        this.code = code;
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public int getCode() {
        return code;
    }

    abstract void setValue(PlayerEnt playerEnt, long value);

    abstract long getValue(PlayerEnt playerEnt);

    public static PurseType typeOf(int code) {
        for (PurseType type : PurseType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    public static PurseType typeOf(String code) {
        return typeOf(Integer.parseInt(code));
    }

}
