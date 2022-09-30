package com.edu.ulab.app.util;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SqlConverter {
    public static String loadResourceToString(final String path) {
        final InputStream stream = Thread
                        .currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(path);
        try {
            return IOUtils.toString(Objects.requireNonNull(stream), StandardCharsets.UTF_8);
        } catch(final IOException e){
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}
