package me.jasonzhang.app.net;

/**
 * Response的处理类，我们关注的是result字段
 * @param <T>
 */
public class BaseResponse<T>{
    public boolean error;
    public T results;
}
