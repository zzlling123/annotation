package com.xinkao.erp.exercise.utils.jx2d;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Size {
    public double width;
    public double height;

    public Size(JSONObject obj) {
        this.width = obj.getDoubleValue("width");
        this.height = obj.getDoubleValue("height");
    }
}
