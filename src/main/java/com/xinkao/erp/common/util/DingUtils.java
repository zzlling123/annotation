package com.xinkao.erp.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import com.xinkao.erp.common.model.entity.DingInfoEntity;
import com.xinkao.erp.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 钉钉相关工具类.
 *
 * @Author 777
 */
@Slf4j
@Component
public class DingUtils {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${ding.APPKEY}")
    private String APPKEY;

    @Value("${ding.APPSCERET}")
    private String APPSCERET;

    @Value("${ding.TOKENKEY}")
    private String TOKENKEY;

    /* 获取钉钉token */
    public String getDingToken() {
        String appKey = APPKEY;
        String token = redisUtil.get(TOKENKEY);
        if (StrUtil.isBlank(token)){
            String appSecret = APPSCERET;
            // 获取token
            try {
                DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
                OapiGettokenRequest request = new OapiGettokenRequest();
                request.setAppkey(appKey);
                request.setAppsecret(appSecret);
                request.setHttpMethod("GET");
                OapiGettokenResponse response = client.execute(request);

                JSONObject jsonObject = JSON.parseObject(response.getBody());
                token = jsonObject.get("access_token").toString();
                redisUtil.set(TOKENKEY, token, 2, TimeUnit.HOURS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    /* 钉钉登录获取用户的userid（企业内部应用免登录） */
    public String getuserId(String code, String token) {
        String userId = "";
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
            OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
            request.setCode(code);
            request.setHttpMethod("GET");
            OapiUserGetuserinfoResponse response = client.execute(request, token);
            userId = response.getUserid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;
    }

    /* 根据手机号获取用户userid 返回 errcode（0 成功，其余的code代表错误） userid */
    public String getDingUserIdByMobile(String mobile) {
        // 获取钉钉token
        String token = getDingToken();
        int code = 0;
        String dingUserId = "";
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile");
            OapiUserGetByMobileRequest req = new OapiUserGetByMobileRequest();
            req.setMobile(mobile);
            req.setHttpMethod("GET");
            OapiUserGetByMobileResponse rsp = client.execute(req, token);
            JSONObject jsonObject = JSON.parseObject(rsp.getBody());
            code = jsonObject.getInteger("errcode");
            if (code == 0) {// 成功获取userId
                dingUserId = jsonObject.getString("userid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dingUserId;
    }

    /* 钉钉登录获取登陆者的姓名和手机号 */
    public User getUser(String code) {
        // 获取token和用户id
        String token = getDingToken();
        String dingUserId = getuserId(code, token);
        String userName = "";
        String mobile = "";
        try {
            // 根据userid获取登陆者的姓名
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get");
            OapiUserGetRequest req = new OapiUserGetRequest();
            req.setUserid(dingUserId);
            req.setHttpMethod("GET");
            OapiUserGetResponse rsp = client.execute(req, token);
            JSONObject jsonObject = JSONObject.parseObject(rsp.getBody());
            userName = jsonObject.getString("name");
            mobile = jsonObject.getString("mobile");
        } catch (Exception e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setRealName(userName);
        user.setMobile(mobile);
        user.setDingId(dingUserId);
        return user;
    }

    /* 钉钉获取最新组织架构 */
    public List<DingInfoEntity> getDingUserList() {
        String token = getDingToken();
        List<DingInfoEntity> list = new ArrayList<>();
        if (!StringUtils.hasText(token)) {
            return list;
        }
        log.info("开始更新钉钉架构");

        try {
            //直接获取钉钉架构中教师团队列表
            list = getDept(1L, token);
            System.out.println("完成更新钉钉架构");
        } catch (ApiException e) {
            log.info("更新钉钉组织架构", e);
        }
        return list;
    }


    /**
     * 获取钉钉组织架构
     *
     * @param deptId 部门id
     * @param token  token
     * @return 钉钉组织架构
     */
    private List<DingInfoEntity> getDept(Long deptId, String token) throws ApiException {
        List<DingInfoEntity> dingInfoList = new ArrayList<>();
        // 根据id获取部门并整理数据
        for (OapiV2DepartmentListsubResponse.DeptBaseResponse deptBaseResponse : getListSub(deptId, token)) {
            if (deptBaseResponse.getDeptId() > 0) {
                dingInfoList.add(
                        new DingInfoEntity(
                                String.valueOf(deptBaseResponse.getDeptId()),
                                deptBaseResponse.getName(),
                                null,
                                deptBaseResponse.getParentId().toString(),
                                null,
                                getDept(deptBaseResponse.getDeptId(), token),
                                true,
                                false
                        ));
            }
        }

        // 根据id获取用户并整理数据
        for (OapiV2UserListResponse.ListUserResponse listUserResponse : getUserList(0L, deptId, token)) {
            dingInfoList.add(
                    new DingInfoEntity(
                            String.valueOf(listUserResponse.getUserid()),
                            listUserResponse.getName(),
                            listUserResponse.getMobile(),
                            deptId.toString(),
                            listUserResponse.getTitle() == null ? "" : listUserResponse.getTitle(),
                            null,
                            false,
                            listUserResponse.getLeader()
                    ));
        }

        if (dingInfoList.size() == 0) {
            return null;
        }

        return dingInfoList;
    }

    /**
     * @param deptId      部门id
     * @param accessToken token
     * @return 部门列表
     * @throws ApiException 异常
     */
    public List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getListSub(Long deptId, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsub");
        OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
        req.setDeptId(deptId);
        req.setLanguage("zh_CN");
        OapiV2DepartmentListsubResponse rsp = client.execute(req, accessToken);

        if (rsp.getErrcode() != 0) {
            throw new ApiException(rsp.getErrmsg());
        }

        return rsp.getResult();
    }

    /**
     * @param page        分页
     * @param deptId      部门id
     * @param accessToken token
     * @return List
     * @throws ApiException 异常
     */
    public List<OapiV2UserListResponse.ListUserResponse> getUserList(Long page, Long deptId, String accessToken) throws ApiException {

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
        OapiV2UserListRequest req = new OapiV2UserListRequest();
        req.setDeptId(deptId);
        req.setCursor(page * 100L);
        req.setSize(100L);
        req.setOrderField("modify_desc");
        req.setContainAccessLimit(false);
        req.setLanguage("zh_CN");
        OapiV2UserListResponse rsp = client.execute(req, accessToken);

        List<OapiV2UserListResponse.ListUserResponse> userList =
                new ArrayList<>(rsp.getResult().getList());

        if (rsp.getResult().getHasMore()) {
            // 如果还有人员
            userList.addAll(getUserList(page + 1, deptId, accessToken));
        }

        if (rsp.getErrcode() != 0) {
            throw new ApiException(rsp.getErrmsg());
        }

        return userList;
    }


//    /* 钉钉获取家校通讯录组织架构 */
//    public List<DingDeptDto> getEduDingDeptList(Long superId,Long page) throws ApiException {
//        String token = getDingToken();
//        List<DingDeptDto> dingDeptDtoList = new ArrayList<>();
//        if (!StringUtils.hasText(token)) {
//            return dingDeptDtoList;
//        }
//        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/edu/dept/list");
//        OapiEduDeptListRequest req = new OapiEduDeptListRequest();
//        req.setPageSize(30L);
//        req.setPageNo(page);
//        req.setSuperId(superId);
//        OapiEduDeptListResponse rsp = client.execute(req, token);
//        JSONObject json = JSON.parseObject(rsp.getBody());
//        JSONObject result = json.getJSONObject("result");
//        JSONArray details = result.getJSONArray("details");
////        dingDeptDtoList = BeanUtil.copyToList(details, DingDeptDto.class);
//
//        for (int i = 0; i < details.size(); i++) {
//            JSONObject detailJson = details.getJSONObject(i);
//            DingDeptDto dto = new DingDeptDto();
//            int section = 0;
//            if (superId == 261890136L){
//                section = 2;
//            }else if (superId == 261890137L){
//                section = 1;
//            }
//            dto.setSection(section);
//            dto.setDeptId(detailJson.getString("dept_id"));
//            dto.setDeptType(detailJson.getString("dept_type"));
//            if (StrUtil.isBlank(detailJson.getString("nick")) || "".equals(detailJson.getString("nick"))){
//                dto.setName(detailJson.getString("name"));
//            }else{
//                dto.setName(detailJson.getString("nick"));
//            }
//            dingDeptDtoList.add(dto);
//        }
//        return dingDeptDtoList;
//    }
//
//    /* 获取班级下人员列表 */
//    public List<DingUserVo> getEduDingUserList(Long classId,Long page) throws ApiException {
//        String token = getDingToken();
//        List<DingUserVo> dingUserVoList = new ArrayList<>();
//        if (!StringUtils.hasText(token)) {
//            return dingUserVoList;
//        }
//        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/edu/user/list");
//        OapiEduUserListRequest req = new OapiEduUserListRequest();
//        req.setPageSize(30L);
//        req.setPageNo(page);
//        req.setRole("student");
//        req.setClassId(classId);
//        OapiEduUserListResponse rsp = client.execute(req, token);
//        JSONObject json = JSON.parseObject(rsp.getBody());
//        JSONObject result = json.getJSONObject("result");
//        JSONArray details = result.getJSONArray("details");
//        dingUserVoList = BeanUtil.copyToList(details, DingUserVo.class);
//        return dingUserVoList;
//    }

    /* 获取班级下人员列表 */
    public String getMobileDingUser(String dingUserId) throws ApiException {
        String token = getDingToken();
        if (!StringUtils.hasText(token)) {
            return null;
        }
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
        OapiV2UserGetRequest req = new OapiV2UserGetRequest();
        req.setUserid(dingUserId);
        req.setLanguage("en_US");
        OapiV2UserGetResponse rsp = client.execute(req, token);
        System.out.println(rsp.getBody());
        JSONObject json = JSON.parseObject(rsp.getBody());
        int code = json.getInteger("errcode");
        if (code == 0) {// 成功获取user信息
            JSONObject result = json.getJSONObject("result");
            return result.getString("mobile");
        }else{
            return null;
        }
    }
}
