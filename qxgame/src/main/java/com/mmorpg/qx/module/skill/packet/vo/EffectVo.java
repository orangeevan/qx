package com.mmorpg.qx.module.skill.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.mmorpg.qx.common.GameUtil;
import com.mmorpg.qx.module.skill.model.effect.Effect;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang ke
 * @description: buff效果
 * @since 15:38 2020-09-11
 */
public class EffectVo {

    @Protobuf(description = "效果配置id")
    private int effectId;

    /**
     * 结束时间
     */
    @Protobuf(description = "结束回合")
    private int endRound;

    /**
     * 下一次执行的时间
     */
    @Protobuf(description = "下一次执行回合")
    private int periodRound;

    /**
     * 开始执行时间
     */
    @Protobuf(description = "开始回合")
    private int startRound;

    @Protobuf(description = "生效次数，有些效果有次数限制")
    private int currentTimes;

    @Protobuf(description = "效果参数，比如召唤魔物娘，对应召唤格子,其他类型里面放的效果值，比如扣血或者回血数值")
    private List<GameUtil.IntegerVo> effectParam;

    public static EffectVo valueOf(Effect effect) {
        EffectVo vo = new EffectVo();
        vo.setEffectId(effect.getEffectResourceId());
        vo.setCurrentTimes(effect.getCurrentTimes());
        vo.setEndRound(effect.getEndRound());
        vo.setStartRound(effect.getStartRound());
        vo.setPeriodRound(effect.getPeriodRound());
        ArrayList<GameUtil.IntegerVo> resultParams = new ArrayList<>();
        if (CollectionUtils.isEmpty(effect.getEffectParam())) {
            resultParams.add(GameUtil.IntegerVo.valueOf(effect.getValue()));
            vo.setEffectParam(resultParams);
        } else {
            effect.getEffectParam().forEach(id -> resultParams.add(GameUtil.IntegerVo.valueOf(id)));
            vo.setEffectParam(resultParams);
        }
        return vo;
    }

    public int getEffectId() {
        return effectId;
    }

    public void setEffectId(int effectId) {
        this.effectId = effectId;
    }

    public int getEndRound() {
        return endRound;
    }

    public void setEndRound(int endRound) {
        this.endRound = endRound;
    }

    public int getPeriodRound() {
        return periodRound;
    }

    public void setPeriodRound(int periodRound) {
        this.periodRound = periodRound;
    }

    public int getStartRound() {
        return startRound;
    }

    public void setStartRound(int startRound) {
        this.startRound = startRound;
    }

    public int getCurrentTimes() {
        return currentTimes;
    }

    public void setCurrentTimes(int currentTimes) {
        this.currentTimes = currentTimes;
    }

    public List<GameUtil.IntegerVo> getEffectParam() {
        return effectParam;
    }

    public void setEffectParam(List<GameUtil.IntegerVo> effectParam) {
        this.effectParam = effectParam;
    }
}
