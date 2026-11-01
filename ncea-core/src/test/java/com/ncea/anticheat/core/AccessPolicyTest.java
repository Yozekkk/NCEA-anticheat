package com.ncea.anticheat.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class AccessPolicyTest {
    @ParameterizedTest
    @CsvSource({"status,true,false,true", "status,false,true,false", "alerts,false,true,true", "alerts,false,false,false", "reset,false,true,false"})
    void permissions(String command, boolean admin, boolean alerts, boolean allowed) {
        assertEquals(allowed,AccessPolicy.canExecute(command,admin,alerts));
    }
}
