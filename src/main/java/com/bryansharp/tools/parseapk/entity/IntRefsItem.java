package com.bryansharp.tools.parseapk.entity;


import com.bryansharp.tools.parseapk.utils.Utils;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;

/**
 * Created by bsp on 17/3/18.
 */
public class IntRefsItem extends DexDataItem<Integer, String> {
    public IntRefsItem(String name) {
        super(name);
    }

    @Override
    protected Integer[] createRefs() {
        return new Integer[count];
    }

    @Override
    protected int getRefSize() {
        return Integer.SIZE / Byte.SIZE;
    }


    protected Integer parseRef(int i) {
        return Utils.bytesToInt(data, i);
    }



}
