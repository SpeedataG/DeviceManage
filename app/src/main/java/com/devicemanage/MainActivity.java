package com.devicemanage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.devicemanage.utils.Device;
import com.devicemanage.utils.DeviceDatas;
import com.devicemanage.utils.DtatUtils;
import com.speedata.libutils.DataConversionUtils;
import com.speedata.ui.adapter.CommonAdapter;
import com.speedata.ui.adapter.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button mBtnInstorage;
    private Button mBtnOutstorage;
    private Button mBtnMovestorage;
    private Device device;
    private TextView mTv;
    /**
     * 设置读头ID或衰减值
     */
    private EditText mEtvCmd;
    private ToggleButton mTogglebtn;
    /**
     * 读头ID
     */
    private Button mBtnSetID;
    /**
     * 衰减值
     */
    private Button mBtnSetshuaijian;
    /**
     * 阅读器信息
     */
    private Button mBtnReadinfo;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initItem() {
        CommonAdapter<String> adapter = new CommonAdapter<String>(this, null, R.layout.item_list_layout) {
            @Override
            public void convert(ViewHolder helper, String item) {
//                helper.setText(R.id.tv_show, item);
            }
        };
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void startAct(Class activity) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        startActivity(intent);
    }

    private void initView() {
        mBtnInstorage = (Button) findViewById(R.id.btn_instorage);
        mBtnInstorage.setOnClickListener(this);
        mBtnOutstorage = (Button) findViewById(R.id.btn_outstorage);
        mBtnOutstorage.setOnClickListener(this);
        mBtnMovestorage = (Button) findViewById(R.id.btn_movestorage);
        mBtnMovestorage.setOnClickListener(this);
        device = new Device(this);
        mTv = (TextView) findViewById(R.id.tv);
        mEtvCmd = (EditText) findViewById(R.id.etv_cmd);
        mTogglebtn = (ToggleButton) findViewById(R.id.togglebtn);
        mTogglebtn.setOnCheckedChangeListener(this);
        mBtnSetID = (Button) findViewById(R.id.btn_setID);
        mBtnSetID.setOnClickListener(this);
        mBtnSetshuaijian = (Button) findViewById(R.id.btn_setshuaijian);
        mBtnSetshuaijian.setOnClickListener(this);
        mTv.setOnClickListener(this);
        mBtnReadinfo = (Button) findViewById(R.id.btn_readinfo);
        mBtnReadinfo.setOnClickListener(this);
    }

    private List<Integer> id = new ArrayList<>();
    private List<Integer> readerID = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
//        id=null;
//        readerID = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void encodeData(DeviceDatas deviceDatas) {
        if (deviceDatas.getCardInfo() != null) {
            byte[] cardDatas = deviceDatas.getCardInfo();
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
            Log.i("ssss", "encodeData: "+(DataConversionUtils.byteArrayToInt(biaoqianid)));
           if (id.contains(DataConversionUtils.byteArrayToInt(biaoqianid))) {
//                return;
            } else {
                id.add(DataConversionUtils.byteArrayToInt(biaoqianid));
                mTv.append("***********************************" +
                        "\n读头ID：" + DataConversionUtils.byteArrayToInt(dutouid) + "\n标签类型：" + DataConversionUtils.byteArrayToInt(leixing)
                        + "\n标签ID：" + DataConversionUtils.byteArrayToInt(biaoqianid) + "\nRSSI：" + DataConversionUtils.byteArrayToInt(RSSI)
                        + "\nLFIDNew：" + DataConversionUtils.byteArrayToInt(LFIDNew) + "\nLFIDOld：" + DataConversionUtils.byteArrayToInt(LFIDOld)
                        + "\nUserDefine：" + DataConversionUtils.byteArrayToInt(UserDefine) + "\nStatus：" + DataConversionUtils.byteArrayToInt(Status));
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
            if (readerID.contains(DataConversionUtils.byteArrayToInt(id))) {
                return;
            } else {
                readerID.add(DataConversionUtils.byteArrayToInt(id));
                mTv.append("***********************************" +
                        "\n读头ID：" + DataConversionUtils.byteArrayToInt(id) + "\n衰减值：" + DataConversionUtils.byteArrayToInt(shuaiJianZhi)
                        + "\n低频绑定标志：" + DataConversionUtils.byteArrayToInt(diPinBangDing) + "\n低频ID起始：" + DataConversionUtils.byteArrayToInt(dipinIdStart)
                        + "\n低频ID结束：" + DataConversionUtils.byteArrayToInt(dipinIdStot) + "\n软件版本：" + DataConversionUtils.byteArrayToInt(V)
                );
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_instorage:
                startAct(InstorageActivity.class);
                break;
            case R.id.btn_outstorage:
                break;
            case R.id.btn_movestorage:
                break;
            case R.id.btn_setID:
                String duTouID = mEtvCmd.getText().toString();
                int a = duTouID.length();
                if (duTouID.isEmpty() || duTouID.length() < 4) {
                    Toast.makeText(this, "请输入正确的ID", Toast.LENGTH_SHORT).show();
                } else {
                    device.setID(duTouID);
                }
                break;
            case R.id.btn_setshuaijian:
                String shuaiJian = mEtvCmd.getText().toString();
                if (shuaiJian.isEmpty() || shuaiJian.length() < 2) {
                    Toast.makeText(this, "请输入正确的衰减值", Toast.LENGTH_SHORT).show();
                } else {
                    device.setAtten(shuaiJian);
                }
                break;
            case R.id.tv:
                break;
            case R.id.btn_readinfo:
//                mTv.setText("");
                device.getInfo();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            mTv.setText("");
            id.clear();
            readerID.clear();
            mBtnSetID.setEnabled(false);
            mBtnSetshuaijian.setEnabled(false);
            mBtnReadinfo.setEnabled(false);
            device.scanOn();
        } else {
            device.scanOff();
            mBtnSetID.setEnabled(true);
            mBtnSetshuaijian.setEnabled(true);
            mBtnReadinfo.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        device.releaseDecvice();
    }
}
