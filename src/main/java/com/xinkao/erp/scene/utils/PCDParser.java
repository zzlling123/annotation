package com.xinkao.erp.scene.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

//public class PCDImageExtractor {
//
//    // PCD文件头部信息
//    private static class PCDHeader {
//        int width;
//        int height;
//        int pointCount;
//        int rgbOffset;
//        boolean isBinary;
//        int rowSize;
//    }
//
//    /**
//     * 解析PCD文件并保存关键帧图像
//     * @param pcdFile 输入PCD文件
//     * @param outputDir 输出目录
//     * @param isKeyFrame 是否关键帧的标记
//     * @throws IOException 文件处理异常
//     */
//    public static void extractImagesFromPCD(File pcdFile, File outputDir, boolean isKeyFrame) throws IOException {
//        if (!isKeyFrame) {
//            return; // 只处理关键帧
//        }
//
//        PCDHeader header = parsePCDHeader(pcdFile);
//        BufferedImage image = createImageFromPCD(pcdFile, header);
//
//        // 确保输出目录存在
//        if (!outputDir.exists()) {
//            outputDir.mkdirs();
//        }
//
//        // 生成输出文件名
//        String outputName = pcdFile.getName().replace(".pcd", ".png");
//        File outputFile = new File(outputDir, outputName);
//
//        // 保存图像
//        ImageIO.write(image, "PNG", outputFile);
//    }
//
//    /**
//     * 解析PCD文件头部信息
//     */
//    private static PCDHeader parsePCDHeader(File pcdFile) throws IOException {
//        PCDHeader header = new PCDHeader();
//        try (BufferedReader reader = new BufferedReader(new FileReader(pcdFile))) {
//            String line;
//            List<String> fields = new ArrayList<>();
//            int fieldCount = 0;
//
//            while ((line = reader.readLine()) != null) {
//                line = line.trim();
//                if (line.startsWith("#") || line.isEmpty()) {
//                    continue;
//                }
//
//                if (line.startsWith("VERSION")) {
//                    // 跳过版本行
//                } else if (line.startsWith("FIELDS")) {
//                    String[] parts = line.split("\\s+");
//                    for (int i = 1; i < parts.length; i++) {
//                        fields.add(parts[i]);
//                        if (parts[i].equals("rgb")) {
//                            header.rgbOffset = i - 1; // 记录RGB字段偏移
//                        }
//                    }
//                    fieldCount = fields.size();
//                } else if (line.startsWith("SIZE")) {
//                    // 解析字段大小
//                } else if (line.startsWith("TYPE")) {
//                    // 解析字段类型
//                } else if (line.startsWith("COUNT")) {
//                    // 解析字段计数
//                } else if (line.startsWith("WIDTH")) {
//                    header.width = Integer.parseInt(line.split("\\s+"));
//                } else if (line.startsWith("HEIGHT")) {
//                    header.height = Integer.parseInt(line.split("\\s+"));
//                    header.pointCount = header.width * header.height;
//                } else if (line.startsWith("VIEWPOINT")) {
//                    // 跳过视点信息
//                } else if (line.startsWith("POINTS")) {
//                    header.pointCount = Integer.parseInt(line.split("\\s+"));
//                } else if (line.startsWith("DATA")) {
//                    header.isBinary = line.split("\\s+").equalsIgnoreCase("binary");
//                    break; // 头部结束
//                }
//            }
//
//            // 计算每行数据大小（字节）
//            header.rowSize = fieldCount * 4; // 假设每个字段4字节
//        }
//        return header;
//    }
//
//    /**
//     * 从PCD文件创建图像
//     */
//    private static BufferedImage createImageFromPCD(File pcdFile, PCDHeader header) throws IOException {
//        BufferedImage image = new BufferedImage(header.width, header.height, BufferedImage.TYPE_INT_RGB);
//
//        try (RandomAccessFile raf = new RandomAccessFile(pcdFile, "r")) {
//            // 跳过头部
//            String line;
//            while ((line = raf.readLine()) != null) {
//                if (line.trim().equals("DATA ascii") || line.trim().equals("DATA binary")) {
//                    break;
//                }
//            }
//
//            if (header.isBinary) {
//                processBinaryData(raf, image, header);
//            } else {
//                processAsciiData(raf, image, header);
//            }
//        }
//
//        return image;
//    }
//
//    /**
//     * 处理二进制数据
//     */
//    private static void processBinaryData(RandomAccessFile raf, BufferedImage image, PCDHeader header) throws IOException {
//        byte[] buffer = new byte[header.rowSize];
//        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
//        byteBuffer.order(ByteOrder.LITTLE_ENDIAN); // PCD通常使用小端序
//
//        for (int y = 0; y < header.height; y++) {
//            for (int x = 0; x < header.width; x++) {
//                raf.readFully(buffer);
//
//                // 提取RGB值（假设rgb是float类型）
//                float rgbFloat = byteBuffer.getFloat(header.rgbOffset * 4);
//                int rgbInt = Float.floatToIntBits(rgbFloat);
//
//                // 转换为标准RGB格式
//                int r = (rgbInt >> 16) & 0xFF;
//                int g = (rgbInt >> 8) & 0xFF;
//                int b = rgbInt & 0xFF;
//                int rgb = (r << 16) | (g << 8) | b;
//
//                image.setRGB(x, y, rgb);
//                byteBuffer.rewind(); // 重置缓冲区位置
//            }
//        }
//    }
//
//    /**
//     * 处理ASCII数据
//     */
//    private static void processAsciiData(RandomAccessFile raf, BufferedImage image, PCDHeader header) throws IOException {
//        raf.seek(0); // 重新从头开始读取
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(raf.getFD())));
//
//        // 跳过头部
//        String line;
//        while ((line = reader.readLine()) != null) {
//            if (line.trim().equals("DATA ascii")) {
//                break;
//            }
//        }
//
//        // 读取数据
//        for (int y = 0; y < header.height; y++) {
//            for (int x = 0; x < header.width; x++) {
//                line = reader.readLine();
//                if (line == null) break;
//
//                String[] parts = line.trim().split("\\s+");
//                if (parts.length <= header.rgbOffset) continue;
//
//                // 解析RGB值
//                float rgbFloat = Float.parseFloat(parts[header.rgbOffset]);
//                int rgbInt = Float.floatToIntBits(rgbFloat);
//                int r = (rgbInt >> 16) & 0xFF;
//                int g = (rgbInt >> 8) & 0xFF;
//                int b = rgbInt & 0xFF;
//                int rgb = (r << 16) | (g << 8) | b;
//
//                image.setRGB(x, y, rgb);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        File inputDir = new File("path/to/pcd/files");
//        File outputDir = new File("path/to/output/images");
//
//        // 假设这些是关键帧文件
//        String[] keyFrames = {"frame_001.pcd", "frame_005.pcd", "frame_010.pcd"};
//
//        for (String fileName : keyFrames) {
//            File pcdFile = new File(inputDir, fileName);
//            try {
//                extractImagesFromPCD(pcdFile, outputDir, true);
//                System.out.println("Processed keyframe: " + fileName);
//            } catch (IOException e) {
//                System.err.println("Error processing " + fileName + ": " + e.getMessage());
//            }
//        }
//    }
//}
