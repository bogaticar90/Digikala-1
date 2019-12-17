package ir.mahdidev.digikala.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.mahdidev.digikala.R;

public class CustomerInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);
    }
    public static Intent newIntent(Context context){
        return new Intent(context , CustomerInfoActivity.class);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}