package ui.core;

import com.microsoft.playwright.*;
import org.testng.annotations.*;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Base class for all Playwright-based UI tests.
 *
 * - A single headed Chromium browser is launched once per test class.
 * - Each test method runs in an isolated context with its own page.
 * - Includes utility methods for date generation and shared Random instance.
 */
public abstract class BasePlaywrightTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    // Shared random instance for all test classes
    protected Random random = new Random();

    @BeforeClass
    public void launchBrowserOnce() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeMethod
    public void createNewContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterMethod(alwaysRun = true)
    public void closeContextAfterEachTest() {
        if (page != null && !page.isClosed()) page.close();
        if (context != null) context.close();
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowserAfterAllTests() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    /**
     * Generates a random future date between today and today + daysAheadInclusive.
     *
     * @param daysAheadInclusive upper bound for future date
     * @return randomly generated LocalDate within the range
     */
    protected LocalDate randomFutureDate(int daysAheadInclusive) {
        long start = LocalDate.now().toEpochDay();
        long end = LocalDate.now().plusDays(daysAheadInclusive).toEpochDay();
        return LocalDate.ofEpochDay(
                ThreadLocalRandom.current().nextLong(start, end + 1)
        );
    }
}
