package com.xinkao.erp.exercise.utils.jx2d;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AttrData {
    public List<List<String>> attr;
    public Position position;
    public Size size;
    public Double canvasW;
    public Double canvasH;

    public AttrData(JSONObject obj) {
        this.attr = new ArrayList<>();
        JSONArray attrArray = obj.getJSONArray("attr");
        if (attrArray == null) {
            return;
        }
        //判断attrArray是否存在下级数组

        List<String> innerList = new ArrayList<>();
        if (attrArray.size()==1){
            innerList.add(attrArray.getString(0));
            this.attr.add(innerList);
        }else if (attrArray.size()>1){
            Object firstElement = attrArray.get(0);
            if(firstElement instanceof JSONObject){
                for (int i = 0; i < attrArray.size(); i++) {
                    JSONArray subArray = attrArray.getJSONArray(i);
                    for (int j = 0; j < subArray.size(); j++) {
                        innerList.add(subArray.getString(j));
                    }
                    this.attr.add(innerList);
                }
            }else if(firstElement instanceof String){
                for (int i = 0; i < attrArray.size(); i++) {
                    innerList.add(attrArray.getString(i));
                }
                this.attr.add(innerList);
            }
        }
        JSONObject positionObj = obj.getJSONObject("position");
        this.position = new Position(positionObj);

        JSONObject sizeObj = obj.getJSONObject("size");
        this.size = new Size(sizeObj);

        this.canvasW = obj.getDouble("canvasW");
        this.canvasH = obj.getDouble("canvasH");
    }
}
