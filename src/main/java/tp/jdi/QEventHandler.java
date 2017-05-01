package tp.jdi;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by liwangchun on 17/4/20.
 */
public class QEventHandler {
    public static final Logger LOG = LoggerFactory.getLogger(QEventHandler.class);
    private String LIST_SIGNATURE="Ljava/util/List";
//    JNITypeparser
    private Coordinate cd = null;
    ThreadReference threadRef;
    public QEventHandler(Coordinate cd){
        this.cd=cd;
    }

    public void breakpointRequest(Event evt){
        BreakpointEvent bpEvt = (BreakpointEvent) evt;
        threadRef = bpEvt.thread();
        try {
            StackFrame stackFrame = threadRef.frame(0);
            for (String varName:cd.getVarNames()){
                LocalVariable var=stackFrame.visibleVariableByName(varName);
                if (var!=null)
                {
                    defaultPrintVar(stackFrame.getValue(var),varName);
                }else{
                    LOG.info("QEventHandler.breakpointRequest var:{} not exist",varName);
                }
            }
        }catch (AbsentInformationException e) {
            LOG.error("QEventHandler.breakpointRequest  cd:{}", cd, e);
        } catch (IncompatibleThreadStateException e) {

            LOG.error("QEventHandler.breakpointRequest  cd:{} cause:{}", cd, e.getCause(),e);
        }
    }

    public void methodEntryRequest(Event evt){
        MethodEntryEvent meEvt = (MethodEntryEvent)evt;
        Method method=meEvt.method();
        Set<String> methods = cd.getEntryClassMethods();
        try {
            if (methods == null || (methods!=null && methods.isEmpty()) || (methods!=null && methods.contains(method.name())))
            {
                LOG.info("ENTRY {}({})",method,method.arguments());
            }
        } catch (AbsentInformationException e) {
            LOG.info("ENTRY {}",method);
        }
    }
    public void methodExitRequest(Event evt){
        MethodExitEvent meEvt = (MethodExitEvent)evt;
        Method method=meEvt.method();
        Set<String> methods = cd.getExitClassMethods();
        if (methods == null || (methods!=null && methods.isEmpty()) || (methods!=null && methods.contains(method.name()))){
            LOG.info("EXIT {}(return Type {}) ", method, method.returnTypeName());
        }
    }

    /**
     * 访问属性的时候产生事件
     * @param evt
     */
    public void accessWatchPointRequest(Event evt){
        //直接拿到的是主线程
        AccessWatchpointEvent aqEvt = (AccessWatchpointEvent) evt;
        Field f=aqEvt.field();
        defaultPrintVar(aqEvt.valueCurrent(), f.name());
    }

    /**
     * 属性被修改的时候访问
     * @param evt
     */
    public void modificationWatchpointRequest(Event evt){
        ModificationWatchpointEvent mqEvt= (ModificationWatchpointEvent) evt;
        String name=mqEvt.field().name();
        defaultPrintVar(mqEvt.valueCurrent(),name+" current");
        defaultPrintVar(mqEvt.valueToBe(),name+" valueTobe");
    }

    private void defaultPrintVar(Value value,String varName){
        if (value instanceof PrimitiveValue){
            primitiveValue(value, varName);
        }else if (value instanceof ObjectReference){
            objectReference(value, varName);
        }else{
            LOG.info("value:{} not exist varName:{}",value,varName);
        }
    }

    public void objectReference(Value value,String varName){
        if (value instanceof StringReference){
            String strV = ((StringReference)value).value();
            LOG.info("String varName:{} value:{}", varName, strV);
        }else if (value instanceof 	ArrayReference){
            ArrayReference arr = (ArrayReference) value;
            List<Value> vals=arr.getValues();
            for (Value val:vals) {
                defaultPrintVar(val, varName);
            }
        }else if (value instanceof ObjectReference){
            ObjectReference objRef=(ObjectReference)value;
            ReferenceType rt = objRef.referenceType();
            String gSignature = rt.genericSignature();
            if (gSignature != null && gSignature.contains(LIST_SIGNATURE)) {
                try {
                    //获取list的大小
                    List<Method> sizeMethods = rt.methodsByName("size");
                    Method sizeMethod = sizeMethods.get(0);
                    Value sizeValue=objRef.invokeMethod(threadRef, sizeMethod, new ArrayList<Value>(), ObjectReference.INVOKE_SINGLE_THREADED);
                    int size=((IntegerValue)sizeValue).value();
                    List<Method> getMethods = rt.methodsByName("get");
                    Method getMethod = getMethods.get(0);
                    VirtualMachine vm=value.virtualMachine();
                    for (int j=0;j<size;j++)
                    {
                        List<IntegerValue> integerParams=new ArrayList<IntegerValue>();
                        integerParams.add(vm.mirrorOf(j));
                        Value indexValue = objRef.invokeMethod(threadRef, getMethod, integerParams, ObjectReference.INVOKE_SINGLE_THREADED);
                        defaultPrintVar(indexValue, indexValue.type().name());
                    }

                } catch (InvalidTypeException e) {
                    LOG.info("objectReference e:{}",e);
                } catch (ClassNotLoadedException e) {
                    LOG.info("objectReference e:{}", e);
                } catch (IncompatibleThreadStateException e) {
                    LOG.info("objectReference e:{}", e);
                } catch (InvocationException e) {
                    LOG.info("objectReference e:{}", e);
                }
            }else{
                //获取所有的实例
//              虚拟机中可能存在多个实例没有被回收
                List<ObjectReference> ors=rt.instances(0l);
                ObjectReference or = null;
                for (ObjectReference objectReference:ors){
                    if (objectReference.uniqueID() == objRef.uniqueID()){
                        or = objectReference;
                    }
                }
                if (or == null){
                    LOG.warn("no object reference:{} find, should not be here",objRef);
                    return;
                }
                List<Field> fields = getFields(rt);
                for (Field field:fields)
                {
                    LOG.info("name:{} value:{}", field.name(), or.getValue(field));
                }
            }
        }else{
            LOG.info("value type:{}", value.type().name());
        }
    }

    private List<Field> getFields (ReferenceType rt){
        List<Field> fields = new ArrayList<Field>();
        Set<String> names = cd.getObjFields();
        if (names==null || names.isEmpty()){
            return rt.fields();
        }
        for (String name:names){
            Field field=rt.fieldByName(name);
            if (field!=null){
                fields.add(field);
            }else{
                LOG.info("field:{} not exist",name);
            }
        }
        return fields;
    }

    public void primitiveValue(Value value,String varName){
        if (value instanceof IntegerValue){
            int intV = ((IntegerValue) value).intValue();
            LOG.info("int varName:{} value:{}",varName,intV);
        }else if (value instanceof LongValue){
            long longV = ((LongValue) value).longValue();
            LOG.info("long varName:{} value:{}", varName, longV);
        }else if (value instanceof DoubleValue){
            double doubleV = ((DoubleValue) value).doubleValue();
            LOG.info("double varName:{} value:{}", varName, doubleV);
        }else if (value instanceof  FloatValue){
            float floatV = ((FloatValue) value).floatValue();
            LOG.info("float varName:{} value:{}", varName, floatV);
        }else if (value instanceof BooleanValue){
            boolean boolV = ((BooleanValue) value).booleanValue();
            LOG.info("bool varName:{} value:{}", varName, boolV);
        }else if (value instanceof  CharValue){
            char charV = ((CharValue) value).charValue();
            LOG.info("cher varName:{} value:{}", varName, charV);
        }else if (value instanceof ShortValue){
            Short shortV = ((ShortValue) value).shortValue();
            LOG.info("short varName:{} value:{}", varName, shortV);
        }else if (value instanceof ByteValue){
            byte byteV = ((ByteValue) value).byteValue();
            LOG.info("byte varName:{} value:{}", varName, byteV);
        }else{
            LOG.info("value type:{}", value.type().name());
        }
    }
}
