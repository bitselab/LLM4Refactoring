package org.apache.flink.table.planner.runtime.stream.jsonplan;

import org.apache.flink.table.planner.runtime.stream.jsonplan.ConfigureOperatorLevelStateTtlJsonITCase;
import org.junit.Test;

public class ConfigureOperatorLevelStateTtlJsonITCaseTest {
    
    ConfigureOperatorLevelStateTtlJsonITCase configureOperatorLevelStateTtlJsonITCase = new ConfigureOperatorLevelStateTtlJsonITCase();

    @Test
    public void testTestDifferentStateTtlForDifferentOneInputOperator() throws Exception {
        configureOperatorLevelStateTtlJsonITCase.testDifferentStateTtlForDifferentOneInputOperator();
    }

    @Test
    public void testTestDifferentStateTtlForSameTwoInputStreamOperator() throws Exception {
        configureOperatorLevelStateTtlJsonITCase.testDifferentStateTtlForSameTwoInputStreamOperator();
    }

    @Test
    public void testTestDifferentStateTtlThroughSqlHintForSameTwoInputStreamOperator() throws Exception {
        configureOperatorLevelStateTtlJsonITCase.testDifferentStateTtlThroughSqlHintForSameTwoInputStreamOperator();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme