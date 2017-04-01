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
        char p, np;
        int repTimes = 0;
        for (int i = 0; i < formForUse.length(); i++) {
            p = formForUse.charAt(i);
            if (i < formForUse.length() - 1) {
                np = formForUse.charAt(i + 1);
                if (p != np) {
                    if (("" + p + np).equals("op")) {
                        i++;
                    } else {
                    }
                } else {
                    repTimes++;
                }
            } else {
                int appearTimes = repTimes + 1;
            }
        }
    }

    public static class InnerDataDesc {
        public char dataChar;
        public int type;
        public int length;
    }
}
