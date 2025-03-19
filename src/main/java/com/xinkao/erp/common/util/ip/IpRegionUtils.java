package com.xinkao.erp.common.util.ip;

import java.io.File;
import javax.annotation.PostConstruct;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * IP地区工具类
 **/
@Slf4j
@Component
public class IpRegionUtils {
	 // 2、使用全局的 vIndex 创建带 VectorIndex 缓存的查询对象。
    static Searcher searcher = null;

    /**
     * 初始化IP库
     */
    @PostConstruct
    public void init() {
    	// 因为jar无法读取文件,复制创建临时文件
    	String tmpDir = System.getProperty("user.dir") + File.separator + "temp";
    	String dbPath = tmpDir + File.separator + "ip2region.xdb";
    	log.info("init ip region db path [{}]", dbPath);
    	File file = new File(dbPath);
    	FileUtil.writeFromStream(IpRegionUtils.class.getClassLoader().getResourceAsStream("data/ip2region.xdb"), file);
    	String absolutePath = file.getAbsolutePath();
    	// 1、从 dbPath 中预先加载 VectorIndex 缓存，并且把这个得到的数据作为全局变量，后续反复使用。
    	byte[] vIndex;
    	 try {
             vIndex = Searcher.loadVectorIndexFromFile(absolutePath);
         } catch (Exception e) {
             System.out.printf("failed to load vector index from `%s`: %s\n", absolutePath, e);
             return;
         }
        try {
            searcher = Searcher.newWithVectorIndex(absolutePath, vIndex);
            log.info("bean [{}]", searcher);
        } catch (Exception e) {
            log.error("init ip region error:{}", e);
        }
    }

    /**
     * 解析IP
     *
     * @param ip
     * @return
     */
    public static String getRegion(String ip) {
        try {
            // db
            if (searcher == null) {
                log.error("DbSearcher is null");
                return null;
            }
            long startTime = System.currentTimeMillis();
            String region = searcher.search(ip);
            long endTime = System.currentTimeMillis();
            log.debug("region use time[{}] result[{}]", endTime - startTime, region);
            return region;

        } catch (Exception e) {
            log.error("error:{}", e);
        }
        return null;
    }
}
