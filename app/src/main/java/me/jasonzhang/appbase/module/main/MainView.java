package me.jasonzhang.appbase.module.main;

import me.jasonzhang.appbase.base.BaseView;

/**
 * Created by JifengZhang on 2017/4/27.
 */

public interface MainView extends BaseView{
    void setResultText(String result);
    void showError(String errMsg);
    void showBegin(String msg);
    void showEnd(String msg);
}
