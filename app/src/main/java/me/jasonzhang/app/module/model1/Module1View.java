package me.jasonzhang.app.module.model1;

import me.jasonzhang.app.base.BaseView;

/**
 * Created by JifengZhang on 2017/4/27.
 */

public interface Module1View extends BaseView{
    String setResultText(String result);
    void showError(String errMsg);
    void showBegin(String msg);
    void showEnd(String msg);
}
