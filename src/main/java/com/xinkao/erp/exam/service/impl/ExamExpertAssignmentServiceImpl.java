package com.xinkao.erp.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinkao.erp.exam.entity.ExamExpert;
import com.xinkao.erp.exam.entity.ExamExpertAssignment;
import com.xinkao.erp.exam.entity.ExamPageUser;
import com.xinkao.erp.exam.mapper.ExamExpertAssignmentMapper;
import com.xinkao.erp.exam.service.ExamExpertAssignmentService;
import com.xinkao.erp.exam.service.ExamExpertService;
import com.xinkao.erp.exam.service.ExamPageUserService;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.RoleService;
import com.xinkao.erp.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamExpertAssignmentServiceImpl extends ServiceImpl<ExamExpertAssignmentMapper, ExamExpertAssignment> implements ExamExpertAssignmentService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ExamPageUserService examPageUserService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ExamExpertService examExpertService;

    @Override
    @Transactional
    public boolean assignExamToExperts(Integer examId) {
        try {
            //查询这个考试的评审专家
            List<ExamExpert> examExperts = examExpertService.lambdaQuery()
                    .eq(ExamExpert::getExamId, examId)
                    .list() ;
            if (examExperts.isEmpty()) {
                return false;
            }
            // 1. 获取所有评审专家
            List<User> experts = new ArrayList<>();
            examExperts.forEach(examExpert -> {
                Integer expertId = examExpert.getExpertId();
                experts.add(userService.getById(expertId));
            });
            if (experts.isEmpty()) {
                return false;
            }
            
            // 2. 获取该考试的所有学生试卷
            List<ExamPageUser> examPapers = examPageUserService.lambdaQuery()
                    .eq(ExamPageUser::getExamId, examId)
                    .list();
            
            if (examPapers.isEmpty()) {
                return false;
            }
            
            // 3. 清除之前的分配记录
            this.lambdaUpdate()
                    .eq(ExamExpertAssignment::getExamId, examId)
                    .set(ExamExpertAssignment::getIsDel, 1)
                    .update();
            
            // 4. 平均分配试卷给专家
            List<ExamExpertAssignment> assignments = new ArrayList<>();
            int expertCount = experts.size();
            int paperCount = examPapers.size();
            for (int i = 0; i < paperCount; i++) {
                 ExamPageUser paper = examPapers.get(i);
                 User expert = experts.get(i % expertCount); // 循环分配

                 // 获取学生信息
                 User student = userService.getById(paper.getUserId());
                 String studentName = student != null ? student.getRealName() : "";

                 ExamExpertAssignment assignment = new ExamExpertAssignment();
                 assignment.setExamId(examId);
                 assignment.setExpertId(expert.getId());
                 assignment.setUserId(paper.getUserId());
                 assignment.setUserName(studentName);
                 assignment.setStatus(0); // 未判卷
                 assignment.setIsDel(0);

                 assignments.add(assignment);
             }
            
            // 5. 批量保存分配记录
            return this.saveBatch(assignments);
            
        } catch (Exception e) {
            throw new RuntimeException("分配失败：" + e.getMessage());
        }
    }

    @Override
    public List<ExamExpertAssignment> getAssignmentsByExamId(Integer examId) {
        return this.lambdaQuery()
                .eq(ExamExpertAssignment::getExamId, examId)
                .eq(ExamExpertAssignment::getIsDel, 0)
                .list();
    }

    @Override
    public List<ExamExpertAssignment> getAssignmentsByExpertId(Integer expertId) {
        return this.lambdaQuery()
                .eq(ExamExpertAssignment::getExpertId, expertId)
                .eq(ExamExpertAssignment::getIsDel, 0)
                .list();
    }

    //通过ExamId和ExpertId获取专家的分配信息
    @Override
    public List<ExamExpertAssignment> getAssignmentsByExamIdAndExpertId(Integer examId, Integer expertId) {
        return this.lambdaQuery()
                .eq(ExamExpertAssignment::getExamId, examId)
                .eq(ExamExpertAssignment::getExpertId, expertId)
                .eq(ExamExpertAssignment::getIsDel, 0)
                .list();
    }
    
    /**
     * 获取评审专家角色ID
     */
    private Integer getExpertRoleId() {
        int roleId = 0;
        // 这里需要根据你的角色表查询"评审专家"的角色ID
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName, "评审专家");
        Role expertRole = roleService.getOne(queryWrapper);
        if (expertRole == null) {
        }else {
            roleId = expertRole.getId();
        }
        // 可以注入RoleService来查询，或者直接返回已知的角色ID
        return roleId; // 假设评审专家的角色ID是3，请根据实际情况调整
    }
} 