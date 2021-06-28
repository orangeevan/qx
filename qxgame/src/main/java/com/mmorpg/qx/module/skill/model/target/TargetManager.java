package com.mmorpg.qx.module.skill.model.target;

import com.mmorpg.qx.module.condition.Result;
import com.mmorpg.qx.module.object.gameobject.AbstractCreature;
import com.mmorpg.qx.module.skill.model.Skill;
import com.mmorpg.qx.module.skill.model.target.chooser.AbstractTargetChooser;
import com.mmorpg.qx.module.skill.model.target.chooser.ITargetChooser;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 技能选择管理器
 *
 * @author wang ke
 * @since v1.0 2019年3月2日
 */
@Component
public final class TargetManager {

    /**
     * 当前已经注册的选择器
     */
    private Map<TargetType, ITargetChooser> targetChooserMap = new HashMap<>();

    private static TargetManager instance;

    @PostConstruct
    private void init() {
        instance = this;
        Arrays.stream(TargetType.values()).forEach(targetType -> {
            AbstractTargetChooser targetChooser = targetType.createTargetChooser();
            this.registerTargetChooser(targetChooser);
        });

    }

    public void registerTargetChooser(ITargetChooser chooser) {
        targetChooserMap.put(chooser.getTargetType(), chooser);
    }

    public Result judgeTarget(Skill skill, Target target) {
        // 根据skill类型获取到target对象
//        List<TargetType> targetTypes = skill.getResource().getTargetType();
//        for (TargetType targetType : targetTypes) {
//            ITargetChooser targetChooser = targetChooserMap.get(targetType);
//            // 根据tagetchooser装备skill的攻击目标
//            if (targetChooser.verifyTarget(skill.getSkillCaster(), target).isFailure()) {
//                return Result.FAILURE;
//            }
//        }
        Map<TargetType, String> targets = skill.getResource().getTargets();
        for (Map.Entry<TargetType, String> entry : targets.entrySet()) {
            ITargetChooser targetChooser = targetChooserMap.get(entry.getKey());
            // 根据tagetchooser装备skill的攻击目标
            if (StringUtils.isNotEmpty(entry.getValue()) && targetChooser.verifyTarget(skill.getSkillCaster(), target, entry.getValue()).isFailure()) {
                return Result.FAILURE;
            } else if (targetChooser.verifyTarget(skill.getSkillCaster(), target).isFailure()) {
                return Result.FAILURE;
            }
        }

        return Result.SUCCESS;

    }

    public static TargetManager getInstance() {
        return instance;
    }

    public ITargetChooser getTargetChooser(TargetType targetType) {
        return targetChooserMap.get(targetType);
    }

    public Target chooseTarget(List<TargetType> targetTypeList, AbstractCreature caster) {
        if (CollectionUtils.isEmpty(targetTypeList) || caster == null) {
            return null;
        }
        List<Target> targets = new ArrayList<>();
        for (TargetType targetType : targetTypeList) {
            ITargetChooser targetChooser = TargetManager.getInstance().getTargetChooser(targetType);
            Target target = targetChooser.chooseTarget(caster);
            if (Objects.isNull(target)) {
                return null;
            }
            targets.add(target);
        }
        //从多个目标取交集
        Target target = targets.remove(0);
        if (CollectionUtils.isEmpty(targets)) {
            return target;
        }
        if (target.getGridId() != 0) {
            for (Target t : targets) {
                if (t.getGridId() != target.getGridId()) {
                    target.setGridId(-1);
                    break;
                }
            }
        }
        Iterator<Long> iterator = target.getTargetIds().iterator();
        while (iterator.hasNext()) {
            long tid = iterator.next();
            for (Target t : targets) {
                if (t == null || CollectionUtils.isEmpty(t.getTargetIds()) || !t.getTargetIds().contains(tid)) {
                    iterator.remove();
                }
            }
        }
        return target;
    }
}
