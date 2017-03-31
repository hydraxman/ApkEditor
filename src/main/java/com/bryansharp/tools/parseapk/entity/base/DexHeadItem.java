package com.bryansharp.tools.parseapk.entity.base;

import com.bryansharp.tools.parseapk.utils.Utils;

/**
 * Created by bushaopeng on 17/3/16.
 */
public class DexHeadItem extends DexItem {


    public DexHeadItem(String name, int start, int length) {
        this.name = name;
        this.start = start;
        this.byteSize = length;
        this.type = TYPE_INT_INFO;
    }

    public DexHeadItem(String name, int start, int length, int type) {
        this.name = name;
        this.start = start;
        this.byteSize = length;
        this.type = type;
    }

    @Override
    public String toString() {
        int data = bytes2Int();
        String dataStr;
        if (data < 0) {
            dataStr = Utils.bytesToString(this.data, 0);
        } else {
            dataStr = data + "";
        }
        return name + ":" + dataStr;
    }

    public String getContentDesc() {
        switch (type) {
            case TYPE_INT_SIZE:
                return name.replace("Size", "").replace("size", "");
            case TYPE_INT_OFFSET:
                return name.replace("Off", "").replace("off", "");
        }
        return "";
    }

    public boolean hasSubContent() {
        return type == TYPE_INT_OFFSET || type == TYPE_INT_SIZE;
    }
}
