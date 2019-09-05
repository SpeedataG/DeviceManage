package com.devicemanage.utils;

public class DeviceDatas {

    private byte[] ReaderInfo;
    private byte[] CardInfo;

    public DeviceDatas(byte[] readerInfo, byte[] cardInfo) {
        ReaderInfo = readerInfo;
        CardInfo = cardInfo;
    }

    public byte[] getReaderInfo() {
        return ReaderInfo;
    }

    public void setReaderInfo(byte[] readerInfo) {
        ReaderInfo = readerInfo;
    }

    public byte[] getCardInfo() {
        return CardInfo;
    }

    public void setCardInfo(byte[] cardInfo) {
        CardInfo = cardInfo;
    }
}
