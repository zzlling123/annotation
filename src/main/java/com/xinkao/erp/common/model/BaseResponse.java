package com.xinkao.erp.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.xinkao.erp.common.enums.system.XinKaoState;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BaseResponse<T> {

    private String state;

    private String msg;

    private String devMsg;

    private T data;

    public BaseResponse(String state, String msg, T data) {
        this.state = state;
        this.msg = msg;
        this.data = data;
    }

    @NonNull
    public static <T> BaseResponse<T> ok(@Nullable String msg, @Nullable T data) {
        return new BaseResponse<>(XinKaoState.OK.value(), msg, data);
    }

    @NonNull
    public static <T> BaseResponse<T> ok(@Nullable String msg) {
        return ok(msg, null);
    }

    public static <T> BaseResponse<T> ok(@Nullable T data) {
        return ok(XinKaoState.OK.getReasonPhrase() , data);
    }

    public static BaseResponse ok() {
        return new BaseResponse<>(XinKaoState.OK.value(), XinKaoState.OK.getReasonPhrase(), null);
    }

    public static BaseResponse fail() {
        return new BaseResponse<>(XinKaoState.FAIL.value(), XinKaoState.FAIL.getReasonPhrase(), null);
    }

    @NonNull
    public static <T> BaseResponse<T> fail(@Nullable String msg) {
        return fail(msg, null);
    }



    @NonNull
    public static <T> BaseResponse<T> fail(@Nullable String msg, @Nullable T data) {
        return new BaseResponse<>(XinKaoState.FAIL.value(), msg, data);
    }

    @NonNull
    public static <T> BaseResponse<T> other(@Nullable String msg) {
        return new BaseResponse<>(XinKaoState.OTHER.value(), msg,null);
    }

    @NonNull
    public static <T> BaseResponse<T> other(@Nullable String msg, @Nullable T data) {
        return new BaseResponse<>(XinKaoState.OTHER.value(), msg,data);
    }
}
