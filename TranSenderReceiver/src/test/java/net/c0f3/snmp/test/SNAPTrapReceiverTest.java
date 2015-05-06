package net.c0f3.snmp.test;

import com.mahesh.SNMPTrapReceiver;
import org.junit.Test;

/**
 * kostapc on 07.05.15.
 */
public class SNAPTrapReceiverTest {

    @Test
    public void SNMPTrapReceiver() {
        new SNMPTrapReceiver().run();
        System.out.println("main is ended");
    }
}
