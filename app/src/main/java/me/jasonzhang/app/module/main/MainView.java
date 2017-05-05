package me.jasonzhang.app.module.main;

import me.jasonzhang.app.base.BaseView;

/**
 * Created by JifengZhang on 2017/4/27.
 */

public interface MainView extends BaseView{
    String setResultText(String result);
    void showError(String errMsg);
    void showBegin(String msg);
    void showEnd(String msg);
}
