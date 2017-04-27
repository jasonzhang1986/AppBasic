package me.jasonzhang.appbase.module.main;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.jasonzhang.appbase.R;
import me.jasonzhang.appbase.base.BaseActivity;

/**
 * Created by JifengZhang on 2017/4/12.
 */

public class MainActivity extends BaseActivity<MainPresenter> implements MainView{
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
    public void setResultText(@Nullable String result) {
        mResult.setText(result);
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
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        ButterKnife.bind(this);
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }
}
