package com.xinkao.erp.manage.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.manage.mapper.ClassInfoMapper;
import com.xinkao.erp.manage.param.ClassInfoParam;
import com.xinkao.erp.manage.query.ClassInfoQuery;
import com.xinkao.erp.manage.service.ClassInfoService;
import com.xinkao.erp.manage.vo.ClassInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClassInfoServiceImpl extends BaseServiceImpl<ClassInfoMapper, ClassInfo> implements ClassInfoService {

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Override
    public Page<ClassInfoVo> page(ClassInfoQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return classInfoMapper.page(page, query);
    }

    @Override
    public BaseResponse<?> save(ClassInfoParam classInfoParam) {
        if (lambdaQuery().eq(ClassInfo::getClassName, classInfoParam.getClassName()).eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("班级名称已存在！");
        }
        //获取当前登录用户信息
        LoginUser loginUserAll = redisUtil.getInfoByToken();
        if(loginUserAll.getUser().getRoleId()==19){
            classInfoParam.setDirectorId(loginUserAll.getUser().getId());
        }
        ClassInfo classInfo = new ClassInfo();
        BeanUtils.copyProperties(classInfoParam, classInfo);
        return save(classInfo) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> update(ClassInfoParam classInfoParam) {
        if (lambdaQuery().eq(ClassInfo::getClassName, classInfoParam.getClassName()).ne(ClassInfo::getId, classInfoParam.getId()).eq(ClassInfo::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("班级名称已存在！");
        }
        ClassInfo classInfo = new ClassInfo();
        BeanUtils.copyProperties(classInfoParam, classInfo);
        return updateById(classInfo) ? BaseResponse.ok("更新成功！") : BaseResponse.fail("更新失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        return lambdaUpdate().eq(ClassInfo::getId, id).set(ClassInfo::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }
}