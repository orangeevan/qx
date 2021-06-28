package com.mmorpg.qx.module.team.model;

import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.team.enums.TeamType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description: 实际队伍
 * @since 11:12 2020-08-06
 */
public class Team {
    /**
     * 队伍成员
     */
    private List<AbstractTrainerCreature> members;
    /**
     * 队伍类型
     */
    private TeamType teamType;
    /**
     * 队伍Id
     **/
    private int id;

    public Team(int teamId, TeamType teamType, int teamSize) {
        this.teamType = teamType;
        members = new ArrayList<>(teamSize);
        this.id = teamId;
    }

    public List<AbstractTrainerCreature> getMembers() {
        return members;
    }

    public void setMembers(List<AbstractTrainerCreature> members) {
        this.members = members;
    }

    public TeamType getTeamType() {
        return teamType;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return teamType.hashCode() + members.hashCode() + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Team)) {
            return false;
        }
        return Integer.compare(this.id, ((Team) obj).id) == 0;
    }

    /***
     * 清除队伍成员，队伍标记
     */
    public void dismiss() {
        if (!CollectionUtils.isEmpty(members)) {
            members.forEach(creature -> creature.setTeamId(0));
            members.clear();
        }
    }
}
