package com.bryansharp.tools.parseapk.entity.data;


import com.bryansharp.tools.parseapk.DexData;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;
import com.bryansharp.tools.parseapk.entity.base.DvmOpcode;
import com.bryansharp.tools.parseapk.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsp on 17/3/19.
 */
public class DexCodeInsn {
    public DvmOpcode op;
    public byte[] data;
    public String desc;
    public Map<String, Integer> descMap = new HashMap<>();
    public Object opTarget;

    @Override
    public String toString() {
        String opTargetStr = "unknown";
        if (opTarget instanceof String) {
            opTargetStr = (String) opTarget;
        } else if (opTarget instanceof MethodContent) {
            opTargetStr = "method:" + ((MethodContent) opTarget).className + "." + ((MethodContent) opTarget).name;
        }
        return "\n\n\t\t\t\t " + op.form + " " + op.pattern + " " + op.dataPos1 + " \n\t\t\t\t[" + Utils.bytesToInsnForm(data) + "] " + op.name + " " + opTargetStr + " " + descMap;
    }

    public void parseInsn(Map<String, DexDataItem> dataItems) {
        String hexLine = Utils.bytesToInsnForm(data).replace(" ", "");
        String formForUse = op.formForUse;
        char p;
        char np;
        int repTimes = 0;
        int off = 0;
        for (int i = 0; i < formForUse.length(); i++) {
            p = formForUse.charAt(i);
            if (i < formForUse.length() - 1) {
                np = formForUse.charAt(i + 1);
                if (p != np) {
                    if (("" + p + np).equals("op")) {
                        String digit = hexLine.substring(off, off + 2);
                        off += 2;
                        descMap.put("op", Utils.hexToInt(digit));
                        i++;
                    } else {
                        String digit = hexLine.substring(off, off + repTimes + 1);
                        off += repTimes + 1;
                        descMap.put(p + "", Utils.hexToInt(digit));
                        repTimes = 0;
                    }
                } else {
                    repTimes++;
                }
            } else {
                int appearTimes = repTimes + 1;
                String digit = hexLine.substring(off, off + appearTimes);
                if (digit.length() >= 8) {
                    digit = digit.substring(4, 8) + digit.substring(0, 4);
                }
                if (appearTimes >= 4) {
                    if (op.dataPos1 != 'N') {
                        op.dataPos1 = p;
                    } else if (op.dataPos2 != 'N') {
                        op.dataPos2 = p;
                    } else if (op.dataPos3 != 'N') {
                        op.dataPos3 = p;
                    }
                }
                descMap.put(p + "", Utils.hexToInt(digit));
            }
        }
        if (op.name.contains("invok")) {
            Integer b = descMap.get("B");
            opTarget = dataItems.get(DexData.METHOD_IDS).realData[b];
        } else if ("const-string".equals(op.name)) {
            Integer b = descMap.get("B");
            opTarget = dataItems.get(DexData.STRING_IDS).realData[b];
        }
    }
}
