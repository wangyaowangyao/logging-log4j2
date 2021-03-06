package org.apache.logging.log4j.core.config.plugins.convert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

/**
 *
 */
public class Base64Converter {

    private static final Logger LOGGER = StatusLogger.getLogger();
    private static Method method = null;
    private static Object decoder = null;

    static {
        try {
            // Base64 is available in Java 8 and up.
            Class<?> clazz = LoaderUtil.loadClass("java.util.Base64");
            final Method getDecoder = clazz.getMethod("getDecoder", null);
            decoder = getDecoder.invoke(null, null);
            clazz = decoder.getClass();
            method = clazz.getMethod("decode", String.class);
        } catch (final ClassNotFoundException ex) {

        } catch (final NoSuchMethodException ex) {

        } catch (final IllegalAccessException ex) {

        } catch (final InvocationTargetException ex) {

        }
        if (method == null) {
            try {
                // DatatypeConverter is not in the default module in Java 9.
                final Class<?> clazz = LoaderUtil.loadClass("javax.xml.bind.DatatypeConverter");
                method = clazz.getMethod("parseBase64Binary", String.class);
            } catch (final ClassNotFoundException ex) {
                LOGGER.error("No Base64 Converter is available");
            } catch (final NoSuchMethodException ex) {

            }
        }
    }

    public static byte[] parseBase64Binary(final String encoded) {
        if (method == null) {
            LOGGER.error("No base64 converter");
        } else {
            try {
                return (byte[]) method.invoke(decoder, encoded);
            } catch (final IllegalAccessException ex) {
                LOGGER.error("Error decoding string - " + ex.getMessage());
            } catch (final InvocationTargetException ex) {
                LOGGER.error("Error decoding string - " + ex.getMessage());
            }
        }
        return new byte[0];
    }
}
