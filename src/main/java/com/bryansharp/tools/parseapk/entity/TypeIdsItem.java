package com.bryansharp.tools.parseapk.entity;


import com.bryansharp.tools.parseapk.entity.base.DexData;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;

import java.util.Map;

/**
 * Created by bsp on 17/3/18.
 */
public class TypeIdsItem extends IntRefsItem {
    public TypeIdsItem(String name) {
        super(name);
    }

    @Override
    protected void parse1stRealData(Map<String, DexDataItem> dataItems, byte[] dexData) {
        StringIdsItem dexDataItem = (StringIdsItem) dataItems.get(DexData.STRING_IDS);
        realData = new String[refs.length];
        for (int i = 0; i < realData.length; i++) {
            Integer ref = refs[i];
            realData[i] = dexDataItem.realData[ref];
        }
    }
}
