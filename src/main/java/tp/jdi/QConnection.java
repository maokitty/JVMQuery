package tp.jdi;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by liwangchun on 17/4/20.
 */
public class QConnection {
    private static final Logger LOG = LoggerFactory.getLogger(QConnection.class);
    private int port;
    private String host;
    //毫秒 底层是socket.setSoTimeout
    private int timeout;


    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public int getTimeout() {
        return timeout;
    }

    private QConnection(int port, String host, int timeout) {
        this.port = port;
        this.host = host;
        this.timeout = timeout;
    }

    public static class Param{
        private int port=9999;
        private String host="localhost";
        private int timeout=1000;
        public Param port(int port){
            this.port = port;
            return this;
        }
        public Param host(String host){
            this.host=host;
            return this;
        }
        public Param timeout(int timeout){
            this.timeout=timeout;
            return this;
        }
        public QConnection create(){
            return new QConnection(this.port,this.host,this.timeout);
        }
    }

    public VirtualMachine attachVMBySocket(){
        VirtualMachineManager vmMgr = Bootstrap.virtualMachineManager();
        AttachingConnector conn = null;
        List<AttachingConnector> attachingConnectors = vmMgr.attachingConnectors();
        for (AttachingConnector ac:attachingConnectors){
            if ("dt_socket".equals(ac.transport().name())){
                conn=ac;
                break;
            }
        }
        Map<String,Connector.Argument> paramsMap = conn.defaultArguments();
        Connector.IntegerArgument portArg = (Connector.IntegerArgument) paramsMap.get("port");
        portArg.setValue(this.getPort());
        Connector.StringArgument hostArg = (Connector.StringArgument) paramsMap.get("hostname");
        hostArg.setValue(this.getHost());
        Connector.IntegerArgument timeoutArg = (Connector.IntegerArgument) paramsMap.get("timeout");
        timeoutArg.setValue(this.getTimeout());
        try {
            VirtualMachine vm= conn.attach(paramsMap);
            LOG.info("QConnection.attachVMBySocket connect to vm name:{} version:{}", vm.name(), vm.version());
            return vm;
        } catch (IOException e) {
            LOG.error("QConnection.attachVMBySocket qConn:{}",this,e);
        } catch (IllegalConnectorArgumentsException e) {
            LOG.error("QConnection.attachVMBySocket qConn:{}", this, e);
        }
        return null;

    }

    @Override
    public String toString() {
        return "QConnection{" +
                "port=" + port +
                ", host='" + host + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
