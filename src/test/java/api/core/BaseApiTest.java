// src/main/java/api/core/BaseApiTest.java
package api.core;

import com.microsoft.playwright.*;

public abstract class BaseApiTest {
    protected static final String BASE_URL = "https://restful-booker.herokuapp.com/apidoc/index.html";

    /** Shared for all tests: Playwright + shared HTTP context */
    protected static final Playwright PW = Playwright.create();
    protected static final APIRequestContext REQ =
            PW.request().newContext(new APIRequest.NewContextOptions().setBaseURL(BASE_URL));

    /** Proper resource cleanup is triggered by the TestRunner after all tests */
    public static void shutdown() {
        if (REQ != null) REQ.dispose();
        if (PW != null) PW.close();
    }
}