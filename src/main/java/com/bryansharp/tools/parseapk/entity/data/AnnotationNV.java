package com.bryansharp.tools.parseapk.entity.data;

/**
 * Created by bushaopeng on 17/4/5.
 */
public class AnnotationNV {
    public String name;
    public Object value;

    @Override
    public String toString() {
        return "\n\t\t\t\t\t\t\t\tAnnotationNV{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
