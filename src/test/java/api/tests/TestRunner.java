// src/main/java/api/tests/TestRunner.java
package api.tests;

import api.core.BaseApiTest;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import ui.tests.BookingFlowTest;
import ui.tests.DeleteBookingTest;
import ui.tests.UpdateBookingTest;

import java.util.ArrayList;
import java.util.List;

/**
 * TestRunner runs API tests by invoking their run() method,
 * and runs UI tests via TestNG programmatic API.
 *
 * Usage:
 *  - no args: run all (API + UI)
 *  - "api": run only API tests
 *  - "ui": run only UI tests
 */
public class TestRunner {

    private static final Class<?>[] API_TESTS = {
            PingApiTest.class,
            AuthApiTest.class,
            BookingApiTest.class
    };

    private static final Class<?>[] UI_TESTS = {
            BookingFlowTest.class,
            DeleteBookingTest.class,
            UpdateBookingTest.class
    };

    public static void main(String[] args) {
        boolean apiOnly = args.length > 0 && "api".equalsIgnoreCase(args[0]);
        boolean uiOnly = args.length > 0 && "ui".equalsIgnoreCase(args[0]);

        int passedApiTests = 0;
        int totalApiTests = 0;

        // Run API tests by calling run() method on each test class instance
        if (!uiOnly) {
            totalApiTests = API_TESTS.length;
            for (Class<?> apiTestClass : API_TESTS) {
                try {
                    Object testInstance = apiTestClass.getDeclaredConstructor().newInstance();
                    apiTestClass.getMethod("run").invoke(testInstance);
                    System.out.printf("[PASS] %s%n", apiTestClass.getSimpleName());
                    passedApiTests++;
                } catch (Throwable t) {
                    Throwable cause = (t.getCause() == null) ? t : t.getCause();
                    System.err.printf("[FAIL] %s – %s%n", apiTestClass.getSimpleName(), cause);
                }
            }
        }

        int passedUiTests = 0;
        int totalUiTests = UI_TESTS.length;

        // Run UI tests with TestNG if needed
        if (!apiOnly) {
            TestNG testng = new TestNG();
            testng.setTestClasses(UI_TESTS);
            TestListenerAdapter tla = new TestListenerAdapter();
            testng.addListener(tla);
            testng.run();

            passedUiTests = tla.getPassedTests().size();
            int failedUiTests = tla.getFailedTests().size();
            int skippedUiTests = tla.getSkippedTests().size();

            System.out.printf("[UI TESTS] Passed: %d, Failed: %d, Skipped: %d%n", passedUiTests, failedUiTests, skippedUiTests);

            if (failedUiTests > 0 || skippedUiTests > 0) {
                System.err.println("[UI TESTS] Some UI tests failed or were skipped.");
            }
        }

        // Cleanup API test resources
        BaseApiTest.shutdown();

        // Summary
        int totalPassed = passedApiTests + passedUiTests;
        int totalTests = (uiOnly ? totalUiTests : 0) + (apiOnly ? totalApiTests : 0);
        if (!uiOnly && !apiOnly) {
            totalTests = totalApiTests + totalUiTests;
        }

        System.out.printf("%nSUMMARY: %d/%d tests passed%n", totalPassed, totalTests);

        if (totalPassed != totalTests) {
            System.exit(1);
        }
    }
}
