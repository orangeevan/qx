package com.mmorpg.qx.module.player.resource;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.player.model.RoleType;
import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;

/**
 * 等级属性
 * @author wang ke
 * @since v1.0 2018年3月20日
 *
 */
@Resource
public class PlayerLevelResource {
	@Id
	private int level;
	private long exp;
	private List<Attr> zhanShiStats;
	private List<Attr> faShiStats;
	private List<Attr> daoShiStats;

	@JSONField(serialize = false)
	public List<Attr> getRoleStat(RoleType roleType) {
		if (roleType == RoleType.ZHAN_SHI) {
			return zhanShiStats;
		} else if (roleType == RoleType.FA_SHI) {
			return faShiStats;
		} else if (roleType == RoleType.DAO_SHI) {
			return daoShiStats;
		}
		return null;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public List<Attr> getZhanShiStats() {
		return zhanShiStats;
	}

	public void setZhanShiStats(List<Attr> zhanShiStats) {
		this.zhanShiStats = zhanShiStats;
	}

	public List<Attr> getFaShiStats() {
		return faShiStats;
	}

	public void setFaShiStats(List<Attr> faShiStats) {
		this.faShiStats = faShiStats;
	}

	public List<Attr> getDaoShiStats() {
		return daoShiStats;
	}

	public void setDaoShiStats(List<Attr> daoShiStats) {
		this.daoShiStats = daoShiStats;
	}

}
