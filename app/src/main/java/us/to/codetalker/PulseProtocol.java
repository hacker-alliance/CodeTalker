package us.to.codetalker;

import java.util.ArrayList;

final class PulseProtocol {
    //Puslse width in millicseconds
    static final int PULSEWIDTH = 100;
    //Converts Text to Pulse Array
    static ArrayList<Boolean> convertTextToPulses(String inputtext) {
        ArrayList<Byte> outputbytes = convertTextToBytes(inputtext);
        return convertBytesToPulses(outputbytes);
    }
    //Convert text to byte array
    static ArrayList<Byte> convertTextToBytes(String inputtext) {
        ArrayList<Byte> outputbytes = new ArrayList<>();
        //Add first byte determining length
        byte n = (byte) (inputtext.length() - 128); //Convert unsigned value to signed
        outputbytes.add(n);

        //Add remaining message bytes
        for (char c : inputtext.toCharArray()) {
            outputbytes.add((byte) c);
        }
        return outputbytes;
    }
    //Convert bytes to pulses
    static ArrayList<Boolean> convertBytesToPulses(ArrayList<Byte> inputbytes) {
        ArrayList<Boolean> outputpulses = new ArrayList<>();
        for (byte b : inputbytes) {
            for (int i = 0; i <= 7; ++i) {
                outputpulses.add(((b >> i) & 1) == 1);
            }
        }
        return outputpulses;
    }
}
