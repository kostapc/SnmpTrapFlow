package org.c0f3.snmp;

import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;

/**
 * by kostapc
 * date: 15.01.14.
 */
public class SNMPFlowListener {
    private static final int DEFAULT_THREAD_POOL_SIZE = 10;
    public static final int DEFAULT_PORT = 162;
    public static final String PROTO_UDP = "udp";
    public static final String PROTO_TCP = "tcp";

    private Snmp snmp = null;
    private Address listenAddress;

    private static ThreadPool threadPool = null;
    private static MultiThreadedMessageDispatcher dispatcher;
    private static USM usm;

    private static void init() {
        threadPool = ThreadPool.create("Trap listener pool", DEFAULT_THREAD_POOL_SIZE);
        dispatcher = new MultiThreadedMessageDispatcher(
                threadPool,
                new MessageDispatcherImpl()
        );

        usm = new USM(SecurityProtocols.getInstance(),
                new OctetString(MPv3.createLocalEngineID())
                , 0
        );
        usm.setEngineDiscoveryEnabled(true);

        SecurityModels.getInstance().addSecurityModel(usm);

        dispatcher.addMessageProcessingModel(new MPv1());
        dispatcher.addMessageProcessingModel(new MPv2c());
        dispatcher.addMessageProcessingModel(new MPv3(usm));
    }

    private static SNMPFlowListener instance;

    public static synchronized SNMPFlowListener getSNMPFlowListener(String proto, String address, int port)
            throws SNMPFlowException {
        if(threadPool==null) {
            init();
        }
        if(instance==null) {
            instance = new SNMPFlowListener(generateAddressString(
                    proto, address, port
            ));
        }
        return instance;
    }

    private static String generateAddressString(String proto, String address, int port) {
        return proto+":"+address+"/"+port;
    }

    private SNMPFlowListener(String addressString) throws SNMPFlowException {
        listenAddress = GenericAddress.parse(addressString);
        TransportMapping<?> transport;
        try {
            if (listenAddress instanceof UdpAddress) {
                transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
            } else {
                transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
            }
        } catch (IOException e) {
            throw new SNMPFlowException(e);
        }
        USM usm = new USM(SecurityProtocols.getInstance(),
                new OctetString(MPv3.createLocalEngineID())
                , 0
        );
        usm.setEngineDiscoveryEnabled(true);

        snmp = new Snmp(dispatcher, transport);
    }

    public void start(FlowCommandResponder responser) throws SNMPFlowException {
        try {
            snmp.addCommandResponder(responser);
            snmp.listen();
        } catch (IOException e) {
            throw new SNMPFlowException(e);
        }
    }

    public static synchronized void addUser() {
        usm.addUser(new OctetString("MD5DES"),
                new UsmUser(new OctetString("MD5DES"), null, null, null, null));
    }

    public static synchronized void addUser(String username, String userpassword) {
        usm.addUser(
                new OctetString("MD5DES"),
                new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
                        new OctetString(username), PrivDES.ID,
                        new OctetString(userpassword))
        );
    }





}
