package com.bryansharp.tools.parseapk.entity.data;

import com.android.dex.Annotation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bushaopeng on 17/4/5.
 */
public class AnnotationsDirItem {
    public int classAnnoOff;
    public int fieldsSize;
    public int annotatedMethodsSize;
    public int annotatedParametersSize;
    public FieldAnnotations[] fieldAnnotations;
    public MethodAnnotations[] methodAnnotations;
    public ParamAnnotations[] paramAnnotations;
    public List<AnnotationItemContent> annotations = new LinkedList<>();

    @Override
    public String toString() {
        return "\n\t\t\t\t\t\tAnnotationsDirItem{" +
                "classAnnoOff=" + classAnnoOff +
                ", fieldsSize=" + fieldsSize +
                ", annotatedMethodsSize=" + annotatedMethodsSize +
                ", annotatedParametersSize=" + annotatedParametersSize +
                ", annotations=" + annotations +
                ", fieldAnnotations=" + Arrays.asList(fieldAnnotations) +
                ", methodAnnotations=" + Arrays.asList(methodAnnotations) +
                ", paramAnnotations=" + Arrays.asList(paramAnnotations) +
                '}';
    }

    public static class FieldAnnotations {
        public FieldContent fieldContent;
        public List<AnnotationItemContent> annotations = new LinkedList<>();

        @Override
        public String toString() {
            return "FieldAnnotations{" +
                    "fieldContent=" + fieldContent +
                    ", annotations=" + annotations +
                    '}';
        }
    }

    public static class MethodAnnotations {
        public MethodContent methodContent;
        public List<AnnotationItemContent> annotations = new LinkedList<>();

        @Override
        public String toString() {
            return "MethodAnnotations{" +
                    "methodContent=" + methodContent +
                    ", annotations=" + annotations +
                    '}';
        }
    }

    public static class ParamAnnotations {
        public MethodContent methodContent;
        public AnnotationSetRefList[] annotationSetRefList;

        @Override
        public String toString() {
            return "ParamAnnotations{" +
                    "methodContent=" + methodContent +
                    ", annotationSetRefList=" + Arrays.asList(annotationSetRefList) +
                    '}';
        }
    }

    public static class AnnotationSetRefList {
        public List<AnnotationItemContent> annotations = new LinkedList<>();
    }
}
