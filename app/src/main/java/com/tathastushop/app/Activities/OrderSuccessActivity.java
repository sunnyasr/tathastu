package com.tathastushop.app.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tathastushop.app.App.APIConfig;
import com.tathastushop.app.App.MySingleton;
import com.tathastushop.app.PrefManager.RegManager;
import com.tathastushop.app.R;

import java.util.HashMap;
import java.util.Map;

public class OrderSuccessActivity extends AppCompatActivity implements View.OnClickListener {
    private RegManager regManager;
    private TextView tvMemberID, tvPhone, tvPassword;
    private Button btnLoginGo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Order");

        regManager = new RegManager(this);

        tvMemberID = (TextView) findViewById(R.id.tv_mid);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvPassword = (TextView) findViewById(R.id.tv_password);
        btnLoginGo = (Button) findViewById(R.id.btn_yes);
        btnLoginGo.setOnClickListener(this);

        tvMemberID.setText(getIntent().getStringExtra("memberid"));
        tvPhone.setText(getIntent().getStringExtra("phone"));
        tvPassword.setText(getIntent().getStringExtra("password"));
        sendSMS(getIntent().getStringExtra("name"), getIntent().getStringExtra("memberid"), getIntent().getStringExtra("password"), getIntent().getStringExtra("phone"));

//        regManager.setFirstTimeLaunch(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_yes) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();
        }
    }

    public void sendSMS(String name, String memberid, String password, final String mobile) {

        final String message = "Dear " + name +
                "\nThanks for Registration with Tathastu." +
                "\nMember ID: " + memberid +
                "\nPassword: " + password +
                "\nRegards" +
                "\nwww.teamtathastu.com";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.SMS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderSuccessActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("to", mobile);
                params.put("uname", "ABHIJITDAS");
                params.put("pwd", "abhijit@1234");
                params.put("senderid", "TTHSTU");
                params.put("route", "T");
                params.put("msg", message);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

}
