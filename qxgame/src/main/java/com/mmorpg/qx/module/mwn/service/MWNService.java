package com.mmorpg.qx.module.mwn.service;

import com.mmorpg.qx.common.BeanService;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.common.RelationshipUtils;
import com.mmorpg.qx.common.exception.ManagedErrorCode;
import com.mmorpg.qx.common.exception.ManagedException;
import com.mmorpg.qx.common.identify.manager.IdentifyManager;
import com.mmorpg.qx.common.session.PacketSendUtility;
import com.mmorpg.qx.module.item.manager.ItemManager;
import com.mmorpg.qx.module.item.model.PackItem;
import com.mmorpg.qx.module.item.resource.ItemResource;
import com.mmorpg.qx.module.item.service.ItemService;
import com.mmorpg.qx.module.mwn.enums.MwnQuality;
import com.mmorpg.qx.module.mwn.manager.MWNCasteManager;
import com.mmorpg.qx.module.mwn.manager.MWNLevelManager;
import com.mmorpg.qx.module.mwn.manager.MWNManager;
import com.mmorpg.qx.module.mwn.manager.MWNStarManager;
import com.mmorpg.qx.module.mwn.model.MoWuNiang;
import com.mmorpg.qx.module.mwn.packet.DevelopMwnResp;
import com.mmorpg.qx.module.mwn.packet.MwnChangeSkinResp;
import com.mmorpg.qx.module.mwn.packet.MwnEvoResp;
import com.mmorpg.qx.module.mwn.packet.MwnListResp;
import com.mmorpg.qx.module.mwn.packet.vo.MoWuNiangVo;
import com.mmorpg.qx.module.mwn.resource.MWNCasteResource;
import com.mmorpg.qx.module.mwn.resource.MWNLevelResource;
import com.mmorpg.qx.module.mwn.resource.MWNResource;
import com.mmorpg.qx.module.mwn.resource.MWNStarResource;
import com.mmorpg.qx.module.object.Reason;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.AbstractTrainerCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;
import com.mmorpg.qx.module.object.gameobject.attr.Attr;
import com.mmorpg.qx.module.object.gameobject.attr.AttrType;
import com.mmorpg.qx.module.object.gameobject.packet.vo.AttrVo;
import com.mmorpg.qx.module.player.model.Player;
import com.mmorpg.qx.module.skin.manager.SkinResourceManager;
import com.mmorpg.qx.module.skin.resource.MWNSkinResource;
import com.mmorpg.qx.module.worldMap.model.VirPoint;
import com.mmorpg.qx.module.worldMap.model.WorldMapInstance;
import com.mmorpg.qx.module.worldMap.resource.MapGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wang ke
 * @description: ?????????service
 * @since 11:01 2020-08-18
 */
@Component
public class MWNService {

    public static MWNService getInstance() {
        return BeanService.getBean(MWNService.class);
    }

    @Autowired
    private MWNManager mwnManager;
    @Autowired
    private MWNLevelManager mwnLevelManager;
    @Autowired
    private MWNCasteManager mwnCasteManager;
    @Autowired
    private MWNStarManager mwnStarManager;
    @Autowired
    private ItemManager itemManager;

    /***
     * ???????????????
     * @param player
     * @param resourceId
     */
    public MoWuNiang createMwn(Player player, int resourceId) {
        //TODO ?????????????????????????????????????????????

        MoWuNiang moWuNiang = valueOf(resourceId);
        if (player.getPlayerMoWuNiang() == null) {
            player.setPlayerMoWuNiang(new HashMap<>());
        }
        player.getPlayerMoWuNiang().put(moWuNiang.getId(), moWuNiang);
        mwnManager.update(player);
        mwnList(player);
        return moWuNiang;
    }

    public MoWuNiang valueOf(int resourceId) {
        MWNResource mwnResource = mwnManager.getMWNResource(resourceId);
        if (mwnResource == null) {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
        MoWuNiang moWuNiang = new MoWuNiang();
        moWuNiang.setId(IdentifyManager.getInstance().getNextIdentify(IdentifyManager.IdentifyType.MOWUNIANG));
        moWuNiang.setResourceId(mwnResource.getId());
        MWNSkinResource mwnOriginalSkin = SkinResourceManager.getInstance().getMwnOriginalSkin(resourceId);
        moWuNiang.setSkinResourceId(mwnOriginalSkin.getId());
        if (moWuNiang.getSkinIds() == null) {
            moWuNiang.setSkinIds(new ArrayList<>());
        }

        moWuNiang.getSkinIds().add(mwnOriginalSkin.getId());
//        moWuNiang.getSkinIds().addAll(mwnResource.getGainableSkin());

        moWuNiang.setExp(0);
        //moWuNiang.setAttrList(mwnResource.getBaseAttrsList());
        if (mwnResource.getQualityType() == MwnQuality.R || mwnResource.getQualityType() == MwnQuality.SR) {
            moWuNiang.setStarLv(1);
        } else if (mwnResource.getQualityType() == MwnQuality.SSR) {
            moWuNiang.setStarLv(2);
        } else if (mwnResource.getQualityType() == MwnQuality.UR) {
            moWuNiang.setStarLv(3);
        }
        return moWuNiang;
    }

    public boolean deadRemoveTrainerEleAttr(MWNCreature mwnCreature) {
        int casteLv = mwnCreature.getMwn().getCasteLv();
        if (casteLv >= 4) {
            return false;
        }
        return true;
    }

    /**
     * ???????????????
     *
     * @param player
     */
    public void mwnList(Player player) {
        MwnListResp resp = new MwnListResp();
        Map<Long, MoWuNiang> mwn = player.getPlayerMoWuNiang();
        if (!CollectionUtils.isEmpty(mwn)) {
            List<MoWuNiangVo> vos = mwn.values().stream().map(MoWuNiangVo::valueOf).collect(Collectors.toList());
            resp.setMoWuNiangVos(vos);
        } else {
            resp.setMoWuNiangVos(new ArrayList<>());
        }
        PacketSendUtility.sendPacket(player, resp);
    }

    /**
     * ???????????????
     *
     * @param player
     * @param mwnId
     * @param type   ???????????? 1?????? 2?????? 3??????
     */
    public void developMwn(Player player, long mwnId, int type) {
        if (type == 1) {
            upLevel(player, mwnId);
        } else if (type == 2) {
            upCaste(player, mwnId);
        } else if (type == 3) {
            upStar(player, mwnId);
        } else {
            throw new ManagedException(ManagedErrorCode.PARAMETER_ILLEGAL);
        }
    }

    /**
     * ???????????????
     *
     * @param player
     * @param mwnId  ?????????ID
     */
    private void upLevel(Player player, long mwnId) {
        MoWuNiang mwn = getOrThrowMWN(player, mwnId);
        MWNResource mwnResource = mwnManager.getMWNResource(mwn.getResourceId());
        if (mwnResource == null) {
            throw new ManagedException(ManagedErrorCode.SYS_ERROR);
        }
        // ??????????????????????????????
        if (mwn.getLevel() >= mwnLevelManager.getMaxLevel()) {
            throw new ManagedException(ManagedErrorCode.MWN_REACH_MAX_LEVEL);
        }
        // ????????????????????????
        MWNLevelResource levelResource = mwnLevelManager.getLevelResource(mwn.getLevel());
        if (mwn.getCasteLv() < levelResource.getCasteLimit()) {
            throw new ManagedException(ManagedErrorCode.MWN_UPLEVEL_CASTE_LIMIT);
        }
        int itemKey = levelResource.getCostId();
        List<PackItem> items = getItems(player, itemKey);
        if (items.size() == 0) {
            throw new ManagedException(ManagedErrorCode.NOT_ENOUGH_ITEM);
        }
        int sumNum = items.stream().mapToInt(PackItem::getNum).sum();
        ItemResource itemResource = itemManager.getResource(itemKey);
        int requireNum = (levelResource.getFullExp() - mwn.getExp()) / itemResource.getUseEffectNum();
        // ?????????????????????1?????????1??? ????????????????????????1????????????????????????
        // ????????????/???????????????????????????
        int finalNum = Math.min(requireNum, sumNum);

        // ????????????
        for (PackItem item : items) {
            if (item.getNum() < finalNum) {
                ItemService.getInstance().consumeItem(player, item.getObjectId(), "item", item.getNum());
                finalNum -= item.getNum();
            } else {
                ItemService.getInstance().consumeItem(player, item.getObjectId(), "item", finalNum);
                break;
            }
        }

        List<AttrVo> voList = new ArrayList<>();
        // ?????????????????????
        mwn.addExp(itemResource.getUseEffectNum() * finalNum);
        if (mwn.getExp() == levelResource.getFullExp()) {
            mwn.setExp(0);
            mwn.addOneLevel();
            // ????????????????????????
            int hp = calculateHp(mwn);
            int attack = getAttack(mwn);
            updateAttr(mwn, AttrType.Max_Hp, hp);
            updateAttr(mwn, AttrType.Attack, attack);

            voList.add(AttrVo.valueOf(AttrType.Max_Hp.value(), hp));
            voList.add(AttrVo.valueOf(AttrType.Attack.value(), attack));
        }
        mwnManager.update(player);

        PacketSendUtility.sendPacket(player, DevelopMwnResp.valueOf(mwn, voList));
    }

    /**
     * ???????????????
     *
     * @param player
     * @param mwnId  ?????????ID
     */
    private void upCaste(Player player, long mwnId) {
        MoWuNiang mwn = getOrThrowMWN(player, mwnId);
        // ??????????????????????????????
        if (mwn.getCasteLv() >= mwnCasteManager.getMaxCasteLv(mwn.getResourceId())) {
            throw new ManagedException(ManagedErrorCode.MWN_REACH_MAX_CASTE);
        }
        // ????????????????????????
        MWNCasteResource casteResource = mwnCasteManager.getCasteResource(mwn.getResourceId(), mwn.getCasteLv());
        if (mwn.getLevel() < casteResource.getLevelLimit()) {
            throw new ManagedException(ManagedErrorCode.MWN_UPCASTE_LEVEL_LIMIT);
        }
        List<Integer> costIds = casteResource.getCostIds();
        List<Integer> nums = casteResource.getNums();
        // ???????????????????????????????????????
        for (int i = 0; i < costIds.size(); i++) {
            Integer key = costIds.get(i);
            Integer num = nums.get(i);
            int sumNum = getItems(player, key).stream().mapToInt(PackItem::getNum).sum();
            if (sumNum < num) {
                throw new ManagedException(ManagedErrorCode.NOT_ENOUGH_ITEM);
            }
        }
        // ????????????
        for (int i = 0; i < costIds.size(); i++) {
            ItemService.getInstance().consumeItemByRid(player, costIds.get(i), "item", nums.get(i));
        }
        // ?????????
        mwn.addOneCaste();
        mwnManager.update(player);

        PacketSendUtility.sendPacket(player, DevelopMwnResp.valueOf(mwn, null));
    }

    /**
     * ??????????????????ID??????????????????
     *
     * @param player
     * @param resourceId ????????????ID
     * @return
     */
    private List<PackItem> getItems(Player player, int resourceId) {
        return player.getWarehouse().getItems().values().stream()
                .filter(t -> t.getKey() == resourceId && t.getNum() > 0).collect(Collectors.toList());
    }

    /**
     * ???????????????
     *
     * @param player
     * @param mwnId  ?????????ID
     */
    private void upStar(Player player, long mwnId) {
        MoWuNiang mwn = getOrThrowMWN(player, mwnId);
        // ??????????????????????????????
        if (mwn.getStarLv() >= mwnStarManager.getMaxStarLv(mwn.getResourceId())) {
            throw new ManagedException(ManagedErrorCode.MWN_REACH_MAX_STAR);
        }
        MWNStarResource resource = mwnStarManager.getStarResource(mwn.getResourceId(), mwn.getStarLv());
        ItemService.getInstance().consumeItemByRid(player, resource.getCostId(), "item", resource.getNum());
        // ?????????
        mwn.addOneStar();
        // ????????????
        int hp = calculateHp(mwn);
        updateAttr(mwn, AttrType.Max_Hp, hp);
        mwnManager.update(player);

        List<AttrVo> voList = new ArrayList<>();
        voList.add(AttrVo.valueOf(AttrType.Max_Hp.value(), hp));
        PacketSendUtility.sendPacket(player, DevelopMwnResp.valueOf(mwn, voList));
    }

    private MoWuNiang getOrThrowMWN(Player player, long mwnId) {
        MoWuNiang mwn = player.getPlayerMoWuNiang().get(mwnId);
        if (mwn == null) {
            throw new ManagedException(ManagedErrorCode.MWN_NOT_EXISTS);
        }
        return mwn;
    }

    /**
     * ????????????
     * ?????? = ???????????????*(1 + ??????????????????????????????)
     *
     * @param mwn
     * @return
     */
    private int calculateHp(MoWuNiang mwn) {
        int costMp = mwnManager.getMWNResource(mwn.getResourceId()).getCostMp();
        int oneCostHp = mwnLevelManager.getLevelResource(mwn.getLevel()).getHp();
        // ??????????????? = ?????? * 1?????????
        int hp = costMp * oneCostHp;
        int addHpPercent = mwnStarManager.getStarResource(mwn.getResourceId(), mwn.getStarLv()).getAddHpPercent();
        return hp + hp * addHpPercent / 100;
    }

    /**
     * ???????????????
     *
     * @param mwn
     * @return
     */
    private int getAttack(MoWuNiang mwn) {
        return mwnLevelManager.getLevelResource(mwn.getLevel()).getAttack();
    }

    /**
     * ????????????
     *
     * @param mwn
     * @param type
     */
    private void updateAttr(MoWuNiang mwn, AttrType type, int value) {
        mwn.getAttrList().removeIf(attr -> attr.getType() == type);
        mwn.getAttrList().add(Attr.valueOf(type, value));
    }

    /**
     * ?????????????????????????????????
     *
     * @param mwnCreature
     * @return
     */
    public AttrType getEleAttrType(MWNCreature mwnCreature) {
        return AttrType.getAllEleType().stream().filter(attrType -> mwnCreature.getAttrController().getCurrentAttr(attrType) > 0)
                .findFirst().orElse(null);
    }

    /**
     * ?????????????????????????????????
     *
     * @param mwnCreature
     * @return
     */
    public AttrType getJobAttrType(MWNCreature mwnCreature) {
        return AttrType.getAllJobType().stream().filter(attrType -> mwnCreature.getAttrController().getCurrentAttr(attrType) > 0)
                .findFirst().orElse(null);
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param trainerCreature
     * @param eleAttrType
     * @return
     */
    public List<MWNCreature> getMwnHasEleNeedEvo(AbstractTrainerCreature trainerCreature, AttrType eleAttrType) {
        Collection<MWNCreature> mwns = trainerCreature.getMWN(true);
        if (CollectionUtils.isEmpty(mwns)) {
            return null;
        }
        return mwns.stream().filter(mwnCreature -> mwnCreature.hasAttr(eleAttrType) && mwnCreature.canSkillEvo()
                && !mwnCreature.hasSkillEvo()).collect(Collectors.toList());
    }

    /**
     * ?????????????????????????????????
     *
     * @param trainerCreature
     * @param eleAttrType
     * @return
     */
    public List<MWNCreature> getMwnHasEleEvo(AbstractTrainerCreature trainerCreature, AttrType eleAttrType) {
        Collection<MWNCreature> mwns = trainerCreature.getMWN(true);
        if (CollectionUtils.isEmpty(mwns)) {
            return null;
        }
        return mwns.stream().filter(mwnCreature -> mwnCreature.hasAttr(eleAttrType) && mwnCreature.canSkillEvo()
                && mwnCreature.hasSkillEvo()).collect(Collectors.toList());
    }

    public void mwnSKillEvo(List<MWNCreature> mwnCreatures, boolean isEvo) {
        if (CollectionUtils.isEmpty(mwnCreatures)) {
            return;
        }
        mwnCreatures.forEach(mwnCreature -> {
            MWNResource resource = mwnCreature.getResource();
            //??????
            if (isEvo) {
                mwnCreature.getSkillController().replaceEvoSkill(resource.getSkillEvoBefore(), resource.getSkillEvoAfter());
            } else {
                //????????????
                mwnCreature.getSkillController().replaceEvoSkill(resource.getSkillEvoAfter(), resource.getSkillEvoBefore());
            }
        });
    }

    public void sendMwnEvoOrOcc(List<MWNCreature> mwnCreatures, int evo, AbstractTrainerCreature trainerCreature, Reason reason, Attr alterBefore) {
        int occupy = -1;
        if (reason == Reason.Call_Mwn || reason == Reason.Mwn_Fight_Win) {
            occupy = 1;
        } else if (reason == Reason.Mwn_Replace) {
            occupy = 0;
        } else if (reason == Reason.Mwn_Fight_Fail) {
            occupy = 2;
        }
        PacketSendUtility.broadcastInWorldMap(trainerCreature, MwnEvoResp.valueOf(trainerCreature, mwnCreatures, evo, occupy, alterBefore), true);
    }

    /**
     * ????????????????????????????????????
     *
     * @param mwnCreature
     * @param target
     * @return
     */
    public boolean isInDomainRange(MWNCreature mwnCreature, AbstractCreature target) {
        WorldMapInstance mapInstance = mwnCreature.getWorldMapInstance();
        MapGrid casterGrid = mapInstance.getParent().getMapGrid(mwnCreature.getGridId());
        MapGrid targetGrid = mapInstance.getParent().getMapGrid(target.getGridId());
        //????????????
        int faceRange = mwnCreature.getAttrController().getCurrentAttr(AttrType.Attack_Range_Face);
        int faceRangeVer = mwnCreature.getAttrController().getCurrentAttr(AttrType.Attack_Range_Face_Ver);
        VirPoint virPoint = VirPoint.valueOf(casterGrid.getX2(), casterGrid.getY2(), casterGrid.getBirthDir().getOpposite());
        if (RelationshipUtils.isMWN(mwnCreature)) {
            virPoint = GameUtil.getMwnVirPoint(mwnCreature.getWorldMapInstance().getParent(), casterGrid.getId());
        }
        return GameUtil.isInRange(mapInstance.getParent(), virPoint, targetGrid.getId(), faceRange, faceRangeVer);
    }

    public void updateHpOrAttack(MoWuNiang mwn, AttrType type) {
        if (type == AttrType.Max_Hp) {
            updateAttr(mwn, type, calculateHp(mwn));
        }
        if (type == AttrType.Attack) {
            updateAttr(mwn, type, getAttack(mwn));
        }
    }

    /**
     * ?????????????????????
     *
     * @param player
     * @param mwnId
     * @param skinId
     */
    public void mwnChangeSkin(Player player, long mwnId, int skinId) {
        MoWuNiang mwn = player.getPlayerMoWuNiang().get(mwnId);
        if (mwn == null) {
            throw new ManagedException(ManagedErrorCode.MWN_NOT_EXISTS);
        }
        if (skinId == mwn.getSkinResourceId()) {
            throw new ManagedException(ManagedErrorCode.MWN_HAS_WEAR_SKIN);
        }
        if (!mwn.getSkinIds().contains(skinId)) {
            throw new ManagedException(ManagedErrorCode.MWN_NOT_HAVE_SKIN);
        }
        mwn.setSkinResourceId(skinId);
        mwnManager.update(player);
        PacketSendUtility.sendPacket(player, MwnChangeSkinResp.valueOf(mwnId, skinId));
    }
}
