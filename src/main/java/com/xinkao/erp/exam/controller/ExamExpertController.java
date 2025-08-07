package com.xinkao.erp.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.exam.dto.ExamExpertDTO;
import com.xinkao.erp.exam.dto.BatchExamExpertDTO;
import com.xinkao.erp.exam.entity.ExamExpert;
import com.xinkao.erp.exam.service.ExamExpertService;
import com.xinkao.erp.exam.service.ExamExpertAssignmentService;
import com.xinkao.erp.exam.entity.ExamExpertAssignment;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.user.service.UserService;
import com.xinkao.erp.user.service.RoleService;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

/**
 * 考试和专家模块控制器
 *
 * @author zzl
 * @since 2025-04-05 23:15:56
 */
@RestController
@RequestMapping("/exam-expert")
@Api(tags = "考试专家管理")
public class ExamExpertController {

    @Autowired
    private ExamExpertService examExpertService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private ExamExpertAssignmentService examExpertAssignmentService;
    
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 新增考试专家关联
     */
    @PostMapping("/add")
    @ApiOperation("新增考试专家关联")
    public BaseResponse<?> addExamExpert(@RequestBody ExamExpertDTO examExpertDTO) {
        try {
            ExamExpert examExpert = new ExamExpert();
            examExpert.setExpertId(examExpertDTO.getExpertId());
            examExpert.setExamId(examExpertDTO.getExamId());
            
            boolean result = examExpertService.save(examExpert);
            return result ? BaseResponse.ok("新增成功") : BaseResponse.fail("新增失败");
        } catch (Exception e) {
            return BaseResponse.fail("新增失败：" + e.getMessage());
        }
    }

    /**
     * 批量新增考试专家关联
     */
    @PostMapping("/batch-add")
    @ApiOperation("批量新增考试专家关联")
    public BaseResponse<?> batchAddExamExperts(@RequestBody BatchExamExpertDTO batchExamExpertDTO) {
        try {
            if (batchExamExpertDTO.getExamId() == null) {
                return BaseResponse.fail("考试ID不能为空");
            }
            
            if (batchExamExpertDTO.getExpertIds() == null || batchExamExpertDTO.getExpertIds().isEmpty()) {
                return BaseResponse.fail("专家ID列表不能为空");
            }
            
            // 检查是否已存在相同的关联
            List<ExamExpert> existingExperts = examExpertService.lambdaQuery()
                    .eq(ExamExpert::getExamId, batchExamExpertDTO.getExamId())
                    .in(ExamExpert::getExpertId, batchExamExpertDTO.getExpertIds())
                    .list();
            
            if (!existingExperts.isEmpty()) {
                List<Integer> existingExpertIds = existingExperts.stream()
                        .map(ExamExpert::getExpertId)
                        .collect(java.util.stream.Collectors.toList());
                return BaseResponse.fail("以下专家已关联该考试：" + existingExpertIds);
            }
            
            // 批量创建关联
            List<ExamExpert> examExperts = new ArrayList<>();
            for (Integer expertId : batchExamExpertDTO.getExpertIds()) {
                ExamExpert examExpert = new ExamExpert();
                examExpert.setExamId(batchExamExpertDTO.getExamId());
                examExpert.setExpertId(expertId);
                examExperts.add(examExpert);
            }
            
            boolean result = examExpertService.saveBatch(examExperts);
            return result ? BaseResponse.ok("批量新增成功，共添加 " + examExperts.size() + " 个专家") : BaseResponse.fail("批量新增失败");
        } catch (Exception e) {
            return BaseResponse.fail("批量新增失败：" + e.getMessage());
        }
    }

    /**
     * 根据专家ID和考试ID删除关联
     */
    @DeleteMapping("/delete")
    @ApiOperation("根据专家ID和考试ID删除关联")
    public BaseResponse<?> deleteExamExpertByExpertAndExam(
            @RequestParam Integer expertId,
            @RequestParam Integer examId) {
        try {
            LambdaQueryWrapper<ExamExpert> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamExpert::getExpertId, expertId)
                   .eq(ExamExpert::getExamId, examId);
            boolean result = examExpertService.remove(wrapper);
            return result ? BaseResponse.ok("删除成功") : BaseResponse.fail("删除失败");
        } catch (Exception e) {
            return BaseResponse.fail("删除失败：" + e.getMessage());
        }
    }

    /**
     * 更新考试专家关联
     */
    @PutMapping("/update")
    @ApiOperation("更新考试专家关联")
    public BaseResponse<?> updateExamExpert(@RequestBody ExamExpertDTO examExpertDTO) {
        try {
            ExamExpert examExpert = new ExamExpert();
            examExpert.setExpertId(examExpertDTO.getExpertId());
            examExpert.setExamId(examExpertDTO.getExamId());
            
            boolean result = examExpertService.updateById(examExpert);
            return result ? BaseResponse.ok("更新成功") : BaseResponse.fail("更新失败");
        } catch (Exception e) {
            return BaseResponse.fail("更新失败：" + e.getMessage());
        }
    }


    /**
     * 查询所有考试专家关联
     */
    @GetMapping("/list")
    @ApiOperation("查询所有考试专家关联")
    public BaseResponse<List<ExamExpert>> getAllExamExperts() {
        try {
            List<ExamExpert> list = examExpertService.list();
            return BaseResponse.ok(list);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据考试ID查询专家列表
     */
    @GetMapping("/experts/{examId}")
    @ApiOperation("根据考试ID查询专家列表")
    public BaseResponse<List<ExamExpert>> getExpertsByExamId(@PathVariable Integer examId) {
        try {
            List<ExamExpert> experts = examExpertService.getExpertsByExamId(examId);
            return BaseResponse.ok(experts);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据专家ID查询考试列表
     */
    @GetMapping("/exams/{expertId}")
    @ApiOperation("根据专家ID查询考试列表")
    public BaseResponse<List<ExamExpert>> getExamsByExpertId(@PathVariable Integer expertId) {
        try {
            List<ExamExpert> exams = examExpertService.getExamsByExpertId(expertId);
            return BaseResponse.ok(exams);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询角色名为"评审专家"的用户信息
     */
    @GetMapping("/expert-users")
    @ApiOperation("查询角色名为'评审专家'的用户信息")
    public BaseResponse<List<User>> getExpertUsers() {
        try {
            // 1. 先查询角色名为"评审专家"的角色ID
            Role expertRole = roleService.lambdaQuery()
                    .eq(Role::getRoleName, "评审专家")
                    .eq(Role::getIsDel, 0)  // 未删除的角色
                    .one();
            
            if (expertRole == null) {
                return BaseResponse.fail("未找到'评审专家'角色");
            }
            
            // 2. 根据角色ID查询用户列表
            List<User> expertUsers = userService.lambdaQuery()
                    .eq(User::getRoleId, expertRole.getId())
                    .eq(User::getIsDel, 0)  // 未删除的用户
                    .eq(User::getState, 1)   // 启用的用户
                    .orderByDesc(User::getCreateTime)  // 按创建时间倒序
                    .list();
            
            return BaseResponse.ok(expertUsers);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 为考试分配专家判卷任务
     */
    @PostMapping("/assign-exam/{examId}")
    @ApiOperation("为考试分配专家判卷任务")
    public BaseResponse<?> assignExamToExperts(@PathVariable Integer examId) {
        try {
            boolean result = examExpertAssignmentService.assignExamToExperts(examId);
            return result ? BaseResponse.ok("分配成功") : BaseResponse.fail("分配失败");
        } catch (Exception e) {
            return BaseResponse.fail("分配失败：" + e.getMessage());
        }
    }

    /**
     * 查询考试的专家判卷分配情况
     */
    @GetMapping("/assignments/{examId}")
    @ApiOperation("查询考试的专家判卷分配情况")
    public BaseResponse<List<ExamExpertAssignment>> getExamAssignments(@PathVariable Integer examId) {
        try {
            List<ExamExpertAssignment> assignments = examExpertAssignmentService.getAssignmentsByExamId(examId);
            return BaseResponse.ok(assignments);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询专家的判卷任务
     */
    @GetMapping("/expert-assignments/{expertId}")
    @ApiOperation("查询专家的判卷任务")
    public BaseResponse<List<ExamExpertAssignment>> getExpertAssignments(@PathVariable Integer expertId) {
        try {
            List<ExamExpertAssignment> assignments = examExpertAssignmentService.getAssignmentsByExpertId(expertId);
            return BaseResponse.ok(assignments);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据考试ID查询当前专家需要判卷的考生列表
     */
    @GetMapping("/my-assignments/{examId}")
    @ApiOperation("根据考试ID查询当前专家需要判卷的考生列表")
    public BaseResponse<List<ExamExpertAssignment>> getMyAssignments(@PathVariable Integer examId) {
        try {
            // 获取当前登录用户信息
            LoginUser loginUserAll = redisUtil.getInfoByToken();
            if (loginUserAll == null || loginUserAll.getUser() == null) {
                return BaseResponse.fail("用户未登录");
            }
            
            Integer expertId = loginUserAll.getUser().getId();
            
            // 查询当前专家在该考试中的判卷任务
            List<ExamExpertAssignment> assignments = examExpertAssignmentService.lambdaQuery()
                    .eq(ExamExpertAssignment::getExamId, examId)
                    .eq(ExamExpertAssignment::getExpertId, expertId)
                    .eq(ExamExpertAssignment::getIsDel, 0)
                    .orderByAsc(ExamExpertAssignment::getCreateTime)
                    .list();
            
            return BaseResponse.ok(assignments);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据考试ID查询当前专家需要判卷的考生列表（未判卷的）
     */
    @GetMapping("/my-pending-assignments/{examId}")
    @ApiOperation("根据考试ID查询当前专家需要判卷的考生列表（未判卷的）")
    public BaseResponse<List<ExamExpertAssignment>> getMyPendingAssignments(@PathVariable Integer examId) {
        try {
            // 获取当前登录用户信息
            LoginUser loginUserAll = redisUtil.getInfoByToken();
            if (loginUserAll == null || loginUserAll.getUser() == null) {
                return BaseResponse.fail("用户未登录");
            }
            
            Integer expertId = loginUserAll.getUser().getId();
            
            // 查询当前专家在该考试中未判卷的任务
            List<ExamExpertAssignment> assignments = examExpertAssignmentService.lambdaQuery()
                    .eq(ExamExpertAssignment::getExamId, examId)
                    .eq(ExamExpertAssignment::getExpertId, expertId)
                    .eq(ExamExpertAssignment::getStatus, 0) // 未判卷
                    .eq(ExamExpertAssignment::getIsDel, 0)
                    .orderByAsc(ExamExpertAssignment::getCreateTime)
                    .list();
            
            return BaseResponse.ok(assignments);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据考试ID查询当前专家已判卷的考生列表
     */
    @GetMapping("/my-completed-assignments/{examId}")
    @ApiOperation("根据考试ID查询当前专家已判卷的考生列表")
    public BaseResponse<List<ExamExpertAssignment>> getMyCompletedAssignments(@PathVariable Integer examId) {
        try {
            // 获取当前登录用户信息
            LoginUser loginUserAll = redisUtil.getInfoByToken();
            if (loginUserAll == null || loginUserAll.getUser() == null) {
                return BaseResponse.fail("用户未登录");
            }
            
            Integer expertId = loginUserAll.getUser().getId();
            
            // 查询当前专家在该考试中已判卷的任务
            List<ExamExpertAssignment> assignments = examExpertAssignmentService.lambdaQuery()
                    .eq(ExamExpertAssignment::getExamId, examId)
                    .eq(ExamExpertAssignment::getExpertId, expertId)
                    .eq(ExamExpertAssignment::getStatus, 2) // 已判卷
                    .eq(ExamExpertAssignment::getIsDel, 0)
                    .orderByAsc(ExamExpertAssignment::getCreateTime)
                    .list();
            
            return BaseResponse.ok(assignments);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }


}
