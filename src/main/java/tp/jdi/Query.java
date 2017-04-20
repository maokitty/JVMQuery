package tp.jdi;

import java.util.List;
import java.util.Set;

/**
 * Created by liwangchun on 17/4/20.
 */
public class Query {
    public Set<String> getVarNames() {
        return varNames;
    }
    private Set<String> varNames;//局部变量
    private String className;
    private int lineNumber;
    private String accessField;//类的属性
    private String modifyField;//类的方法
    private String entryMethodClass;
    private String exitMethodClass;
    private List<String> entryExcludeClass;
    private List<String> exitExcludeClass;

    public String getEntryMethodClass() {
        return entryMethodClass;
    }

    public String getExitMethodClass() {
        return exitMethodClass;
    }

    public List<String> getEntryExcludeClass() {
        return entryExcludeClass;
    }

    public List<String> getExitExcludeClass() {
        return exitExcludeClass;
    }

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

    private void setEntryMethodClass(String entryMethodClass) {
        this.entryMethodClass = entryMethodClass;
    }

    private void setExitMethodClass(String exitMethodClass) {
        this.exitMethodClass = exitMethodClass;
    }

    private void setEntryExcludeClass(List<String> entryExcludeClass) {
        this.entryExcludeClass = entryExcludeClass;
    }

    private void setExitExcludeClass(List<String> exitExcludeClass) {
        this.exitExcludeClass = exitExcludeClass;
    }

    private void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    private Query(){ }
    public static class Param{
        private Query query;
        private Set<String> varNames;//局部变量
        private String className;
        private int lineNumber;
        private String sourceFileName="";
        private String accessField="";
        private String modifyField="";
        private String entryMethodClass;
        private String exitMethodClass;
        private List<String> entryExcludeClass;
        private List<String> exitExcludeClass;
        public Param(){
            query = new Query();
        }

        public Param varName(Set<String> varNames){
            query.setVarNames(varNames);
            return this;
        }
        public Param className(String className){
            query.setClassName(className);
            return this;
        }
        public Param lineNumber(int lineNumber){
            query.setLineNumber(lineNumber);
            return this;
        }

        public Param sourceFileName(String sourceFileName){
            query.setSourceFileName(sourceFileName);
            return this;
        }

        public Param modifyField(String modifyField){
            query.setModifyField(modifyField);
            return this;
        }

        public Param accessField(String accessField){
            query.setAccessField(accessField);
            return this;
        }
        public Param entryMethodClass(String entryMethodClass){
            query.setEntryMethodClass(entryMethodClass);
            return this;
        }
        public Param exitMethodClass(String exitMethodClass){
            query.setExitMethodClass(exitMethodClass);
            return this;
        }
        public Param exitExcludeClass(List<String> exitExcludeClass){
            query.setExitExcludeClass(exitExcludeClass);
            return this;
        }
        public Param entryExcludeClass(List<String> entryExcludeClass){
            query.setEntryExcludeClass(entryExcludeClass);
            return this;
        }

        public Query build(){
            return query;
        }
    }

    @Override
    public String toString() {
        return "Query{" +
                "varNames=" + varNames +
                ", className='" + className + '\'' +
                ", lineNumber=" + lineNumber +
                ", accessField='" + accessField + '\'' +
                ", modifyField='" + modifyField + '\'' +
                ", entryMethodClass='" + entryMethodClass + '\'' +
                ", exitMethodClass='" + exitMethodClass + '\'' +
                ", entryExcludeClass=" + entryExcludeClass +
                ", exitExcludeClass=" + exitExcludeClass +
                ", sourceFileName='" + sourceFileName + '\'' +
                '}';
    }
}
