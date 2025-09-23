package com.xinkao.erp.user.excel;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.HandleResult;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.common.util.ResultUtils;


import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.user.entity.Role;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.manage.service.ClassInfoService;
import com.xinkao.erp.user.service.RoleService;
import com.xinkao.erp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Listener - 用于监听导入学生调班
 * @Description:
 * @Author: 777
 * @Date: 2021/8/3 18:04
 */
@Slf4j
public class UserModelListener extends AnalysisEventListener<UserImportModel> {


    private RedisUtil redisUtils;
    private ResultUtils resultUtils;
    private HttpServletResponse response;

    private List<String> errorList = new ArrayList<>();



    private Map<Integer, User> addUserMap = new HashMap<>();

    private HandleResult handleResult = new HandleResult();

    private List<UserImportErrorModel> userImportErrorModelList = new ArrayList<>();

    private UserService userService;

    private ClassInfoService classInfoService;

    private RoleService roleService;

    private final String token;

    public UserModelListener(HttpServletResponse response, String token) {
        this.response = response;
        this.token = token;
        this.redisUtils = SpringUtil.getBean(RedisUtil.class);
        this.resultUtils = SpringUtil.getBean(ResultUtils.class);
        this.userService = SpringUtil.getBean(UserService.class);
        this.classInfoService = SpringUtil.getBean(ClassInfoService.class);
        this.roleService = SpringUtil.getBean(RoleService.class);

    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        log.error("exception");
        super.onException(exception, context);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(UserImportModel userImportModel, AnalysisContext analysisContext) {
        log.debug("解析到一条数据:{}", JSON.toJSONString(userImportModel));
        // 可以增加对数据的必要解析
        int rowNum = analysisContext.readRowHolder().getRowIndex();
        String errorMsg = checkEmptyError(userImportModel, rowNum + 1);
        if(StrUtil.isNotBlank(errorMsg)){
            errorList.add(errorMsg);
            handleResult.setErrorList(errorList);
            log.error("姓名：{}，导入信息有误：{}", userImportModel.getRealName(), errorMsg);
            return;
        }


        String msg = "";
        String sex = userImportModel.getSex();
        String realName = userImportModel.getRealName();
        String roleName = userImportModel.getRoleName();
        String mobile = userImportModel.getMobile();
        String idCard = userImportModel.getIdCard();
        String className = userImportModel.getClassName();
        
        // 根据角色获取角色信息，生成用户名
        Role role = roleService.lambdaQuery().eq(Role::getRoleName,roleName).eq(Role::getIsDel,0).one();
        if (role == null){
            msg = "该角色不存在";
            errorList.add(getHandleMsg(rowNum + 1, msg));
            handleResult.setErrorList(errorList);

            UserImportErrorModel userImportErrorModel = new UserImportErrorModel();
            userImportErrorModel.setSex(sex);
            userImportErrorModel.setRealName(realName);
            userImportErrorModel.setRoleName(roleName);
            userImportErrorModel.setMobile(mobile);
            userImportErrorModel.setIdCard(idCard);
            userImportErrorModel.setClassName(className);
            userImportErrorModel.setErrorInfo(msg);
            userImportErrorModelList.add(userImportErrorModel);

            log.error("姓名：{}，导入信息有误：{}", realName, msg);
            return;
        }
        
        // 根据角色验证班级是否必填（只有学生和社会考生需要班级）
        ClassInfo classInfo = null;
        boolean needClass = role.getId().equals(3) || role.getId().equals(21); // 学生(3)和社会考生(21)需要班级
        
        if (needClass) {
            // 需要班级的角色必须填写班级
            if (StrUtil.isBlank(className)) {
                msg = "该角色必须填写班级名称";
                errorList.add(getHandleMsg(rowNum + 1, msg));
                handleResult.setErrorList(errorList);

                UserImportErrorModel userImportErrorModel = new UserImportErrorModel();
                userImportErrorModel.setSex(sex);
                userImportErrorModel.setRealName(realName);
                userImportErrorModel.setRoleName(roleName);
                userImportErrorModel.setMobile(mobile);
                userImportErrorModel.setIdCard(idCard);
                userImportErrorModel.setClassName(className);
                userImportErrorModel.setErrorInfo(msg);
                userImportErrorModelList.add(userImportErrorModel);

                log.error("姓名：{}，导入信息有误：{}", realName, msg);
                return;
            }
            
            // 验证班级是否存在
            classInfo = classInfoService.lambdaQuery().eq(ClassInfo::getClassName,className).eq(ClassInfo::getIsDel,0).one();
            if (classInfo == null){
                msg = "该班级不存在";
                errorList.add(getHandleMsg(rowNum + 1, msg));
                handleResult.setErrorList(errorList);

                UserImportErrorModel userImportErrorModel = new UserImportErrorModel();
                userImportErrorModel.setSex(sex);
                userImportErrorModel.setRealName(realName);
                userImportErrorModel.setRoleName(roleName);
                userImportErrorModel.setMobile(mobile);
                userImportErrorModel.setIdCard(idCard);
                userImportErrorModel.setClassName(className);
                userImportErrorModel.setErrorInfo(msg);
                userImportErrorModelList.add(userImportErrorModel);

                log.error("姓名：{}，导入信息有误：{}", realName, msg);
                return;
            }
        } else {
            // 不需要班级的角色，如果填写了班级也要验证是否存在
            if (StrUtil.isNotBlank(className)) {
                classInfo = classInfoService.lambdaQuery().eq(ClassInfo::getClassName,className).eq(ClassInfo::getIsDel,0).one();
                if (classInfo == null){
                    msg = "该班级不存在";
                    errorList.add(getHandleMsg(rowNum + 1, msg));
                    handleResult.setErrorList(errorList);

                    UserImportErrorModel userImportErrorModel = new UserImportErrorModel();
                    userImportErrorModel.setSex(sex);
                    userImportErrorModel.setRealName(realName);
                    userImportErrorModel.setRoleName(roleName);
                    userImportErrorModel.setMobile(mobile);
                    userImportErrorModel.setIdCard(idCard);
                    userImportErrorModel.setClassName(className);
                    userImportErrorModel.setErrorInfo(msg);
                    userImportErrorModelList.add(userImportErrorModel);

                    log.error("姓名：{}，导入信息有误：{}", realName, msg);
                    return;
                }
            }
        }
        
        // 检查手机号是否已存在
        User existingUserByMobile = userService.lambdaQuery().eq(User::getMobile, mobile).eq(User::getIsDel, 0).one();
        if (existingUserByMobile != null) {
            msg = "该手机号已存在：" + mobile;
            errorList.add(getHandleMsg(rowNum + 1, msg));
            handleResult.setErrorList(errorList);

            UserImportErrorModel userImportErrorModel = new UserImportErrorModel();
            userImportErrorModel.setSex(sex);
            userImportErrorModel.setRealName(realName);
            userImportErrorModel.setRoleName(roleName);
            userImportErrorModel.setMobile(mobile);
            userImportErrorModel.setIdCard(idCard);
            userImportErrorModel.setClassName(className);
            userImportErrorModel.setErrorInfo(msg);
            userImportErrorModelList.add(userImportErrorModel);

            log.error("姓名：{}，导入信息有误：{}", realName, msg);
            return;
        }
        
        // 生成用户名
        String username = generateUsername(role.getId(), mobile);
        
        //获取用户信息，检查用户名是否已存在
        User user = userService.lambdaQuery().eq(User::getUsername,username).eq(User::getIsDel,0).one();
        //判断用户是否存在
        if (user != null){
            msg = "该用户名已存在：" + username;
            errorList.add(getHandleMsg(rowNum + 1, msg));
            handleResult.setErrorList(errorList);

            UserImportErrorModel userImportErrorModel = new UserImportErrorModel();
            userImportErrorModel.setSex(sex);
            userImportErrorModel.setRealName(realName);
            userImportErrorModel.setRoleName(roleName);
            userImportErrorModel.setMobile(mobile);
            userImportErrorModel.setIdCard(idCard);
            userImportErrorModel.setClassName(className);
            userImportErrorModel.setErrorInfo(msg);
            userImportErrorModelList.add(userImportErrorModel);

            log.error("姓名：{}，导入信息有误：{}", realName, msg);
            return;
        }



        try {
            User addUser = new User();
            addUser.setUsername(username);
            addUser.setSex("男".equals(sex)?1:2);
            addUser.setRealName(realName);
            // 只有需要班级且班级存在时才设置班级ID
            if (classInfo != null) {
                addUser.setClassId(classInfo.getId());
            }
            addUser.setRoleId(role.getId());
            addUser.setMobile(mobile);
            addUser.setIdCard(idCard);
            //赋值
            addUserMap.put(rowNum, addUser);
            //将正确的也进行保存（错误原因为空）
            UserImportErrorModel userImportErrorModel = new UserImportErrorModel();
            userImportErrorModel.setSex(sex);
            userImportErrorModel.setRealName(realName);
            userImportErrorModel.setRoleName(roleName);
            userImportErrorModel.setMobile(mobile);
            userImportErrorModel.setIdCard(idCard);
            userImportErrorModel.setClassName(className);
            userImportErrorModel.setErrorInfo(msg);
            userImportErrorModelList.add(userImportErrorModel);
        } catch (BusinessException e) {
            errorList.add(getHandleMsg(rowNum + 1, e.getMessage()));
            handleResult.setErrorList(errorList);
            return;
        }
    }

    private String checkEmptyError(UserImportModel userImportModel, int row) {
        String msg = "";
        String sex = userImportModel.getSex();
        String realName = userImportModel.getRealName();
        String roleName = userImportModel.getRoleName();
        String mobile = userImportModel.getMobile();
        String idCard = userImportModel.getIdCard();
        String className = userImportModel.getClassName();
        
        if (StrUtil.isBlank(realName)){
            msg = "姓名不能为空";
        }else if (StrUtil.isBlank(idCard)){
            msg = "身份证号不能为空";
        }else if (StrUtil.isBlank(mobile)){
            msg = "手机号不能为空";
        }else if (StrUtil.isBlank(sex)){
            msg = "性别不能为空";
        }else if (StrUtil.isBlank(roleName)){
            msg = "角色名称不能为空";
        }
        //如果msg不为空，则添加到错误集合
        if (StrUtil.isNotBlank(msg)){
            UserImportErrorModel userImportErrorModel = new UserImportErrorModel();
            userImportErrorModel.setSex(sex);
            userImportErrorModel.setRealName(realName);
            userImportErrorModel.setRoleName(roleName);
            userImportErrorModel.setMobile(mobile);
            userImportErrorModel.setIdCard(idCard);
            userImportErrorModel.setClassName(className);
            userImportErrorModel.setErrorInfo(msg);
            userImportErrorModelList.add(userImportErrorModel);
        }
        return getHandleMsg(row, msg);
    }

    /**
     * 返回msg
     * @param index
     * @param msg
     * @return
     */
    private String getHandleMsg(Integer index, String msg){
        if(StrUtil.isBlank(msg)){
            return "";
        }
        return resultUtils.getErrMsg(index, msg);
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("所有数据解析完成！");
    }

    private void saveData() {
        handleResult.setTotalCount(addUserMap.size() + errorList.size());
        userService.importUser(response,addUserMap, handleResult,userImportErrorModelList,token);
        log.info("存储数据库成功！");
    }

    /**
     * 生成用户名
     * @param roleId 角色ID
     * @param mobile 手机号
     * @return 用户名
     */
    private String generateUsername(Integer roleId, String mobile) {
        String prefix = "";
        switch (roleId.toString()) {
            case "18":
                prefix = "XX"; // 学校管理员
                break;
            case "19":
                prefix = "BJ"; // 社保局管理员
                break;
            case "2":
                prefix = "JS"; // 教师
                break;
            case "3":
                prefix = "XS"; // 学生
                break;
            case "20":
                prefix = "ZJ"; // 评审专家
                break;
            case "21":
                prefix = "SH"; // 社会考生
                break;
            default:
                prefix = "YH"; // 默认用户
                break;
        }
        return prefix + mobile;
    }

    /**
     * 返回结果
     * @return
     */
    public BaseResponse getResult() {
        return BaseResponse.ok(handleResult.getResult());
    }
}
