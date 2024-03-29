package com.bryansharp.tools.parseapk.entity;


import com.bryansharp.tools.parseapk.entity.base.DexDataItem;

/**
 * Created by bsp on 17/3/18.
 */
public class ByteItem extends DexDataItem<Byte, Byte> {
    public ByteItem(String name) {
        super(name);
    }

    @Override
    protected Byte[] createRefs() {
        return new Byte[0];
    }

    @Override
    protected int getRefSize() {
        return 1;
    }

    @Override
    protected Byte parseRef(int i) {
        return null;
    }
}
