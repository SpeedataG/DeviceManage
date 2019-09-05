package com.devicemanage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devicemanage.base.MyAplicatin;
import com.devicemanage.db.Datas;
import com.devicemanage.db.LDatas;
import com.devicemanage.utils.BDatas;
import com.devicemanage.utils.DBUitl;
import com.devicemanage.utils.Device;
import com.devicemanage.utils.DeviceDatas;
import com.devicemanage.utils.DtatUtils;
import com.devicemanage.utils.SharedPreferencesUitl;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;
import com.speedata.libutils.DataConversionUtils;
import com.speedata.r6lib.IMifareManager;
import com.speedata.r6lib.R6Manager;
import com.speedata.ui.adapter.CommonAdapter;
import com.speedata.ui.adapter.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.speedata.r6lib.R6Manager.CardType.MIFARE;

public class InstorageActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 手动输入标签id
     */
    private EditText mEdittvCardid;
    private ImageView mImageScan;
    private Device device;
    private TextView mTv;
    /**
     * 上传
     */
    private Button mBtnUplod;
    private SharedPreferencesUitl sharedPreferencesUitl;
    private ScanInterface scanDecode;
    private ListView listView;
    private IMifareManager dev;
    private DBUitl dbUitl;
    private List<String> LpieceDatas = new ArrayList<>();
    private List<String> BpipleDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instorage);
        EventBus.getDefault().register(this);
        initView();
        sharedPreferencesUitl = new SharedPreferencesUitl(this, "manages");
        dbUitl = new DBUitl();
        //R6高频 读14443A卡初始化
        dev = R6Manager.getMifareInstance(MIFARE);
        dev.InitDev();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int LPiece = 0;

    private void initView() {
        mEdittvCardid = findViewById(R.id.edittv_cardid);
        mImageScan = findViewById(R.id.image_scan);
        mImageScan.setOnClickListener(this);
        device = new Device(this);
        mTv = findViewById(R.id.tv);
        listView = findViewById(R.id.listview);
        mBtnUplod = findViewById(R.id.btn_uplod);
        mBtnUplod.setOnClickListener(this);
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");
        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String s) {
                if (!ID.contains(s)) {
                    ID.add(s);
                    LPiece++;
                    LpieceDatas.add(s);
                    rukuDatas.add(new Datas(s, "润滑油", 1, "大庆", "", null, null));
                    initItem(rukuDatas);
                } else {
                    Toast.makeText(InstorageActivity.this, "已入库或正准备入库", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void getBarcodeByte(byte[] bytes) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        dev.ReleaseDev();
        device.scanOff();
        handler.removeCallbacks(runnable);
        scanDecode.stopScan();//停止扫描
        scanDecode.onDestroy();//回复初始状态
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.image_scan:
                showSingleChoiceDialog();
                break;
            case R.id.btn_uplod:
                handler.removeCallbacks(runnable);
                device.scanOff();
                if (rukuTDatas.size() > 0) {

                    for (int i = 0; i < rukuTDatas.size(); i++) {
                        MyAplicatin.getsInstance().getTDaoSession().getDatasDao().insertOrReplace(rukuTDatas.get(i));
                    }
                    Toast.makeText(this, "入库成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "请扫描标签再入库！！！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private List<String> ID = new ArrayList<>();

    private List<Integer> readerID = new ArrayList<>();
    private List<Datas> rukuDatas = new ArrayList<>();
    private List<Datas> rukuTDatas = new ArrayList<>();
    private int KU = 0;

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
            Log.i("ssss", "encodeData: " + (DataConversionUtils.byteArrayToString(biaoqianid)) + Integer.parseInt(sharedPreferencesUitl.read("rrsi", "")));
            if (DataConversionUtils.byteArrayToInt(RSSI) <= Integer.parseInt(sharedPreferencesUitl.read("rrsi", ""))) {

                if (ID.contains(DataConversionUtils.byteArrayToString(biaoqianid))) {
                } else {
                    ID.add(DataConversionUtils.byteArrayToString(biaoqianid));
                    if (dbUitl.queryID(DataConversionUtils.byteArrayToString(biaoqianid))) {
                        Toast.makeText(InstorageActivity.this, "已入库或正准备入库", Toast.LENGTH_SHORT).show();
                    } else {
                        if (LpieceDatas.size()==0) {
                            LpieceDatas=null;
                        }
                        if (BpipleDatas.size()==0) {
                            BpipleDatas=null;
                        }
                        rukuTDatas.add(new Datas(DataConversionUtils.byteArrayToString(biaoqianid), "润滑油", BPiece + LPiece, "大庆",
                                DataConversionUtils.byteArrayToString(biaoqianid)+ "库", LpieceDatas, BpipleDatas));
                        initItem(rukuTDatas);
                    }
                }
            } else {
                Log.i("ssss", "encodeData: " + DataConversionUtils.byteArrayToInt(RSSI));
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

    private int yourChoice;

    private void showSingleChoiceDialog() {
        final String[] items = {"零件", "包装", "托盘"};
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle("扫描方式选择");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;

                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                            scanDevice(yourChoice);
                            Toast.makeText(InstorageActivity.this,
                                    "你选择了" + items[yourChoice] + "扫描方式",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        singleChoiceDialog.show();
    }

    private void scanDevice(int i) {
        switch (i) {
            case 0:
                handler.removeCallbacks(runnable);
                scanDecode.starScan();
                break;
            case 1:
                handler.postDelayed(runnable, 50);
                break;
            case 2:
                handler.removeCallbacks(runnable);
                device.scanOn();
//                device.test();
//测试专用
//                if (ID.contains(mEdittvCardid.getText().toString())) {
//                } else {
//                    ID.add(mEdittvCardid.getText().toString());
//                    if (dbUitl.queryID(mEdittvCardid.getText().toString())) {
//                        Toast.makeText(InstorageActivity.this, "已入库或正准备入库", Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (LpieceDatas.size()==0) {
//                            LpieceDatas=null;
//                        }
//                        if (BpipleDatas.size()==0) {
//                            BpipleDatas=null;
//                        }
//                        rukuTDatas.add(new Datas(mEdittvCardid.getText().toString(), "润滑油", BPiece + LPiece, "大庆",
//                                mEdittvCardid.getText().toString() + "库", LpieceDatas, BpipleDatas));
//                        initItem(rukuTDatas);
//                    }
//                }
                break;
        }
    }

    private Handler handler = new Handler();
    private int BPiece = 0;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            byte[] R6datas = dev.SearchCard();
            String ids = DataConversionUtils.byteArrayToString(R6datas);
            if (ids != null) {
                if (ID.contains(ids)) {
                    Toast.makeText(InstorageActivity.this, "已入库或正准备入库", Toast.LENGTH_SHORT).show();
                } else {
                    ID.add(ids);
                    BPiece++;
                    BpipleDatas.add(ids);
                    rukuDatas.add(new Datas(ids, "润滑油", 1, "大庆", "", null, null));
                    initItem(rukuDatas);
                }
            }
            handler.postDelayed(runnable, 1000);
        }
    };


    public void initItem(List<Datas> datas) {
        CommonAdapter<Datas> adapter = new CommonAdapter<Datas>(this, datas, R.layout.item_list_layout) {
            @Override
            public void convert(ViewHolder helper, Datas item) {
                helper.setText(R.id.tv_id, "ID:" + item.getTagId());
                helper.setText(R.id.tv_name, "名称：" + item.getName());
                helper.setText(R.id.tv_num, "数量：" + item.getNum());
                helper.setText(R.id.tv_address, "产地：" + item.getChangdi());
                helper.setText(R.id.tv_storage, "库房：" + item.getStorage());
            }
        };
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
