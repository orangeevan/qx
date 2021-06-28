package com.mmorpg.qx.module.equipment.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.equipment.enums.EquipType;
import com.mmorpg.qx.module.equipment.enums.EquipUseLimit;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrResource;

import java.util.List;

/**
 * @author wang ke
 * @description: 装备
 * @since 10:36 2020-11-02
 */
@Resource
public class EquipmentResource extends AttrResource {
    @Id
    private int id;

    private String name;

    private int type;

    private EquipType equipType;

    /**
     * 使用限制
     */
    private int useLimit;

    private EquipUseLimit equipUseLimit;

    /**
     * 耐久
     */
    private int durability;

    /**
     * 工匠驯养师装备耐久度
     */
    private int specialDurability;

    /**
     * 附带效果
     */
    private List<Integer> effects;


    private transient List<Attr> baseAttrsList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EquipType getEquipType() {
        return equipType;
    }

    public void setEquipType(EquipType equipType) {
        this.equipType = equipType;
    }

    public EquipUseLimit getEquipUseLimit() {
        return equipUseLimit;
    }

    public void setEquipUseLimit(EquipUseLimit equipUseLimit) {
        this.equipUseLimit = equipUseLimit;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getSpecialDurability() {
        return specialDurability;
    }

    public void setSpecialDurability(int specialDurability) {
        this.specialDurability = specialDurability;
    }

    public List<Integer> getEffects() {
        return effects;
    }

    public void setEffects(List<Integer> effects) {
        this.effects = effects;
    }

    public List<Attr> getBaseAttrsList() {
        return baseAttrsList;
    }

    public void setBaseAttrsList(List<Attr> baseAttrsList) {
        this.baseAttrsList = baseAttrsList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUseLimit() {
        return useLimit;
    }

    public void setUseLimit(int useLimit) {
        this.useLimit = useLimit;
    }

    public void init() {
        baseAttrsList = GameUtil.buildAttr(this);
        equipType = EquipType.valueOf(type);
        equipUseLimit = EquipUseLimit.valueOf(useLimit);
    }
}
