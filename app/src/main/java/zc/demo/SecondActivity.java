package zc.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_second, R.id.tv_second_down})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_second:
                finish();
                break;
            case R.id.tv_second_down:
                Log.i("Test", "tv_second_down: ");
                break;
        }
    }
}
