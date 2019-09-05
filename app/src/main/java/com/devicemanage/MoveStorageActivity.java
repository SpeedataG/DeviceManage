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

public class MoveStorageActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    /**
     * 手动输入标签ID
     */
    private EditText mEdittvCardid;
    private ImageView mImageScan;
    private TextView mTv;
    /**
     * 移库
     */
    private Button mBtnMovestorage;
    private ListView listView;
    private Device device;
    private ScanInterface scanDecode;
    private IMifareManager dev;
    private List<Datas> datasList = new ArrayList<>();
    private SharedPreferencesUitl sharedPreferencesUitl;
    private CommonAdapter<Datas> adapter;
    private DBUitl dbUitl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_storage);
        EventBus.getDefault().register(this);
        initView();
        sharedPreferencesUitl = new SharedPreferencesUitl(this, "manages");
        device = new Device(this);
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
//        datasList = dbUitl.queryAll();

        for (int i = 0; i < dbUitl.queryAll().size(); i++) {
            datasList.add(dbUitl.queryAll().get(i));
        }
//        initItem(datasList);
    }

    private void initView() {
        mEdittvCardid = findViewById(R.id.edittv_cardid);
        mImageScan = findViewById(R.id.image_scan);
        mImageScan.setOnClickListener(this);
        mTv = findViewById(R.id.tv);
        mBtnMovestorage = findViewById(R.id.btn_movestorage);
        mBtnMovestorage.setOnClickListener(this);
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
        choiceID.clear();
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
            case R.id.btn_movestorage:
                List<String> ids = new ArrayList<>();
                for (int i = 0; i < datasList.size(); i++) {
                    ids.add(datasList.get(i).getTagId());
                }
                showSingle(ids);
//
                break;
            case R.id.image_scan:
                showSingleChoiceDialog();
                break;
        }
    }

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
//                for (int i = 0; i < datasList.size(); i++) {
//                    String iii = String.valueOf(DataConversionUtils.byteArrayToInt(biaoqianid));
//                    String ssss = datasList.get(i).getId();
//                    if (ssss.equals(iii)) {
//                        mTv.append("\n***********************************\n" +
//                                "ID:" + datasList.get(i).getId() +
//                                "名称：" + datasList.get(i).getName() + i + "\n数量：" + datasList.get(i).getNum() + "瓶\n产地：" + datasList.get(i).getChangdi() +
//                                "\n读头ID：" + DataConversionUtils.byteArrayToInt(dutouid) + "\n标签类型：" + DataConversionUtils.byteArrayToInt(leixing)
//                                + "\n标签ID：" + DataConversionUtils.byteArrayToInt(biaoqianid) + "\nRSSI：" + DataConversionUtils.byteArrayToInt(RSSI)
//                                + "\nLFIDNew：" + DataConversionUtils.byteArrayToInt(LFIDNew) + "\nLFIDOld：" + DataConversionUtils.byteArrayToInt(LFIDOld)
//                                + "\nUserDefine：" + DataConversionUtils.byteArrayToInt(UserDefine) + "\nStatus：" + DataConversionUtils.byteArrayToInt(Status));
//                    } else {
//                        Toast.makeText(this, "无此物品", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
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
    private List<String> ID = new ArrayList<>();

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
                            Toast.makeText(MoveStorageActivity.this,
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
            handler.postDelayed(runnable, 1000);
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
    private List<String> LpieceList = new ArrayList<>();
    private List<String> BpipleList = new ArrayList<>();
    private List<String> TpipleList = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    private void checkDatas(String id) {
//        if (ID.contains(id)) {
//            Toast.makeText(this, "已移库或等待移库", Toast.LENGTH_SHORT).show();
//        } else {
//            ID.add(id);
//            for (int i = 0; i < datasList.size(); i++) {
//                for (int j = 0; j < datasList.get(i).getLpieceDatas().size(); j++) {
//                    String ids = datasList.get(i).getLpieceDatas().get(j);
//                    if (id.equals(ids)) {
//                        String strings = datasList.get(i).getLpieceDatas().get(j);
//                        rukuDatas.add(new Datas(strings, "润滑油", 1, "大庆", datasList.get(i).getStorage(), null, null));
//                        LpieceList.add(id);
//                        initItem(rukuDatas);
//                    }
//                }
//            }
//            for (int i = 0; i < datasList.size(); i++) {
//                for (int j = 0; j < datasList.get(i).getBpieceDatas().size(); j++) {
//                    String ids = datasList.get(i).getBpieceDatas().get(j);
//                    if (id.equals(ids)) {
//                        String strings = datasList.get(i).getBpieceDatas().get(j);
//                        rukuDatas.add(new Datas(strings, "润滑油", 1, "大庆", datasList.get(i).getStorage(), null, null));
//                        initItem(rukuDatas);
//                        BpipleList.add(id);
//                    }
//                }
//            }
//            for (int i = 0; i < datasList.size(); i++) {
//                if (id.equals(datasList.get(i).getTagId())) {
//
//                    rukuDatas.add(new Datas(datasList.get(i).getTagId(), datasList.get(i).getName(), datasList.get(i).getNum(), datasList.get(i).getChangdi(), datasList.get(i).getStorage(), null, null));
//                    initItem(rukuDatas);
//                    TpipleList.add(id);
//                }
//
//            }
//            choiceID.add(id);
//        }
        if (ID.contains(id)) {
            Toast.makeText(this, "重复扫描或等待出库", Toast.LENGTH_SHORT).show();
        } else {
            ID.add(id);
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
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        choiceID = i;
    }

    private List<String> LpieceDatas = new ArrayList<>();
    private List<String> BpipleDatas = new ArrayList<>();
    private int yiku;

    private void showSingle(List<String> idList) {
        final String[] items = new String[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            items[i] = idList.get(i);
        }
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle("移库列表");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yiku = which;
                        if (yiku
                                != -1) {
                            scanDevice(yiku);
                            Toast.makeText(MoveStorageActivity.this,
                                    "你选择了" + items[yiku] + "库",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (choiceID.size() > 0) {
                            if (datasList.size() > 0) {
                                for (int i = 0; i < choiceID.size(); i++) {
                                }
                                for (int i = 0; i < datasList.size(); i++) {
                                    for (int j = 0; j < choiceID.size(); j++) {
                                        if (choiceID.get(j).equals(datasList.get(i).getTagId())) {

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
                                                    LpieceList.add(lids);
                                                }
                                            }
                                        }
//                                        for (int j = 0; j < datasList.size(); j++) {
//                                            if (items[yiku].equals(datasList.get(j).getTagId())) {
//                                                //如果选择的id 等于查到的id 就行替换保存 移库
//                                                for (int k = 0; j < LpieceList.size(); k++) {
//                                                    datasList.get(j).getLpieceDatas().add(LpieceList.get(k));
//                                                }
//                                            }
//                                        }
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
                                                    BpipleList.add(bids);
                                                }
                                            }
                                        }
//                                        for (int j = 0; j < datasList.size(); j++) {
//                                            if (items[yiku].equals(datasList.get(j).getTagId())) {
//                                                //如果选择的id 等于查到的id 就行替换保存 移库
//                                                for (int k = 0; j < BpipleList.size(); k++) {
//                                                    BpipleDatas.add(BpipleList.get(k));
//                                                }
//                                            }
//                                        }

                                    }
                                    String ssss = items[yiku];
                                    String ssss2 = datasList.get(i).getTagId();
                                    if (ssss.equals(ssss2)) {
                                        //如果选择的id 等于查到的id 就行替换保存 移库
                                        for (int h = 0; h < LpieceList.size(); h++) {
                                            LpieceDatas.add(LpieceList.get(h));
                                        }
                                        for (int h = 0; h < BpipleList.size(); h++) {
                                            BpipleDatas.add(BpipleList.get(h));
                                        }
                                    }

                                    if (LpieceDatas.size() == 0) {
                                        LpieceDatas = null;
                                    }
                                    if (BpipleDatas.size() == 0) {
                                        BpipleDatas = null;
                                    }
                                    dbUitl.cahageID(datasList.get(i).getTagId(), LpieceDatas, BpipleDatas);
                                    LpieceDatas = new ArrayList<>();
                                    BpipleDatas = new ArrayList<>();
                                }


                            } else {
                                Toast.makeText(MoveStorageActivity.this, "没有匹配的库！！！", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(MoveStorageActivity.this, "移库成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MoveStorageActivity.this, "请扫描标签再入库！！！", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        singleChoiceDialog.show();
    }

}
