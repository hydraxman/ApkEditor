package com.bryansharp.tools.parseapk.entity;


import com.android.dex.EncodedValueCodec;
import com.android.dex.EncodedValueReader;
import com.bryansharp.tools.parseapk.DexData;
import com.bryansharp.tools.parseapk.entity.data.AnnotationItemContent;
import com.bryansharp.tools.parseapk.entity.data.AnnotationNV;
import com.bryansharp.tools.parseapk.entity.data.AnnotationsDirItem;
import com.bryansharp.tools.parseapk.entity.data.ClassContent;
import com.bryansharp.tools.parseapk.utils.IntPointer;
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
        MethodIdsItem mItem = (MethodIdsItem) dataItems.get(DexData.METHOD_IDS);
        FieldIdsItem fItem = (FieldIdsItem) dataItems.get(DexData.FIELD_IDS);
        for (int i = 0; i < realData.length; i++) {
            ClassContent content = realData[i];
            LogUtils.log("current parsing " + i);
            content.classData.fillData(dataItems, dexData);
            ClassRef ref = refs[i];
            int staticValuesOff = ref.staticValuesOff;
            if (staticValuesOff > 0) {
                content.staticValues.addAll(Utils.readEncodedArray(dexData, dataItems, IntPointer.get(staticValuesOff)));
            }
            int classDefAnnotationOff = ref.annotationsOff;
            if (classDefAnnotationOff > 0) {
                int classAnnotationsOff = Utils.bytesToInt(dexData, classDefAnnotationOff);
                classDefAnnotationOff += 4;
                AnnotationsDirItem annotationsDirItem = new AnnotationsDirItem();
                annotationsDirItem.classAnnoOff = classAnnotationsOff;
                if (classAnnotationsOff > 0) {
                    annotationsDirItem.annotations.addAll(readAnnotations(dataItems, dexData, classAnnotationsOff));
                }
                annotationsDirItem.fieldsSize = Utils.bytesToInt(dexData, classDefAnnotationOff);
                annotationsDirItem.fieldAnnotations = new AnnotationsDirItem.FieldAnnotations[annotationsDirItem.fieldsSize];
                classDefAnnotationOff += 4;
                annotationsDirItem.annotatedMethodsSize = Utils.bytesToInt(dexData, classDefAnnotationOff);
                annotationsDirItem.methodAnnotations = new AnnotationsDirItem.MethodAnnotations[annotationsDirItem.annotatedMethodsSize];
                classDefAnnotationOff += 4;
                annotationsDirItem.annotatedParametersSize = Utils.bytesToInt(dexData, classDefAnnotationOff);
                annotationsDirItem.paramAnnotations = new AnnotationsDirItem.ParamAnnotations[annotationsDirItem.annotatedParametersSize];
                classDefAnnotationOff += 4;
                int fieldsSize = annotationsDirItem.fieldsSize;
                if (fieldsSize > 0) {
                    while (fieldsSize > 0) {
                        annotationsDirItem.fieldAnnotations[fieldsSize - 1] = new AnnotationsDirItem.FieldAnnotations();
                        int fieldIndex = Utils.bytesToInt(dexData, classDefAnnotationOff);
                        classDefAnnotationOff += 4;
                        annotationsDirItem.fieldAnnotations[fieldsSize - 1].fieldContent = fItem.realData[fieldIndex];
                        int annotationsOff = Utils.bytesToInt(dexData, classDefAnnotationOff);
                        classDefAnnotationOff += 4;
                        annotationsDirItem.fieldAnnotations[fieldsSize - 1].annotations.addAll(readAnnotations(dataItems, dexData, annotationsOff));
                        fieldsSize--;
                    }
                }
                int annotatedMethodsSize = annotationsDirItem.annotatedMethodsSize;
                if (annotatedMethodsSize > 0) {
                    while (annotatedMethodsSize > 0) {
                        annotationsDirItem.methodAnnotations[annotatedMethodsSize - 1] = new AnnotationsDirItem.MethodAnnotations();
                        int methodIdx = Utils.bytesToInt(dexData, classDefAnnotationOff);
                        classDefAnnotationOff += 4;
                        annotationsDirItem.methodAnnotations[annotatedMethodsSize - 1].methodContent = mItem.realData[methodIdx];
                        int annotationsOff = Utils.bytesToInt(dexData, classDefAnnotationOff);
                        classDefAnnotationOff += 4;
                        annotationsDirItem.methodAnnotations[annotatedMethodsSize - 1].annotations.addAll(readAnnotations(dataItems, dexData, annotationsOff));
                        annotatedMethodsSize--;
                    }
                }
                int annotatedParametersSize = annotationsDirItem.annotatedParametersSize;
                if (annotatedParametersSize > 0) {
                    while (annotatedParametersSize > 0) {
                        annotationsDirItem.paramAnnotations[annotatedParametersSize - 1] = new AnnotationsDirItem.ParamAnnotations();
                        int methodIdx = Utils.bytesToInt(dexData, classDefAnnotationOff);
                        classDefAnnotationOff += 4;
                        annotationsDirItem.paramAnnotations[annotatedParametersSize - 1].methodContent = mItem.realData[methodIdx];
                        int annotationsOff = Utils.bytesToInt(dexData, classDefAnnotationOff);
                        classDefAnnotationOff += 4;
                        int sizeOfRefList = Utils.bytesToInt(dexData, annotationsOff);
                        annotationsDirItem.paramAnnotations[annotatedParametersSize - 1].annotationSetRefList = new AnnotationsDirItem.AnnotationSetRefList[sizeOfRefList];
                        annotationsOff += 4;
                        while (sizeOfRefList > 0) {
                            int annotationsOffInRef = Utils.bytesToInt(dexData, annotationsOff);
                            annotationsOff += 4;
                            annotationsDirItem.paramAnnotations[annotatedParametersSize - 1].annotationSetRefList[sizeOfRefList - 1] = new AnnotationsDirItem.AnnotationSetRefList();
                            annotationsDirItem.paramAnnotations[annotatedParametersSize - 1].annotationSetRefList[sizeOfRefList - 1].annotations.addAll(readAnnotations(dataItems, dexData, annotationsOffInRef));
                            sizeOfRefList--;
                        }
                        annotatedParametersSize--;
                    }
                }
                content.annotationsDirItem = annotationsDirItem;
            }
        }
    }

    private List<AnnotationItemContent> readAnnotations(Map<String, DexDataItem> dataItems, byte[] dexData, int annotationsOff) {
        LinkedList<AnnotationItemContent> annotations = new LinkedList<>();
        int sizeOfAnnotSet = Utils.bytesToInt(dexData, annotationsOff);
        annotationsOff += 4;
        while (sizeOfAnnotSet > 0) {
            int annotationItemOff = Utils.bytesToInt(dexData, annotationsOff);
            if (annotationItemOff > 0) {
                AnnotationItemContent annotation = new AnnotationItemContent();
                annotation.offset = annotationItemOff;
                IntPointer intPointer = IntPointer.get(annotationItemOff);
                annotation.fillData(dexData, intPointer, dataItems);
                annotations.add(annotation);
            }
            annotationsOff += 4;
            sizeOfAnnotSet--;
        }
        return annotations;
    }


}
