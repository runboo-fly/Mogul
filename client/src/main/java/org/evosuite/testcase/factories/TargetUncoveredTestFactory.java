package org.evosuite.testcase.factories;

import org.evosuite.Properties;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetUncoveredTestFactory implements ChromosomeFactory<TestChromosome> {
    protected static final Logger logger = LoggerFactory.getLogger(FixedLengthTestChromosomeFactory.class);

    protected TestCase getNewTestCase() {
        return new DefaultTestCase(); // empty test case
    }
    @Override
    public TestChromosome getChromosome() {
        TestChromosome c = new TestChromosome();
        c.setTestCase(getUncoveredTestCase());
        return c;
    }

    private TestCase getUncoveredTestCase() {
        boolean tracerEnabled = ExecutionTracer.isEnabled();
        if (tracerEnabled)
            ExecutionTracer.disable();

        final TestCase test = getNewTestCase();
        final TestFactory testFactory = TestFactory.getInstance();
        testFactory.modelStatement(test);

        if (logger.isDebugEnabled())
            logger.debug("Modeled test case:" + test.toCode());

        if (tracerEnabled)
            ExecutionTracer.enable();

        return test;

    }
}
