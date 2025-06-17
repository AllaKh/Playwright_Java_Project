package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page object model for the Admin Login page at /admin.
 */
public class AdminLoginPage extends BasePage {

    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;

    public AdminLoginPage(Page page) {
        super(page);
        usernameInput = page.locator("#username");
        passwordInput = page.locator("#password");
        loginButton   = page.locator("button[type='submit']");
    }

    public boolean isAt() {
        // Verify we're on the /admin page and the login form is visible
        return page.url().contains("/admin") && usernameInput.isVisible();
    }

    public void login(String username, String password) {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
    }
}
