package tp.jdi;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by liwangchun on 17/4/20.
 */
public class QEventHandler {
    public static final Logger LOG = LoggerFactory.getLogger(QEventHandler.class);

    public void breakpointRequest(Event evt,Query query){
        BreakpointEvent bpEvt = (BreakpointEvent) evt;
        ThreadReference threadRef = bpEvt.thread();
        LOG.info("QEventHandler.breakpointRequest threadRef name:{} type:{} threadRef", bpEvt.thread().name(), bpEvt.thread().type(), threadRef.type().name());
        try {
            StackFrame stackFrame = threadRef.frame(0);
            List<LocalVariable> vars = stackFrame.visibleVariables();
            for (LocalVariable var : vars) {
                if (query.getVarNames().contains(var.name())) {
                    defaultPrintVar(stackFrame.getValue(var), var.name());
                }
            }
        }catch (AbsentInformationException e) {
            LOG.error("QEventHandler.breakpointRequest  query:{}", query, e);
        } catch (IncompatibleThreadStateException e) {

            LOG.error("QEventHandler.breakpointRequest  query:{} cause:{}", query, e.getCause(),e);
        }
    }

    public void classPrepareRequest(Event event){
        ClassPrepareEvent cpEvt = (ClassPrepareEvent)event;
        LOG.info("cpEvt rt:{}",cpEvt.referenceType());
    }
    public void classUnloadRequest(Event evt){
        ClassUnloadEvent cuEvt = (ClassUnloadEvent)evt;
        LOG.info("unlocad class:{}",cuEvt.className());
    }

    public void methodEntryRequest(Event evt){
        MethodEntryEvent meEvt = (MethodEntryEvent)evt;
        Method method=meEvt.method();
        try {
            LOG.info("entry {}.{}({}) ",method.declaringType().name(),method.name(),method.arguments());
        } catch (AbsentInformationException e) {
            LOG.error("QEventHandler.methodEntryRequest ",e);
        }
    }
    public void methodExitRequest(Event evt){
        MethodExitEvent meEvt = (MethodExitEvent)evt;
        Method method=meEvt.method();
        LOG.info("exit {}.{}(return Type name {}) ",method.declaringType().name(),method.name(),method.returnTypeName());
    }

    /**
     * 访问属性的时候产生事件
     * @param evt
     */
    public void accessWatchPointRequest(Event evt){
        //直接拿到的是主线程  threadRef name:main type:class java.lang.Thread (no class loader) threadRef
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
            primitiveValue(value,varName);
        }else if (value instanceof ObjectReference){
            objectReference(value,varName);
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
        }else if(value instanceof ClassObjectReference){
            ClassObjectReference coRef = (ClassObjectReference) value;
            LOG.info("coRef:{}",coRef);

        }else if (value instanceof ObjectReference){
            ObjectReference objRef=(ObjectReference)value;
            //ObjectReference没有提供对应获取的方法,需要有和StringValue一样对应的方法
            ReferenceType rt = objRef.referenceType();//得到的是对应的变量相关的引用类型，无法得到value
            LOG.info("objRef:{} rt:{}",objRef,rt);
        }else if(value instanceof ClassLoaderReference){
            ClassLoaderReference clRef = (ClassLoaderReference)value;
            LOG.info("clRef:{}",clRef);
        }else if(value instanceof  ThreadReference){
            ThreadReference tRef=(ThreadReference)value;
            LOG.info("tRef:{}",tRef);
        }else if(value instanceof ThreadGroupReference){
            ThreadGroupReference tgRef=(ThreadGroupReference)value;
            LOG.info("tgRef:{}",tgRef);
        }else{
            LOG.info("value type:{}", value.type().name());
        }
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
