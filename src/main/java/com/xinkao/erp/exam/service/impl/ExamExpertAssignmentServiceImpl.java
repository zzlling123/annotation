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
            List<ExamExpert> examExperts = examExpertService.lambdaQuery()
                    .eq(ExamExpert::getExamId, examId)
                    .list() ;
            if (examExperts.isEmpty()) {
                return false;
            }
            List<User> experts = new ArrayList<>();
            examExperts.forEach(examExpert -> {
                Integer expertId = examExpert.getExpertId();
                experts.add(userService.getById(expertId));
            });
            if (experts.isEmpty()) {
                return false;
            }

            List<ExamPageUser> examPapers = examPageUserService.lambdaQuery()
                    .eq(ExamPageUser::getExamId, examId)
                    .list();
            
            if (examPapers.isEmpty()) {
                return false;
            }

            this.lambdaUpdate()
                    .eq(ExamExpertAssignment::getExamId, examId)
                    .set(ExamExpertAssignment::getIsDel, 1)
                    .update();

            List<ExamExpertAssignment> assignments = new ArrayList<>();
            int expertCount = experts.size();
            int paperCount = examPapers.size();
            for (int i = 0; i < paperCount; i++) {
                 ExamPageUser paper = examPapers.get(i);
                 User expert = experts.get(i % expertCount);

                 User student = userService.getById(paper.getUserId());
                 String studentName = student != null ? student.getRealName() : "";

                 ExamExpertAssignment assignment = new ExamExpertAssignment();
                 assignment.setExamId(examId);
                 assignment.setExpertId(expert.getId());
                 assignment.setUserId(paper.getUserId());
                 assignment.setUserName(studentName);
                 assignment.setStatus(0);
                 assignment.setIsDel(0);

                 assignments.add(assignment);
             }

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

    @Override
    public List<ExamExpertAssignment> getAssignmentsByExamIdAndExpertId(Integer examId, Integer expertId) {
        return this.lambdaQuery()
                .eq(ExamExpertAssignment::getExamId, examId)
                .eq(ExamExpertAssignment::getExpertId, expertId)
                .eq(ExamExpertAssignment::getIsDel, 0)
                .list();
    }
    
    
    private Integer getExpertRoleId() {
        int roleId = 0;
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName, "评审专家");
        Role expertRole = roleService.getOne(queryWrapper);
        if (expertRole == null) {
        }else {
            roleId = expertRole.getId();
        }
        return roleId;
    }
} 