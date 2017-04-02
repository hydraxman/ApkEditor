package com.bryansharp.tools.parseapk.entity.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bsp on 17/3/18.
 */
public class ProtoContent {
    public String shorty;
    public String returnType;
    public List<String> parameters=new LinkedList<>();

    @Override
    public String toString() {
        return "\nProtoContent{" +
                "shorty='" + shorty + '\'' +
                ", returnType='" + returnType + '\'' +
                ", parameters='" + parameters + '\'' +
                '}';
    }
}
