package net.c0f3.snmp.test;

import com.mahesh.SNMPTrapGeneratorClient;
import org.junit.Ignore;
import org.junit.Test;

/**
 * kostapc on 07.05.15.
 */

public class SNMPTrapGenerator {

    private static final String community = "public";
    private static final String  trapOid = ".1.3.6.1.2.1.1.6";
    private static final String ipAddress = "127.0.0.1";
    private static final int port = 162;

    @Test
    public void sendSnmpV1Trap() {
        SNMPTrapGeneratorClient.sendSnmpV1Trap(community, ipAddress, port, trapOid);
    }

    @Test
    @Ignore
    public void sendSnmpV2Trap() {
        SNMPTrapGeneratorClient.sendSnmpV2Trap(community, ipAddress, port, trapOid);
    }

    @Test
    @Ignore
    public void sendSnmpV3Trap() {
        SNMPTrapGeneratorClient.sendSnmpV3Trap(community, ipAddress, port, trapOid);
    }
}
