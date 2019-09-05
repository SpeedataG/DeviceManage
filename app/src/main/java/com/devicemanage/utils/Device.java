package com.devicemanage.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Log;
import android.widget.Toast;

import com.speedata.libutils.DataConversionUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Device {
    private SerialPort serialPort = null;
    private int fd;
    private Context context;
    private StartTherd startTherd;

    public Device(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        try {
            serialPort = new SerialPort();
            serialPort.OpenSerial(SerialPort.SERIAL_TTYMT2, 38400);
            fd = serialPort.getFd();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test() {
        String cmd = "ffff0018f1888808000003e82f0000000000000010022";
        EventBus.getDefault().post(new DeviceDatas(null, DataConversionUtils.hexStringToByteArray(cmd)));
    }

    private String scanOn = "FFFF0007109AAA";
    private String scanOff = "FFFF0007118A8B";
    private String getInfo = "FFFF000720acf9";

    private String TAG = "tw";

    /**
     * 读头接收到该指令后，立即回复确认信息，随后主动上传接收到的标签信息。
     * SCAN_ON FF FF 00 07 10 9A AA
     */
    public void scanOn() {
//        String crc = "FFFF00081000";
//        int ss = Crc16_DATAs(DataConversionUtils.HexString2Bytes(crc), 0, 6);
//        String ssss = Integer.toHexString(ss);

        try {
            serialPort.WriteSerialByte(fd, DataConversionUtils.HexString2Bytes(scanOn));
            byte[] result = serialPort.ReadSerial(fd, 7);
            if (result != null && result.length > 5) {
                Log.i(TAG, "scanOn: " + DataConversionUtils.byteArrayToString(result));
                result = DtatUtils.cutBytes(result, 5, 1);
                if (result[0] == 0) {
                    if (startTherd == null) {
                        startTherd = new StartTherd();
                        startTherd.start();
                    }
//                    Toast.makeText(context, "启动扫描成功", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "scanOn:成功 ");
                } else {
                    Toast.makeText(context, "启动扫描失败", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "scanOn: 失败");
                }
            } else {
                Log.i(TAG, "scanOn: 失败返回null");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读头接收到该指令后，立即回复确认信息，并停止主动上传标签信息。
     * SCAN_OFF    FF FF 00 07 11 8A 8B
     */
    public void scanOff() {
        if (startTherd != null) {
            startTherd.interrupt();
            startTherd = null;
        }
        try {
            serialPort.WriteSerialByte(fd, DataConversionUtils.HexString2Bytes(scanOff));
            byte[] result = serialPort.ReadSerial(fd, 7);
            if (result != null && result.length > 5) {
                result = DtatUtils.cutBytes(result, 5, 1);
                Log.i(TAG, "scanOn: " + DataConversionUtils.byteArrayToString(result));
                if (result[0] == 0) {
//                    Toast.makeText(context, "关闭扫描成功", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "scanOff:成功 ");
                } else {
                    Toast.makeText(context, "关闭扫描失败", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "scanOff: 失败");
                }
            } else {
                Log.i(TAG, "scanOff: 关闭失败");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 描述 : 读头接收到该指令后，会返回内部的设置信息
     * (包括读头ID、射频衰减值、低频绑定使能标志、低频ID绑定区间)和软件版本信息。
     * FF FF 00 07 20 ac f9
     */
    public void getInfo() {
        try {
            serialPort.WriteSerialByte(fd, DataConversionUtils.HexString2Bytes(getInfo));
            SystemClock.sleep(300);
            byte[] result = serialPort.ReadSerial(fd, 51);
            if (result != null) {
                EventBus.getDefault().post(new DeviceDatas(result, null));
                Log.i(TAG, "getInfo: " + DataConversionUtils.byteArrayToString(result));
            } else {
                Log.i(TAG, "getInfo: 关闭失败");
                Toast.makeText(context, "获取阅读器信息失败", Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 描述 : 该指令用于更改读头的ID号。
     * FF FF 00 09 21 88 88 31 FE
     *
     * @param newID 读头的ID号
     */

    public void setID(String newID) {
        try {
            String setID = "FFFF000921" + newID;
            int ss = Crc16_DATAs(DataConversionUtils.HexString2Bytes(setID), 0, 7);
            String crc = Integer.toHexString(ss);
            setID = "FFFF000921" + newID + crc;
            serialPort.WriteSerialByte(fd, DataConversionUtils.HexString2Bytes(setID));
            byte[] result = serialPort.ReadSerial(fd, 10);
            if (result != null) {
                Log.i(TAG, "setID: " + DataConversionUtils.byteArrayToString(result));
                result = DtatUtils.cutBytes(result, 5, 1);
                Log.i(TAG, "setID: " + DataConversionUtils.byteArrayToString(result));
                if (result[0] == 0) {
                    Toast.makeText(context, "设置ID成功", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "setID:成功 ");
                } else {
                    Toast.makeText(context, "设置ID失败", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "setID: 失败");
                }
            } else {
                Log.i(TAG, "setID: 关闭失败");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * SET_ATTEN : 设置读头射频衰减器的衰减值 无
     * 说明：衰减值取值范围为 [ 0-31 ，255] 。注：255时未开启PA放大功能(适用于硬件带PA功能的)
     *
     * @param atten 衰减值
     */
    public void setAtten(String atten) {
        try {
            String setAtten = "FFFF000822" + atten;
            int ss = Crc16_DATAs(DataConversionUtils.HexString2Bytes(setAtten), 0, 6);
            String crc = Integer.toHexString(ss);
            setAtten = "FFFF000921" + atten + crc;
            serialPort.WriteSerialByte(fd, DataConversionUtils.HexString2Bytes(setAtten));
            byte[] result = serialPort.ReadSerial(fd, 10);
            if (result != null) {
                Log.i(TAG, "setAtten: " + DataConversionUtils.byteArrayToString(result));
                result = DtatUtils.cutBytes(result, 5, 1);
                Log.i(TAG, "setAtten: " + DataConversionUtils.byteArrayToString(result));
                if (result[0] == 0) {
                    Toast.makeText(context, "设置衰减值成功", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "setAtten:设置衰减值成功 ");
                } else {
                    Toast.makeText(context, "设置衰减值失败", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "setAtten: 设置衰减值失败");
                }
            } else {
                Toast.makeText(context, "设置衰减值失败", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "setID: 设置衰减值失败");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    private String scanUp = "FFFF0018F1";

    public void scanUp() {
        //SCAN_UP : 读头主动上传卡片信息
    }

    public void releaseDecvice() {
        if (startTherd != null) {
            startTherd.interrupt();
            startTherd = null;
        }
        if (serialPort != null) {
            serialPort.CloseSerial(fd);
        }
    }

    public class StartTherd extends Thread {
        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                try {
                    byte[] bytes = serialPort.ReadSerial(serialPort.getFd(), 24);
                    Log.i("tw", "run: " + DataConversionUtils.byteArrayToString(bytes));
                    if (bytes != null) {
                        EventBus.getDefault().post(new DeviceDatas(null, bytes));
//                        handler.sendMessage(handler.obtainMessage(0, bytes));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] cardDatas = (byte[]) msg.obj;
            cardDatas = DtatUtils.cutBytes(cardDatas, 6, 17);
            EventBus.getDefault().post(cardDatas);
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
            Log.i("aaaaaaaa", "handleMessage: \t读头ID：" + DataConversionUtils.byteArrayToInt(dutouid) + "\n标签类型" + DataConversionUtils.byteArrayToInt(leixing)
                    + "\n标签ID" + DataConversionUtils.byteArrayToInt(biaoqianid) + "\nRSSI" + DataConversionUtils.byteArrayToInt(RSSI)
                    + "\nLFIDNew" + DataConversionUtils.byteArrayToInt(LFIDNew) + "\nLFIDOld" + DataConversionUtils.byteArrayToInt(LFIDOld)
                    + "\nUserDefine" + DataConversionUtils.byteArrayToInt(UserDefine) + "\nStatus" + DataConversionUtils.byteArrayToInt(Status));
        }
    };


    private static char[] crc_ta = {0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5,
            0x60c6, 0x70e7, 0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad,
            0xe1ce, 0xf1ef};

    private static int Crc16_DATAs(byte[] buffer, int start, int end) {
        short da;
        char CRC_16_Data = 0;
        byte[] temp = new byte[end - start];
        System.arraycopy(buffer, start, temp, 0, temp.length);
        for (int i = start; i < end; i++) {
            da = (short) (CRC_16_Data >> 12);
            CRC_16_Data <<= 4;
            CRC_16_Data ^= crc_ta[da ^ ((short) (char) (buffer[i] & 0xff) / 16)];
            da = (short) (CRC_16_Data >> 12);
            CRC_16_Data <<= 4;
            CRC_16_Data ^= crc_ta[da
                    ^ ((short) (char) (buffer[i] & 0xff) & 0x0f)];
        }
        return CRC_16_Data;
    }
}
