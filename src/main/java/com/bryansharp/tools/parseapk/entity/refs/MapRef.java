package com.bryansharp.tools.parseapk.entity.refs;

/**
 * Created by bushaopeng on 17/3/30.
 */
public class MapRef {
    public static final int SIZE = 12;
    public int type;
    public String typeDesc;
    public int unused;
    public int size;
    public int offset;

    public void fillTypeDesc() {
        switch (type) {
            case 0x0:
                typeDesc = "header_item";
                break;
            case 0x1:
                typeDesc = "string_id_item";
                break;
            case 0x2:
                typeDesc = "type_id_item";
                break;
            case 0x3:
                typeDesc = "proto_id_item";
                break;
            case 0x4:
                typeDesc = "field_id_item";
                break;
            case 0x5:
                typeDesc = "method_id_item";
                break;
            case 0x6:
                typeDesc = "class_def_item";
                break;
            case 0x1000:
                typeDesc = "map_list";
                break;
            case 0x1001:
                typeDesc = "type_list";
                break;
            case 0x1002:
                typeDesc = "annotation_set_ref_list";
                break;
            case 0x1003:
                typeDesc = "annotation_set_item";
                break;
            case 0x2000:
                typeDesc = "class_data_item";
                break;
            case 0x2001:
                typeDesc = "code_item";
                break;
            case 0x2002:
                typeDesc = "string_data_item";
                break;
            case 0x2003:
                typeDesc = "debug_info_item";
                break;
            case 0x2004:
                typeDesc = "annotation_item";
                break;
            case 0x2005:
                typeDesc = "encoded_array_item";
                break;
            case 0x2006:
                typeDesc = "annotations_directory_item";
                break;
        }
    }

    @Override
    public String toString() {
        return "MapRef{" +
                "typeDesc='" + typeDesc + '\'' +
                ", unused=" + unused +
                ", size=" + size +
                ", offset=" + offset +
                '}';
    }
}
