package me.jasonzhang.appbase.module.main;

import android.os.Bundle;
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

public class MainActivity extends BaseActivity implements MainContract.View{
    @BindView(R.id.tv_result) TextView mResult;
    private MainContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new MainPresenter(this);
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {}

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7})
    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                mPresenter.testRxJava();
                break;
            case R.id.btn2:
                mPresenter.testZip();
                break;
            case R.id.btn3:
                mPresenter.testZipWith();
                break;
            case R.id.btn4:
                mPresenter.testZipUseLambda();
                break;
            case R.id.btn5:
                mPresenter.testZipWithUseLambda();
                break;
            case R.id.btn6:
                mPresenter.testComplex();
                break;
            case R.id.btn7:
                mPresenter.testComplexUseLambda();
                break;
        }
    }

    private int lineNum;
    @Override
    public void setResultText(@Nullable String result) {
        StringBuilder sb = mResult.getText()==null?new StringBuilder():new StringBuilder(mResult.getText()).append("\n\n");
        lineNum +=2;
        if (lineNum>30) {
            lineNum = 0;
            sb = new StringBuilder();
        }
        sb.append(result);
        mResult.setText(sb.append(result).toString());
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
}
