package com.xinkao.erp.scene.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.scene.entity.Scene;
import com.xinkao.erp.scene.query.SceneQuery;
import com.xinkao.erp.scene.service.SceneService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 *  场景管理 前端控制器
 *
 * @author zzl
 * @since 2025-04-12 23:11:56
 */
@RestController
@RequestMapping("/scene")
public class SceneController {
    @Autowired
    private SceneService sceneService;

    /**
     * 查询所有场景信息不分页
     */
    @RequestMapping("/getAll")
    public BaseResponse<List<Scene>> getAll() {
        //查询isDel=0的场景
        LambdaQueryWrapper<Scene> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Scene::getIsDel, 0);
        wrapper.orderByDesc(Scene::getCreateTime);
        wrapper.select(Scene::getId, Scene::getSceneName, Scene::getScenePath, Scene::getScenePic, Scene::getSceneDescription, Scene::getSceneFrameNum, Scene::getSceneFrameNumPics);
        List<Scene> scenes = sceneService.list(wrapper);
        return BaseResponse.ok(scenes);
    }


    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询即时反馈表")
    public BaseResponse<Page<Scene>> page(@Valid @RequestBody SceneQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<Scene> voPage = sceneService.page(query, pageable);
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


    private static final String UPLOAD_DIR = "e://mark_view"; // 修改为你的上传目录路径
    private static final String IMAGE_DIR = UPLOAD_DIR + "/images"; // 图片存储目录路径
    /**
     * 添加场景记录表，里面有一个场景名称，场景地址（需要文件上传），场景图片（需要文件上传），场景描述，场景帧数，场景帧对应的图片集合
     */
    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("添加场景记录表")
    public BaseResponse<?>  save(Scene scene) throws IOException {
        MultipartFile sceneFile = scene.getSceneFile(); // 获取文件对象
        MultipartFile scenePicFile = scene.getScenePicFile();
        if (!sceneFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + sceneFile.getOriginalFilename(); // 生成唯一文件名
            Path path = Paths.get(IMAGE_DIR + "/" + fileName); // 构建文件路径对象
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent()); // 创建父目录
            }
            try {
                Files.copy(sceneFile.getInputStream(), path); // 保存文件到服务器路径下
                scene.setScenePath(path.toString()); // 设置图片URL或路径
                System.out.println("场景文件保存成功: " + path);
            } catch (IOException e) {
                System.err.println("场景文件保存失败: " + e.getMessage());
                throw e;
            }
        } else {
            scene.setScenePath(null); // 处理空文件情况
            System.out.println("场景文件为空");
        }

        if (!scenePicFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + scenePicFile.getOriginalFilename();
            Path path = Paths.get(IMAGE_DIR + "/" + fileName);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            try {
                Files.copy(scenePicFile.getInputStream(), path);
                scene.setScenePic(path.toString());
                System.out.println("场景图片保存成功: " + path);
            } catch (IOException e) {
                System.err.println("场景图片保存失败: " + e.getMessage());
                throw e;
            }
        } else {
            scene.setScenePic(null);
            System.out.println("场景图片为空");
        }

        return sceneService.save1(scene);// 保存产品到数据库，不包括图片文件本身，只保存图片的URL或路径。
    }

//    /**
//     * 请帮我写一个方法，可以解析pcd文件，并解析出关键帧对应的图片集合，并保存到数据库中，并返回图片集合的url或路径
//     */
//    @PostMapping("/parsePsd")
//    public BaseResponse<?> parsePsd(@RequestParam("psdFile") MultipartFile psdFile) {
//        List<String> imageUrls = PSDParser.parsePSDAndSaveImages(psdFile.getOriginalFilename(), UPLOAD_DIR);
//        return BaseResponse.ok(imageUrls);
//    }



    /**
     * 编辑场景记录表信息
     *
     * @param scene 场景记录表
     * @return 操作结果
     */
    @PostMapping("/update")
    @ApiOperation("更新场景记录表")
    @Log(content = "更新场景记录表", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody Scene scene) {
        if (scene.getId() == null) {
            return BaseResponse.fail("场景记录ID不能为空");
        }
        return sceneService.update(scene);
    }

    /**
     * 删除场景记录表信息
     *
     * @param id 场景ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除场景记录表")
    @Log(content = "删除场景记录表", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return sceneService.delete(id);
    }

    /**
     * 获取场景记录表
     *
     * @return {@link String}
     */
    @RequestMapping("/get/{id}")
    @ApiOperation("根据id获取场景记录详情")
    public String get() {
        return "根据id获取场景记录详情";
    }

}
