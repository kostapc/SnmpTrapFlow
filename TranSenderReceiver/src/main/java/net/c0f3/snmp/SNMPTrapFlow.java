package net.c0f3.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

/**
 * by kostapc
 * date: 15.01.14.
 */
public class SNMPTrapFlow implements Runnable {

    public static void main(String[] argc) {
        long pause = Long.parseLong(argc[0]);
        int  threads = Integer.parseInt(argc[1]);
        String oid = argc[2];
        if(argc.length>3) {
            ipAddress = argc[3];
        }
        while (threads-->0) {
            new SNMPTrapFlow(oid, pause).start();
        }
    }

    private static final String community = "public";
    private static final int port = 162;
    private static String ipAddress = "127.0.0.1";

    private long pause;
    private String oid;

    public SNMPTrapFlow(String inOid, long inPause) {
        pause = inPause;
        oid = inOid;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                TrapPacket packet = new TrapPacket(TrapPacket.SNMPv2, oid);
                sendSnmpTrap(packet);
                Thread.sleep(pause);
            } catch (SNMPFlowException e) {
                e.printStackTrace();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private static void sendSnmpTrap(TrapPacket packet) {
        TransportMapping transport = null;
        try {
            transport = new DefaultUdpTransportMapping();
            // Create Target
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(community));
            comtarget.setVersion(packet.getVersion());
            comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            comtarget.setRetries(1);
            comtarget.setTimeout(5000);

            Snmp snmp = new Snmp(transport);

            snmp.send(packet.getPDU(), comtarget);

            System.out.println("Send Trap to " + ipAddress + " on Port " + port);

            snmp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
