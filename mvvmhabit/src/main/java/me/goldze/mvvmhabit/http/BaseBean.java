package me.goldze.mvvmhabit.http;

import java.io.Serializable;

public class BaseBean<T> implements Serializable {

    public int code;
    public String message;

    public T data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isOk() {
        return code == 200;
    }
}
