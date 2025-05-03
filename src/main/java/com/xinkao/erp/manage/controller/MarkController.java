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

/**
 * 标记管理
 *
 * @author Ldy
 * @since 2025-04-20 21:22:31
 */
@RestController
@RequestMapping("/mark")
public class MarkController extends BaseController {

    @Autowired
    private MarkService markService;

    /**
     * 获取标记树状图
     *
     * @return
     */
    @PrimaryDataSource
    @PostMapping("/getList")
    @ApiOperation("获取标记树状图")
    public BaseResponse<List<Mark>> getList(@RequestBody MarkQuery query) {
        return markService.getList(query);
    }

    /**
     * 根据type获取标记树状图(录题模块)
     *
     * @return
     */
    @PrimaryDataSource
    @PostMapping("/getListForType/{type}")
    @ApiOperation("根据type获取标记树状图(录题模块)")
    public BaseResponse<List<Mark>> getListForType(@PathVariable Integer type) {
        return BaseResponse.ok(markService.lambdaQuery().eq(Mark::getType,type).eq(Mark::getPid,0).eq(Mark::getState, 1).eq(Mark::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list());
    }

    /**
     * 新增标记
     *
     * @return
     */
    @PrimaryDataSource
    @PostMapping("/save")
    @ApiOperation("新增标记")
    @Log(content = "新增标记",operationType = OperationType.INSERT)
    public BaseResponse save(@Valid @RequestBody MarkParam param) {
        return markService.save(param);
    }

    /**
     * 修改标记
     *
     * @return
     */
    @PrimaryDataSource
    @PostMapping("/update")
    @ApiOperation("修改标记")
    @Log(content = "修改标记",operationType = OperationType.UPDATE)
    public BaseResponse update(@Valid @RequestBody MarkParam param) {
        return markService.update(param);
    }

    /**
     * 删除
     *
     * @return
     */
    @PrimaryDataSource
    @PostMapping("/del")
    @ApiOperation("删除标记")
    @Log(content = "删除标记",operationType = OperationType.UPDATE)
    public BaseResponse del(@RequestBody UpdateStateParam updateStateParam) {
        if (StrUtil.isBlank(updateStateParam.getIds())){
            return BaseResponse.fail("参数错误,id不可为空！");
        }
        return markService.del(updateStateParam.getIds());
    }

    /**
     * 根据题目ID获取标记树状图
     *
     * @return
     */
    @PrimaryDataSource
    @PostMapping("/getListByQuestionId/{qid}")
    @ApiOperation("根据题目ID获取标记树状图")
    public BaseResponse<List<Mark>> getListByQuestionId(@PathVariable Integer qid){
        return markService.getListByQuestionId(qid);
    }
}
