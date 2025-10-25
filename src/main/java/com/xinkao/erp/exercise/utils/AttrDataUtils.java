package com.xinkao.erp.exercise.utils;

import com.xinkao.erp.exercise.utils.jx2d.AttrData;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

public class AttrDataUtils {
    public static AttrData findClosestWithSameAttr(AttrData target, List<AttrData> dataList) {
        if (dataList == null || dataList.isEmpty() || target == null) {
            return null;
        }

        List<AttrData> filteredList = dataList.stream()
                .filter(data -> isAttrEqual(target.attr, data.attr))
                .collect(Collectors.toList());

        if (filteredList.isEmpty()) {
            return null;
        }

        return Collections.min(filteredList, (a, b) -> {
            double distanceA = calculateDistance(target, a);
            double distanceB = calculateDistance(target, b);
            return Double.compare(distanceA, distanceB);
        });
    }

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

    private static double calculateDistance(AttrData a, AttrData b) {
        if (a == null || b == null) return Double.MAX_VALUE;

        double dx = a.position.x - b.position.x;
        double dy = a.position.y - b.position.y;
        double dw = a.size.width - b.size.width;
        double dh = a.size.height - b.size.height;

        return Math.sqrt(dx * dx + dy * dy + dw * dw + dh * dh);
    }

    public static double calculateRelativePositionError(double a, double b) {
        if (a == 0 && b == 0) {
            return 0.0;
        }
        double max = Math.max(Math.abs(a), Math.abs(b));
        return Math.abs(a - b) / max;
    }

    public static double calculatePosition(double a, double b) {
        return Math.abs(a - b);
    }

    public static boolean isNumericalClose(AttrData a, AttrData b) {
        if (a == null || b == null) {
            return false;
        }

        if (!isWithinRelativeError(a.position.x/a.canvasW*b.canvasW, b.position.x, 0.2)) {
            return false;
        }

        if (!isWithinRelativeError(a.position.y/a.canvasH*b.canvasH, b.position.y, 0.2)) {
            return false;
        }

        if (!isWithinRelativeError(a.size.width/a.canvasW*b.canvasW, b.size.width, 0.2)) {
            return false;
        }

        return isWithinRelativeError(a.size.height/a.canvasH*b.canvasH, b.size.height, 0.2);
    }

    private static boolean isWithinRelativeError(double val1, double val2, double threshold) {
        if (val1 == val2) {
            return true;
        }
        double max = Math.max(Math.abs(val1), Math.abs(val2));
        double relativeError = Math.abs(val1 - val2) / max;
        return relativeError <= threshold;
    }

}
