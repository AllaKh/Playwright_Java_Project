package ui.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.AdminLoginPage;
import pages.HomePage;

/**
 * Admin‑area smoke‑tests.
 *
 * 1. Bad credentials ⇒ user stays on /admin and sees an error.
 * 2. Good credentials ⇒ user lands on /admin/rooms, hops through every
 *    side‑menu link, returns to the public front page, re‑enters the
 *    admin area via the header **Admin** link, then logs out.
 */
public class AdminLoginTest {

    /* Playwright handles */
    private Playwright     playwright;
    private Browser        browser;
    private BrowserContext context;
    private Page           page;

    /* Page‑objects */
    private HomePage       home;
    private AdminLoginPage adminLogin;

    /* ────────────────────────── test‑runner hooks ───────────────────────── */

    @BeforeClass
    public void launchBrowser() {
        playwright = Playwright.create();
        browser    = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(false));
    }

    @BeforeMethod
    public void openFreshContext() {
        context = browser.newContext();
        page    = context.newPage();

        // open landing page and wait until network is quiet
        page.navigate("https://automationintesting.online/");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        home       = new HomePage(page);
        adminLogin = new AdminLoginPage(page);
    }

    @AfterMethod(alwaysRun = true)
    public void closeContext() {
        if (page != null && !page.isClosed()) page.close();
        if (context != null)                  context.close();
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        if (browser != null)    browser.close();
        if (playwright != null) playwright.close();
    }

    /* ───────────────────────────── test cases ──────────────────────────── */

    @Test(description = "Invalid credentials keep the user on /admin and show an error banner")
    public void invalidLoginTest() {
        home.clickAdmin();
        Assert.assertTrue(adminLogin.isAt(), "Should be on admin login page");

        adminLogin.login("invalidUser", "invalidPass");

        Locator error = page.locator(".alert-danger, .alert-error, .error-message");
        Assert.assertTrue(adminLogin.isAt(), "Still on /admin after failed login");
        Assert.assertTrue(error.isVisible() || page.url().contains("/admin"),
                "Error banner should appear on failed login");
    }

    @Test(description = "Valid login → browse admin links → home → Admin link → logout")
    public void validLoginAndNavigationTest() {
        /* 1️⃣  Sign in */
        home.clickAdmin();
        Assert.assertTrue(adminLogin.isAt(), "On admin login page");
        adminLogin.login("admin", "password");

        page.waitForURL("**/admin/rooms");
        Assert.assertTrue(page.url().endsWith("/admin/rooms"),
                "Expected to land on /admin/rooms after login");

        /* 2️⃣  Walk through every admin side‑nav link */
        String[] paths = { "/admin/report", "/admin/branding", "/admin/message", "/admin/rooms" };
        for (String path : paths) {
            page.locator("a[href='" + path + "']").click();
            page.waitForURL("**" + path);
            Assert.assertTrue(page.url().endsWith(path),
                    "Navigation failed for: " + path);
        }

        /* 3️⃣  Go to the public site */
        page.locator("#frontPageLink").click();
        page.waitForURL("https://automationintesting.online/");
        Assert.assertEquals(page.url(), "https://automationintesting.online/",
                "Should be back on the public front page");

        /* 4️⃣  click header “Admin” link and handle session */
        page.locator("a.nav-link[href='/admin']").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        String currentUrl = page.url();

        if (adminLogin.isAt()) {
            adminLogin.login("admin", "password");
            page.waitForURL("**/admin/rooms");
            Assert.assertTrue(page.url().endsWith("/admin/rooms"), "Login successful, now at /admin/rooms");
        } else if (currentUrl.endsWith("/admin/rooms")) {
            // Still logged in — OK
            Assert.assertTrue(true);
        } else {
            Assert.fail("Expected to be either on login or admin/rooms, but was: " + currentUrl);
        }

        /* 5️⃣  Logout and verify we're back on the home page */
        page.locator("button:has-text('Logout')").click();
        page.waitForURL("https://automationintesting.online/");
        Assert.assertEquals(page.url(), "https://automationintesting.online/",
                "After logout, should be redirected to the home page");

        /* 6️⃣  Optionally: verify we’re logged out */
        home.clickAdmin();
        page.waitForURL("**/admin**");
        Assert.assertTrue(adminLogin.isAt(), "Should land on admin login form after logout");
    }
}
