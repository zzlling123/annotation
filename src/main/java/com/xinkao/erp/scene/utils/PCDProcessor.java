package com.xinkao.erp.scene.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCDProcessor {

    public Map<String, String> parseHeader(BufferedReader reader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.trim().isEmpty()) continue;
            if (line.startsWith("DATA")) break; // 头部结束标志
            String[] parts = line.split("\\s+", 2);
            header.put(parts[0], parts.length > 1 ? parts[1] : "");
        }
        return header;
    }


    List<float[]> parseAsciiData(BufferedReader reader, int pointCount) throws IOException {
        List<float[]> points = new ArrayList<>(pointCount);
        String line;
        while ((line = reader.readLine()) != null && points.size() < pointCount) {
            String[] tokens = line.split("\\s+");
            float[] point = new float[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                point[i] = Float.parseFloat(tokens[i]);
            }
            points.add(point);
        }
        return points;
    }

    List<float[]> parseBinaryData(InputStream is, Map<String, String> header) throws IOException {
        int floatSize = header.get("SIZE").split(" ").length;
        DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
        int totalPoints = Integer.parseInt(header.get("POINTS"));
        List<float[]> points = new ArrayList<>(totalPoints);

        for (int i = 0; i < totalPoints; i++) {
            float x = dis.readFloat();
            float y = dis.readFloat();
            float z = dis.readFloat();
            points.add(new float[]{x, y, z});
        }
        return points;
    }
    public List<List<float[]>> splitToFrames(List<float[]> allPoints, int frameSize) {
        List<List<float[]>> frames = new ArrayList<>();
        for (int i = 0; i < allPoints.size(); i += frameSize) {
            int end = Math.min(i + frameSize, allPoints.size());
            frames.add(allPoints.subList(i, end));
        }
        return frames;
    }


//    // 使用Three.js加载点云数据（需引入Three.js依赖）‌:ml-citation{ref="8" data="citationList"}
//    private PCDLoader pcdLoader = new PCDLoader();
//
//    /**
//     * 解析PCD并生成关键帧图像
//     * @param pcdFile PCD文件输入流
//     * @param dbConfig 数据库配置
//     * @return 图像URL集合
//     */
//    public List<String> processPCD(InputStream pcdFile, DBConfig dbConfig) {
//        // 1. 加载点云数据
//        Points pointCloud = pcdLoader.load(pcdFile); // 获取XYZ坐标及强度值‌:ml-citation{ref="7" data="citationList"}
//
//        // 2. 关键帧判定（基于点云密度变化）
//        List<Integer> keyFrames = detectKeyFrames(pointCloud);
//
//        // 3. 生成可视化图片
//        List<String> urls = new ArrayList<>();
//        for (int frameIndex : keyFrames) {
//            // 创建深度图渲染器
//            DepthMapRenderer renderer = new DepthMapRenderer(800, 600);
//            // 提取当前帧点云子集（示例截取前1000个点）
//            Points frameData = extractFrameData(pointCloud, frameIndex, 1000);
//            // 生成PNG图像
//            BufferedImage image = renderer.render(frameData);
//            // 存储并获取URL
//            String url = saveToStorage(image, frameIndex, dbConfig);
//            urls.add(url);
//        }
//        return urls;
//    }
//
//    // 关键帧检测逻辑（基于点密度变化阈值）
//    private List<Integer> detectKeyFrames(Points cloud) {
//        List<Integer> frames = new ArrayList<>();
//        int totalPoints = cloud.getPointsCount();
//        double prevDensity = calculateDensity(cloud, 0, 100);
//        for (int i = 100; i < totalPoints; i += 100) {
//            double currentDensity = calculateDensity(cloud, i, i+100);
//            if (Math.abs(currentDensity - prevDensity) > 0.15) { // 密度变化超15%判定为关键帧‌:ml-citation{ref="2" data="citationList"}
//                frames.add(i);
//                prevDensity = currentDensity;
//            }
//        }
//        return frames;
//    }
}

