package com.xinkao.erp.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.xinkao.erp.common.enums.system.XinKaoState;

/**
 * 全局的返回内容
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BaseResponse<T> {

    /**
     * 成功/失败
     */
    private String state;

    /**
     * 返回的信息
     */
    private String msg;

    /**
     *  返回的开发模式下的信息
     */
    private String devMsg;

    /**
     * 返回的数据
     */
    private T data;

    public BaseResponse(String state, String msg, T data) {
        this.state = state;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 创建一个成功的返回，接收msg和data参数
     * @param msg 返回的消息
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return
     */
    @NonNull
    public static <T> BaseResponse<T> ok(@Nullable String msg, @Nullable T data) {
        return new BaseResponse<>(XinKaoState.OK.value(), msg, data);
    }

    /**
     * 创建一个成功的返回，只接受msg参数（
     * @param msg 返回的消息
     * @return
     */
    @NonNull
    public static <T> BaseResponse<T> ok(@Nullable String msg) {
        return ok(msg, null);
    }

    /**
     * 创建一个成功的返回，只接受data参数
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return
     */
    public static <T> BaseResponse<T> ok(@Nullable T data) {
        return ok(XinKaoState.OK.getReasonPhrase() , data);
    }

    /**
     * 创建一个默认的成功返回
     * @return
     */
    public static BaseResponse ok() {
        return new BaseResponse<>(XinKaoState.OK.value(), XinKaoState.OK.getReasonPhrase(), null);
    }

    /**
     * 创建一个默认的失败返回
     * @return
     */
    public static BaseResponse fail() {
        return new BaseResponse<>(XinKaoState.FAIL.value(), XinKaoState.FAIL.getReasonPhrase(), null);
    }

    /**
     * 创建一个失败的返回，只接受msg参数（
     * @param msg 返回的消息
     * @return
     */
    @NonNull
    public static <T> BaseResponse<T> fail(@Nullable String msg) {
        return fail(msg, null);
    }



    /**
     * 创建一个失败的返回，接收msg和data参数
     * @param msg 返回的消息
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return
     */
    @NonNull
    public static <T> BaseResponse<T> fail(@Nullable String msg, @Nullable T data) {
        return new BaseResponse<>(XinKaoState.FAIL.value(), msg, data);
    }

    /**
     * 创建一个其他的返回，只接受msg参数（
     * @param msg 返回的消息
     * @return
     */
    @NonNull
    public static <T> BaseResponse<T> other(@Nullable String msg) {
        return new BaseResponse<>(XinKaoState.OTHER.value(), msg,null);
    }

    /**
     * 创建一个其他的返回，只接受错误信息（
     * @param data 返回的消息
     * @return
     */
    @NonNull
    public static <T> BaseResponse<T> other(@Nullable String msg, @Nullable T data) {
        return new BaseResponse<>(XinKaoState.OTHER.value(), msg,data);
    }
}
