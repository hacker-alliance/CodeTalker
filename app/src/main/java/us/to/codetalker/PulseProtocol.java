package us.to.codetalker;

import java.util.ArrayList;

final class PulseProtocol {
    //Puslse width in millicseconds
    static final int PULSEWIDTH = 100;
    static final int NUMSYNCBYTES = 2;
    static final int NUMTRANSMITBYTES = 2;
    static final byte SYNCBYTE = -86;
    static final byte TRANSMITBYTE = -52;

    //Converts Text to Pulse Array
    static ArrayList<Boolean> convertTextToPulses(String inputtext) {
        ArrayList<Byte> outputbytes = convertTextToBytes(inputtext);
        return convertBytesToPulses(outputbytes);
    }

    //Converts Pulse Array to Text
    static String convertPulsesToText(ArrayList<Boolean> inputpulses) {
        ArrayList<Byte> inputbytes = convertPulsesToBytes(inputpulses);
        return convertBytesToText(inputbytes);
    }

    //Convert text to byte array
    static ArrayList<Byte> convertTextToBytes(String inputtext) {
        ArrayList<Byte> outputbytes = new ArrayList<>();
        //Add preamble bytes
        //Add sync signal
        //Add several signed 2's complement of 10101010
        for (int i = 0; i < NUMSYNCBYTES; ++i) {
            outputbytes.add(SYNCBYTE);
        }
        //Add 2 transmit signal bytes //signed 2's complement of 11001100
        for (int i = 0; i < NUMTRANSMITBYTES; ++i) {
            //outputbytes.add(TRANSMITBYTE);
        }

        //Add transmit signal
        //Add message length byte
        byte n = (byte) (inputtext.length() - 128); //Convert unsigned value to signed
        outputbytes.add(n);

        //Add remaining message bytes
        for (char c : inputtext.toCharArray()) {
            outputbytes.add((byte) c);
        }
        return outputbytes;
    }
    //Convert byte array to text
    static String convertBytesToText(ArrayList<Byte> inputbytes) {
        String outputstring = "";
        //Remove message length indicator
        inputbytes.remove(0);
        //Convert each byte to char and add to string
        for (byte b : inputbytes) {
            outputstring += (char) b;
        }
        return outputstring;
    }

    //Convert byte array to pulses
    static ArrayList<Boolean> convertBytesToPulses(ArrayList<Byte> inputbytes) {
        ArrayList<Boolean> outputpulses = new ArrayList<>();
        //For each byte
        for (byte b : inputbytes) {
            for (int i = 0; i <= 7; ++i) {
                //Convert each byte to pulse
                outputpulses.add(((b >> i) & 1) == 1);
            }
        }
        return outputpulses;
    }

    //Convert pulses to bytes
    static ArrayList<Byte> convertPulsesToBytes(ArrayList<Boolean> inputpulses) {
        ArrayList<Byte> inputbytes = new ArrayList<>();
        int i = 7;
        int bitset = 0;
        byte inputbyte = 0;
        for (boolean b : inputpulses) {

            if (b) {
                bitset = 1;
            }
            else {
                bitset = 0;
            }
            bitset = bitset << i;
            inputbyte |= bitset;

            if (i == 0) {
                inputbytes.add(inputbyte);
                i = 7;
                inputbyte = 0;
            }
        }
        return inputbytes;
    }

    //Convert pulses to bytes
    static ArrayList<Byte> convertPulsesToBytes2(ArrayList<Boolean> inputpulses) {
        ArrayList<Byte> inputbytes = new ArrayList<>();

        for (int j = 0; j < inputpulses.size() / 8; j++) {
            int bitset = 0;
            byte inputbyte = 0;
            for (int i = j * 8; i < j * 8 + 8; ++i) {
                boolean b = inputpulses.get(i);
                if (b) {
                    bitset = 1;
                    bitset = bitset << 7 - i;
                    inputbyte |= bitset;
                }
            }
            inputbytes.add(inputbyte);
        }

        return inputbytes;
    }

    //Check for n byte transmit sequence and discard everything before
    static ArrayList<Boolean> discardTransmitStatus(ArrayList<Boolean> inputpulses) {
        for (int i = 0; i < inputpulses.size() - 16; ++i) {
            //ArrayList<Boolean> firstbytebools = new ArrayList<> (inputpulses.subList(i, i+8-1));
            //ArrayList<Boolean> secondbytebools = new ArrayList<> (inputpulses.subList(i+9-1, i+16-1));
            ArrayList<Boolean> twobytebools = new ArrayList<> (inputpulses.subList(i, i+16));
            ArrayList<Byte> boolbytes = convertPulsesToBytes2(twobytebools);
            if (boolbytes.get(0) == TRANSMITBYTE
                    && boolbytes.get(1) == TRANSMITBYTE) {
                    return new ArrayList<Boolean> (inputpulses.subList(i, inputpulses.size()));
            }
        }
        return inputpulses;
    }
}
