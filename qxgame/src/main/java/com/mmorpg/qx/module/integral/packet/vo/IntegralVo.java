package com.mmorpg.qx.module.integral.packet.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * @author zhang peng
 * @description:
 * @since 11:56 2021/3/5
 */
public class IntegralVo {

    @Protobuf(description = "积分类型")
    private int type;
    @Protobuf(description = "积分数量")
    private int num;

    public static IntegralVo valueOf(int type, int num) {
        IntegralVo vo = new IntegralVo();
        vo.setType(type);
        vo.setNum(num);
        return vo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
