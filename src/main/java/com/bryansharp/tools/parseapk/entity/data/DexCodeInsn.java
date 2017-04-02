package com.bryansharp.tools.parseapk.entity.data;


import com.bryansharp.tools.parseapk.DexData;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;
import com.bryansharp.tools.parseapk.entity.base.DvmOpcode;
import com.bryansharp.tools.parseapk.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by bsp on 17/3/19.
 */
public class DexCodeInsn {
    public DvmOpcode op;
    public byte[] data;
    public String desc;
    public Map<String, Integer> descMap = new HashMap<>();
    public Object[] opTarget;

    @Override
    public String toString() {
        String opTargetStr = "";
        if (opTarget != null) {
            for (int i = 0; i < opTarget.length; i++) {
                if (opTarget[i] instanceof String) {
                    opTargetStr += (String) opTarget[i];
                } else if (opTarget[i] instanceof MethodContent) {
                    opTargetStr = " method:" + ((MethodContent) opTarget[i]).className + ((MethodContent) opTarget[i]).name;
                } else if (opTarget[i] instanceof FieldContent) {
                    opTargetStr = " field:" + ((FieldContent) opTarget[i]).className + ((FieldContent) opTarget[i]).name;
                } else if (opTarget[i] instanceof ProtoContent) {
                    opTargetStr = "method:" + ((ProtoContent) opTarget[i]).shorty + ((ProtoContent) opTarget[i]).returnType;
                }
            }
        }
        return "\n\n\t\t\t\t " + op.opcodeType.form + " " + op.pattern + " " + op.opcodeType.getDataChars() + " \n\t\t\t\t[" + Utils.bytesToInsnForm(data) + "] " + op.name + " " + opTargetStr + " " + descMap;
    }

    public void parseInsn(Map<String, DexDataItem> dataItems) {
        String hexLine = Utils.bytesToInsnForm(data).replace(" ", "");
        String formForUse = op.opcodeType.formForUse;
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
                descMap.put(p + "", Utils.hexToInt(digit));
            }
        }
        String[] dataTypes = op.dataTypes;
        if (dataTypes != null) {
            opTarget = new Object[dataTypes.length];
            Iterator<Character> iterator = op.opcodeType.dataDescMap.keySet().iterator();
            for (int i = 0; i < dataTypes.length; i++) {
                Character next = iterator.next();
                Integer integer = descMap.get(next + "");
                opTarget[i] = dataItems.get(dataTypes[i]).realData[integer];
            }
        }
    }
}
