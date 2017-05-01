package tp.jdi;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.tools.javac.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by liwangchun on 17/4/20.
 */
public class Query {
    private static Logger LOG = LoggerFactory.getLogger(Query.class);
    private QConnection qcon;
    private  Coordinate cd;
    private VirtualMachine vm;

    public Query(QConnection qcon, Coordinate cd) {
        this.qcon = qcon;
        this.cd = cd;
        this.vm = qcon.attachVMBySocket();
        Assert.checkNonNull(vm);
    }

   public void lovalVar(){
       String className = cd.getClassName();
       Assert.checkNonNull(className, "class name is null");
       Assert.check(!"".equals(className), "class name is empty");
       Assert.check(!(cd.getLineNumber() <= 0), "line number less than zero");
       String sourceFileName = cd.getSourceFileName();
       Assert.checkNonNull(sourceFileName, "source file name is null");
       Assert.check(!"".equals(sourceFileName), "source file name is empty");
       Set<String> varNames = cd.getVarNames();
       Assert.checkNonNull(varNames,"var name is null");
       Assert.check(!cd.getVarNames().isEmpty(),"var name is empty");

       QEventRegister register = new QEventRegister(vm.eventRequestManager());
       List<ReferenceType> rts=vm.classesByName(className);
       ReferenceType rt = rts.get(0);
       register.breakpointRequest(rt,cd, EventRequest.SUSPEND_EVENT_THREAD);
       EventQueue queue = vm.eventQueue();
       QEventHandler handler = new QEventHandler(cd);
       EventSet evts=null;
       while (true) {
           try {
               evts = queue.remove();
               EventIterator iterator = evts.eventIterator();
               while (iterator.hasNext()) {
                   Event evt = iterator.next();
                   EventRequest evtReq = evt.request();
                   if (evtReq instanceof BreakpointRequest) {
                       handler.breakpointRequest(evt);
                   }
               }
           } catch (InterruptedException e) {
               LOG.error("Query.lovalVar",e);
           }finally {
               if (evts!=null){
                   evts.resume();
               }
           }
       }
   }
    public void methodTrace(){
        boolean noEntryClass = cd.getClassName()==null || "".equals(cd.getClassName());
        boolean noEntryExcludeClass = cd.getEntryExcludeClass() == null || cd.getEntryExcludeClass().isEmpty();
        boolean noExitClass = cd.getExitClassName() == null || "".equals(cd.getExitClassName());
        boolean noExitExcludeClass = cd.getExitExcludeClass() == null || cd.getExitExcludeClass().isEmpty();
        Assert.check(!noEntryClass || !noEntryExcludeClass || !noExitClass || !noExitExcludeClass,"no class specify");
        QEventRegister register = new QEventRegister(vm.eventRequestManager());
        register.methodEntryRequest(EventRequest.SUSPEND_NONE,cd)
                .methodExitRequest(EventRequest.SUSPEND_NONE,cd);
        EventQueue queue = vm.eventQueue();
        QEventHandler handler = new QEventHandler(cd);
        while (true) {
            try {
                EventSet evts = queue.remove();
                EventIterator iterator = evts.eventIterator();
                while (iterator.hasNext()) {
                    Event evt = iterator.next();
                    EventRequest evtReq = evt.request();
                    if (!noEntryClass || !noEntryExcludeClass)
                    {
                        if (evtReq instanceof MethodEntryRequest) {
                            handler.methodEntryRequest(evt);
                        }
                    }
                    if (!noExitClass || !noExitExcludeClass)
                    {
                        if (evtReq instanceof MethodExitRequest) {
                            handler.methodExitRequest(evt);
                        }
                    }
                }
            } catch (InterruptedException e) {
                LOG.error("Query.methodTrace",e);
            }
        }
    }

    public void localVarMethodTrace(){
        boolean noEntryClass = cd.getClassName()==null || "".equals(cd.getClassName());
        boolean noEntryExcludeClass = cd.getEntryExcludeClass() == null || cd.getEntryExcludeClass().isEmpty();
        boolean noExitClass = cd.getExitClassName() == null || "".equals(cd.getExitClassName());
        boolean noExitExcludeClass = cd.getExitExcludeClass() == null || cd.getExitExcludeClass().isEmpty();
        Assert.check(!noEntryClass || !noEntryExcludeClass || !noExitClass || !noExitExcludeClass,"no class specify");
        String className = cd.getClassName();
        Assert.checkNonNull(className, "class name is null");
        Assert.check(!"".equals(className), "class name is empty");
        Assert.check(!(cd.getLineNumber() <= 0), "line number less than zero");
        String sourceFileName = cd.getSourceFileName();
        Assert.checkNonNull(sourceFileName, "source file name is null");
        Assert.check(!"".equals(sourceFileName), "source file name is empty");
        Set<String> varNames = cd.getVarNames();
        Assert.checkNonNull(varNames,"var name is null");
        Assert.check(!cd.getVarNames().isEmpty(),"var name is empty");

        QEventRegister register = new QEventRegister(vm.eventRequestManager());
        List<ReferenceType> rts=vm.classesByName(cd.getClassName());
        ReferenceType rt = rts.get(0);
        register.breakpointRequest(rt,cd,EventRequest.SUSPEND_EVENT_THREAD)
                .methodEntryRequest(EventRequest.SUSPEND_NONE, cd)
                .methodExitRequest(EventRequest.SUSPEND_NONE,cd);
        EventQueue queue = vm.eventQueue();
        QEventHandler handler = new QEventHandler(cd);
        EventSet evts = null;
        while (true) {
            try {
                evts = queue.remove();
                EventIterator iterator = evts.eventIterator();
                while (iterator.hasNext()) {
                    Event evt = iterator.next();
                    EventRequest evtReq = evt.request();
                    if (!noEntryClass || !noEntryExcludeClass)
                    {
                        if (evtReq instanceof MethodEntryRequest) {
                            handler.methodEntryRequest(evt);
                        }
                    }
                    if (!noExitClass || !noExitExcludeClass)
                    {
                        if (evtReq instanceof MethodExitRequest) {
                            handler.methodExitRequest(evt);
                        }
                    }
                    if (evtReq instanceof BreakpointRequest) {
                        handler.breakpointRequest(evt);
                    }
                }
            } catch (InterruptedException e) {
                LOG.error("Query.localVarMethodTrace",e);
            }finally {
                if (evts!=null){
                    evts.resume();
                }
            }
        }
    }
}
