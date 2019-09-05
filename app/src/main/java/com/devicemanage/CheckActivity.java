package com.devicemanage;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.devicemanage.base.MyAplicatin;
import com.devicemanage.db.Datas;
import com.devicemanage.utils.DBUitl;
import com.devicemanage.utils.Device;
import com.devicemanage.utils.DeviceDatas;
import com.devicemanage.utils.DtatUtils;
import com.devicemanage.utils.SharedPreferencesUitl;
import com.speedata.libutils.DataConversionUtils;
import com.speedata.ui.adapter.CommonAdapter;
import com.speedata.ui.adapter.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * 手动输入标签ID
     */
    private EditText mEdittvCardid;
    private ImageView mImageScan;
    private ListView mListview;
    /**
     * 盘点
     */
    private Button mBtnMovestorage;
    private Device device;
    private List<String> ID = new ArrayList<>();
    private List<Datas> checkDatas = new ArrayList<>();
    private SharedPreferencesUitl sharedPreferencesUitl;
    private DBUitl dbUitl = null;
    private List<Datas> datasList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        initView();
        device = new Device(this);
        dbUitl = new DBUitl();
        EventBus.getDefault().register(this);
        sharedPreferencesUitl = new SharedPreferencesUitl(this, "manages");


    }

    private int lCount;
    private int bCount;

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < dbUitl.queryAll().size(); i++) {
            datasList.add(dbUitl.queryAll().get(i));
        }
        initItem(datasList);
    }

    private void initView() {
        mEdittvCardid = findViewById(R.id.edittv_cardid);
        mImageScan = findViewById(R.id.image_scan);
        mImageScan.setOnClickListener(this);
        mListview = findViewById(R.id.listview);
        mBtnMovestorage = findViewById(R.id.btn_movestorage);
        mBtnMovestorage.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        device.scanOff();
        ID.clear();
        checkDatas.clear();
        EventBus.getDefault().unregister(this);
        device.releaseDecvice();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_movestorage:
                device.scanOn();
                String id = mEdittvCardid.getText().toString();
                Datas datas = dbUitl.queryTAGID(id);
                SystemClock.sleep(1000);
                if (ID.contains(mEdittvCardid.getText().toString())) {
                } else {
                    ID.add(mEdittvCardid.getText().toString());
                    checkDatas.add(datas);
                    initItem(checkDatas);
                }
                break;
            case R.id.image_scan:
                device.scanOn();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void encodeData(DeviceDatas deviceDatas) {
        if (deviceDatas.getCardInfo() != null) {
            byte[] cardDatas = deviceDatas.getCardInfo();
            cardDatas = DtatUtils.cutBytes(cardDatas, 5, 17);
//            mTv.append(DataConversionUtils.byteArrayToString(deviceDatas.getCardInfo()) + "\n");
            //读头ID	 标签类型	标签ID	RSSI	LFID New	LFID Old	UserDefine	Status
            // 2Byte	1Byte	    4Byte	1Byte	2Byte	     2Byte	       4Byte	1Byte
            byte[] dutouid = DtatUtils.cutBytes(cardDatas, 0, 2);
            byte[] leixing = DtatUtils.cutBytes(cardDatas, 2, 1);
            byte[] biaoqianid = DtatUtils.cutBytes(cardDatas, 3, 4);
            byte[] RSSI = DtatUtils.cutBytes(cardDatas, 7, 1);
            byte[] LFIDNew = DtatUtils.cutBytes(cardDatas, 8, 2);
            byte[] LFIDOld = DtatUtils.cutBytes(cardDatas, 10, 2);
            byte[] UserDefine = DtatUtils.cutBytes(cardDatas, 12, 4);
            byte[] Status = DtatUtils.cutBytes(cardDatas, 16, 1);
            Log.i("ssss", "encodeData: " + (DataConversionUtils.byteArrayToString(biaoqianid)));
            if (DataConversionUtils.byteArrayToInt(RSSI) <= Integer.parseInt(sharedPreferencesUitl.read("rrsi", ""))) {
                if (ID.contains(DataConversionUtils.byte
                ArrayToString(biaoqianid))) {
//                Toast.makeText(CheckActivity.this, "已入库或正准备入库", Toast.LENGTH_SHORT).show();
                } else {
                    ID.add(DataConversionUtils.byteArrayToString(biaoqianid));
                    checkDatas.add(new Datas(DataConversionUtils.byteArrayToString(biaoqianid), "润滑油", 0, "大庆", "G库", null, null));
//                if (dbUitl.queryID(DataConversionUtils.byteArrayToString(biaoqianid))) {
//                    Toast.makeText(CheckActivity.this, "已入库或正准备入库", Toast.LENGTH_SHORT).show();
//                } else {
//                    rukuDatas.add(new Datas(DataConversionUtils.byteArrayToString(biaoqianid), "润滑油", "56箱", "大庆", "B库"));
                    initItem(checkDatas);
//                }

                }
            }
            Log.i("aaaaaaaa", "handleMessage: \t读头ID：" + DataConversionUtils.byteArrayToInt(dutouid) + "\n标签类型" + DataConversionUtils.byteArrayToInt(leixing)
                    + "\n标签ID" + DataConversionUtils.byteArrayToInt(biaoqianid) + "\nRSSI" + DataConversionUtils.byteArrayToInt(RSSI)
                    + "\nLFIDNew" + DataConversionUtils.byteArrayToInt(LFIDNew) + "\nLFIDOld" + DataConversionUtils.byteArrayToInt(LFIDOld)
                    + "\nUserDefine" + DataConversionUtils.byteArrayToInt(UserDefine) + "\nStatus" + DataConversionUtils.byteArrayToInt(Status));
        }

        if (deviceDatas.getReaderInfo() != null) {
            byte[] result = deviceDatas.getReaderInfo();
            result = DtatUtils.cutBytes(result, 5, 44);
            //读头ID	   衰减值	低频绑定标志 	低频ID起始	低频ID结束	软件版本
            //   2Byte	1Byte    	1Byte	    2Byte	     2Byte	     36Byte
            byte[] id = DtatUtils.cutBytes(result, 0, 2);
            byte[] shuaiJianZhi = DtatUtils.cutBytes(result, 2, 1);
            byte[] diPinBangDing = DtatUtils.cutBytes(result, 3, 1);
            byte[] dipinIdStart = DtatUtils.cutBytes(result, 4, 2);
            byte[] dipinIdStot = DtatUtils.cutBytes(result, 6, 2);
            byte[] V = DtatUtils.cutBytes(result, 8, 36);
            Log.i("tw", "***********************************" +
                    "\n读头ID：" + DataConversionUtils.byteArrayToInt(id) + "\n衰减值：" + DataConversionUtils.byteArrayToInt(shuaiJianZhi)
                    + "\n低频绑定标志：" + DataConversionUtils.byteArrayToInt(diPinBangDing) + "\n低频ID起始：" + DataConversionUtils.byteArrayToInt(dipinIdStart)
                    + "\n低频ID结束：" + DataConversionUtils.byteArrayToInt(dipinIdStot) + "\n软件版本：" + DataConversionUtils.byteArrayToInt(V)
            );
        }

    }

    public void initItem(final List<Datas> datas) {
        CommonAdapter adapter = new CommonAdapter<Datas>(this, datas, R.layout.item_list_layout) {
            @Override
            public void convert(ViewHolder helper, Datas item) {
                helper.setText(R.id.tv_id, "ID:" + item.getTagId());
                helper.setText(R.id.tv_name, "名称：" + item.getName());
                if (item.getLpieceDatas() == null&&item.getBpieceDatas() != null) {
                    helper.setText(R.id.tv_num, "零件数量：" + 0 + "包装数量：" + item.getBpieceDatas().size());
                } else if (item.getLpieceDatas() != null&&item.getBpieceDatas() == null) {
                    helper.setText(R.id.tv_num, "零件数量：" + item.getLpieceDatas().size() + "包装数量：" + 0);
                } else if (item.getLpieceDatas() == null && item.getBpieceDatas() == null) {
                    helper.setText(R.id.tv_num, "零件数量：" + 0 + "包装数量：" + 0);
                } else {
                    helper.setText(R.id.tv_num, "零件数量：" + item.getLpieceDatas().size() + "包装数量：" + item.getBpieceDatas().size());
                }
                helper.setText(R.id.tv_address, "产地：" + item.getChangdi());
                helper.setText(R.id.tv_storage, "库房：" + item.getStorage());
            }
        };
        mListview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
