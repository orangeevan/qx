package com.mmorpg.qx.module.object.gameobject.attr;

import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.object.gameobject.MWNCreature;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wang ke
 * @description: 元素属性相互克制
 * @since 10:52 2021/3/13
 */
public class AttrRestrainUtils {

    /**
     * 魔物娘之间是否相互克制
     *
     * @param mwnA
     * @param mwnB
     * @return
     */
    public static boolean mwnRestrain(MWNCreature mwnA, MWNCreature mwnB) {
        return creatureAttrRestrain(mwnA, mwnB);
    }

    public static boolean creatureAttrRestrain(AbstractCreature creatureA, AbstractCreature creatureB) {
        if (!creatureA.getAttrController().hasAnyAttr() || !creatureB.getAttrController().hasAnyAttr()) {
            return false;
        }
        for (RestrainType restrainType : RestrainType.values()) {
            if (creatureA.getAttrController().hasAttr(restrainType.getAttr()) && creatureB.getAttrController().hasAttr(restrainType.getRestrainedAttr())) {
                return true;
            }
        }
        return false;
    }


    private static enum RestrainType {
        Wind_Restrain(AttrType.Ele_Wind, AttrType.Ele_Water),
        Water_Restrain(AttrType.Ele_Water, AttrType.Ele_Fire),
        Fire_Restrain(AttrType.Ele_Fire, AttrType.Ele_Earth),
        Earth_Restrain(AttrType.Ele_Earth, AttrType.Ele_Wind),
        ;

        //元素类型
        private AttrType attr;
        //被克制元素
        private AttrType restrainedAttr;

        private RestrainType(AttrType attr, AttrType restrainedAttr) {
            this.attr = attr;
            this.restrainedAttr = restrainedAttr;
        }

        public AttrType getAttr() {
            return attr;
        }

        public AttrType getRestrainedAttr() {
            return restrainedAttr;
        }

        public static boolean isRestrained(AttrType eleType, AttrType restrainedEleType) {
            Optional<RestrainType> typeOptional = Arrays.stream(RestrainType.values()).filter(restrainType -> restrainType.attr == eleType && restrainType.getRestrainedAttr() == restrainedEleType).findFirst();
            if (typeOptional.isPresent()) {
                return true;
            }
            return false;
        }
    }
}
