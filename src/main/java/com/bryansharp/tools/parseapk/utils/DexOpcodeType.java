package com.bryansharp.tools.parseapk.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bushaopeng on 17/4/1.
 */
public class DexOpcodeType {
    public String name;
    public String form;
    public String formForUse;
    public LinkedHashMap<Character, InnerDataDesc> dataDescMap = new LinkedHashMap<>();
    private int typesCount;

    public DexOpcodeType(String name, String form) {
        this.name = name;
        this.form = form;
        if (form != null) {
            this.formForUse = form.replaceAll("[|\\s]+", "").replaceAll("op.", "op").replace("lo", "").replace("hi", "");
            char p, np;
            int repTimes = 0;
            for (int i = 0; i < formForUse.length(); i++) {
                p = formForUse.charAt(i);
                if (i < formForUse.length() - 1) {
                    np = formForUse.charAt(i + 1);
                    if (p != np) {
                        if (("" + p + np).equals("op")) {
                            i++;
                            repTimes = 0;
                        } else {
                            checkAddThis(p, repTimes);
                            repTimes = 0;
                        }
                    } else {
                        repTimes++;
                    }
                } else {
                    checkAddThis(p, repTimes);
                    repTimes = 0;
                }
            }
        }
    }

    public List<Character> getDataChars() {
        LinkedList<Character> characters = new LinkedList<>();
        for (Map.Entry<Character, InnerDataDesc> entry : dataDescMap.entrySet()) {
            characters.add(entry.getKey());
        }
        return characters;
    }

    private void checkAddThis(char p, int repTimes) {
        int appearTime = repTimes + 1;
        if (dataDescMap.get(p) != null) {
            return;
        }
        if (appearTime > 3) {
            dataDescMap.put(p, new InnerDataDesc(p, appearTime));
        }
    }

    public static class InnerDataDesc {
        public char dataChar;
        public int type;
        public int length;

        public InnerDataDesc(char dataChar, int length) {
            this.dataChar = dataChar;
            this.length = length;
        }
    }
}
