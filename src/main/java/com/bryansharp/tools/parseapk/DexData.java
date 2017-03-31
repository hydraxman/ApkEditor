package com.bryansharp.tools.parseapk;


import com.bryansharp.tools.parseapk.entity.FieldIdsItem;
import com.bryansharp.tools.parseapk.entity.MapItem;
import com.bryansharp.tools.parseapk.entity.StringIdsItem;
import com.bryansharp.tools.parseapk.entity.ByteItem;
import com.bryansharp.tools.parseapk.entity.ClassDefsItem;
import com.bryansharp.tools.parseapk.entity.MethodIdsItem;
import com.bryansharp.tools.parseapk.entity.ProtoIdsItem;
import com.bryansharp.tools.parseapk.entity.TypeIdsItem;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;
import com.bryansharp.tools.parseapk.entity.base.DexHeadItem;
import com.bryansharp.tools.parseapk.entity.base.DexItem;
import com.bryansharp.tools.parseapk.utils.LogUtils;
import com.bryansharp.tools.parseapk.utils.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Adler32;

/**
 * Created by bushaopeng on 17/3/14.
 */
public class DexData {
    public static final String STRING_IDS = "stringIds";
    public static final String CLASS_DEFS = "classDefs";
    public static final String TYPE_IDS = "typeIds";
    public static final String METHOD_IDS = "methodIds";
    public static final String PROTO_IDS = "protoIds";
    public static final String FIELD_IDS = "fieldIds";
    public static final String MAP_OFF = "map";
    private static final String[] sizeIsCountNames = new String[]{
            STRING_IDS, CLASS_DEFS, TYPE_IDS, METHOD_IDS, PROTO_IDS, FIELD_IDS, MAP_OFF
    };
    public static final List<String> sizeIsCountNamesArr = Arrays.asList(sizeIsCountNames);
    private int sh1SumStart;
    private Map<String, DexHeadItem> headers = new LinkedHashMap<>();
    private Map<String, DexDataItem> dataItems = new HashMap<>();
    private byte[] dexData;
    private int checkSumStart;

    public DexData() {
        ArrayList<DexHeadItem> headerList = new ArrayList<>();
        int start = 0;
        headerList.add(new DexHeadItem("magic", start, 8));
        start += 8;
        headerList.add(new DexHeadItem("checksum", start, 4));
        start += 4;
        checkSumStart = start;
        headerList.add(new DexHeadItem("signature", start, 0x14));
        start += 0x14;
        sh1SumStart = start;
        headerList.add(new DexHeadItem("fileSize", start, 4));
        start += 4;
        headerList.add(new DexHeadItem("headerSize", start, 4));
        start += 4;
        headerList.add(new DexHeadItem("endianTag", start, 4));
        start += 4;
        headerList.add(new DexHeadItem("linkSize", start, 4, DexItem.TYPE_INT_SIZE));
        start += 4;
        headerList.add(new DexHeadItem("linkOff", start, 4, DexItem.TYPE_INT_OFFSET));
        start += 4;
        headerList.add(new DexHeadItem("mapOff", start, 4, DexItem.TYPE_INT_OFFSET));
        start += 4;
        headerList.add(new DexHeadItem("stringIdsSize", start, 4, DexItem.TYPE_INT_SIZE));
        start += 4;
        headerList.add(new DexHeadItem("stringIdsOff", start, 4, DexItem.TYPE_INT_OFFSET));
        start += 4;
        headerList.add(new DexHeadItem("typeIdsSize", start, 4, DexItem.TYPE_INT_SIZE));
        start += 4;
        headerList.add(new DexHeadItem("typeIdsOff", start, 4, DexItem.TYPE_INT_OFFSET));
        start += 4;
        headerList.add(new DexHeadItem("protoIdsSize", start, 4, DexItem.TYPE_INT_SIZE));
        start += 4;
        headerList.add(new DexHeadItem("protoIdsOff", start, 4, DexItem.TYPE_INT_OFFSET));
        start += 4;
        headerList.add(new DexHeadItem("fieldIdsSize", start, 4, DexItem.TYPE_INT_SIZE));
        start += 4;
        headerList.add(new DexHeadItem("fieldIdsOff", start, 4, DexItem.TYPE_INT_OFFSET));
        start += 4;
        headerList.add(new DexHeadItem("methodIdsSize", start, 4, DexItem.TYPE_INT_SIZE));
        start += 4;
        headerList.add(new DexHeadItem("methodIdsOff", start, 4, DexItem.TYPE_INT_OFFSET));
        start += 4;
        headerList.add(new DexHeadItem("classDefsSize", start, 4, DexItem.TYPE_INT_SIZE));
        start += 4;
        headerList.add(new DexHeadItem("classDefsOff", start, 4, DexItem.TYPE_INT_OFFSET));
        start += 4;
        headerList.add(new DexHeadItem("dataSize", start, 4, DexItem.TYPE_INT_SIZE));
        start += 4;
        headerList.add(new DexHeadItem("dataOff", start, 4, DexItem.TYPE_INT_OFFSET));
        int len = start + 4;
        LogUtils.log("header总长度：" + len);
        for (DexHeadItem header : headerList) {
            headers.put(header.name, header);
        }
    }

    public void fillHeaders(byte[] dexData) {
        preverifyDex(dexData);
        byte[] data;
        for (Map.Entry<String, DexHeadItem> entry : headers.entrySet()) {
            DexHeadItem headItem = entry.getValue();
            data = new byte[headItem.byteSize];
            System.arraycopy(dexData, headItem.start, data, 0, headItem.byteSize);
            headItem.data = data;
        }
    }

    private void preverifyDex(byte[] dexData) {
        this.dexData = dexData;
        Adler32 adler32 = new Adler32();
        adler32.update(dexData, checkSumStart, dexData.length - checkSumStart);
        long value = adler32.getValue();
        LogUtils.log("my checkSum:" + value, true);
        try {
            MessageDigest sh1 = MessageDigest.getInstance("sha1");
            sh1.update(dexData, sh1SumStart, dexData.length - sh1SumStart);
            byte[] digest = sh1.digest();
            LogUtils.log("my sh1:" + Utils.bytesToString(digest, 0), true);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void fillData() {
        Set<Map.Entry<String, DexHeadItem>> entries = headers.entrySet();
        DexDataItem dataItem;
        //生成数据条目对象
        for (Map.Entry<String, DexHeadItem> entry : entries) {
            DexHeadItem item = entry.getValue();
            if (item.hasSubContent()) {
                String desc = item.getContentDesc();
                dataItem = dataItems.get(desc);
                if (dataItem == null) {
                    dataItem = getDexDataItem(desc);
                    dataItems.put(desc, dataItem);
                }
                if (item.type == DexItem.TYPE_INT_SIZE) {
                    dataItem.fillSize(item.bytes2Int());
                }
                if (item.type == DexItem.TYPE_INT_OFFSET) {
                    dataItem.start = item.bytes2Int();
                    if (dataItem instanceof MapItem) {
                        dataItem.fillSize(Utils.bytesToInt(dexData, dataItem.start));
                    }
                }
            }
        }
        //第0层 String
        for (Map.Entry<String, DexDataItem> entry : dataItems.entrySet()) {
            dataItem = entry.getValue();
            if (dataItem.readyToFill()) {
                dataItem.fillData(dexData);
                dataItem.fillRefs();
                dataItem.parseBaseRealData(dexData);
            }
        }
        //第1层 Type
        for (Map.Entry<String, DexDataItem> entry : dataItems.entrySet()) {
            dataItem = entry.getValue();
            if (dataItem.readyToFill()) {
                dataItem.parse1stRealData(dataItems, dexData);
            }
        }
        // 第2层 FieldIdsItem ClassDef Proto
        for (Map.Entry<String, DexDataItem> entry : dataItems.entrySet()) {
            dataItem = entry.getValue();
            if (dataItem.readyToFill()) {
                dataItem.parse2ndRealData(dataItems, dexData);
            }
        }
        // 第3层 Method
        for (Map.Entry<String, DexDataItem> entry : dataItems.entrySet()) {
            dataItem = entry.getValue();
            if (dataItem.readyToFill()) {
                dataItem.parse3rdRealData(dataItems, dexData);
            }
        }
        // 第四次 ClassDef Data
        for (Map.Entry<String, DexDataItem> entry : dataItems.entrySet()) {
            dataItem = entry.getValue();
            if (dataItem.readyToFill()) {
                dataItem.parse4thRealData(dataItems, dexData);
            }
        }
    }

    private DexDataItem getDexDataItem(String desc) {
        DexDataItem dataItem;
        if (DexData.STRING_IDS.equals(desc)) {
            dataItem = new StringIdsItem(desc);
        } else if (DexData.TYPE_IDS.equals(desc)) {
            dataItem = new TypeIdsItem(desc);
        } else if (DexData.CLASS_DEFS.equals(desc)) {
            dataItem = new ClassDefsItem(desc);
        } else if (DexData.PROTO_IDS.equals(desc)) {
            dataItem = new ProtoIdsItem(desc);
        } else if (DexData.METHOD_IDS.equals(desc)) {
            dataItem = new MethodIdsItem(desc);
        } else if (DexData.FIELD_IDS.equals(desc)) {
            dataItem = new FieldIdsItem(desc);
        } else if (DexData.MAP_OFF.equals(desc)) {
            dataItem = new MapItem(desc);
        } else {
            dataItem = new ByteItem(desc);
        }
        return dataItem;
    }

    //日志打印日期
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd号HH:mm:ss");

    public void printData() {
        for (Map.Entry<String, DexHeadItem> entry : headers.entrySet()) {
            LogUtils.log(entry.getValue(), true);
        }
        LogUtils.log("===" + simpleDateFormat.format(new Date()) + "===", true);
        for (Map.Entry<String, DexDataItem> entry : dataItems.entrySet()) {
            DexDataItem item = entry.getValue();
            if (item != null) {
                item.printData();
            }
        }
    }
}
