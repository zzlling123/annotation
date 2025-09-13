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
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private UserService userService;

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
        LoginUser loginUser = redisUtil.getInfoByToken();
        
        // 角色1和18可以查看所有班级
        if (loginUser.getUser().getRoleId() == 1 || loginUser.getUser().getRoleId() == 18) {
            // 不设置任何过滤条件，查看所有班级
        }
        // 角色19可以查看所有角色19用户创建的班级
        else if (loginUser.getUser().getRoleId() == 19) {
            List<Integer> role19UserIds = userService.lambdaQuery()
                    .eq(User::getRoleId, 19)
                    .eq(User::getIsDel, 0)
                    .list()
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            query.setDirectorIdList(role19UserIds);
        }
        // 其他角色只能查看自己管理的班级
        else {
            query.setDirectorId(loginUser.getUser().getId());
        }
        
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
        LoginUser loginUser = redisUtil.getInfoByToken();
        
        // 如果用户角色为19，返回所有角色19用户创建的班级
        if (loginUser.getUser().getRoleId() == 19) {
            List<Integer> role19UserIds = userService.lambdaQuery()
                    .eq(User::getRoleId, 19)
                    .eq(User::getIsDel, 0)
                    .list()
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            
            return BaseResponse.ok(classInfoService.lambdaQuery()
                .in(ClassInfo::getDirectorId, role19UserIds)
                .eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode())
                .list());
        }
        
        // 其他角色返回所有未删除的班级
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
        if (loginUser.getUser().getRoleId() == 1|| loginUser.getUser().getRoleId() == 18|| loginUser.getUser().getRoleId() == 19){
            return BaseResponse.ok(classInfoService.lambdaQuery().eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list());
        }
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