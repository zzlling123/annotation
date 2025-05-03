package com.xinkao.erp.scene.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.scene.entity.Scene;
import com.xinkao.erp.scene.entity.ScenePcd;
import com.xinkao.erp.scene.entity.ScenePcdImg;
import com.xinkao.erp.scene.param.ImagePathParam;
import com.xinkao.erp.scene.query.SceneQuery;
import com.xinkao.erp.scene.service.ScenePcdImgService;
import com.xinkao.erp.scene.service.ScenePcdService;
import com.xinkao.erp.scene.service.SceneService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *  场景管理 前端控制器
 *
 * @author zzl
 * @since 2025-04-12 23:11:56
 */
@RestController
@RequestMapping("/scene")
public class SceneController {

    @Value("${scene.scan.imgPath}")
    String imgPath;

    @Value("${scene.scan.scanPath}")
    String scanPath;

    @Value("${scene.scan.txtPath}")
    String txtPath ;

    @Value("${scene.scan.pcdPath}")
    String pcdPath;

    @Value("${ipurl.url}")
    private String ipurl;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private ScenePcdService scenePcdService;

    @Autowired
    private ScenePcdImgService scenePcdImgService;

    @Resource
    private RedisUtil redisUtil;


    /**
     * 查询所有场景信息不分页
     */
    @PrimaryDataSource
    @ApiOperation("查询所有场景信息不分页")
    @RequestMapping("/getAll")
    public BaseResponse<List<Scene>> getAll() {
        //查询isDel=0的场景
        LambdaQueryWrapper<Scene> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Scene::getIsDel, 0);
        wrapper.orderByDesc(Scene::getCreateTime);
        wrapper.select(Scene::getId, Scene::getSceneName, Scene::getScenePath, Scene::getScenePic, Scene::getSceneDescription, Scene::getSceneFrameNum, Scene::getSceneFrameNumPics);
        List<Scene> scenes = sceneService.list(wrapper);
        //通过场景id查询场景pcd文件
        for (Scene scene : scenes) {
            List<ScenePcd> scenePcds = scenePcdService.lambdaQuery()
                    .eq(ScenePcd::getSceneId, scene.getId())
                    .list();
            scene.setScenePcdFileList(scenePcds);
            //通过pcdid查询场景pcd图片
            for (ScenePcd scenePcd : scenePcds) {
                List<ScenePcdImg> scenePcdImgs = scenePcdImgService.lambdaQuery()
                        .eq(ScenePcdImg::getPcdId, scenePcd.getId())
                        .list();
                scenePcd.setScenePcdImgList(scenePcdImgs);
            }
        }
        return BaseResponse.ok(scenes);
    }

    @PostMapping("/image")
    public BaseResponse<String> serveImage(@RequestBody ImagePathParam imagePath) throws IOException {
        String[] strings = imagePath.getImagePath().split("/");
        return BaseResponse.ok(ipurl+"/annotation/image/"+strings[strings.length-3]+"/"+strings[strings.length-2]+"/"+strings[strings.length-1]);
    }

    @PostMapping("/pcd")
    public BaseResponse<String> servePcd(@RequestBody ImagePathParam imagePath) throws IOException {
        String[] strings = imagePath.getImagePath().split("/");
        return BaseResponse.ok(ipurl+"/annotation/pcd/"+strings[strings.length-3]+"/"+strings[strings.length-1]);
    }

    @PostMapping("/cres")
    public BaseResponse<String> serveCres(@RequestBody ImagePathParam imagePath) throws IOException {
        //将字符串的\换成/
        imagePath.setImagePath(imagePath.getImagePath().replace("\\", "/"));
        String[] strings = imagePath.getImagePath().split("/");
        return BaseResponse.ok(ipurl+"/annotation/cres/"+strings[strings.length-1]);
    }

    /**
     * 自动扫描磁盘路径下的场景pcd文件，并自动加入到数据库中
     */
    @PrimaryDataSource
    @ApiOperation("自动扫描磁盘路径下的场景pcd文件，并自动加入到数据库中")
    @PostMapping("/scan")
    public BaseResponse<?> scan() {
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        String loginUserId = loginUserAll.getUser().getUsername();
        //String loginUserId = "1";
        // 扫描磁盘路径下的场景E://mark_view文件夹下pcd文件，并读取所有pcd文件
        System.out.println("正在扫描磁盘"+scanPath);
        // 判断磁盘路径是否存在
        if (!new File(scanPath).exists()) {
            return BaseResponse.fail("磁盘路径不存在");
        }
        //读取文件夹下的txt的文档
        System.out.println("正在读取文件夹下的txt的文档");
        File[] files = new File(scanPath+"/txt").listFiles();
        //判断此路径下有几个文件夹并获取它的文件夹名
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("文件夹名：" + file.getName());
                if (sceneService.getOne(Wrappers.lambdaQuery(Scene.class).eq(Scene::getSceneName, file.getName())) != null) {
                    System.out.println("文件夹名：" + file.getName() + "的场景已存在");
                    continue;
                }
                Scene scene = new Scene();
                scene.setSceneName(file.getName());
                scene.setScenePath(file.getAbsolutePath());
                scene.setScenePic("");
                scene.setSceneDescription(file.getName());
                scene.setSceneFrameNum(10);
                scene.setSceneFrameNumPics("");
                scene.setCreateBy(loginUserId);
                scene.setUpdateBy(loginUserId);
                //保存到数据库中并返回主键id
                System.out.println("正在保存到数据库中");
                sceneService.save1(scene);
                System.out.println("正在获取主键id");
                Scene scene1 = sceneService.getOne(Wrappers.lambdaQuery(Scene.class).eq(Scene::getSceneName, file.getName()));
                Integer sceneId = scene1.getId();
                //获取当前文件夹下的所有txt文件
                System.out.println("正在获取当前文件夹下的所有txt文件");
                File[] txtFiles = file.listFiles();
                for (File file_txt : txtFiles) {
                    if (file_txt.getName().endsWith(".txt")) {
                        try {
                            Path path = Paths.get(file_txt.getPath());
                            String content = new String(Files.readAllBytes(path));
                            System.out.println("正在读取txt文件"+content);
                            //根据操作系统 判断换行符
                            String[] split = content.split("\\n");
                            System.out.println("正在读取txt文件,文件数组个数"+split.length);
                            //从split字符串数组中随机取10个下标连续的字符串
                            int randomIndex = (int) (Math.random() * (split.length-10));
                            int endNum = randomIndex + 10;
                            if (randomIndex < 0){
                                randomIndex = 0;
                            }
                            if (randomIndex+10 > split.length){
                                endNum = split.length;
                            } else if (randomIndex+10 <= split.length){
                                endNum = 10;
                            }
                            System.out.println(randomIndex);
                            for (int i = 0; i < endNum; i++) {
                                String img_pcd = split[randomIndex+i];
                                String[] img_pcds = img_pcd.split(" ");
                                ScenePcd scenePcd = new ScenePcd();
                                scenePcd.setSceneId(sceneId);
                                scenePcd.setPcdPath(pcdPath+scene.getSceneName()+"/"+file_txt.getName().split("\\.txt")[0] + "/"+ img_pcds[1]+".pcd");
                                scenePcd.setCreateBy(loginUserId);
                                scenePcd.setUpdateBy(loginUserId);
                                scenePcdService.save(scenePcd);
                                ScenePcd scenePcd1 = scenePcdService.getOne(Wrappers.lambdaQuery(ScenePcd.class)
                                        .eq(ScenePcd::getPcdPath, pcdPath+scene.getSceneName()+"/"+file_txt.getName().split("\\.txt")[0] + "/"+ img_pcds[1]+".pcd")
                                        .eq(ScenePcd::getSceneId, scene1.getId()));
                                Integer scenePcdId = scenePcd1.getId();
                                for (File file_txt_img : txtFiles) {
                                    if (file_txt_img.getName().endsWith(".txt")){
                                        Path path_txt_img = Paths.get(file_txt_img.getPath());
                                        String content_txt_img = new String(Files.readAllBytes(path_txt_img));
                                        String[] split_txt_img = content_txt_img.split("\\n");
                                        for (String s : split_txt_img) {
                                            if (s.contains(img_pcds[1])){
                                                ScenePcdImg scenePcdImg = new ScenePcdImg();
                                                scenePcdImg.setPcdId(scenePcdId);
                                                scenePcdImg.setImgPath(imgPath+scene.getSceneName()+"/"+file_txt.getName().split("\\.txt")[0] + "/"+s.split(" ")[0]);
                                                scenePcdImg.setImgDirection("");
                                                scenePcdImg.setCreateBy(loginUserId);
                                                scenePcdImg.setUpdateBy(loginUserId);
                                                scenePcdImgService.save(scenePcdImg);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                }
            }
        }
        return BaseResponse.ok("扫描成功");
    }

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询场景列表")
    public BaseResponse<Page<Scene>> page(@Valid @RequestBody SceneQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<Scene> voPage = sceneService.page(query, pageable);
        voPage.getRecords().forEach(scene -> {
            List<ScenePcd> scenePcds = scenePcdService.lambdaQuery()
                    .eq(ScenePcd::getSceneId, scene.getId())
                    .list();
            scene.setScenePcdFileList(scenePcds);
            //通过pcdid查询场景pcd图片
            for (ScenePcd scenePcd : scenePcds) {
                List<ScenePcdImg> scenePcdImgs = scenePcdImgService.lambdaQuery()
                        .eq(ScenePcdImg::getPcdId, scenePcd.getId())
                        .list();
                scenePcd.setScenePcdImgList(scenePcdImgs);
            }
        });
        return BaseResponse.ok(voPage);
    }

//    /**
//     * 添加场景记录表
//     *
//     * @param scene 场景记录表
//     * @return 操作结果
//     */
//    @PrimaryDataSource
//    @PostMapping("/save")
//    @ApiOperation("添加场景记录表")
//    @Log(content = "添加场景记录表", operationType = OperationType.INSERT, isSaveRequestData = false)
//    public BaseResponse<?> save(@Valid @RequestBody Scene scene) {
//        return sceneService.save1(scene);
//    }


//    private static final String UPLOAD_DIR = "e://mark_view"; // 修改为你的上传目录路径
//    private static final String IMAGE_DIR = UPLOAD_DIR + "/images"; // 图片存储目录路径
//    /**
//     * 添加场景记录表，里面有一个场景名称，场景地址（需要文件上传），场景图片（需要文件上传），场景描述，场景帧数，场景帧对应的图片集合
//     */
//    @PrimaryDataSource
//    @PostMapping("/save")
//    @ApiOperation("添加场景记录表")
//    public BaseResponse<?>  save(Scene scene) throws IOException {
//        MultipartFile sceneFile = scene.getSceneFile(); // 获取文件对象
//        MultipartFile scenePicFile = scene.getScenePicFile();
//        if (!sceneFile.isEmpty()) {
//            String fileName = UUID.randomUUID().toString() + "_" + sceneFile.getOriginalFilename(); // 生成唯一文件名
//            Path path = Paths.get(IMAGE_DIR + "/" + fileName); // 构建文件路径对象
//            if (!Files.exists(path.getParent())) {
//                Files.createDirectories(path.getParent()); // 创建父目录
//            }
//            try {
//                Files.copy(sceneFile.getInputStream(), path); // 保存文件到服务器路径下
//                scene.setScenePath(path.toString()); // 设置图片URL或路径
//                System.out.println("场景文件保存成功: " + path);
//            } catch (IOException e) {
//                System.err.println("场景文件保存失败: " + e.getMessage());
//                throw e;
//            }
//        } else {
//            scene.setScenePath(null); // 处理空文件情况
//            System.out.println("场景文件为空");
//        }
//
//        if (!scenePicFile.isEmpty()) {
//            String fileName = UUID.randomUUID().toString() + "_" + scenePicFile.getOriginalFilename();
//            Path path = Paths.get(IMAGE_DIR + "/" + fileName);
//            if (!Files.exists(path.getParent())) {
//                Files.createDirectories(path.getParent());
//            }
//            try {
//                Files.copy(scenePicFile.getInputStream(), path);
//                scene.setScenePic(path.toString());
//                System.out.println("场景图片保存成功: " + path);
//            } catch (IOException e) {
//                System.err.println("场景图片保存失败: " + e.getMessage());
//                throw e;
//            }
//        } else {
//            scene.setScenePic(null);
//            System.out.println("场景图片为空");
//        }
//
//        return sceneService.save1(scene);// 保存产品到数据库，不包括图片文件本身，只保存图片的URL或路径。
//    }
//
////    /**
////     * 可以解析psd文件，并解析出关键帧对应的图片集合，并保存到数据库中，并返回图片集合的url或路径
////     */
////    @PostMapping("/parsePsd")
////    public BaseResponse<?> parsePsd(@RequestParam("psdFile") MultipartFile psdFile) {
////        List<String> imageUrls = PSDParser.parsePSDAndSaveImages(psdFile.getOriginalFilename(), UPLOAD_DIR);
////        return BaseResponse.ok(imageUrls);
////    }
//
//    /**
//     * 编辑场景记录表信息
//     *
//     * @param scene 场景记录表
//     * @return 操作结果
//     */
//    @PostMapping("/update")
//    @ApiOperation("更新场景记录表")
//    @Log(content = "更新场景记录表", operationType = OperationType.UPDATE, isSaveRequestData = false)
//    public BaseResponse<?> update(@Valid @RequestBody Scene scene) {
//        if (scene.getId() == null) {
//            return BaseResponse.fail("场景记录ID不能为空");
//        }
//        return sceneService.update(scene);
//    }
//
//    /**
//     * 删除场景记录表信息
//     *
//     * @param id 场景ID
//     * @return 操作结果
//     */
//    @PostMapping("/delete/{id}")
//    @ApiOperation("删除场景记录表")
//    @Log(content = "删除场景记录表", operationType = OperationType.DELETE, isSaveRequestData = false)
//    public BaseResponse<?> delete(@PathVariable Integer id) {
//        return sceneService.delete(id);
//    }

    /**
     * 获取场景记录表
     *
     * @return {@link String}
     */
    @RequestMapping("/get/{id}")
    @ApiOperation("根据id获取场景记录详情")
    public BaseResponse<?> get(@PathVariable Integer id) {
        LambdaQueryWrapper<Scene> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Scene::getIsDel, 0);
        wrapper.eq(Scene::getId, id);
        wrapper.orderByDesc(Scene::getCreateTime);
        wrapper.select(Scene::getId, Scene::getSceneName, Scene::getScenePath, Scene::getScenePic, Scene::getSceneDescription, Scene::getSceneFrameNum, Scene::getSceneFrameNumPics);
        Scene scene = sceneService.getOne(wrapper);
        //通过场景id查询场景pcd文件
        List<ScenePcd> scenePcds = scenePcdService.lambdaQuery()
                .eq(ScenePcd::getSceneId, scene.getId())
                .list();
        scene.setScenePcdFileList(scenePcds);
        //通过pcdid查询场景pcd图片
        for (ScenePcd scenePcd : scenePcds) {
            List<ScenePcdImg> scenePcdImgs = scenePcdImgService.lambdaQuery()
                    .eq(ScenePcdImg::getPcdId, scenePcd.getId())
                    .list();
            scenePcd.setScenePcdImgList(scenePcdImgs);
        }
        return BaseResponse.ok(scene);
    }

}
