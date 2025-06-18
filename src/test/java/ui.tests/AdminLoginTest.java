package ui.tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.testng.Assert;
import org.testng.annotations.*;

import pages.AdminLoginPage;
import pages.HomePage;
import ui.core.BasePlaywrightTest;

/**
 * AdminBookingFlowTest – admin‑area smoke flow<>
 */
public class AdminLoginTest extends BasePlaywrightTest {

    /* page‑object instances (fresh for every @Test) */
    private HomePage       home;
    private AdminLoginPage adminLogin;

    /* initialise page objects after BasePlaywrightTest has created the page */
    @BeforeMethod(alwaysRun = true)
    public void initPageObjects() {
        // BasePlaywrightTest already navigates nowhere – we do it here
        page.navigate("https://automationintesting.online/");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        home       = new HomePage(page);
        adminLogin = new AdminLoginPage(page);
    }

    /* test */

    @Test(description =
            "Valid login → browse admin links → front page → Admin link → logout")
    public void validLoginAndNavigationTest() {
        /* 1. Login */
        home.clickAdmin();
        Assert.assertTrue(adminLogin.isAt(), "Should be on the admin login page");
        adminLogin.login("admin", "password");

        page.waitForURL("**/admin/rooms");
        Assert.assertTrue(page.url().endsWith("/admin/rooms"),
                "Landing page after login must be /admin/rooms");

        /* 2.Visit each side‑menu link */
        String[] links = { "/admin/report", "/admin/branding",
                "/admin/message", "/admin/rooms" };
        for (String path : links) {
            page.locator("a[href='" + path + "']").click();
            page.waitForURL("**" + path);
            Assert.assertTrue(page.url().endsWith(path),
                    "Navigation failed for: " + path);
        }

        /* 3. Switch to the public site */
        page.locator("#frontPageLink").click();
        page.waitForURL("https://automationintesting.online/");
        Assert.assertEquals(page.url(),
                "https://automationintesting.online/",
                "Should be on the public front page");

        /* 4. Re‑enter admin via header link */
        page.locator("a.nav-link[href='/admin']").first().click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        if (adminLogin.isAt()) {                 // session expired – log in again
            adminLogin.login("admin", "password");
            page.waitForURL("**/admin/rooms");
        } else {
            Assert.assertTrue(page.url().endsWith("/admin/rooms"),
                    "Expected to be on /admin/rooms after clicking Admin link");
        }

        /* 5. Logout – verify redirect to home */
        page.locator("button:has-text('Logout')").click();
        page.waitForURL("https://automationintesting.online/");
        Assert.assertEquals(page.url(),
                "https://automationintesting.online/",
                "After logout the user should be back on the home page");

        /* 6. Extra check – /admin should now show the login form */
        home.clickAdmin();
        page.waitForURL("**/admin**");
        Assert.assertTrue(adminLogin.isAt(),
                "After logout, /admin must display the login form");
    }
}
