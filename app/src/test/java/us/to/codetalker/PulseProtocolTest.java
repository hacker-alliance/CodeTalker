package us.to.codetalker;

import org.junit.Test;
//import org.junit.Before;
import org.junit.Assert;

import java.util.ArrayList;

//Pulse Protocol Tests
public class PulseProtocolTest {
    //Test Conversions
    @Test
    public void testTextToPulses() {
        String teststring = "A";
        ArrayList<Boolean> testpulses = PulseProtocol.convertTextToPulses(teststring);
        //Check SYNCBYTES and TRANSMITEBYTES and message length byte was added
        Assert.assertEquals((teststring.length() + PulseProtocol.NUMSYNCBYTES +PulseProtocol.NUMTRANSMITBYTES + 1) * 8, testpulses.size());
        testpulses = PulseProtocol.discardTransmitStatus(testpulses);
        Assert.assertEquals((teststring.length() + 1) * 8, testpulses.size());


    }
    public void testPulsesToText() {
        String teststring = "HELLO WORLD";
        ArrayList<Boolean> testpulses = PulseProtocol.convertTextToPulses(teststring);
        for (boolean b: testpulses) {
            System.out.print(b);
        }
        Assert.assertEquals(teststring, PulseProtocol.convertPulsesToText(testpulses));
    }
    @Test
    public void testTextToBytes() {
        String teststring = "HELLO WORLD";
        ArrayList<Byte> testbytes = PulseProtocol.convertTextToBytes(teststring);

        //Check message length byte was added
        Assert.assertEquals(teststring.length() + PulseProtocol.NUMSYNCBYTES + PulseProtocol.NUMTRANSMITBYTES + 1, testbytes.size());
        byte testbyte = (byte) teststring.length();
        testbyte -= 128;
        //Check message length is correct
        //testBytes = PulseProtocol.discardTransmitStatus(testbytes);
        //Assert.assertEquals(testbyte, (byte) testbytes.get(0));
        //Check each byte matches each character
        for (int i = 1 + PulseProtocol.NUMSYNCBYTES + PulseProtocol.NUMTRANSMITBYTES; i < teststring.length() + 1; ++i) {
            Assert.assertEquals(teststring.charAt(i-1 - PulseProtocol.NUMSYNCBYTES - PulseProtocol.NUMTRANSMITBYTES), (char) (byte) testbytes.get(i));
        }
    }
    @Test
    public void testBytesToText() {
        String teststring = "HELLO WORLD";
        ArrayList<Byte> testbytes = PulseProtocol.convertTextToBytes(teststring);
        //Assert.assertEquals(teststring, PulseProtocol.convertBytesToText(testbytes));
    }
    @Test
    public void testConvertBytesToPulses() {
        ArrayList<Byte> bytearray = new ArrayList<>();
        bytearray.add((byte) -1);
        //-1 = signed 2's complement 11111111
        ArrayList<Boolean> pulsearray = PulseProtocol.convertBytesToPulses(bytearray);

        Assert.assertEquals(8, pulsearray.size());

        for (boolean b : pulsearray) {
            Assert.assertTrue(b);
        }

        bytearray = new ArrayList<>();
        bytearray.add((byte) 0);
        //-1 = signed 2's complement 00000000
        pulsearray = PulseProtocol.convertBytesToPulses(bytearray);

        Assert.assertEquals(8, pulsearray.size());

        for (boolean b : pulsearray) {
            Assert.assertFalse(b);
        }
    }
    @Test
    public void testConvertPulsesToBytes() {
        ArrayList<Boolean> testboolarray = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            testboolarray.add(true);
        }
        ArrayList<Byte> testbytes = PulseProtocol.convertPulsesToBytes2(testboolarray);
        Assert.assertEquals((byte) -1, (byte) testbytes.get(0));

        testboolarray = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            testboolarray.add(false);
        }
        testbytes = PulseProtocol.convertPulsesToBytes2(testboolarray);
        Assert.assertEquals((byte) 0, (byte) testbytes.get(0));

        testboolarray = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            testboolarray.add( i % 2 == 0);
        }
        testbytes = PulseProtocol.convertPulsesToBytes2(testboolarray);
        Assert.assertEquals((byte) -86, (byte) testbytes.get(0));
    }
}
