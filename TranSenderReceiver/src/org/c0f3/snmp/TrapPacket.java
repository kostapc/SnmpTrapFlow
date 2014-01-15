package org.c0f3.snmp;

import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;

import java.lang.management.ManagementFactory;
import java.text.ParseException;

/**
 * by kostapc
 * date: 15.01.14.
 */
public class TrapPacket {
    private static String defaultAdress = "127.0.0.1";

    public static final int SNMPv1 = SnmpConstants.version1;
    public static final int SNMPv2 = SnmpConstants.version2c;
    //public static final int SNMPv3 = SnmpConstants.version3;

    private int version;
    private String trapOid;
    private PDU pdu;
    private String address;


    public TrapPacket(CommandResponderEvent event) {
        address = event.getPeerAddress().toString();
        pdu = event.getPDU();
        trapOid = getOIDString(pdu);
        //version = event.getSecurityLevel(); // ???????? where is version?
    }

    /**
     * Constructor used for generating new packets
     * @param trapPacketVersion - version of used SNMP
     * @throws SNMPFlowException
     */
    public TrapPacket(int trapPacketVersion, String inTrapOid) throws SNMPFlowException {
        address = defaultAdress;
        trapOid = inTrapOid;
        version = trapPacketVersion;
        switch (trapPacketVersion) {
            case SNMPv1:
                PDUv1 pdu1 = new PDUv1();
                pdu1.setType(PDU.V1TRAP);
                pdu1.setEnterprise(new OID(trapOid));
                pdu1.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
                pdu1.setSpecificTrap(1);
                pdu1.setAgentAddress(new IpAddress(defaultAdress));
                pdu1.setTimestamp(getJVMUpTime());
                pdu = pdu1;
            break;
            case SNMPv2:
                pdu = new PDU();
                pdu.setType(PDU.NOTIFICATION);
                // need to specify the system up time
                pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(getJVMUpTime())));
                pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
                pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress,new IpAddress(defaultAdress)));

                // variable binding for Enterprise Specific objects
                pdu.add(new VariableBinding(new OID(trapOid), new OctetString("Major")));

            break;
            /* not finished, not checked for implementing RFC */
            /*case SNMPv3:
                ScopedPDU spdu = new ScopedPDU();
                spdu.setType(ScopedPDU.NOTIFICATION);

                spdu.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(getJVMUpTime())));
                spdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
                spdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress,new IpAddress(defaultAdress)));
                //spdu.add(new VariableBinding(new OID(trapOid), new Integer32(1)));

                pdu = spdu;
            break;*/
            default:
                throw new SNMPFlowException("invalid version of SNMP");
        }
    }



    public void addVariable(OID variableOID, String data) throws SNMPFlowException {
        try {
            pdu.add(new VariableBinding(variableOID, data));
        } catch (ParseException e) {
            throw new SNMPFlowException(e);
        }
    }

    public String getVariable(int pos) {
        return pdu.get(pos).getVariable().toString();
    }

    public String getVariable(String varOid) {
        OID oid = new OID(varOid);
        return pdu.getVariable(oid).toString();
    }

    public PDU getPDU() {
        return pdu;
    }

    public String getAddress() {
        return address;
    }

    public String getOID() {
        return trapOid;
    }

    private static long getJVMUpTime() {
        return ManagementFactory.getRuntimeMXBean().getUptime()/10;
    }

    public static String getOIDString(PDU pdu) {
        return pdu.getVariable(SnmpConstants.snmpTrapOID).toString();
    }

    public static void setSenderAddress(String inAdress) {
        defaultAdress = inAdress;
    }

    public int getVersion() {
        return version;
    }
}
