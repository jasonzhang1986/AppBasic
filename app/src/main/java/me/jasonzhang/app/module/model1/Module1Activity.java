package me.jasonzhang.app.module.model1;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.jasonzhang.app.R;
import me.jasonzhang.app.base.BaseActivity;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public class Module1Activity extends BaseActivity<Module1Presenter> implements Module1View {
    @BindView(R.id.tv_result) TextView mResult;

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7})
    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                presenter.testRxJava();
                break;
            case R.id.btn2:
                presenter.testZip();
                break;
            case R.id.btn3:
                presenter.testZipWith();
                break;
            case R.id.btn4:
                presenter.testZipUseLambda();
                break;
            case R.id.btn5:
                presenter.testZipWithUseLambda();
                break;
            case R.id.btn6:
                presenter.testComplex();
                break;
            case R.id.btn7:
                presenter.testComplexUseLambda();
                break;
        }
    }

    @Override
    public String setResultText(@Nullable String result) {
        mResult.setText(result);
        return null;
    }

    @Override
    public void showError(@Nullable String errMsg) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBegin(@Nullable String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEnd(@Nullable String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_module1;
    }

    @Override
    protected void init() {
        ButterKnife.bind(this);
    }

    @Override
    protected Module1Presenter createPresenter() {
        return new Module1Presenter();
    }
}
