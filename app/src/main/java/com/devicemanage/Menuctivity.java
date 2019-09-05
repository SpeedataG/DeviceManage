package com.devicemanage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.devicemanage.utils.SharedPreferencesUitl;

public class Menuctivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnInstorage;
    private Button mBtnOutstorage;
    private Button mBtnMovestorage;
    private Button mBtnCheckstorage;
    /**
     * 设置2.4G标签RSSI值
     */
    private EditText mEtRssi;
    private SharedPreferencesUitl sharedPreferencesUitl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuctivity);
        initView();
        sharedPreferencesUitl = new SharedPreferencesUitl(this, "manages");

    }

    @Override
    protected void onResume() {
        super.onResume();
//        DBUitl dbUitl = new DBUitl();
//        List<Datas> datas = dbUitl.queryAll();
//        for (int i = 0; i < datas.size(); i++) {
//            Log.i("db", "onCreate: " + datas.get(i).getLpieceDatas() + "包装：" + datas.get(i).getBpieceDatas().get(0));
//        }
    }

    private void startAct(Class activity) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        startActivity(intent);
    }

    private void initView() {
        mBtnInstorage = findViewById(R.id.btn_instorage);
        mBtnInstorage.setOnClickListener(this);
        mBtnOutstorage = findViewById(R.id.btn_outstorage);
        mBtnOutstorage.setOnClickListener(this);
        mBtnMovestorage = findViewById(R.id.btn_movestorage);
        mBtnMovestorage.setOnClickListener(this);
        mBtnCheckstorage = findViewById(R.id.btn_checkstorage);
        mBtnCheckstorage.setOnClickListener(this);
        mEtRssi = findViewById(R.id.et_rssi);
//        mEtRssi.seton
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_instorage:
                sharedPreferencesUitl.write("rrsi", mEtRssi.getText().toString());
                startAct(InstorageActivity.class);
                break;
            case R.id.btn_outstorage:
                sharedPreferencesUitl.write("rrsi", mEtRssi.getText().toString());
                startAct(OutStorageActivity.class);
                break;
            case R.id.btn_movestorage:
                sharedPreferencesUitl.write("rrsi", mEtRssi.getText().toString());
                startAct(MoveStorageActivity.class);
                break;
            case R.id.btn_checkstorage:
                sharedPreferencesUitl.write("rrsi", mEtRssi.getText().toString());
                startAct(CheckActivity.class);
                break;
        }
    }
}
