package com.bryansharp.tools.parseapk.entity;


import com.android.dex.EncodedValueCodec;
import com.android.dex.EncodedValueReader;
import com.bryansharp.tools.parseapk.DexData;
import com.bryansharp.tools.parseapk.entity.data.ClassContent;
import com.bryansharp.tools.parseapk.utils.Utils;
import com.bryansharp.tools.parseapk.entity.base.DexDataItem;
import com.bryansharp.tools.parseapk.entity.refs.ClassRef;
import com.bryansharp.tools.parseapk.utils.LogUtils;
import com.bryansharp.tools.parseapk.utils.Mutf8;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bsp on 17/3/18.
 */
public class ClassDefsItem extends DexDataItem<ClassRef, ClassContent> {
    public ClassDefsItem(String name) {
        super(name);
    }

    @Override
    protected ClassRef[] createRefs() {
        return new ClassRef[count];
    }

    @Override
    protected int getRefSize() {
        return ClassRef.SIZE;
    }

    @Override
    protected ClassRef parseRef(int i) {
        ClassRef classRef = new ClassRef();
        classRef.classIdx = Utils.bytesToInt(data, i);
        i += 4;
        classRef.accessFlags = Utils.bytesToInt(data, i);
        i += 4;
        classRef.superclassIdx = Utils.bytesToInt(data, i);
        i += 4;
        classRef.interfacesOff = Utils.bytesToInt(data, i);
        i += 4;
        classRef.sourceFileIdx = Utils.bytesToInt(data, i);
        i += 4;
        classRef.annotationsOff = Utils.bytesToInt(data, i);
        i += 4;
        classRef.classDataOff = Utils.bytesToInt(data, i);
        i += 4;
        classRef.staticValuesOff = Utils.bytesToInt(data, i);
        return classRef;
    }

    @Override
    public void parse2ndRealData(Map<String, DexDataItem> dataItems, byte[] dexData) {
        TypeIdsItem item = (TypeIdsItem) dataItems.get(DexData.TYPE_IDS);
        StringIdsItem sItem = (StringIdsItem) dataItems.get(DexData.STRING_IDS);
        String[] realData = item.realData;
        this.realData = new ClassContent[refs.length];

        for (int i = 0; i < refs.length; i++) {
            ClassRef ref = refs[i];
            ClassContent classContent = new ClassContent();
            classContent.className = realData[ref.classIdx];
            classContent.superClassName = realData[ref.superclassIdx];
            classContent.sourceFile = sItem.realData[ref.sourceFileIdx];
            int classDataOff = ref.classDataOff;
            int[] sizes = new int[4];
            int sizeOfSizes = 0;
            for (int j = 0; j < 4; j++) {
                int[] result = Mutf8.readUnsignedLeb128(dexData, classDataOff);
                sizes[j] = result[0];
                classDataOff += result[1];
                sizeOfSizes += result[1];
            }


            classContent.classData = new ClassContent.ClassData(sizes);
            classContent.classData.sizeOfSizes = sizeOfSizes;
            if (i < refs.length - 1) {
                int size = refs[i + 1].classDataOff - ref.classDataOff;
                classContent.classData.data = new byte[size];
                System.arraycopy(dexData, ref.classDataOff, classContent.classData.data, 0, size);
            } else {
                int size = dexData.length - ref.classDataOff;
                classContent.classData.data = new byte[size];
                System.arraycopy(dexData, ref.classDataOff, classContent.classData.data, 0, size);
            }
            this.realData[i] = classContent;
        }
    }

    @Override
    public void parse4thRealData(Map<String, DexDataItem> dataItems, byte[] dexData) {
        TypeIdsItem item = (TypeIdsItem) dataItems.get(DexData.TYPE_IDS);
        for (int i = 0; i < realData.length; i++) {
            ClassContent content = realData[i];
            LogUtils.log("current parsing " + i);
            content.classData.fillData(dataItems, dexData);
            ClassRef ref = refs[i];
            int staticValuesOff = ref.staticValuesOff;
            if (staticValuesOff > 0) {
                content.staticValues.addAll(readEncodedArray(dexData, dataItems, staticValuesOff));
            }
            int annotationOff = ref.annotationsOff;
            if (annotationOff > 0) {
                int classAnnotationsOff = Utils.bytesToInt(dexData, annotationOff);
                if (classAnnotationsOff > 0) {
                    int sizeOfAnnotSet = Utils.bytesToInt(dexData, classAnnotationsOff);
                    classAnnotationsOff += 4;
                    while (sizeOfAnnotSet > 0) {
                        int annotationItemOff = Utils.bytesToInt(dexData, classAnnotationsOff);
                        classAnnotationsOff += 4;
                        sizeOfAnnotSet--;
                    }
                }
                annotationOff += 4;

            }
        }
    }

    private List<Object> readEncodedArray(byte[] byteData, Map<String, DexDataItem> dataItems, int off) {
        LinkedList<Object> array = new LinkedList<>();
        TypeIdsItem item = (TypeIdsItem) dataItems.get(DexData.TYPE_IDS);
        StringIdsItem sItem = (StringIdsItem) dataItems.get(DexData.STRING_IDS);
        int[] ints = Mutf8.readUnsignedLeb128(byteData, off);
        int size = ints[0];
        off += ints[1];
        int argAndType, arg, type, bytesSize;
        while (size > 0) {
            argAndType = byteData[off++] & 0xff;
            type = argAndType & 0x1f;
            arg = (argAndType & 0xe0) >> 5;
            switch (type) {
                case EncodedValueReader.ENCODED_ARRAY:
                    //todo 解决off传入传出的问题
                    array.add(readEncodedArray(byteData, dataItems, off));
                    break;
                case EncodedValueReader.ENCODED_NULL:
                    array.add(null);
                    break;
                case EncodedValueReader.ENCODED_LONG:
                    bytesSize = arg + 1;
                    array.add(Utils.bytesToLong(byteData, off, bytesSize));
                    off += bytesSize;
                    break;
                case EncodedValueReader.ENCODED_BYTE:
                    array.add(byteData[off++]);
                    break;
                case EncodedValueReader.ENCODED_SHORT:
                    bytesSize = arg + 1;
                    int shortValue = Utils.bytesToInt(byteData, off, bytesSize);
                    off += bytesSize;
                    array.add(shortValue);
                    break;
                case EncodedValueReader.ENCODED_CHAR:
                    bytesSize = arg + 1;
                    char charValue = (char) Utils.bytesToInt(byteData, off, bytesSize);
                    off += bytesSize;
                    array.add(charValue);
                    break;
                case EncodedValueReader.ENCODED_TYPE:
                    bytesSize = arg + 1;
                    int typeIndex = (char) Utils.bytesToInt(byteData, off, bytesSize);
                    off += bytesSize;
                    array.add(item.realData[typeIndex]);
                    break;
                case EncodedValueReader.ENCODED_FLOAT:
                    bytesSize = arg + 1;
                    int floatInt = Utils.bytesToIntFillRight(byteData, off, bytesSize);
                    array.add(Float.intBitsToFloat(floatInt));
                    off += bytesSize;
                    break;
                case EncodedValueReader.ENCODED_DOUBLE:
                    bytesSize = arg + 1;
                    long floatLong = Utils.bytesToLongFillRight(byteData, off, bytesSize);
                    array.add(Double.longBitsToDouble(floatLong));
                    off += bytesSize;
                    break;
                case EncodedValueReader.ENCODED_BOOLEAN:
                    array.add(arg == 0 ? false : true);
                    break;
                case EncodedValueReader.ENCODED_STRING: {
                    bytesSize = arg + 1;
                    int stringIndex = Utils.bytesToInt(byteData, off, bytesSize);
                    off += bytesSize;
                    array.add(sItem.realData[stringIndex]);
                    break;
                }
                case EncodedValueReader.ENCODED_INT: {
                    bytesSize = arg + 1;
                    int intValue = Utils.bytesToInt(byteData, off, bytesSize);
                    off += bytesSize;
                    array.add(intValue);
                    break;
                }
            }
            size--;
        }
        return array;
    }
}
