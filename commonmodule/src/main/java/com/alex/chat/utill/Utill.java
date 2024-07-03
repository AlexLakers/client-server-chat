package com.alex.chat.utill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * This class contains the utill functions.
 */
public final class Utill {

    private static  BufferedReader reader;

    static {
        reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }

    private Utill() {

    }

    /**
     * Reads some string from the console and prints a transmitted message to console.
     * By the way, if some IOException has been detected then it will handle in the try-catch block.
     * @param message for printing process.
     * @return teh reading string.
     */
    public static String readLineWithMessage(String message) {
        Utill.writeString(message);
        return readLine();

    }
    /**
     * Reads some string from the console.
     * If entered string is null or empty then we can see warning message.
     * @return teh reading string.
     */
    public static String readLine() {
        String inputData = "";
        try {
            while (true) {
                inputData = reader.readLine();
                if (inputData == null || inputData.isEmpty()) {
                    Utill.writeString("You entered an empty string");
                } else {
                    return inputData;
                }
            }
        } catch (IOException ioExc) {
            Utill.writeString(ioExc.getMessage());
        }
        return inputData;
    }
    /**
     * Prints a transmitted message to console.
     * @param message for printing process.
     */
    public static void writeString(String message) {
        System.out.println(message);
    }

    /**
     * Parse and returns the integer value of the property from the file properties by name.
     * If the name property is not found or null then IllegalArgumentException will handle in the try-catch block.
     * @param props property container.
     * @param pName name of property.
     * @see IllegalArgumentException IllegalArgumentException
     * @return int value of property.
     */
    public static int tryParseToInt(Properties props, String pName) {
        try {
            if (pName == null || pName.isEmpty() || props == null) {
                throw new IllegalArgumentException("The properties is null or property name is null(empty)");
            }
            return Integer.parseInt(props.getProperty(pName));
        }
        catch (NumberFormatException nfe){
            Utill.writeString(String.format("The property '%s' don't have a valid value",pName));
            throw nfe;
        }
        catch(IllegalArgumentException iae){
            Utill.writeString("The properties is null or property name is null(empty)");
            throw iae;
        }
    }


    /**
     * Reads some ip address from the console and returns it as a {@link InetAddress inetAddress}.
     * By the way, ip address can be the fallowing format:'#.#.#.#' else will be thrown IOException and handles in the
     * try-catch-block.
     * @see IOException IOException
     * @return ip address as a InetAddress.
     */
    public static InetAddress readIpAddress() {
        while (true) {
            try {
                return InetAddress.getByName(reader.readLine());
            } catch (IOException e) {
                Utill.writeString("You entered incorrect ip address,try again");

            }
        }
    }

    /**
     * Reads and parse some entered string line to integer value.
     * Buy the way if it isn't possible then we can see the warning message.
     * @return line as a integer value
     */
    public static int readInt(){
        while(true) {
            try {
                return Integer.parseInt(reader.readLine());
            }
            catch (IOException|NumberFormatException e){
                Utill.writeString("You entered incorrect value,try again");
            }
        }
    }

    /**
     * Closes {@link #reader reader} and init it again using transmitted input stream.
     * @param buff an input
     */
    public static void reInitBufferedReader(InputStream buff){
        try {
            reader.close();
        } catch (IOException e) {
            Utill.writeString(e.getMessage());
        } finally {
            reader=new BufferedReader(new InputStreamReader(buff,StandardCharsets.UTF_8));
        }
    }


}

