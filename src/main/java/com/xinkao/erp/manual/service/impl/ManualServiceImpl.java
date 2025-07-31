package com.xinkao.erp.manual.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.manual.entity.Manual;
import com.xinkao.erp.manual.mapper.ManualMapper;
import com.xinkao.erp.manual.param.ManualParam;
import com.xinkao.erp.manual.query.ManualQuery;
import com.xinkao.erp.manual.service.ManualService;
import com.xinkao.erp.manual.vo.ManualVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 使用文档表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-07-26
 */
@Slf4j
@Service
public class ManualServiceImpl extends BaseServiceImpl<ManualMapper, Manual> implements ManualService {



    /**
     * 角色ID与用户类型映射枚举
     */
    public enum RoleUserTypeMapping {
        ADMIN(1, 1, "管理员"),
        TEACHER(2, 5, "教师"),
        STUDENT(3, 4, "学生"),
        SCHOOL_ADMIN(18, 2, "学校管理员"),
        SOCIAL_SECURITY_ADMIN(19, 3, "社保局管理员"),
        EXPERT(20, 6, "评审专家"),
        SOCIAL_CANDIDATE(21, 7, "社会考生");

        private final Integer roleId;
        private final Integer userType;
        private final String description;

        RoleUserTypeMapping(Integer roleId, Integer userType, String description) {
            this.roleId = roleId;
            this.userType = userType;
            this.description = description;
        }

        public Integer getRoleId() {
            return roleId;
        }

        public Integer getUserType() {
            return userType;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据角色ID获取用户类型
         * @param roleId 角色ID
         * @return 用户类型，如果未找到返回null
         */
        public static Integer getUserTypeByRoleId(Integer roleId) {
            if (roleId == null) {
                return null;
            }
            for (RoleUserTypeMapping mapping : values()) {
                if (mapping.roleId.equals(roleId)) {
                    return mapping.userType;
                }
            }
            return null;
        }

        /**
         * 根据角色ID获取描述
         * @param roleId 角色ID
         * @return 描述信息
         */
        public static String getDescriptionByRoleId(Integer roleId) {
            if (roleId == null) {
                return "未知角色";
            }
            for (RoleUserTypeMapping mapping : values()) {
                if (mapping.roleId.equals(roleId)) {
                    return mapping.description;
                }
            }
            return "未知角色";
        }
    }

    @Override
    public Page<ManualVo> page(ManualQuery query) {
        LambdaQueryWrapper<Manual> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        wrapper.eq(query.getUserType() != null, Manual::getUserType, query.getUserType())
                .eq(query.getCreateBy() != null, Manual::getCreateBy, query.getCreateBy())
                .orderByDesc(Manual::getCreateTime);

        // 获取分页信息
        Page<Manual> page = query.getPageInfo().toPage();
        Page<Manual> result = this.page(page, wrapper);

        // 转换为VO
        Page<ManualVo> voPage = new Page<>();
        BeanUtil.copyProperties(result, voPage);
        
        List<ManualVo> voList = result.getRecords().stream().map(manual -> {
            ManualVo vo = BeanUtil.copyProperties(manual, ManualVo.class);
            vo.setUserTypeName(); // 设置用户类型名称
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<?> save(ManualParam manualParam) {
        try {
            // 参数校验
            if (Manual.UserTypeEnum.getByCode(manualParam.getUserType()) == null) {
                return BaseResponse.fail("用户类型无效");
            }

            // 检查该用户类型是否已存在文档
            LambdaQueryWrapper<Manual> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(Manual::getUserType, manualParam.getUserType());
            Manual existingManual = this.getOne(checkWrapper);
            
            if (existingManual != null) {
                // 获取用户类型名称
                String userTypeName = Manual.UserTypeEnum.getByCode(manualParam.getUserType()).getName();
                return BaseResponse.fail("该用户类型（" + userTypeName + "）的文档已存在，每个类型只能有一个文档");
            }

            Manual manual = manualParam.convertTo();
            boolean result = this.save(manual);
            
            return result ? BaseResponse.ok("新增成功") : BaseResponse.fail("新增失败");
        } catch (Exception e) {
            return BaseResponse.fail("新增失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<?> update(ManualParam manualParam) {
        try {
            // 参数校验
            if (manualParam.getId() == null) {
                return BaseResponse.fail("文档ID不能为空");
            }
            
            if (Manual.UserTypeEnum.getByCode(manualParam.getUserType()) == null) {
                return BaseResponse.fail("用户类型无效");
            }

            // 检查文档是否存在
            Manual existManual = this.getById(manualParam.getId());
            if (existManual == null) {
                return BaseResponse.fail("文档不存在");
            }

            // 如果修改了用户类型，检查新的用户类型是否已存在文档（排除当前文档）
            if (!existManual.getUserType().equals(manualParam.getUserType())) {
                LambdaQueryWrapper<Manual> checkWrapper = new LambdaQueryWrapper<>();
                checkWrapper.eq(Manual::getUserType, manualParam.getUserType())
                           .ne(Manual::getId, manualParam.getId());
                Manual existingTypeManual = this.getOne(checkWrapper);
                
                if (existingTypeManual != null) {
                    // 获取用户类型名称
                    String userTypeName = Manual.UserTypeEnum.getByCode(manualParam.getUserType()).getName();
                    return BaseResponse.fail("该用户类型（" + userTypeName + "）的文档已存在，每个类型只能有一个文档");
                }
            }

            Manual manual = BeanUtil.copyProperties(manualParam, Manual.class);
            boolean result = this.updateById(manual);
            
            return result ? BaseResponse.ok("修改成功") : BaseResponse.fail("修改失败");
        } catch (Exception e) {
            return BaseResponse.fail("修改失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<?> del(DeleteParam param) {
        try {
            boolean result = this.removeByIds(param.getIds());
            return result ? BaseResponse.ok("删除成功") : BaseResponse.fail("删除失败");
        } catch (Exception e) {
            return BaseResponse.fail("删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户对应的使用文档
     * @return 文档信息
     */
    @Override
    public ManualVo getByUserType() {
        try {
            // 获取当前登录用户信息
            LoginUser loginUser = redisUtil.getInfoByToken();
            if (loginUser == null || loginUser.getUser() == null) {
                log.warn("获取登录用户信息失败");
                return null;
            }

            Integer roleId = loginUser.getUser().getRoleId();
            if (roleId == null) {
                log.warn("用户角色ID为空, userId: {}", loginUser.getUser().getId());
                return null;
            }

            // 根据角色ID获取对应的用户类型
            Integer userType = RoleUserTypeMapping.getUserTypeByRoleId(roleId);
            if (userType == null) {
                log.warn("未找到角色ID对应的用户类型, roleId: {}, 支持的角色: {}", roleId, 
                        "1-管理员, 2-教师, 3-学生, 18-学校管理员, 19-社保局管理员, 20-评审专家, 21-社会考生");
                return null;
            }

            String roleDescription = RoleUserTypeMapping.getDescriptionByRoleId(roleId);
            log.debug("用户角色映射: roleId={}, userType={}, roleDescription={}", roleId, userType, roleDescription);

            // 查询对应用户类型的文档
            return getManualByUserType(userType);

        } catch (Exception e) {
            log.error("根据用户类型获取使用文档失败", e);
            return null;
        }
    }

    /**
     * 根据用户类型获取文档信息
     * @param userType 用户类型
     * @return 文档信息
     */
    private ManualVo getManualByUserType(Integer userType) {
        LambdaQueryWrapper<Manual> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Manual::getUserType, userType);

        Manual manual = this.getOne(wrapper);
        if (manual == null) {
            log.info("用户类型 {} 暂无对应的使用文档", userType);
            return null;
        }

        // 转换为VO对象
        ManualVo vo = BeanUtil.copyProperties(manual, ManualVo.class);
        
        // 设置用户类型名称
        Manual.UserTypeEnum userTypeEnum = Manual.UserTypeEnum.getByCode(userType);
        if (userTypeEnum != null) {
            vo.setUserTypeName(userTypeEnum.getName());
        }

        log.debug("成功获取用户类型 {} 的使用文档, fileUrl: {}", userType, vo.getFileUrl());
        return vo;
    }
} 