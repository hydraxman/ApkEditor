package com.bryansharp.tools.parseapk.entity.base;


import com.bryansharp.tools.parseapk.DexData;
import com.bryansharp.tools.parseapk.utils.LogUtils;

import java.util.Map;

/**
 * Created by bushaopeng on 17/3/17.
 */
public abstract class DexDataItem<T, U> extends DexItem {

    public T[] refs;
    public U[] realData;
    public int count;


    public DexDataItem(String name) {
        this.name = name;
    }

    public boolean readyToFill() {
        return byteSize > 0 && start > 0 && DexData.sizeIsCountNamesArr.contains(name);
    }

    protected abstract T[] createRefs();

    protected abstract int getRefSize();

    public void printData() {
        printTitle();
        if (data != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < refs.length; i++) {
                stringBuilder.append("\n").append(i).append(",").append(refs[i]).append(",");
                if (realData != null) {
                    stringBuilder.append(realData[i]);
                }
            }
            LogUtils.log(stringBuilder.toString(), true);
        }
    }

    protected void printTitle() {
        LogUtils.log("\n" + name + " : byteSize is " + byteSize + " : count is " + count + " : start is " + start + " : data is :", true);
    }

    protected int refStart = 0;

    public void fillRefs() {
        int refSize = getRefSize();
        for (int i = refStart; i < byteSize; i += refSize) {
            refs[i / refSize] = parseRef(i);
        }
    }

    public void fillData(byte[] dexData) {
        byte[] data = new byte[byteSize];
        System.arraycopy(dexData, start, data, 0, byteSize);
        this.data = data;
    }

    public void parseBaseRealData(byte[] dexData) {
    }

    public void parse1stRealData(Map<String, DexDataItem> dataItems, byte[] dexData) {
    }

    protected abstract T parseRef(int i);

    public void parse2ndRealData(Map<String, DexDataItem> dataItems, byte[] dexData) {

    }

    public void parse3rdRealData(Map<String, DexDataItem> dataItems, byte[] dexData) {

    }

    public void parse4thRealData(Map<String, DexDataItem> dataItems, byte[] dexData) {
    }

    public void fillSize(int count) {
        this.count = count;
        this.byteSize = count * getRefSize();
        this.refs = createRefs();
    }
}
