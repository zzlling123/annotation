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

@RequestMapping("/class_info")
@RestController
@Slf4j
public class ClassInfoController extends BaseController {

    @Autowired
    private ClassInfoService classInfoService;

    @Autowired
    private UserService userService;

    @PrimaryDataSource
	@PostMapping("/page")
    public BaseResponse<Page<ClassInfoVo>> page(@Valid @RequestBody ClassInfoQuery query) {
        LoginUser loginUser = redisUtil.getInfoByToken();

        if (loginUser.getUser().getRoleId() == 1 || loginUser.getUser().getRoleId() == 18) {
        }
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
        else {
            query.setDirectorId(loginUser.getUser().getId());
        }
        
        Pageable pageable = query.getPageInfo();
        Page<ClassInfoVo> voPage = classInfoService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
	@PostMapping("/getList")
    public BaseResponse<List<ClassInfo>> getList() {
        LoginUser loginUser = redisUtil.getInfoByToken();

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

        return BaseResponse.ok(classInfoService.lambdaQuery().eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list());
    }

    @PrimaryDataSource
	@PostMapping("/getListForTea")
    public BaseResponse<List<ClassInfo>> getListForTea() {
        LoginUser loginUser = redisUtil.getInfoByToken();
        if (loginUser.getUser().getRoleId() == 1|| loginUser.getUser().getRoleId() == 18|| loginUser.getUser().getRoleId() == 19){
            return BaseResponse.ok(classInfoService.lambdaQuery().eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list());
        }
        return BaseResponse.ok(classInfoService.lambdaQuery().eq(ClassInfo::getDirectorId, loginUser.getUser().getId()).eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list());
    }

    @PrimaryDataSource
	@PostMapping("/save")
    public BaseResponse<?> save(@Valid @RequestBody ClassInfoParam classInfoParam) {
        return classInfoService.save(classInfoParam);
    }

    @PostMapping("/update")
    public BaseResponse<?> update(@Valid @RequestBody ClassInfoParam classInfoParam) {
        return classInfoService.update(classInfoParam);
    }

    @PostMapping("/delete/{id}")
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return classInfoService.delete(id);
    }
}