package net.c0f3.snmp;

import java.io.IOException;

/**
 * by kostapc
 * date: 15.01.14.
 */
public class SNMPFlowException extends Exception {

    public SNMPFlowException(String inMessage) {
        super(inMessage);
    }

    public SNMPFlowException(Exception e) {
        super(e.getMessage());
        super.setStackTrace(e.getStackTrace());
    }
}
