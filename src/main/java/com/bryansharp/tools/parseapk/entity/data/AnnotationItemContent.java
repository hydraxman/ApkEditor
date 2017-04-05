package com.bryansharp.tools.parseapk.entity.data;

import com.bryansharp.tools.parseapk.DexData;
import com.bryansharp.tools.parseapk.entity.StringIdsItem;
import com.bryansharp.tools.parseapk.entity.TypeIdsItem;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;
import com.bryansharp.tools.parseapk.utils.IntPointer;
import com.bryansharp.tools.parseapk.utils.Mutf8;
import com.bryansharp.tools.parseapk.utils.Utils;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by bushaopeng on 17/4/5.
 */
public class AnnotationItemContent extends BaseAnnotationContent {
    public byte visibility;
    public String typeContent;

    @Override
    public String toString() {
        return "\n\t\t\t\t\t\t\tAnnotationItemContent{" +
                "visibility=" + visibility +
                ", offset=" + offset +
                ", typeContent='" + typeContent + '\'' +
                ", sizeOfValues=" + sizeOfValues +
                ", values=" + Arrays.toString(values) +
                '}';
    }

    public void fillData(byte[] dexData, IntPointer annotationItemOff, Map<String, DexDataItem> dataItems) {
        TypeIdsItem item = (TypeIdsItem) dataItems.get(DexData.TYPE_IDS);
        visibility = dexData[annotationItemOff.addOne()];
        int[] ints = Mutf8.readUnsignedLeb128(dexData, annotationItemOff.getValue());
        int typeIdx = ints[0];
        typeContent = item.realData[typeIdx];
        annotationItemOff.add(ints[1]);
        fillNV(dexData, annotationItemOff, dataItems);
    }
}
