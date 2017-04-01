package com.bryansharp.tools.parseapk.entity.base;


import com.bryansharp.tools.parseapk.utils.Utils;

import java.util.HashMap;

/**
 * Created by bsp on 17/3/19.
 */
public class DvmOpcode {
    private static final int DATA_TYPE_FIELD = 1;
    private static final int DATA_TYPE_METHOD = 2;
    private static final int DATA_TYPE_TYPE = 3;
    private static final int DATA_TYPE_STRING = 4;
    public String form;
    public String formForUse;
    public int opcode;
    public int opSize;
    public String pattern;
    public String name;
    public int dataType;
    public char dataPos1 = 'N';
    public char dataPos2 = 'N';
    public char dataPos3 = 'N';

    public DvmOpcode(int opcode, String pattern, String name) {
        this.opcode = opcode;
        this.pattern = pattern.trim();
        this.name = name.trim();
        char c = pattern.charAt(0);
        if (c != 'r') {
            opSize = Integer.parseInt(c + "");
        }
        this.form = Utils.getPatternFormMap().get(pattern);
        if (this.form != null) {
            this.formForUse = form.replaceAll("[|\\s]+", "").replaceAll("op.", "op").replace("lo", "").replace("hi", "");
        }
    }

    public DvmOpcode(int opcode, String pattern, String desc, String form) {
        this(opcode, pattern, desc);
        this.form = form;
    }

    @Override
    public String toString() {
        return "\n\t\t\t\tcode{" +
                "opcode=" + opcode +
                ", opSize=" + opSize +
                ", pattern='" + pattern + '\'' +
                ", name='" + name + '\'' +
                ", form='" + form + '\'' +
                '}';
    }

    public void studyOp() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("iget", DATA_TYPE_FIELD);
        hashMap.put("sget", DATA_TYPE_FIELD);
        hashMap.put("iput", DATA_TYPE_FIELD);
        hashMap.put("sput", DATA_TYPE_FIELD);
        hashMap.put("invoke", DATA_TYPE_METHOD);
        hashMap.put("invoke-polymorphic", DATA_TYPE_METHOD);
        hashMap.put("const-string", DATA_TYPE_STRING);
        hashMap.put("const-class", DATA_TYPE_TYPE);
        hashMap.put("check-cast", DATA_TYPE_TYPE);
        hashMap.put("new", DATA_TYPE_TYPE);
    }

}
