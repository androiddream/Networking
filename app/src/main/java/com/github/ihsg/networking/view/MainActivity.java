package com.github.ihsg.networking.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.ihsg.networking.R;
import com.github.ihsg.networking.model.bean.GitHubUserBean;
import com.github.ihsg.networking.presenter.GitHubUserPresenter;

public class MainActivity extends BaseActivity implements IMainView {

    private GitHubUserPresenter presenter;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.presenter = new GitHubUserPresenter(this);
        this.textView = (TextView) findViewById(R.id.text_content);
        this.button = (Button) findViewById(R.id.btn_action);

        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.loadGitHubUser();
            }
        });

    }

    @Override
    public void updateView(GitHubUserBean gitHubUserBean) {
        this.textView.setText(gitHubUserBean.toString());
    }
}
