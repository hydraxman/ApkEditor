package com.bryansharp.tools.parseapk.entity;


import com.bryansharp.tools.parseapk.entity.base.DexData;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;
import com.bryansharp.tools.parseapk.entity.data.FieldContent;
import com.bryansharp.tools.parseapk.entity.data.MapContent;
import com.bryansharp.tools.parseapk.entity.refs.FieldRef;
import com.bryansharp.tools.parseapk.entity.refs.MapRef;
import com.bryansharp.tools.parseapk.utils.Utils;

import java.util.Map;

/**
 * Created by bsp on 17/3/18.
 */
public class MapItem extends DexDataItem<MapRef, MapContent> {
    public MapItem(String name) {
        super(name);
    }

    @Override
    protected MapRef[] createRefs() {
        return new MapRef[count];
    }

    @Override
    protected int getRefSize() {
        return FieldRef.SIZE;
    }

    @Override
    protected MapRef parseRef(int i) {
        return null;
    }

    @Override
    public void parse2ndRealData(Map<String, DexDataItem> dataItems, byte[] dexData) {
    }
}
