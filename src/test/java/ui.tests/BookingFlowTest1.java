package ui.tests;

import com.microsoft.playwright.*;

import org.testng.annotations.*;

public class BookingFlowTest1 {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeClass
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
    }

    @Test
    public void testOpenHomePage() {
        page.navigate("https://automationintesting.online/");
        String title = page.title();
        System.out.println("Page title is: " + title);
        assert title.contains("Restful-booker-platform demo") : "Title did not contain expected text";
    }

    @AfterClass
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
