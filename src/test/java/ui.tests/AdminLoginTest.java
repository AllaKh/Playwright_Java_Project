package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;

import pages.HomePage;
import pages.AdminLoginPage;

/**
 * Navigates to the Admin login page, then returns to the
 * public home page via the “Restful Booker Platform Demo” link.
 */
public class AdminLoginTest {

    private Playwright playwright;
    private Browser    browser;
    private Page       page;

    private HomePage       home;
    private AdminLoginPage adminLogin;

    /* ── setup / teardown ────────────────────────────────────────── */

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser    = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
        page       = browser.newPage();

        home       = new HomePage(page);
        adminLogin = new AdminLoginPage(page);

        page.navigate("https://automationintesting.online/");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        browser.close();
        playwright.close();
    }

    /* ── test ────────────────────────────────────────────────────── */

    @Test(description = "Go to Admin login, then back to the public home page")
    public void shouldNavigateToAdminAndReturnHome() {

        /* 1) Open the Admin login page */
        home.clickAdmin();
        Assert.assertTrue(adminLogin.isAt(),
                "Expected to be on the Admin login page after clicking Admin link");

        /* 2) Click the header link “Restful Booker Platform Demo” */
        page.locator("a:has-text('Restful Booker Platform Demo')").first().click();

        /* 3) Wait for navigation back to the root URL */
        page.waitForURL("https://automationintesting.online/",
                new Page.WaitForURLOptions().setTimeout(5000));

        /* 4) Verify we are indeed on the public home page */
        Assert.assertEquals(page.url(), "https://automationintesting.online/",
                "Should be back on the home page after clicking the header link");
    }
}
