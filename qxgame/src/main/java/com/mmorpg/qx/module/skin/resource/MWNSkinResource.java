package com.mmorpg.qx.module.skin.resource;

import com.haipaite.common.resource.anno.Id;
import com.haipaite.common.resource.anno.Resource;
import com.mmorpg.qx.module.skin.enums.SkinGetType;
import com.mmorpg.qx.module.skin.enums.SkinQa;

/**
 * @author wang ke
 * @description:魔物娘皮肤
 * @since 20:03 2020-09-27
 */
@Resource
public class MWNSkinResource {
  @Id
  private int id;

  private String name;

  private int MwnId;

  private int skinQa;

  private int getWay;

  private SkinQa skinQaType;

  private SkinGetType skinGetType;

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

    public int getMwnId() {
        return MwnId;
    }

    public void setMwnId(int mwnId) {
        MwnId = mwnId;
    }

    public int getSkinQa() {
        return skinQa;
    }

    public void setSkinQa(int skinQa) {
        this.skinQa = skinQa;
    }

    public int getGetWay() {
        return getWay;
    }

    public void setGetWay(int getWay) {
        this.getWay = getWay;
    }

    public SkinQa getSkinQaType() {
        return skinQaType;
    }

    public void setSkinQaType(SkinQa skinQaType) {
        this.skinQaType = skinQaType;
    }

    public SkinGetType getSkinGetType() {
        return skinGetType;
    }

    public void setSkinGetType(SkinGetType skinGetType) {
        this.skinGetType = skinGetType;
    }
}
