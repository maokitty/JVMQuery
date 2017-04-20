package tp.jdi;

import com.sun.jdi.*;
import com.sun.jdi.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by liwangchun on 17/4/20.
 */
public class QEventRegister {
    private static final Logger LOG = LoggerFactory.getLogger(QEventRegister.class);
    private static String STARTUM="Java";//用的是那种语言
    private EventRequestManager reqMgr;

    public QEventRegister(EventRequestManager reqMgr){
        this.reqMgr=reqMgr;
    }

    public QEventRegister classPrepareRequest(int suspendPolicy){
        ClassPrepareRequest cpReq=reqMgr.createClassPrepareRequest();
        cpReq.setSuspendPolicy(suspendPolicy);
        cpReq.addSourceNameFilter("App.java");
        cpReq.enable();
        return this;
    }

    /**
     * 对类做过滤
     * @param suspendPolicy
     * @param q
     * @return
     */
    public QEventRegister methodEntryRequest(int suspendPolicy,Query q){
        MethodEntryRequest meReq=reqMgr.createMethodEntryRequest();
        meReq.setSuspendPolicy(suspendPolicy);
        if (!"".equals(q.getEntryMethodClass())){
            meReq.addClassFilter(q.getEntryMethodClass());
        }else if (q.getEntryExcludeClass()!=null){
            for (String classPattern : q.getEntryExcludeClass()){
                meReq.addClassExclusionFilter(classPattern);
            }
        }
        meReq.enable();
        return this;
    }

    /**
     * 对类做过滤
     * @param suspendPolicy
     * @param q
     * @return
     */
    public QEventRegister methodExitRequest(int suspendPolicy,Query q){
        MethodExitRequest meReq=reqMgr.createMethodExitRequest();
        meReq.setSuspendPolicy(suspendPolicy);
        if (!"".equals(q.getExitMethodClass())){
            meReq.addClassFilter(q.getEntryMethodClass());
        }else if (q.getExitExcludeClass()!=null){
            for (String classPattern : q.getExitExcludeClass()){
                meReq.addClassExclusionFilter(classPattern);
            }
        }
        meReq.enable();
        return this;
    }

    public QEventRegister classUnloadRequest(int suspendPolicy){
        ClassUnloadRequest cuReq = reqMgr.createClassUnloadRequest();
        cuReq.setSuspendPolicy(suspendPolicy);
        cuReq.enable();
        return this;
    }


    public QEventRegister modificationWatchpointRequest(VirtualMachine vm,Field f,int suspendPolicy){
        if (vm.canWatchFieldModification()){
            ModificationWatchpointRequest mwpReq = reqMgr.createModificationWatchpointRequest(f);
            mwpReq.setSuspendPolicy(suspendPolicy);
            mwpReq.enable();
        }else{
            LOG.info("QEventRegister.modificationWatchpointRequest can not watch field modification f:{}",f);
        }
        return this;
    }

    public QEventRegister accessWatchpointRequest(VirtualMachine vm,Field f,int suspendPolicy){
        if (vm.canWatchFieldAccess()) {
            AccessWatchpointRequest awpReq = reqMgr.createAccessWatchpointRequest(f);
            awpReq.setSuspendPolicy(suspendPolicy);
            awpReq.enable();
        }else{
            LOG.info("QEventRegister.accessWatchpointRequest can not watch field access f:{}",f);
        }
        return this;
    }

    /**
     *
     * @param rt
     * @param query
     * @param suspendPolicy 必须暂停线程，否则会抛出线程类型不兼容
     */
    public QEventRegister breakpointRequest(ReferenceType rt,Query query,int suspendPolicy){
        try {
            List<Location> locs=null;
            if (!"".equals(query.getSourceFileName())){
                locs=rt.locationsOfLine(STARTUM,query.getSourceFileName(),query.getLineNumber());
            }else{
                locs=rt.locationsOfLine(query.getLineNumber());
            }
            if (locs.size()==1)
            {
                Location bpLocation = locs.get(0);
                BreakpointRequest req = reqMgr.createBreakpointRequest(bpLocation);
                req.setSuspendPolicy(suspendPolicy);
                req.enable();
            }else if (locs.size()>0){
                for (Location loc:locs){
                    LOG.info("QEventRegister.breakpointRequest  declaryType:{} sourceName:{} sourcePath:{} loc:{}"
                            , loc.declaringType(), loc.sourceName(), loc.sourcePath(),loc);
                    BreakpointRequest req = reqMgr.createBreakpointRequest(loc);
                    req.setSuspendPolicy(suspendPolicy);
                    req.enable();
                }
            }else{
                LOG.info("QEventRegister.breakpointRequest locs:{} query:{}",locs,query);
            }
        } catch (AbsentInformationException e) {
            LOG.error("bpEvt query:{}",query,e);
        }
        return this;
    }
}
