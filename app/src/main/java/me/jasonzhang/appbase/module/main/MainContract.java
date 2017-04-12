package me.jasonzhang.appbase.module.main;

import me.jasonzhang.appbase.base.BasePresenter;
import me.jasonzhang.appbase.base.BaseView;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public interface MainContract {
    interface Presenter extends BasePresenter {
        void testRxJava();
        void testZip();
        void testZipWith();
        void testZipUseLambda();
        void testZipWithUseLambda();
        void testComplex();
        void testComplexUseLambda();
    }

    interface View extends BaseView<Presenter> {
        void setResultText(String result);
        void showError(String errMsg);
        void showBegin(String msg);
        void showEnd(String msg);
    }
}
