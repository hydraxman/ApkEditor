package com.bryansharp.tools.parseapk.entity.data;

import com.bryansharp.tools.parseapk.DexData;
import com.bryansharp.tools.parseapk.entity.StringIdsItem;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;
import com.bryansharp.tools.parseapk.utils.IntPointer;
import com.bryansharp.tools.parseapk.utils.Mutf8;
import com.bryansharp.tools.parseapk.utils.Utils;

import java.util.Map;

/**
 * Created by bushaopeng on 17/4/5.
 */
public class BaseAnnotationContent {
    public int offset;
    public int sizeOfValues;
    public AnnotationNV[] values;

    protected void fillNV(byte[] dexData, IntPointer annotationItemOff, Map<String, DexDataItem> dataItems) {
        StringIdsItem sItem = (StringIdsItem) dataItems.get(DexData.STRING_IDS);
        int[] ints = Mutf8.readUnsignedLeb128(dexData, annotationItemOff.getValue());
        this.sizeOfValues = ints[0];
        annotationItemOff.add(ints[1]);
        values = sizeOfValues > 0 ? new AnnotationNV[sizeOfValues] : null;
        while (sizeOfValues > 0) {
            ints = Mutf8.readUnsignedLeb128(dexData, annotationItemOff.getValue());
            int nameIdx = ints[0];
            annotationItemOff.add(ints[1]);
            Object value = Utils.readEncodedValue(dexData, dataItems, annotationItemOff);
            AnnotationNV annotationNV = new AnnotationNV();
            annotationNV.name = sItem.realData[nameIdx];
            annotationNV.value = value;
            values[sizeOfValues - 1] = annotationNV;
            sizeOfValues--;
        }
    }
}
