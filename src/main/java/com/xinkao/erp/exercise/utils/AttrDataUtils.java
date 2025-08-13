package com.xinkao.erp.exercise.utils;

import com.xinkao.erp.exercise.utils.jx2d.AttrData;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

public class AttrDataUtils {

    /**
     * 找出 attr 相同的前提下，数值上最接近的 AttrData 对象
     * @param target 目标对象
     * @param dataList 数据列表
     * @return 最接近的对象（或 null）
     */
    public static AttrData findClosestWithSameAttr(AttrData target, List<AttrData> dataList) {
        if (dataList == null || dataList.isEmpty() || target == null) {
            return null;
        }

        // 过滤出 attr 相同的对象
        List<AttrData> filteredList = dataList.stream()
                .filter(data -> isAttrEqual(target.attr, data.attr))
                .collect(Collectors.toList());

        if (filteredList.isEmpty()) {
            return null; // 没有满足条件的对象
        }

        // 使用 Collections.min 找出距离最小的对象
        return Collections.min(filteredList, (a, b) -> {
            double distanceA = calculateDistance(target, a);
            double distanceB = calculateDistance(target, b);
            return Double.compare(distanceA, distanceB);
        });
    }

    /**
     * 判断两个 attr 是否相等（考虑嵌套 List<String>）
     */
    private static boolean isAttrEqual(List<List<String>> attr1, List<List<String>> attr2) {
        if (attr1 == null && attr2 == null) return true;
        if (attr1 == null || attr2 == null) return false;
        if (attr1.size() != attr2.size()) return false;

        for (int i = 0; i < attr1.size(); i++) {
            List<String> sub1 = attr1.get(i);
            List<String> sub2 = attr2.get(i);
            if (!sub1.equals(sub2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算两个 AttrData 对象之间的数值差异（使用欧几里得距离）
     */
    private static double calculateDistance(AttrData a, AttrData b) {
        if (a == null || b == null) return Double.MAX_VALUE;

        double dx = a.position.x - b.position.x;
        double dy = a.position.y - b.position.y;
        double dw = a.size.width - b.size.width;
        double dh = a.size.height - b.size.height;

        return Math.sqrt(dx * dx + dy * dy + dw * dw + dh * dh);
    }

    /**
     * 计算两个坐标值的相对误差，更精确
     *
     * @param a 坐标值 a
     * @param b 坐标值 b
     * @return 相对误差百分比
     */
    public static double calculateRelativePositionError(double a, double b) {
        if (a == 0 && b == 0) {
            return 0.0;
        }
        double max = Math.max(Math.abs(a), Math.abs(b));
        return Math.abs(a - b) / max;
    }

    /**
     * 计算两个坐标值的绝对差值 0.1误差
     *
     * @param a 坐标值 a
     * @param b 坐标值 b
     * @return 两者的绝对差值
     */
    public static double calculatePosition(double a, double b) {
        return Math.abs(a - b);
    }

    /**
     * 判断两个 AttrData 对象的 position 和 size 是否在 20% 的相对误差范围内（含等于）
     *
     * @param a 第一个对象
     * @param b 第二个对象
     * @return 如果所有数值字段的相对误差 ≤ 20%，返回 true；否则返回 false
     */
    public static boolean isNumericalClose(AttrData a, AttrData b) {
        if (a == null || b == null) {
            return false;
        }

        // 判断 position.x 是否在 20% 范围内
        if (!isWithinRelativeError(a.position.x, b.position.x, 0.2)) {
            return false;
        }

        // 判断 position.y 是否在 20% 范围内
        if (!isWithinRelativeError(a.position.y, b.position.y, 0.2)) {
            return false;
        }

        // 判断 size.width 是否在 20% 范围内
        if (!isWithinRelativeError(a.size.width, b.size.width, 0.2)) {
            return false;
        }

        // 判断 size.height 是否在 20% 范围内
        return isWithinRelativeError(a.size.height, b.size.height, 0.2);
    }

    /**
     * 判断两个浮点数之间的相对误差是否小于等于指定阈值
     *
     * @param val1 数值1
     * @param val2 数值2
     * @param threshold 相对误差阈值（如 0.05 表示 5%）
     * @return 是否在误差范围内
     */
    private static boolean isWithinRelativeError(double val1, double val2, double threshold) {
        if (val1 == val2) {
            return true; // 完全相等直接返回 true
        }

        double max = Math.max(Math.abs(val1), Math.abs(val2));
        double relativeError = Math.abs(val1 - val2) / max;

        return relativeError <= threshold;
    }

}
