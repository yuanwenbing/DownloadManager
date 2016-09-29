package com.yuan.library.db.exception;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Yuan on 27/09/2016:11:19 AM.
 * <p/>
 * Description:com.yuan.library.db.exception.OkHttpError
 */

public class OkHttpError extends Exception {
    public static final int TYPE_NO_CONNECTION = 0x01;//网络错误
    public static final int TYPE_TIMEOUT = 0x02;//连接超时
    public static final int TYPE_PARSE = 0x03;// 数据解析错误
    public static final int TYPE_ERROR = 0x04;// 其他原因数据解析失败


    public int mErrorType;

    public OkHttpError(Exception error) {
        super(error);
        initType();
    }

    private void initType() {
        Throwable error = getCause();
        if (error == null) {
            return;
        }
        if (error instanceof UnknownHostException) {
            mErrorType = TYPE_NO_CONNECTION;
        } else if (error instanceof SocketTimeoutException) {
            mErrorType = TYPE_TIMEOUT;
        } else if (error instanceof JsonSyntaxException) {
            mErrorType = TYPE_PARSE;
        } else if (error instanceof IOException) {
            mErrorType = TYPE_PARSE;
        } else if (error instanceof Exception) {
            mErrorType = TYPE_ERROR;
        }
    }

    public int getType() {
        return mErrorType;
    }
}
