package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class Cuboid {

    private double centerX, centerY, centerZ;

    private double length, width, height;

    public Cuboid(double centerX, double centerY, double centerZ,
                  double length, double width, double height) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public double getVolume() {
        return length * width * height;
    }

    public double getMinX() { return centerX - length / 2; }
    public double getMaxX() { return centerX + length / 2; }
    public double getMinY() { return centerY - width / 2; }
    public double getMaxY() { return centerY + width / 2; }
    public double getMinZ() { return centerZ - height / 2; }
    public double getMaxZ() { return centerZ + height / 2; }
}