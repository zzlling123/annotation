package com.xinkao.erp.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 考试表
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
@Getter
@Setter
public class Cuboid {
    // 中心点坐标
    private double centerX, centerY, centerZ;
    // 长宽高
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

    // 计算体积
    public double getVolume() {
        return length * width * height;
    }

    // 边界坐标计算方法
    public double getMinX() { return centerX - length / 2; }
    public double getMaxX() { return centerX + length / 2; }
    public double getMinY() { return centerY - width / 2; }
    public double getMaxY() { return centerY + width / 2; }
    public double getMinZ() { return centerZ - height / 2; }
    public double getMaxZ() { return centerZ + height / 2; }
}