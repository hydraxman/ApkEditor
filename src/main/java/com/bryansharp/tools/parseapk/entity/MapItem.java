package com.bryansharp.tools.parseapk.entity;


import com.bryansharp.tools.parseapk.entity.base.DexDataItem;
import com.bryansharp.tools.parseapk.entity.data.MapContent;
import com.bryansharp.tools.parseapk.entity.refs.ClassRef;
import com.bryansharp.tools.parseapk.entity.refs.MapRef;
import com.bryansharp.tools.parseapk.utils.LogUtils;
import com.bryansharp.tools.parseapk.utils.Utils;

import java.util.Map;

/**
 * Created by bsp on 17/3/18.
 */
public class MapItem extends DexDataItem<MapRef, MapContent> {
    private static boolean firstOffset = false;

    public MapItem(String name) {
        super(name);
    }

    @Override
    protected MapRef[] createRefs() {
        return new MapRef[count];
    }

    @Override
    protected int getRefSize() {
        return MapRef.SIZE;
    }

    @Override
    protected MapRef parseRef(int i) {
        MapRef mapRef = new MapRef();
        mapRef.type = Utils.u2ToInt(data, i);
        mapRef.fillTypeDesc();
        i += 2;
        mapRef.unused = Utils.u2ToInt(data, i);
        i += 2;
        mapRef.size = Utils.bytesToInt(data, i);
        i += 4;
        mapRef.offset = Utils.bytesToInt(data, i);
        return mapRef;
    }

    @Override
    public void parse2ndRealData(Map<String, DexDataItem> dataItems, byte[] dexData) {
//        int i = Utils.bytesToInt(dexData, 11928);
//        LogUtils.log(i);
//        byte[] bytes = new byte[11720 - 11344];
//        System.arraycopy(dexData, 11344, bytes, 0, 11720 - 11344);
//        LogUtils.log(i);
    }

    public void fillSize(int count) {
        refStart = 4;
        this.count = count;
        this.byteSize = count * getRefSize() + refStart;
        this.refs = createRefs();
    }
}
