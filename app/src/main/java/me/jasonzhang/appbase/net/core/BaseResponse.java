package me.jasonzhang.appbase.net.core;

/**
 * Created by JifengZhang on 2017/4/5.
 */

public class BaseResponse<T>{
    public int status;
    public String message;
    public T entity;
}
