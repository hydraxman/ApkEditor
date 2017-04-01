package com.bryansharp.tools.parseapk.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bushaopeng on 17/4/1.
 */
public class DexOpcodeType {
    public String name;
    public String form;
    public String formForUse;
    public Map<Character, InnerDataDesc> dataDescMap = new HashMap<>();

    public DexOpcodeType(String name, String form) {
        this.name = name;
        this.form = form;
        if (form != null) {
            this.formForUse = form.replaceAll("[|\\s]+", "").replaceAll("op.", "op").replace("lo", "").replace("hi", "");
        }
    }

    public static class InnerDataDesc {
        public char dataChar;
        public int type;
        public int length;
    }
}
