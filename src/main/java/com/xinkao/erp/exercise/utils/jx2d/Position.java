package com.xinkao.erp.exercise.utils.jx2d;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Position {
    public double x;
    public double y;
    public Position(JSONObject obj) {
        this.x = obj.getDoubleValue("x");
        this.y = obj.getDoubleValue("y");
    }
}
