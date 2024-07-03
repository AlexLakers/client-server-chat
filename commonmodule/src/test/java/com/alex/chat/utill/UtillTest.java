package com.alex.chat.utill;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class UtillTest {
    private static final InputStream consoleInputStream = System.in;
    private static final PrintStream consoleOutputStream = System.out;
    private static final String MESSAGE = "\nThis is message\n";

    @AfterEach
    void setIOStreams() {
        System.setIn(consoleInputStream);
        System.setOut(consoleOutputStream);
    }

    @Test
    void readLine_shouldReturnString_whenSomeEnteredLineIsNotEmpty() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MESSAGE.replaceFirst("\\n", "").getBytes(StandardCharsets.UTF_8));
        Utill.reInitBufferedReader(byteArrayInputStream);
        String expected = MESSAGE.replaceAll("\\n", "");

        String actual = Utill.readLine();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void readLine_shouldWriteWarningMessage_whenSomeEnteredLineIsEmpty() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MESSAGE.getBytes(StandardCharsets.UTF_8));
        Utill.reInitBufferedReader(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        byte[] expected = "You entered an empty string\r\n".getBytes(StandardCharsets.UTF_8);

        Utill.readLine();

        byte[] actual = byteArrayOutputStream.toByteArray();
        Assertions.assertArrayEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("getValidArgsForParseToInt")
    void tryParseToInt_shouldReturnIntValueProp_whenArgsIsNotNull(Properties props, String name, int expected) {

        int actual = Utill.tryParseToInt(props, name);

        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> getValidArgsForParseToInt() {
        Properties props = new Properties();
        props.setProperty("intProp", "123");
        props.setProperty("intProp1", "-12314");
        props.setProperty("intProp2", "0");
        props.setProperty("intProp3", "-10");
        props.setProperty("intProp4", "5555555");
        return Stream.of(
                Arguments.of(props, "intProp", 123),
                Arguments.of(props, "intProp1", -12314),
                Arguments.of(props, "intProp2", 0),
                Arguments.of(props, "intProp3", -10),
                Arguments.of(props, "intProp4", 5555555)


        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void tryParseToInt_shouldThrowsIllegalArgumentException_whenArgsIsEmptyOrNull(String name) {
        String expected = "The properties is null or property name is null(empty)";

        Throwable thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> Utill.tryParseToInt(null, name));
        String actual = thrown.getMessage();

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getInvalidArgsForParseToInt")
    void tryParseToInt_shouldThrownNumberFormatException_whenPropValueIsNotNumber(Properties props, String propName, String propVal) {
        Throwable thrown = Assertions.assertThrows(NumberFormatException.class, () -> Utill.tryParseToInt(props, propName));
        String expected = String.format("For input string: \"%s\"", propVal);

        String actual = thrown.getMessage();

        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> getInvalidArgsForParseToInt() {
        Properties props = new Properties();
        props.setProperty("NotIntProp", "Hello string");
        props.setProperty("NotIntProp1", " ");
        props.setProperty("NotIntProp2", "-");
        props.setProperty("NotIntProp3", "");
        props.setProperty("NotIntProp4", "S d )");
        props.setProperty("NotIntProp5", "3.31231");
        return Stream.of(
                Arguments.of(props, "NotIntProp", "Hello string"),
                Arguments.of(props, "NotIntProp1", " "),
                Arguments.of(props, "NotIntProp2", "-"),
                Arguments.of(props, "NotIntProp3", ""),
                Arguments.of(props, "NotIntProp4", "S d )"),
                Arguments.of(props, "NotIntProp5", "3.31231")


        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"192.168.0.1", "127.0.0.1", "0.0.0.0"})
    void readIpAddress_shouldReturnsInetAddress_whenEnteredLineIsIpAddress(String validIp) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(validIp.getBytes(StandardCharsets.UTF_8));
        Utill.reInitBufferedReader(byteArrayInputStream);
        String expected = validIp;

        InetAddress inetAddress = Utill.readIpAddress();
        String actual = inetAddress.toString().replaceFirst("\\W", "");

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {" This is not ip", "-.-.-.-", " -100.100.0.0", "0.A.B.-", "300.255.400.800"})
    void readIpAddress_shouldWriteWarningToConsole_whenEnteredLineIsNotIpAddress(String invalidIp) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(invalidIp.getBytes(StandardCharsets.UTF_8));
        Utill.reInitBufferedReader(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        byte[] expected = "You entered incorrect ip address,try again\r\n".getBytes(StandardCharsets.UTF_8);

        Utill.readIpAddress();
        byte[] actual = byteArrayOutputStream.toByteArray();

        Assertions.assertArrayEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("getValidArgsForReadInt")
    void readInt_shouldReturnsIntValue_whenEnteredLineIsInteger(String line, int expected) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8));
        Utill.reInitBufferedReader(byteArrayInputStream);

        int actual = Utill.readInt();

        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> getValidArgsForReadInt() {
        return Stream.of(
                Arguments.of("10", 10),
                Arguments.of("0", 0),
                Arguments.of("-100", -100),
                Arguments.of("512354", 512354)
        );
    }
}