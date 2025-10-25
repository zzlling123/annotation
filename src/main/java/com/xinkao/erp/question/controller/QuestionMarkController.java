package com.xinkao.erp.question.controller;

import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.manage.query.MarkQuery;
import com.xinkao.erp.question.entity.QuestionMark;
import com.xinkao.erp.question.service.QuestionMarkService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/question-mark")
public class QuestionMarkController extends BaseController {

    @Resource
    private QuestionMarkService questionMarkService;

}
