package com.heershingenmosiken.assertions.tests;

import com.heershingenmosiken.assertions.Assertions;
import com.heershingenmosiken.assertions.Utils;

import org.junit.jupiter.api.Test;

public class UselessTestsForCoverage {

    @Test
    void testStaticClassesCreation() {
        new Assertions();
        new Utils();
    }
}
