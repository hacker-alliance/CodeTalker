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
        //Check message length byte was added
        Assert.assertEquals((teststring.length() + 1 ) * 8, testpulses.size());

    }
    @Test
    public void testTextToBytes() {
        String teststring = "ABCDEFGH";
        ArrayList<Byte> testbytes = PulseProtocol.convertTextToBytes(teststring);
        //Check message length byte was added
        Assert.assertEquals(teststring.length() + 1, testbytes.size());
        byte testbyte = (byte) teststring.length();
        testbyte -= 128;
        //Check message length is correct
        Assert.assertEquals(testbyte, (byte) testbytes.get(0));
        //Check each byte matches each character
        for (int i = 1; i < teststring.length() + 1; ++i) {
            Assert.assertEquals(teststring.charAt(i-1), (char) (byte) testbytes.get(i));
        }
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
}
