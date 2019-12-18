package com.sqd.util;

import sun.misc.Cleaner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class IOUtil {

    private IOUtil() {}

    public static void closeMappedByteBuffer( MappedByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }

        if (byteBuffer != null) {
            AccessController.doPrivileged(
                    (PrivilegedAction<Object>) () -> {
                        try {
                            Method cleanerMethod = byteBuffer.getClass().getMethod("cleaner", new Class[0]);
                            cleanerMethod.setAccessible(true);
                            Cleaner cleaner = (Cleaner) cleanerMethod.invoke(byteBuffer,
                                    new Object[0]);
                            cleaner.clean();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
            );
        }
    }
}
