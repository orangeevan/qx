package com.mmorpg.qx.module.object.gameobject;

import com.mmorpg.qx.common.enums.TriggerType;
import com.mmorpg.qx.module.equipment.enums.EquipType;
import com.mmorpg.qx.module.equipment.model.EquipItem;
import com.mmorpg.qx.module.equipment.resource.EquipmentResource;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.mwn.service.MWNService;
import com.mmorpg.qx.module.object.ObjectType;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.controllers.MWNController;
import com.mmorpg.qx.module.object.controllers.packet.MWNInfoResp;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectId;
import com.mmorpg.qx.module.object.gameobject.attr.AttrEffectType;
import com.mmorpg.qx.module.roundFight.model.Room;
import com.mmorpg.qx.module.roundFight.service.RoundFightService;
import com.mmorpg.qx.module.skill.manager.SkillManager;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import com.mmorpg.qx.module.skill.model.target.Target;
import com.mmorpg.qx.module.skill.packet.vo.SkillVo;
import com.mmorpg.qx.module.skill.resource.EffectResource;
import com.mmorpg.qx.module.skin.manager.SkinResourceManager;
import com.mmorpg.qx.module.skin.resource.MWNSkinResource;
import com.mmorpg.qx.module.worldMap.model.WorldPosition;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MWNCreature extends AbstractCreature {

    protected AbstractTrainerCreature master;
    /**
     * 仇恨列表，最后一个认为击杀者
     */
    private List<MWNCreature> hatredMwn;

    /**
     * 援护者列表，当被援护者魔物娘受到伤害，援护者分担伤害
     */
    private List<MWNCreature> assistedMWN;

    /**
     * 被援护者列表
     */
    private List<MWNCreature> beAssistedMWN;

    /**
     * 受效果影响列表，自身死亡时移除敌方身上效果
     */
    private List<Long> effectedList;

    /**
     * 战斗中魔物娘准备完毕，通知双方进行战斗
     */
    private volatile boolean readyFight = false;

    /**
     * 魔物娘配置表id
     */
    private int resourceId;

    private MWNSkinResource skinResource;

    /**
     * 手牌召唤出魔物娘，后续可以复活
     */
    private MoWuNiang mwn;

    /**
     * 战斗回合中穿戴装备
     */
    private Map<EquipType, EquipItem> wearEquips;

    private int callRound;

    //召唤时处于流放状态
    private boolean exile;

    //魔物娘对战援护
    private long supporter;

    public MWNCreature(long objId, MWNController controller, WorldPosition position, AbstractTrainerCreature master, int resourceId) {
        super(objId, controller, position);
        controller.setOwner(this);
        this.master = master;
        this.hatredMwn = new ArrayList<>();
        this.effectedList = new ArrayList<>();
        this.resourceId = resourceId;
        skinResource = SkinResourceManager.getInstance().getMwnOriginalSkin(resourceId);
        Room room = master.getRoom();
        if (Objects.nonNull(room)) {
            callRound = room.getRound();
        }
    }

    @Override
    public String getName() {
        return getSkinResource().getName();
    }

    @Override
    public boolean isEnemy(AbstractCreature creature) {
        return master.isEnemy(creature);
    }

    public void setMaster(AbstractTrainerCreature master) {
        this.master = master;
    }

    @Override
    public String toString() {
        return String.format("SUMMON ID:[%d] NAME:[%s]  grid:[%d]  MASTERID:[%d] MASTERNAME:[%s]", objectId, getName(), getGridId(), master.getObjectId(), master.getName());
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.MWN;
    }

    /**
     * 战斗回合后处理
     */
    public void afterFightRound(MWNCreature attacker, int skillId, int effectId) {
        if (this.getMaster().getRoom().isAttackMWN(this) && this.isAlreadyDead() && attacker.isAlive()) {
            RoundFightService.getInstance().mwnFightHarmTrainer(this.getMaster(), attacker);
        }
        //如果是进攻方胜利，身上有元素需要附加给驯养师
        if (this.getMaster().getRoom().isAttackMWN(this) && this.isAlive()) {
            if (!this.getMaster().getController().onMwnJobOrEleChange(this, Reason.Mwn_Fight_Win)) {
                // 进攻胜利方魔物娘占领地格信息显示
                MWNService.getInstance().sendMwnEvoOrOcc(null, 0, this.getMaster(), Reason.Mwn_Fight_Win, null);
            }
        }
        getMaster().clearFightMWN();
        /**每次战斗结束装备属性移除*/
        clearWearEquip();
        setSupporter(0L);
        if (this.isAlreadyDead()) {
            getController().onDie(attacker, skillId, effectId);
            //如果魔物娘身上没有任何效果，可以直接移除
            if (getEffectController().hasNoEffects()) {
                getController().delete();
            }
        }
    }

    public void fightReady() {
        readyFight = true;
    }

    public boolean isReadyFight() {
        return readyFight;
    }

    public void resetFightReady() {
        readyFight = false;
    }

    public List<MWNCreature> getHatredMwn() {
        return hatredMwn;
    }

    public void setHatredMwn(List<MWNCreature> hatredMwn) {
        this.hatredMwn = hatredMwn;
    }

    public void addHatredMwn(MWNCreature hatred) {
        if (hatredMwn.contains(hatred)) {
            hatredMwn.remove(hatred);
        }
        hatredMwn.add(hatred);
    }

    public MWNCreature getKiller() {
        if (!this.isAlreadyDead()) {
            return null;
        }
        if (hatredMwn.size() == 0) {
            return null;
        }
        return hatredMwn.get(hatredMwn.size() - 1);
    }

    public List<MWNCreature> getAssistedMWN() {
        if (CollectionUtils.isEmpty(assistedMWN)) {
            return Collections.EMPTY_LIST;
        }
        return assistedMWN.stream().filter(mwn -> !mwn.isAlreadyDead()).collect(Collectors.toList());
    }

    public void setAssistedMWN(List<MWNCreature> assistedMWN) {
        this.assistedMWN = assistedMWN;
    }

    /**
     * 是否有援助
     *
     * @return
     */
    public boolean hasAssisted() {
        if (CollectionUtils.isEmpty(assistedMWN)) {
            return false;
        }
        return assistedMWN.stream().anyMatch(mwn -> !mwn.isAlreadyDead());
    }

    public List<MWNCreature> getBeAssistedMWN() {
        return beAssistedMWN;
    }

    public void setBeAssistedMWN(List<MWNCreature> beAssistedMWN) {
        this.beAssistedMWN = beAssistedMWN;
    }

    public int getLevel() {
        //return MWNManager.getInstance().getMWNById(this.getObjectKey()).getLevel();
        //等级后续补充，有等级模块来实现
        return 0;
    }

    public int getCurHpPercent() {
        return getCurrentHp() / getLifeStats().getMaxHp();
    }

    public void addEffected(long objectId) {
        effectedList.add(objectId);
    }

    public List<Long> getEffectedList() {
        return effectedList;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public MWNInfoResp getInfo() {
        MWNInfoResp resp = new MWNInfoResp();
        resp.setModelId(getObjectKey());
        resp.setObjectId(getObjectId());
        resp.setGridId(getPosition().getGridId());
        resp.setDir(getDir().getDir());
        resp.setAttrs(getAttrController().getAttrVos());
        List<SkillVo> skills = this.getSkillController().getSkillVo();
        resp.setSkill(skills);
        resp.setCurHp(getCurrentHp());
        resp.setCurMp(getLifeStats().getCurrentMp());
        resp.setEffects(getEffectController().toEffectVo());
        resp.setSkinId(skinResource.getId());
        resp.setOwner(master.getObjectId());
        //TODO 同步魔物娘信息
        {
            StringBuilder builder = new StringBuilder();
            if (!CollectionUtils.isEmpty(resp.getAttrs())) {
                resp.getAttrs().forEach(attr -> builder.append(attr));
                System.err.println(builder.toString());
            }
        }
        resp.setLevel(mwn.getLevel());
        resp.setCasteLv(mwn.getCasteLv());
        resp.setStarLv(mwn.getStarLv());
        return resp;
    }

    public MWNSkinResource getSkinResource() {
        return skinResource;
    }

    @Override
    public AbstractTrainerCreature getMaster() {
        return master;
    }

    public void setSkinResource(MWNSkinResource skinResource) {
        this.skinResource = skinResource;
    }

    public MoWuNiang getMwn() {
        return mwn;
    }

    public void setMwn(MoWuNiang mwn) {
        this.mwn = mwn;
    }


    public void wearEquip(EquipItem equipItem) {
        if (Objects.isNull(equipItem)) {
            return;
        }
        equipItem.addUseTime();
        EquipmentResource resource = equipItem.getResource();
        List<Attr> attrs = resource.getBaseAttrsList();
        getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Equip_Effect), attrs, true, true);
        //装备自带buff效果处理
        List<Integer> effects = resource.getEffects();
        if (!CollectionUtils.isEmpty(effects)) {
            effects.forEach(id -> {
                EffectResource effectResource = SkillManager.getInstance().getEffectResource(id);
                Effect effect = new Effect(this, this, effectResource.getEffectType().create(), 0, id, Target.valueOf(getGridId(), getObjectId()));
                getEffectController().addEffect(effect);
            });
        }
        if (Objects.isNull(wearEquips)) {
            wearEquips = new HashMap<>();
        }
        wearEquips.put(equipItem.getResource().getEquipType(), equipItem);
        handleEffect(TriggerType.WEAR_EQUIP, this, equipItem.getEquipId());
        System.err.println("穿上装备，血量：" + this.getCurrentHp());
    }

    public void clearWearEquip() {
        if (!CollectionUtils.isEmpty(wearEquips)) {
            List<Attr> removeAttrs = new ArrayList<>();
            wearEquips.values().forEach(equipItem -> {
                List<Attr> baseAttrsList = equipItem.getResource().getBaseAttrsList();
                if (!CollectionUtils.isEmpty(baseAttrsList)) {
                    baseAttrsList.forEach(attr -> removeAttrs.add(attr.reverse()));
                }
            });
            this.getAttrController().addModifiers(AttrEffectId.valueOf(AttrEffectType.Equip_Effect), removeAttrs, true, true);
            wearEquips.clear();
        }
    }

    public EquipItem getWearEquip() {
        if (!CollectionUtils.isEmpty(wearEquips)) {
            return wearEquips.values().iterator().next();
        }
        return null;
    }

    /**
     * 是否召唤回合
     *
     * @param round
     * @return
     */
    public boolean isCallRound(int round) {
        return round == callRound;
    }

    /**
     * 设置流放
     *
     * @param exile
     */
    public void setExile(boolean exile) {
        this.exile = exile;
    }

    /**
     * 重置流放
     */
    public void resetExile() {
        this.exile = false;
    }

    /**
     * 魔物娘技能是否已经进化
     *
     * @return
     */
    public boolean hasSkillEvo() {
        return getSkillController().hasSkill(MWNManager.getInstance().getMWNResource(getResourceId()).getSkillEvoAfter());
    }

    /**
     * 魔物娘能否进化
     *
     * @return
     */
    public boolean canSkillEvo() {
        return MWNManager.getInstance().getMWNResource(getResourceId()).getSkillEvoAfter() > 0;
    }

    public MWNResource getResource() {
        return MWNManager.getInstance().getMWNResource(resourceId);
    }

    public long getSupporter() {
        return supporter;
    }

    public void setSupporter(long supporter) {
        this.supporter = supporter;
    }
}
