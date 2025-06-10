package com.esPublico.kata.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ConfigLoaderTest {

    @Test
    void testGetDdbbUrl_returnsNonNull() {
        String url = ConfigLoader.getDdbbUrl();
        assertThat(url).isNotNull().isNotEmpty();
    }

    @Test
    void testGetDdbbMaximumPoolSize_returnsValidInt() {
        int size = ConfigLoader.getDdbbMaximumPoolSize();
        assertThat(size).isGreaterThan(0);
    }

    @Test
    void testRequireProperty_throwsExceptionForMissingKey() {
        assertThatThrownBy(() -> {
            java.lang.reflect.Method method = ConfigLoader.class.getDeclaredMethod("requireProperty", String.class);
            method.setAccessible(true);
            method.invoke(null, "clave.inexistente");
        }).hasCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage("Falta la propiedad requerida: clave.inexistente");
    }
}


