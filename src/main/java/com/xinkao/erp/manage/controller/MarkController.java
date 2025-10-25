package com.xinkao.erp.manage.controller;

import cn.hutool.core.util.StrUtil;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.manage.param.MarkParam;
import com.xinkao.erp.manage.query.MarkQuery;
import com.xinkao.erp.manage.service.MarkService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mark")
public class MarkController extends BaseController {

    @Autowired
    private MarkService markService;

    @PrimaryDataSource
    @PostMapping("/getList")
    public BaseResponse<List<Mark>> getList(@RequestBody MarkQuery query) {
        return markService.getList(query);
    }

    @PrimaryDataSource
    @PostMapping("/getListForType/{type}")
    public BaseResponse<List<Mark>> getListForType(@PathVariable Integer type) {
        return BaseResponse.ok(markService.lambdaQuery().eq(Mark::getType,type).eq(Mark::getPid,0).eq(Mark::getState, 1).eq(Mark::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list());
    }

    @PrimaryDataSource
    @PostMapping("/save")
    public BaseResponse save(@Valid @RequestBody MarkParam param) {
        return markService.save(param);
    }

    @PrimaryDataSource
    @PostMapping("/update")
    public BaseResponse update(@Valid @RequestBody MarkParam param) {
        return markService.update(param);
    }

    @PrimaryDataSource
    @PostMapping("/del")
    public BaseResponse del(@RequestBody UpdateStateParam updateStateParam) {
        if (StrUtil.isBlank(updateStateParam.getIds())){
            return BaseResponse.fail("参数错误,id不可为空！");
        }
        return markService.del(updateStateParam.getIds());
    }

    @PrimaryDataSource
    @PostMapping("/getListByQuestionId/{qid}")
    public BaseResponse<List<Mark>> getListByQuestionId(@PathVariable Integer qid){
        return BaseResponse.ok(markService.getListByQuestionId(qid));
    }
}
