package com.mmorpg.qx.module.team.manager;

import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.team.enums.TeamType;
import com.mmorpg.qx.module.team.model.Team;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wang ke
 * @description: 组队管理者
 * @since 11:11 2020-08-06
 */

@Component
public class TeamManager {

    /** 队伍Id生成器*/
    private AtomicInteger teamIdGen = new AtomicInteger(0);
    /** 全服队伍缓存*/
    private ConcurrentHashMap<Integer, Team> allTeams = new ConcurrentHashMap(50);

    /***
     * 简单队伍id生成
     * @return
     */
    public synchronized int genTeamId(){
        teamIdGen.compareAndSet(Integer.MAX_VALUE, 0);
        return teamIdGen.incrementAndGet();
    }

    /***
     * 创建队伍
     * @param player
     * @param teamType
     * @param size
     * @return
     */
    public Team createTeam(Player player, TeamType  teamType, int size){
        Team team = new Team(genTeamId(), teamType, size);
        player.setTeamId(team.getId());
        allTeams.put(team.getId(), team);
        return team;
    }

    /***
     * 解散队伍
     * @param teamId
     */
    public void dismissTeam(int teamId){
        Team team = allTeams.remove(teamId);
        if (team == null) {
            return;
        }
        team.dismiss();
    }

    /***
     * 判断多个对象是否在同一个队伍
     * @param creatureSelf
     * @param creatures
     * @return
     */
    public boolean isTeammate(AbstractTrainerCreature creatureSelf, AbstractTrainerCreature... creatures){
        if (creatureSelf == null || creatures == null || creatureSelf.getTeamId() == 0) {
            return false;
        }
        return Arrays.stream(creatures).filter(creature -> creatureSelf.getTeamId() != creature.getTeamId()).count() == 0;
    }


    private static TeamManager instance;

    @PostConstruct
    private void init(){
        instance = this;
    }

    public static TeamManager getInstance() {
        return instance;
    }


}
