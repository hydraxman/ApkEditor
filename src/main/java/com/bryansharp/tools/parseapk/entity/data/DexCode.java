package com.bryansharp.tools.parseapk.entity.data;

import com.bryansharp.tools.parseapk.entity.base.DvmOpcode;
import com.bryansharp.tools.parseapk.utils.Mutf8;
import com.bryansharp.tools.parseapk.utils.Utils;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by bsp on 17/3/19.
 */
public class DexCode {
    public int registersSize;
    public int insSize;
    public int outsSize;
    public int triesSize;
    public int debugInfoOff;
    public int insnsSize;
    public int[] insns;
    public byte[] insnsRaw;
    public List<DexCodeInsn> insnList = new ArrayList<>();
    public DebugInfo debugInfo;

    @Override
    public String toString() {
        return "\n\t\t\tDexCode{" +
                "registersSize=" + registersSize +
                ", insSize=" + insSize +
                ", outsSize=" + outsSize +
                ", triesSize=" + triesSize +
                ", debugInfoOff=" + debugInfoOff +
                ", insnsSize=" + insnsSize +
                ", debugInfo=" + debugInfo +
                ", insns=" + Utils.intsToStringBy4(insns, 0) +
                ", insnList=" + insnList +
                '}';
    }

    public void parseInsns(Map<String, DexDataItem> dataItems) {
        int start = 0;
        DexCodeInsn dexCodeInsn;
        while (start < insnsRaw.length) {
            byte op = insnsRaw[start];
            DvmOpcode dvmOpcode = Utils.getDvmOpCodeMap().get(op & 0xff);
            dexCodeInsn = new DexCodeInsn();
            insnList.add(dexCodeInsn);
            dexCodeInsn.op = dvmOpcode;
            int len = dvmOpcode.opSize * 2;
            dexCodeInsn.data = new byte[len];
            System.arraycopy(insnsRaw, start, dexCodeInsn.data, 0, len);
            dexCodeInsn.parseInsn(dataItems);
            start += len;
        }
    }

    public void parseDebugInfo(byte[] dexData, DexDataItem dexDataItem) {
        int off = this.debugInfoOff;
        if (off > 0) {
            debugInfo = new DebugInfo();
            int[] ints = Mutf8.readUnsignedLeb128(dexData, off);
            debugInfo.lineStart = ints[0];
            off += ints[1];
            ints = Mutf8.readUnsignedLeb128(dexData, off);
            int size = ints[0];
            debugInfo.parametersSize = size;
            off += ints[1];
            if (size > 0) {
                debugInfo.parameterNames = new String[size];
            }
            int i = 0;
            while (size > 0) {
                ints = Mutf8.readUnsignedLeb128(dexData, off);
                int result = ints[0] - 1;
                if (result == -1) {
                    debugInfo.parameterNames[i] = "NO_INDEX";
                } else {
                    debugInfo.parameterNames[i] = (String) dexDataItem.realData[result];
                }
                off += ints[1];
                i++;
                size--;
            }
        }
    }

    public static class DebugInfo {
        int lineStart;
        int parametersSize;
        String[] parameterNames;

        @Override
        public String toString() {
            return "DebugInfo{" +
                    "lineStart=" + lineStart +
                    ", parametersSize=" + parametersSize +
                    ", parameterNames=" + Arrays.toString(parameterNames) +
                    '}';
        }
    }
}
