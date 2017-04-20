package tp.jdi;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.*;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by liwangchun on 17/4/20.
 */
public class AppQuery {
    private static Logger LOG = LoggerFactory.getLogger(AppQuery.class);
    static {
        PropertyConfigurator.configure("./src/test/java/resources/log4j.properties");
    }

    public static void main(String[] args) {
//        breakPoint();
//        method();
//        breakpointMethod();
    }



    public static void breakPoint(){
        Set<String> localVar = new HashSet<String>();
        localVar.add("version");
        QConnection qConn = new QConnection.Param().host("localhost").port(9999).timeout(1000).create();
        VirtualMachine vm=qConn.attachVMBySocket();
        Query q = new Query.Param().sourceFileName("App.java")
                .className("tp.jdi.domain.App")
                .lineNumber(15).varName(localVar).build();
        QEventRegister register = new QEventRegister(vm.eventRequestManager());
        List<ReferenceType> rts=vm.classesByName(q.getClassName());
        ReferenceType rt = rts.get(0);
        register.breakpointRequest(rt,q,EventRequest.SUSPEND_EVENT_THREAD);
        EventQueue queue = vm.eventQueue();
        QEventHandler handler = new QEventHandler();
        EventSet evts=null;
        while (true) {
            try {
                 evts = queue.remove();
                EventIterator iterator = evts.eventIterator();
                while (iterator.hasNext()) {
                    Event evt = iterator.next();
                    EventRequest evtReq = evt.request();
                    if (evtReq instanceof BreakpointRequest) {
                        handler.breakpointRequest(evt, q);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if (evts!=null){
                    evts.resume();
                }
            }
        }
    }

    public static void breakpointMethod(){
        QConnection qConn = new QConnection.Param().host("localhost").port(9999).timeout(1000).create();
        VirtualMachine vm=qConn.attachVMBySocket();
        Set<String> localVar = new HashSet<String>();
        localVar.add("version");
        Query q = new Query.Param().entryMethodClass("tp.jdi.domain.App").exitMethodClass("tp.jdi.App")
                .sourceFileName("App.java").className("tp.jdi.domain.App")
                .lineNumber(15).varName(localVar)
                .build();
        QEventRegister register = new QEventRegister(vm.eventRequestManager());
        List<ReferenceType> rts=vm.classesByName(q.getClassName());
        ReferenceType rt = rts.get(0);
        register.breakpointRequest(rt,q,EventRequest.SUSPEND_EVENT_THREAD)
                .methodEntryRequest(EventRequest.SUSPEND_NONE, q)
                .methodExitRequest(EventRequest.SUSPEND_NONE,q);
        EventQueue queue = vm.eventQueue();
        QEventHandler handler = new QEventHandler();
        EventSet evts = null;
        while (true) {
            try {
                evts = queue.remove();
                EventIterator iterator = evts.eventIterator();
                while (iterator.hasNext()) {
                    Event evt = iterator.next();
                    EventRequest evtReq = evt.request();
                    if (evtReq instanceof MethodEntryRequest) {
                        handler.methodEntryRequest(evt);
                    } else if (evtReq instanceof MethodExitRequest) {
                        handler.methodExitRequest(evt);
                    }else if (evtReq instanceof BreakpointRequest) {
                        handler.breakpointRequest(evt, q);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if (evts!=null){
                    evts.resume();
                }
            }
        }
    }

    public static void method(){
        QConnection qConn = new QConnection.Param().host("localhost").port(9999).timeout(1000).create();
        VirtualMachine vm=qConn.attachVMBySocket();
        Query q = new Query.Param().entryMethodClass("tp.jdi.domain.App").exitMethodClass("tp.jdi.App").build();
        QEventRegister register = new QEventRegister(vm.eventRequestManager());
        register.methodEntryRequest(EventRequest.SUSPEND_NONE, q)
                .methodExitRequest(EventRequest.SUSPEND_NONE,q);
        EventQueue queue = vm.eventQueue();
        QEventHandler handler = new QEventHandler();
        while (true) {
            try {
                EventSet evts = queue.remove();
                EventIterator iterator = evts.eventIterator();
                while (iterator.hasNext()) {
                    Event evt = iterator.next();
                    EventRequest evtReq = evt.request();
                    if (evtReq instanceof MethodEntryRequest) {
                        handler.methodEntryRequest(evt);
                    } else if (evtReq instanceof MethodExitRequest) {
                        handler.methodExitRequest(evt);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
