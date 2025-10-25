package com.xinkao.erp.manual.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("usage_document")
public class Manual extends DataEntity {

    @TableField("file_url")
    private String fileUrl;

    @TableField("user_type")
    private Integer userType;

    public enum UserTypeEnum {
        ADMIN(1, "管理员"),
        SCHOOL_ADMIN(2, "学校管理员"),
        SOCIAL_SECURITY_ADMIN(3, "社保局管理员"),
        STUDENT(4, "学生用户"),
        TEACHER(5, "老师用户"),
        EXPERT(6, "评审专家"),
        SOCIAL_CANDIDATE(7, "社会考生");

        private int code;
        private String name;

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        UserTypeEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public static UserTypeEnum getByCode(int code) {
            for (UserTypeEnum userType : UserTypeEnum.values()) {
                if (userType.getCode() == code) {
                    return userType;
                }
            }
            return null;
        }

        public static String getNameByCode(int code) {
            UserTypeEnum userType = getByCode(code);
            return userType != null ? userType.getName() : "未知类型";
        }
    }
} 