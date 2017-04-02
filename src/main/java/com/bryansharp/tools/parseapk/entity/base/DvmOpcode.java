package com.bryansharp.tools.parseapk.entity.base;


import com.bryansharp.tools.parseapk.DexData;
import com.bryansharp.tools.parseapk.utils.DexOpcodeType;
import com.bryansharp.tools.parseapk.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsp on 17/3/19.
 */
public class DvmOpcode {
    public DexOpcodeType opcodeType;
    public int opcode;
    public int opSize;
    public String pattern;
    public String name;
    public String[] dataTypes;

    public DvmOpcode(int opcode, String pattern, String name) {
        this.opcode = opcode;
        this.pattern = pattern.trim();
        this.name = name.trim();
        char c = pattern.charAt(0);
        if (c != 'r') {
            opSize = Integer.parseInt(c + "");
        }
        this.opcodeType = Utils.getPatternFormMap().get(pattern);
        studyThis();

    }

    private void studyThis() {
        String matchType = null;
        for (Map.Entry<String, String[]> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            if (name.contains(key)) {
                if (matchType != null) {
                    if (matchType.length() < key.length()) {
                        matchType = key;
                    }
                } else {
                    matchType = key;
                }
            }
        }
        if (matchType != null) {
            dataTypes = hashMap.get(matchType);
        }
    }

    @Override
    public String toString() {
        return "\n\t\t\t\tcode{" +
                "opcode=" + opcode +
                ", opSize=" + opSize +
                ", pattern='" + pattern + '\'' +
                ", name='" + name + '\'' +
                ", opcodeType='" + opcodeType + '\'' +
                '}';
    }

    static HashMap<String, String[]> hashMap = new HashMap<>();

    static {
        addTypeItem("iget", DexData.FIELD_IDS);
        addTypeItem("sget", DexData.FIELD_IDS);
        addTypeItem("iput", DexData.FIELD_IDS);
        addTypeItem("sput", DexData.FIELD_IDS);
        addTypeItem("invoke", DexData.METHOD_IDS);
        addTypeItem("invoke-polymorphic", DexData.METHOD_IDS, DexData.PROTO_IDS);
        addTypeItem("const-string", DexData.STRING_IDS);
        addTypeItem("const-class", DexData.TYPE_IDS);
        addTypeItem("check-cast", DexData.TYPE_IDS);
        addTypeItem("new", DexData.TYPE_IDS);
    }

    private static void addTypeItem(String matchName, String... dataType) {
        hashMap.put(matchName, dataType);
    }

}
