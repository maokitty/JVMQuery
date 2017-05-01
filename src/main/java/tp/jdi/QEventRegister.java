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

    /**
     * 对类做过滤
     * 监听了一个类，就不能用排除的
     * @param suspendPolicy
     * @param cd
     * @return
     */
    public QEventRegister methodEntryRequest(int suspendPolicy,Coordinate cd){
        MethodEntryRequest meReq=reqMgr.createMethodEntryRequest();
        meReq.setSuspendPolicy(suspendPolicy);
        if (cd.getEntryClass()!=null && !"".equals(cd.getEntryClass())){
            meReq.addClassFilter(cd.getEntryClass());
        }else if (cd.getEntryExcludeClass()!=null){
            for (String classPattern : cd.getEntryExcludeClass()){
                meReq.addClassExclusionFilter(classPattern);
            }
        }
        meReq.enable();
        return this;
    }

    /**
     * 对类做过滤
     * 监听了一个类，就不能用排除的
     * @param suspendPolicy
     * @param cd
     * @return
     */
    public QEventRegister methodExitRequest(int suspendPolicy,Coordinate cd){
        MethodExitRequest meReq=reqMgr.createMethodExitRequest();
        meReq.setSuspendPolicy(suspendPolicy);
        if (cd.getExitClassName()!=null && !"".equals(cd.getExitClassName())){
            meReq.addClassFilter(cd.getExitClassName());
        }else if (cd.getExitExcludeClass()!=null){
            for (String classPattern : cd.getExitExcludeClass()){
                meReq.addClassExclusionFilter(classPattern);
            }
        }
        meReq.enable();
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
     * 考虑一个文件
     * @param rt
     * @param cd
     * @param suspendPolicy 必须暂停线程，否则会抛出线程类型不兼容
     */
    public QEventRegister breakpointRequest(ReferenceType rt,Coordinate cd,int suspendPolicy){
        try {
            List<Location> locs=rt.locationsOfLine(STARTUM,cd.getSourceFileName(),cd.getLineNumber());
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
                LOG.info("QEventRegister.breakpointRequest locs:{} cd:{}",locs,cd);
            }
        } catch (AbsentInformationException e) {
            LOG.error("bpEvt cd:{}",cd,e);
        }
        return this;
    }
}
