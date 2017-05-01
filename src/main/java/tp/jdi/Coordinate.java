package tp.jdi;

import com.sun.tools.javac.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liwangchun on 17/4/28.
 * 描述要查询的类型
 */
public class Coordinate {
    public Set<String> getVarNames() {
        return varNames;
    }
    private Set<String> varNames;//局部变量
    private String className;
    private int lineNumber;

    public Set<String> getEntryClassMethods() {
        return entryClassMethods;
    }

    private void setEntryClassMethods(Set<String> entryClassMethods) {
        this.entryClassMethods = entryClassMethods;
    }

    private Set<String> entryClassMethods;

    public String getEntryClass() {
        return entryClass;
    }

    private void setEntryClass(String entryClass) {
        this.entryClass = entryClass;
    }

    private String entryClass;

    public Set<String> getExitClassMethods() {
        return exitClassMethods;
    }

    private void setExitClassMethods(Set<String> exitClassMethods) {
        this.exitClassMethods = exitClassMethods;
    }

    private Set<String> exitClassMethods;

    private void setObjFields(Set<String> objFields) {
        this.objFields = objFields;
    }

    public Set<String> getObjFields() {
        return objFields;
    }

    private Set<String> objFields;
    private String accessField;//类的属性
    private String modifyField;//类的方法

    public String getExitClassName() {
        return exitClassName;
    }

    private void setExitClassName(String exitClassName) {
        this.exitClassName = exitClassName;
    }

    private String exitClassName;

    public Set<String> getEntryExcludeClass() {
        return entryExcludeClass;
    }

    private void setEntryExcludeClass(Set<String> entryExcludeClass) {
        this.entryExcludeClass = entryExcludeClass;
    }

    public Set<String> getExitExcludeClass() {
        return exitExcludeClass;
    }

    private void setExitExcludeClass(Set<String> exitExcludeClass) {
        this.exitExcludeClass = exitExcludeClass;
    }

    private Set<String> entryExcludeClass;
    private Set<String> exitExcludeClass;

    public String getModifyField() {
        return modifyField;
    }

    public String getAccessField() {
        return accessField;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    private String sourceFileName;

    public int getLineNumber() {
        return lineNumber;
    }


    public String getClassName() {
        return className;
    }

    private void setVarNames(Set<String> varNames) {
        this.varNames = varNames;
    }

    private void setClassName(String className) {
        this.className = className;
    }

    private void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    private void setAccessField(String accessField) {
        this.accessField = accessField;
    }

    private void setModifyField(String modifyField) {
        this.modifyField = modifyField;
    }

    private void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    private Coordinate(){ }
    public static class Param{
        private Coordinate cd;
        private Set<String> varNames;//局部变量
        private String className;
        private int lineNumber;
        private String sourceFileName;
        private String accessField;
        private String modifyField;
        private String exitClassName;
        private Set<String> entryClassMethods;
        private Set<String> exitClassMethods;
        private List<String> entryExcludeClass;
        private List<String> exitExcludeClass;
        private Set<String> objFields;
        private String entryClass;

        public Param(){
            cd = new Coordinate();
        }

        public Param objField(String fieldName){
            Set<String> objFields = new HashSet<String>();
            objFields.add(fieldName);
            return objField(objFields);
        }
        public Param entryClasssMethods(String name){
            Set<String> set = new HashSet<String>();
            set.add(name);
            return entryClasssMethods(set);
        }

        public Param entryClasssMethods(Set<String> names){
            cd.setEntryClassMethods(names);
            return this;

        }
        public Param exitClasssMethods(String name){
            Set<String> set = new HashSet<String>();
            set.add(name);
            return exitClasssMethods(set);
        }

        public Param exitClasssMethods(Set<String> names){
            cd.setExitClassMethods(names);
            return this;

        }

        public Param objField(Set<String> fieldNames){
            cd.setObjFields(fieldNames);
            return this;
        }

        public Param varName(Set<String> varNames){
            cd.setVarNames(varNames);
            return this;
        }
        public Param varName(String varName){
            Set<String> varNames = new HashSet<String>();
            varNames.add(varName);
            return varName(varNames);
        }

        public Param className(String className){
            cd.setClassName(className);
            return this;
        }
        public Param entryClassName(String name){
            cd.setEntryClass(name);
            return this;
        }
        public Param lineNumber(int lineNumber){
            cd.setLineNumber(lineNumber);
            return this;
        }

        public Param sourceFileName(String sourceFileName){
            cd.setSourceFileName(sourceFileName);
            return this;
        }

        public Param modifyField(String modifyField){
            cd.setModifyField(modifyField);
            return this;
        }

        public Param accessField(String accessField){
            cd.setAccessField(accessField);
            return this;
        }
        public Param exitMethodClass(String exitClassName){
            cd.setExitClassName(exitClassName);
            return this;
        }
        public Param exitExcludeClass(Set<String> exitExcludeClass){
            cd.setExitExcludeClass(exitExcludeClass);
            return this;
        }
        public Param exitExcludeClass(String name){
            Set<String> names = new HashSet<String>();
            names.add(name);
            return exitExcludeClass(names);

        }
        public Param entryExcludeClass(Set<String> entryExcludeClass){
            cd.setEntryExcludeClass(entryExcludeClass);
            return this;
        }

        public Param entryExcludeClass(String name){
            Set<String> names = new HashSet<String>();
            names.add(name);
            return entryExcludeClass(names);
        }

        public Coordinate build(){
            return cd;
        }
    }
}
