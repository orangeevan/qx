package com.mmorpg.qx.module.purse.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.player.entity.PlayerEnt;
import com.mmorpg.qx.module.purse.packet.PurseUpdateResp;
import com.mmorpg.qx.module.purse.packet.vo.PurseUpdateItem;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Purse {

    private Logger logger = SysLoggerFactory.getLogger(Purse.class);

    /**
     * 为了方便查询,货币直接放在玩家身上
     */
    private PlayerEnt playerEnt;

    @JSONField(serialize = false)
    private BitSet marks = new BitSet(PurseType.values().length);

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static Purse valueOf(PlayerEnt playerEnt) {
        Purse purse = new Purse();
        purse.playerEnt = playerEnt;
        return purse;
    }

    @JSONField(serialize = false)
    public void cost(PurseType type, long value) {
        cost(type, value, true);
    }

    @JSONField(serialize = false)
    public void cost(PurseType type, long value, boolean useInsteadGold) {
        if (value < 0) {
            String msg = String.format("type[%s] value[%s]", type.name(), value);
            throw new RuntimeException(msg);
        }
        try {
            readWriteLock.writeLock().lock();
            long beforeCost = type.getValue(playerEnt);
            long beforeInstead = PurseType.INSTEAD_GOLD.getValue(playerEnt);
            if (type == PurseType.GOLD && useInsteadGold) {
                //这里要考虑代币
                long remain = 0;
                if (value > PurseType.INSTEAD_GOLD.getValue(playerEnt)) {
                    // 需要扣除的金额超过代币
                    remain = (value - PurseType.INSTEAD_GOLD.getValue(playerEnt));
                    if (remain > PurseType.GOLD.getValue(playerEnt)) {
                        // 元宝也不够,不应该到这里.说明业务没有正确的判断
                        String msg = String.format("玩家[%s] 消费货币异常 type[%s] value[%s]", type.name(), value);
                        throw new RuntimeException(msg);
                    }
                    PurseType.INSTEAD_GOLD.setValue(playerEnt, 0);
                    PurseType.GOLD.setValue(playerEnt, PurseType.GOLD.getValue(playerEnt) - remain);
                } else {
                    PurseType.INSTEAD_GOLD.setValue(playerEnt, PurseType.INSTEAD_GOLD.getValue(playerEnt) - remain);
                }

            } else {
                if (type.getValue(playerEnt) < value) {
                    String msg = String.format("玩家[%s] 消费货币 type[%s] 当前值value[%s] 需要扣除的值[%s]", playerEnt.getPlayerId() + "|" + playerEnt.getAccount(), type.name(), type.getValue(playerEnt), value);
                    throw new RuntimeException(msg);
                }
                type.setValue(playerEnt, type.getValue(playerEnt) - value);
            }
            logger.info("玩家{} 扣除货币, 类型 {}, 扣除 {}, 操作前货币 {}, 操作前代币 {}, 操作后货币 {}, 操作后代币 {}",
                    playerEnt.getPlayerId() + "|" + playerEnt.getAccount(), type, value, beforeCost, beforeInstead, type.getValue(playerEnt), PurseType.INSTEAD_GOLD.getValue(playerEnt));
        } catch (Exception e) {
            throw e;
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    @JSONField(serialize = false)
    public void add(PurseType type, long value) {
        if (value < 0) {
            String msg = String.format("玩家[%s] 添加货币异常 type[%s] value[%s]", playerEnt.getPlayerId() + "|" + playerEnt.getAccount(), type.name(), value);
            throw new RuntimeException(msg);
        }
        try {
            long before = type.getValue(playerEnt);
            long beforeInstead = PurseType.INSTEAD_GOLD.getValue(playerEnt);
            readWriteLock.writeLock().lock();
            type.setValue(playerEnt, type.getValue(playerEnt) + value);
            marks.set(type.getCode());
            logger.info("玩家{} 添加货币, 类型 {}, 添加 {}, 操作前货币 {}, 操作前代币 {}, 操作后货币 {}, 操作后代币 {}",
                    playerEnt.getPlayerId() + "|" + playerEnt.getAccount(), type, value, before, beforeInstead, type.getValue(playerEnt), PurseType.INSTEAD_GOLD.getValue(playerEnt));
        } catch (Exception e) {
            throw e;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @JSONField(serialize = false)
    public boolean isEnoughTotal(long value, PurseType... types) {
        if (this.getValueTotal(types) >= value) {
            return true;
        }
        return false;
    }

    @JSONField(serialize = false)
    public long getValueTotal(PurseType... types) {
        long sum = 0;
        try {
            readWriteLock.readLock().lock();
            for (PurseType type : types) {
                sum += type.getValue(playerEnt);
            }
        } catch (Exception exception) {
            throw exception;
        } finally {
            readWriteLock.readLock().unlock();
        }
        return sum;
    }

    @JSONField(serialize = false)
    public PurseUpdateResp collectUpdate() {
        List<PurseUpdateItem> items = new ArrayList<>();
        try {
            readWriteLock.readLock().lock();
            for (int i = 0, j = marks.length(); i < j; i++) {
                if (marks.get(i)) {
                    PurseType type = PurseType.typeOf(i);
                    items.add(PurseUpdateItem.valueOf(type.getCode(), type.getValue(playerEnt)));
                }
                marks.set(i, false);
            }
        } catch (Exception exception) {
            throw exception;
        } finally {
            readWriteLock.readLock().unlock();
        }
        PurseUpdateResp resp = new PurseUpdateResp();
        resp.setItems(items);
        return resp;
    }

    @JSONField(serialize = false)
    public PurseUpdateResp update(int code) {
        List<PurseUpdateItem> items = new ArrayList<>();
        readWriteLock.readLock().lock();
        try {
            PurseType type = PurseType.typeOf(code);
            items.add(PurseUpdateItem.valueOf(type.getCode(), type.getValue(playerEnt)));
        } catch (Exception exception) {
            throw exception;
        } finally {
            readWriteLock.readLock().unlock();
        }
        PurseUpdateResp resp = new PurseUpdateResp();
        resp.setItems(items);
        return resp;
    }

}
