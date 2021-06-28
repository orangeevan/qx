package com.mmorpg.qx.module.object.controllers.attack;

import com.haipaite.common.utility.New;
import com.mmorpg.qx.common.SystemOut;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.PlayerTrainerCreature;
import com.mmorpg.qx.module.skill.model.effect.EffectStatus;
import org.slf4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 仇恨列表
 *
 * @author wang ke
 * @since v1.0 2018年3月7日
 */
public final class AggroList {
    @SuppressWarnings("unused")
    private static final Logger log = SysLoggerFactory.getLogger(AggroList.class);

    private AbstractCreature owner;

    private ConcurrentHashMap<AbstractCreature, AggroInfo> aggroList = new ConcurrentHashMap<AbstractCreature, AggroInfo>();

    // private Map<PlayerGroup, Long> groupDamage = New.hashMap();
    //
    // private PlayerGroup mostDamageGroup;

    public AggroList(AbstractCreature owner) {
        this.owner = owner;
    }

    public boolean isEmpty() {
        return aggroList.isEmpty();
    }

    public void addDamage(AbstractCreature creature, long damage) {
        if (creature == null) {
            return;
        }
        if (!owner.isEnemy(creature)) {
            return;
        }

        addCommonDamage(creature, damage);

        owner.getController().onAddDamage();
    }

	/*
	private void addPlayerDamage(Player player, long damage) {
		if (player.isInGroup()) {
			long gd = damage;
			synchronized (groupDamage) {
				if (!groupDamage.containsKey(player.getPlayerGroup())) {
					groupDamage.put(player.getPlayerGroup(), Long.valueOf(damage));
				} else {
					gd = groupDamage.get(player.getPlayerGroup()) + damage;
					groupDamage.put(player.getPlayerGroup(), gd);
				}
				if (mostDamageGroup == null
						|| (mostDamageGroup != player.getPlayerGroup() && groupDamage.get(mostDamageGroup) < gd)) {
					mostDamageGroup = player.getPlayerGroup();
				}
			}
		}
		AggroInfo ai = getAggroInfo(player);
		ai.addDamage(damage);
		ai.addHate(damage);
	}
	*/

	/*
	private void addSummonDamage(Summon creature, long damage) {
		Player player = creature.getMaster();
		if (player.isInGroup()) {
			long gd = damage;
			synchronized (groupDamage) {
				if (!groupDamage.containsKey(player.getPlayerGroup())) {
					groupDamage.put(player.getPlayerGroup(), Long.valueOf(damage));
				} else {
					gd = groupDamage.get(player.getPlayerGroup()) + damage;
					groupDamage.put(player.getPlayerGroup(), gd);
				}
				if (mostDamageGroup == null
						|| (mostDamageGroup != player.getPlayerGroup() && groupDamage.get(mostDamageGroup) < gd)) {
					mostDamageGroup = player.getPlayerGroup();
				}
			}
		}
		AggroInfo playerAI = getAggroInfo(player);
		playerAI.addDamage(damage);
	
		AggroInfo ai = getAggroInfo(creature);
		ai.addHate(damage);
	}
	*/

    private void addCommonDamage(AbstractCreature creature, long damage) {
        AggroInfo ai = getAggroInfo(creature);
        ai.addDamage(damage);
        ai.addHate(damage);
    }

	/*
	public Collection<Player> getMostDamagePlayers() {
		synchronized (groupDamage) {
			long groupMostDamage = 0;
			if (mostDamageGroup != null) {
				groupMostDamage = groupDamage.get(mostDamageGroup);
			}
			Player mp = getMostPlayerDamage();
			if (mp == null) {
				return Collections.EMPTY_LIST;
			}
			long playerMostDamage = getAggroInfo(mp).getDamage();
			if (groupMostDamage >= playerMostDamage) {
				return Collections.unmodifiableCollection((mostDamageGroup.getMembers()));
			} else if (mp.isInGroup()) {
				return Collections.unmodifiableCollection(mp.getPlayerGroup().getMembers());
			} else {
				return Collections.unmodifiableCollection(Arrays.asList(mp));
			}
		}
	}
	*/

    public void addHate(AbstractCreature creature, long hate) {
        if (creature == null || creature == owner || !owner.isEnemy(creature)) {
            return;
        }

        AggroInfo ai = getAggroInfo(creature);
        ai.addHate(hate);

        owner.getController().onAddHate();
    }

    public AbstractCreature getMostDamage() {
        AbstractCreature mostDamage = null;
        long maxDamage = 0;

        for (Entry<AbstractCreature, AggroInfo> entry : aggroList.entrySet()) {
            AggroInfo info = entry.getValue();
            if (info.getAttacker() == null) {
                continue;
            }

            if (info.getDamage() > maxDamage) {
                mostDamage = info.getAttacker();
                maxDamage = info.getDamage();
            }
        }

        return mostDamage;
    }

    /**
     * @return player with most damage
     */
    public PlayerTrainerCreature getMostPlayerDamage() {
        if (aggroList.isEmpty()) {
            return null;
        }

        PlayerTrainerCreature mostDamage = null;
        long maxDamage = 0;

        for (Entry<AbstractCreature, AggroInfo> entry : aggroList.entrySet()) {
            AggroInfo info = entry.getValue();
            AbstractCreature creature = info.getAttacker();
            if (creature instanceof PlayerTrainerCreature) {
                if (info.getDamage() > maxDamage) {
                    mostDamage = (PlayerTrainerCreature) creature;
                    maxDamage = info.getDamage();
                }
            }
        }

        return mostDamage;
    }

    private long totalDamage;

    public double getDamagePercent(long damage) {
        return damage * 1.0 / totalDamage;
    }

    /**
     * @return player with damage
     */
    public Map<PlayerTrainerCreature, Long> getPlayerDamage() {
        Map<PlayerTrainerCreature, Long> players = New.hashMap();
        if (aggroList.isEmpty()) {
            return players;
        }
        totalDamage = 0;
        for (Entry<AbstractCreature, AggroInfo> entry : aggroList.entrySet()) {
            AggroInfo info = entry.getValue();
            if (info.getDamage() == 0) {
                continue;
            }
            AbstractCreature creature = info.getAttacker();
            totalDamage += info.getDamage();
            if (creature instanceof PlayerTrainerCreature) {
                players.put((PlayerTrainerCreature) creature, Long.valueOf(info.getDamage()));
            }
        }

        return players;
    }

    /**
     * 玩家伤害排名
     *
     * @return
     */
    public Map<Integer, PlayerTrainerCreature> getPlayerDamageRank() {
        return getPlayerDamageRank(getPlayerDamage());
    }

    /**
     * 玩家伤害排名
     *
     * @return
     */
    public Map<Integer, PlayerTrainerCreature> getPlayerDamageRank(Map<PlayerTrainerCreature, Long> players) {
        Map<Integer, PlayerTrainerCreature> rankMap = New.hashMap();
        if (players.isEmpty()) {
            return rankMap;
        }
        LinkedList<Entry<PlayerTrainerCreature, Long>> ranks = new LinkedList<Entry<PlayerTrainerCreature, Long>>();
        for (Entry<PlayerTrainerCreature, Long> entry : players.entrySet()) {
            ranks.add(entry);
        }

        Collections.sort(ranks, new Comparator<Entry<PlayerTrainerCreature, Long>>() {
            @Override
            public int compare(Entry<PlayerTrainerCreature, Long> o1, Entry<PlayerTrainerCreature, Long> o2) {
                return (o2.getValue() - o1.getValue()) < 0 ? -1 : 1;
            }
        });
        int i = 1;
        for (Entry<PlayerTrainerCreature, Long> rank : ranks) {
            rankMap.put(i, rank.getKey());
            i++;
        }
        return rankMap;
    }

    public static void main(String[] args) {

        Map<String, Long> players = new HashMap<String, Long>();
        players.put("f", 6L);
        players.put("a", 1L);
        players.put("c", 3L);
        players.put("e", 5L);
        players.put("d", 4L);
        players.put("b", 2L);
        Map<Integer, String> rankMap = New.hashMap();

        LinkedList<Entry<String, Long>> ranks = new LinkedList<Entry<String, Long>>();
        for (Entry<String, Long> entry : players.entrySet()) {
            ranks.add(entry);
        }

        Collections.sort(ranks, new Comparator<Entry<String, Long>>() {
            @Override
            public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
                return (int) (o1.getValue() - o2.getValue());
            }
        });
        int i = 1;
        for (Entry<String, Long> rank : ranks) {
            rankMap.put(i, rank.getKey());
            SystemOut.println(i + ":" + rank.getKey());
            i++;
        }

    }

    /**
     * @return most hated creature
     */
    public AbstractCreature getMostHated() {
        if (aggroList.isEmpty()) {
            return null;
        }

        AbstractCreature mostHated = null;
        long maxHate = 0;

        for (AggroInfo aggroInfo : aggroList.values()) {
            if (aggroInfo == null) {
                continue;
            }

            // aggroList will never contain anything but creatures
            AbstractCreature attacker = aggroInfo.getAttacker();

            if (attacker.isAlreadyDead()) {
                continue;
            }

//			if (!owner.getKnownList().knowns(aggroInfo.getAttacker())) {
//				continue;
//			}

            if (attacker.getEffectController().isInStatus(EffectStatus.Invisible)) {
                // 隐身的对象也不攻击
                continue;
            }

            if (aggroInfo.getHate() > maxHate) {
                mostHated = attacker;
                maxHate = aggroInfo.getHate();
            }
        }

        return mostHated;
    }

    /**
     * @param creature
     * @return
     */
    public boolean isMostHated(AbstractCreature creature) {
        if (creature == null || creature.getLifeStats().isAlreadyDead()) {
            return false;
        }

        AbstractCreature mostHated = getMostHated();
        if (mostHated == null) {
            return false;
        }

        return mostHated.equals(creature);
    }

    /**
     * @param creature
     * @param value
     */
    public void notifyHate(AbstractCreature creature, int value) {
        if (isHating(creature)) {
            addHate(creature, value);
        }
    }

    /**
     * @param creature
     */
    public void stopHating(AbstractCreature creature) {
        AggroInfo aggroInfo = aggroList.get(creature);
        if (aggroInfo != null) {
            aggroInfo.setHate(0);
        }
    }

    public void discountHating(AbstractCreature creature) {
        AggroInfo aggroInfo = aggroList.get(creature);
        if (aggroInfo != null) {
            if (aggroInfo.getHate() > 1) {
                aggroInfo.setHate(aggroInfo.getHate() / 2);
            }
        }
    }

    /**
     * Remove completely creature from aggro list
     *
     * @param creature
     */
    public void remove(AbstractCreature creature) {
        aggroList.remove(creature);
    }

    /**
     * Clear aggroList
     */
    public void clear() {
        aggroList.clear();
		/*
		groupDamage.clear();
		mostDamageGroup = null;
		*/
    }

    /**
     * @param creature
     * @return aggroInfo
     */
    private AggroInfo getAggroInfo(AbstractCreature creature) {
        AggroInfo ai = aggroList.get(creature);
        if (ai == null) {
            ai = new AggroInfo(creature);
            AggroInfo add = aggroList.putIfAbsent(creature, ai);
            if (add != null) {
                ai = add;
            }
        }
        return ai;
    }

    /**
     * @param creature
     * @return boolean
     */
    private boolean isHating(AbstractCreature creature) {
        return aggroList.containsKey(creature);
    }

    /**
     * @return aggro list
     */
    public Collection<AggroInfo> getList() {
        return aggroList.values();
    }

    /**
     * @return total damage
     */
    public int getTotalDamage() {
        int totalDamage = 0;
        for (AggroInfo ai : this.aggroList.values()) {
            totalDamage += ai.getDamage();
        }
        return totalDamage;
    }

}
