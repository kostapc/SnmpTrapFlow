package net.c0f3.snmp;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;

/**
 * by kostapc
 * date: 15.01.14.
 */
public abstract class FlowCommandResponder implements CommandResponder {

    @Override
    public void processPdu(CommandResponderEvent event) {
        processTrapPacket(new TrapPacket(event));
    }

    public abstract void processTrapPacket(TrapPacket packet);

}
