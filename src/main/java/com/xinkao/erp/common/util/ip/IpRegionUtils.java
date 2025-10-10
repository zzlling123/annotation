package com.xinkao.erp.common.util.ip;

import java.io.File;
import javax.annotation.PostConstruct;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class IpRegionUtils {

    static Searcher searcher = null;

    
    @PostConstruct
    public void init() {

    	String tmpDir = System.getProperty("user.dir") + File.separator + "temp";
    	String dbPath = tmpDir + File.separator + "ip2region.xdb";
    	log.info("init ip region db path [{}]", dbPath);
    	File file = new File(dbPath);
    	FileUtil.writeFromStream(IpRegionUtils.class.getClassLoader().getResourceAsStream("data/ip2region.xdb"), file);
    	String absolutePath = file.getAbsolutePath();

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

    
    public static String getRegion(String ip) {
        try {

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
