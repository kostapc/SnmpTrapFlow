package net.c0f3.snmp;


import java.util.logging.Logger;

/**
 * by kostapc
 * date: 16.01.14.
 */
public class SNMPTrapServer extends FlowCommandResponder {

    public static void main(String[] argc) {
        SNMPFlowListener listener;
        SNMPTrapServer processor = new SNMPTrapServer();
        try {
            listener = SNMPFlowListener.getSNMPFlowListener();
            listener.start(processor);
            logger.info("server started for address \""+listener.getAddress()+"\"");
        } catch (SNMPFlowException e) {
            e.printStackTrace();
        }
    }

    private static Logger logger = Logger.getLogger("org.c0f3.snmp");

    @Override
    public void processTrapPacket(TrapPacket packet) {
        StringBuilder msg = new StringBuilder();
        msg
            .append("\n ------- OID: ")
            .append(packet.getOID())
            .append(" - [")
            .append(packet.getAddress())
            .append("] ----------------- ");
        logger.info(msg.toString());
    }
}
