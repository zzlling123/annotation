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

@RestController
@RequestMapping("/exam-expert")
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

    @PostMapping("/add")
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

    @PostMapping("/batch-add")
    public BaseResponse<?> batchAddExamExperts(@RequestBody BatchExamExpertDTO batchExamExpertDTO) {
        try {
            if (batchExamExpertDTO.getExamId() == null) {
                return BaseResponse.fail("考试ID不能为空");
            }
            
            if (batchExamExpertDTO.getExpertIds() == null || batchExamExpertDTO.getExpertIds().isEmpty()) {
                return BaseResponse.fail("专家ID列表不能为空");
            }

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

    @DeleteMapping("/delete")
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

    @DeleteMapping("/batch-delete")
    public BaseResponse<?> batchDeleteExamExperts(@RequestBody BatchExamExpertDTO batchExamExpertDTO) {
        try {
            if (batchExamExpertDTO.getExamId() == null) {
                return BaseResponse.fail("考试ID不能为空");
            }
            
            if (batchExamExpertDTO.getExpertIds() == null || batchExamExpertDTO.getExpertIds().isEmpty()) {
                return BaseResponse.fail("专家ID列表不能为空");
            }

            LambdaQueryWrapper<ExamExpert> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamExpert::getExamId, batchExamExpertDTO.getExamId())
                   .in(ExamExpert::getExpertId, batchExamExpertDTO.getExpertIds());

            long count = examExpertService.count(wrapper);
            if (count == 0) {
                return BaseResponse.fail("未找到要删除的关联记录");
            }

            boolean result = examExpertService.remove(wrapper);
            return result ? BaseResponse.ok("批量删除成功，共删除 " + count + " 条关联记录") : BaseResponse.fail("批量删除失败");
        } catch (Exception e) {
            return BaseResponse.fail("批量删除失败：" + e.getMessage());
        }
    }

    @PutMapping("/update")
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

    @GetMapping("/list")
    public BaseResponse<List<ExamExpert>> getAllExamExperts() {
        try {
            List<ExamExpert> list = examExpertService.list();
            return BaseResponse.ok(list);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/experts/{examId}")
    public BaseResponse<List<ExamExpert>> getExpertsByExamId(@PathVariable Integer examId) {
        try {
            List<ExamExpert> experts = examExpertService.getExpertsByExamId(examId);
            return BaseResponse.ok(experts);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/exams/{expertId}")
    public BaseResponse<List<ExamExpert>> getExamsByExpertId(@PathVariable Integer expertId) {
        try {
            List<ExamExpert> exams = examExpertService.getExamsByExpertId(expertId);
            return BaseResponse.ok(exams);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/expert-users")
    public BaseResponse<List<User>> getExpertUsers() {
        try {
            Role expertRole = roleService.lambdaQuery()
                    .eq(Role::getRoleName, "评审专家")
                    .eq(Role::getIsDel, 0)
                    .one();
            
            if (expertRole == null) {
                return BaseResponse.fail("未找到'评审专家'角色");
            }

            List<User> expertUsers = userService.lambdaQuery()
                    .eq(User::getRoleId, expertRole.getId())
                    .eq(User::getIsDel, 0)
                    .eq(User::getState, 1)
                    .orderByDesc(User::getCreateTime)
                    .list();
            
            return BaseResponse.ok(expertUsers);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    @PostMapping("/assign-exam/{examId}")
    public BaseResponse<?> assignExamToExperts(@PathVariable Integer examId) {
        try {
            boolean result = examExpertAssignmentService.assignExamToExperts(examId);
            return result ? BaseResponse.ok("分配成功") : BaseResponse.fail("分配失败");
        } catch (Exception e) {
            return BaseResponse.fail("分配失败：" + e.getMessage());
        }
    }

    @GetMapping("/assignments/{examId}")
    public BaseResponse<List<ExamExpertAssignment>> getExamAssignments(@PathVariable Integer examId) {
        try {
            List<ExamExpertAssignment> assignments = examExpertAssignmentService.getAssignmentsByExamId(examId);
            return BaseResponse.ok(assignments);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/expert-assignments/{expertId}")
    public BaseResponse<List<ExamExpertAssignment>> getExpertAssignments(@PathVariable Integer expertId) {
        try {
            List<ExamExpertAssignment> assignments = examExpertAssignmentService.getAssignmentsByExpertId(expertId);
            return BaseResponse.ok(assignments);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/my-assignments/{examId}")
    public BaseResponse<List<ExamExpertAssignment>> getMyAssignments(@PathVariable Integer examId) {
        try {
            LoginUser loginUserAll = redisUtil.getInfoByToken();
            if (loginUserAll == null || loginUserAll.getUser() == null) {
                return BaseResponse.fail("用户未登录");
            }
            
            Integer expertId = loginUserAll.getUser().getId();

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

    @GetMapping("/my-pending-assignments/{examId}")
    public BaseResponse<List<ExamExpertAssignment>> getMyPendingAssignments(@PathVariable Integer examId) {
        try {
            LoginUser loginUserAll = redisUtil.getInfoByToken();
            if (loginUserAll == null || loginUserAll.getUser() == null) {
                return BaseResponse.fail("用户未登录");
            }
            
            Integer expertId = loginUserAll.getUser().getId();

            List<ExamExpertAssignment> assignments = examExpertAssignmentService.lambdaQuery()
                    .eq(ExamExpertAssignment::getExamId, examId)
                    .eq(ExamExpertAssignment::getExpertId, expertId)
                    .eq(ExamExpertAssignment::getStatus, 0)
                    .eq(ExamExpertAssignment::getIsDel, 0)
                    .orderByAsc(ExamExpertAssignment::getCreateTime)
                    .list();
            
            return BaseResponse.ok(assignments);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/my-completed-assignments/{examId}")
    public BaseResponse<List<ExamExpertAssignment>> getMyCompletedAssignments(@PathVariable Integer examId) {
        try {
            LoginUser loginUserAll = redisUtil.getInfoByToken();
            if (loginUserAll == null || loginUserAll.getUser() == null) {
                return BaseResponse.fail("用户未登录");
            }
            
            Integer expertId = loginUserAll.getUser().getId();

            List<ExamExpertAssignment> assignments = examExpertAssignmentService.lambdaQuery()
                    .eq(ExamExpertAssignment::getExamId, examId)
                    .eq(ExamExpertAssignment::getExpertId, expertId)
                    .eq(ExamExpertAssignment::getStatus, 2)
                    .eq(ExamExpertAssignment::getIsDel, 0)
                    .orderByAsc(ExamExpertAssignment::getCreateTime)
                    .list();
            
            return BaseResponse.ok(assignments);
        } catch (Exception e) {
            return BaseResponse.fail("查询失败：" + e.getMessage());
        }
    }
}
