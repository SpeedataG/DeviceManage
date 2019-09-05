package com.devicemanage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devicemanage.db.Datas;
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

public class OutStorageActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    /**
     * 手动输入标签ID
     */
    private EditText mEdittvCardid;
    private ImageView mImageScan;
    private TextView mTv;
    /**
     * 出库
     */
    private Button mBtnOutstorage;
    private Device device;
    private ListView listView;
    private ScanInterface scanDecode;
    private IMifareManager dev;
    private List<Datas> datasList;
    private SharedPreferencesUitl sharedPreferencesUitl;
    private CommonAdapter<Datas> adapter;
    private DBUitl dbUitl;
    private List<String> LpieceDatas = new ArrayList<>();
    private List<String> BpipleDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_storage);
        EventBus.getDefault().register(this);
        initView();
        device = new Device(this);
        sharedPreferencesUitl = new SharedPreferencesUitl(this, "manages");
//        datasList = sharedPreferencesUitl.getDataList("rukudatas");
        dbUitl = new DBUitl();
        scanDecode = new ScanDecode(this);
        scanDecode.initService("true");
        scanDecode.getBarCode(new ScanInterface.OnScanListener() {
            @Override
            public void getBarcode(String s) {
                checkDatas(s);

            }

            @Override
            public void getBarcodeByte(byte[] bytes) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        datasList = dbUitl.queryAll();
//        initItem(datasList);
    }


    private void initView() {
        mEdittvCardid = findViewById(R.id.edittv_cardid);
        mImageScan = findViewById(R.id.image_scan);
        mImageScan.setOnClickListener(this);
        mTv = findViewById(R.id.tv);
        mBtnOutstorage = findViewById(R.id.btn_outstorage);
        mBtnOutstorage.setOnClickListener(this);
        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        //R6高频 读14443A卡初始化
        dev = R6Manager.getMifareInstance(MIFARE);
        dev.InitDev();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ID.clear();
        device.scanOff();
        choiceID.clear();
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
            case R.id.btn_outstorage:
                if (choiceID.size() > 0) {
                    if (datasList.size() > 0) {
                        for (int i = 0; i < datasList.size(); i++) {
                            for (int j = 0; j < choiceID.size(); j++) {
                                if (choiceID.get(j).equals(datasList.get(i).getTagId())) {
                                    dbUitl.delete(datasList.get(i).getTagId());
                                }
                            }
                        }
                        for (int i = 0; i < datasList.size(); i++) {
                            //获取数据库里零件与包装的 id的list
                            if (datasList.get(i).getLpieceDatas() != null) {
                                for (int j = 0; j < datasList.get(i).getLpieceDatas().size(); j++) {
                                    LpieceDatas.add(datasList.get(i).getLpieceDatas().get(j));
                                }
                                for (int j = 0; j < LpieceDatas.size(); j++) {
                                    Log.i("tw", "onClick: " + LpieceDatas.get(j));
                                    String lids = LpieceDatas.get(j);
                                    for (int k = 0; k < choiceID.size(); k++) {
                                        if (choiceID.get(k).equals(lids)) {
                                            LpieceDatas.remove(j);
                                        }
                                    }
                                }
                            }
                            if (datasList.get(i).getBpieceDatas() != null) {
                                for (int j = 0; j < datasList.get(i).getBpieceDatas().size(); j++) {
                                    BpipleDatas.add(datasList.get(i).getBpieceDatas().get(j));
                                }
                                for (int j = 0; j < BpipleDatas.size(); j++) {
                                    String bids = BpipleDatas.get(j);
                                    for (int k = 0; k < choiceID.size(); k++) {
                                        if (choiceID.get(k).equals(bids)) {
                                            BpipleDatas.remove(j);
                                        }
                                    }
                                }
                            }
                            if (LpieceDatas.size() == 0) {
                                LpieceDatas = null;
                            }
                            if (BpipleDatas.size() == 0) {
                                BpipleDatas = null;
                            }
                            dbUitl.cahageID(datasList.get(i).getTagId(), LpieceDatas, BpipleDatas);
                            datasList = dbUitl.queryAll();
                            LpieceDatas=new ArrayList<>();
                            BpipleDatas=new ArrayList<>();
                        }
                    } else {
                        Toast.makeText(this, "没有匹配的库", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(this, "出库成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "请扫描标签再出库！！！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private List<String> ID = new ArrayList<>();
    private List<Integer> readerID = new ArrayList<>();

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

                checkDatas(DataConversionUtils.byteArrayToString(biaoqianid));
            }
            Log.i("aaaaaaaa", "handleMessage: \t读头ID：" + DataConversionUtils.byteArrayToInt(dutouid) + "\n标签类型" + DataConversionUtils.byteArrayToInt(leixing)
                    + "\n标签ID" + DataConversionUtils.byteArrayToInt(biaoqianid) + "\nRSSI" + DataConversionUtils.byteArrayToInt(RSSI)
                    + "\nLFIDNew" + DataConversionUtils.byteArrayToInt(LFIDNew) + "\nLFIDOld" + DataConversionUtils.byteArrayToInt(LFIDOld)
                    + "\nUserDefine" + DataConversionUtils.byteArrayToInt(UserDefine) + "\nStatus" + DataConversionUtils.byteArrayToInt(Status));
        }

        if (deviceDatas.getReaderInfo() != null)

        {
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
                            Toast.makeText(OutStorageActivity.this,
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
                break;
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            byte[] R6datas = dev.SearchCard();
            String ids = DataConversionUtils.byteArrayToString(R6datas);
            if (ids != null) {
                checkDatas(ids);
            }
            handler.postDelayed(runnable, 500);
        }
    };

    public void initItem(List<Datas> datas) {
        adapter = new CommonAdapter<Datas>(this, datas, R.layout.item_list_layout) {
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

    private List<String> choiceID = new ArrayList<>();
    private List<Datas> rukuDatas = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    private void checkDatas(String id) {
        if (ID.contains(id)) {
            Toast.makeText(this, "重复扫描或等待出库", Toast.LENGTH_SHORT).show();
        } else {
            if (datasList.size() > 0) {
                ID.add(id);
                for (int i = 0; i < datasList.size(); i++) {
                    if (id.equals(datasList.get(i).getTagId())) {
                        rukuDatas.add(new Datas(datasList.get(i).getTagId(), datasList.get(i).getName(), datasList.get(i).getNum(), datasList.get(i).getChangdi(), datasList.get(i).getStorage(), null, null));
                        initItem(rukuDatas);
                    }
                }
                for (int i = 0; i < datasList.size(); i++) {
                    if (datasList.get(i).getLpieceDatas() != null) {
                        for (int j = 0; j < datasList.get(i).getLpieceDatas().size(); j++) {
                            String ids = datasList.get(i).getLpieceDatas().get(j);
                            if (id.equals(ids)) {
                                String strings = datasList.get(i).getLpieceDatas().get(j);
                                rukuDatas.add(new Datas(strings, "润滑油", 1, "大庆", datasList.get(i).getStorage(), null, null));
                                initItem(rukuDatas);
                            }
                        }
                    } else {
                        Toast.makeText(this, "库不存在此物品", Toast.LENGTH_SHORT).show();
                    }
                }
                for (int i = 0; i < datasList.size(); i++) {
                    if (datasList.get(i).getBpieceDatas() != null) {
                        for (int j = 0; j < datasList.get(i).getBpieceDatas().size(); j++) {
                            String ids = datasList.get(i).getBpieceDatas().get(j);
                            if (id.equals(ids)) {
                                String strings = datasList.get(i).getBpieceDatas().get(j);
                                rukuDatas.add(new Datas(strings, "润滑油", 1, "大庆", datasList.get(i).getStorage(), null, null));
                                initItem(rukuDatas);
                            }
                        }
                    } else {
                        Toast.makeText(this, "库不存在此物品", Toast.LENGTH_SHORT).show();
                    }
                }
                choiceID.add(id);
            } else {
                Toast.makeText(this, "无库，请先入库", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
