package com.xinkao.erp.manage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.param.ClassInfoParam;
import com.xinkao.erp.manage.query.ClassInfoQuery;
import com.xinkao.erp.manage.service.ClassInfoService;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端班级相关服务
 * 
 * @author Ldy
 *
 */
@RequestMapping("/class_info")
@RestController
@Slf4j
public class ClassInfoController extends BaseController {

    @Autowired
    private ClassInfoService classInfoService;

    /**
     * 分页查询班级信息
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
	@PostMapping("/page")
    @ApiOperation("分页查询班级信息")
    public BaseResponse<Page<ClassInfoVo>> page(@Valid @RequestBody ClassInfoQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<ClassInfoVo> voPage = classInfoService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     * 下拉列表查询班级信息
     *
     */
    @PrimaryDataSource
	@PostMapping("/getList")
    @ApiOperation("下拉列表班级信息")
    public BaseResponse<List<ClassInfo>> getList() {
        return BaseResponse.ok(classInfoService.lambdaQuery().eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list());
    }

    /**
     * 班级负责人--下拉列表
     *
     */
    @PrimaryDataSource
	@PostMapping("/getListForTea")
    @ApiOperation("班级负责人--下拉列表")
    public BaseResponse<List<ClassInfo>> getListForTea() {
        LoginUser loginUser = redisUtil.getInfoByToken();
        return BaseResponse.ok(classInfoService.lambdaQuery().eq(ClassInfo::getDirectorId, loginUser.getUser().getId()).eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list());
    }

    /**
     * 新增班级信息
     *
     * @param classInfoParam 班级信息参数
     * @return 操作结果
     */
    @PrimaryDataSource
	@PostMapping("/save")
    @ApiOperation("新增班级信息")
    @Log(content = "新增班级信息", operationType = OperationType.INSERT, isSaveRequestData = false)
    public BaseResponse<?> save(@Valid @RequestBody ClassInfoParam classInfoParam) {
        return classInfoService.save(classInfoParam);
    }

    /**
     * 编辑班级信息
     *
     * @param classInfoParam 班级信息参数
     * @return 操作结果
     */
    @PostMapping("/update")
    @ApiOperation("编辑班级信息")
    @Log(content = "编辑班级信息", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody ClassInfoParam classInfoParam) {
        return classInfoService.update(classInfoParam);
    }

    /**
     * 删除班级信息
     *
     * @param id 班级ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除班级信息")
    @Log(content = "删除班级信息", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return classInfoService.delete(id);
    }
}