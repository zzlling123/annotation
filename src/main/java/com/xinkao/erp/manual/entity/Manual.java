package com.xinkao.erp.manual.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 使用文档表
 * </p>
 *
 * @author Ldy
 * @since 2025-07-26
 */
@Getter
@Setter
@TableName("usage_document")
public class Manual extends DataEntity {

    /**
     * 文件地址
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 使用人群类型:1-管理员 2-学校管理员 3-社保局管理员 4-学生用户 5-老师用户 6-评审专家 7-社会考生
     */
    @TableField("user_type")
    private Integer userType;

    /**
     * 使用人群类型枚举
     */
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

        /**
         * 根据code获取枚举
         */
        public static UserTypeEnum getByCode(int code) {
            for (UserTypeEnum userType : UserTypeEnum.values()) {
                if (userType.getCode() == code) {
                    return userType;
                }
            }
            return null;
        }

        /**
         * 根据code获取名称
         */
        public static String getNameByCode(int code) {
            UserTypeEnum userType = getByCode(code);
            return userType != null ? userType.getName() : "未知类型";
        }
    }
} 