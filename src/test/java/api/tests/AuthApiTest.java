// src/main/java/api/tests/AuthApiTest.java
package api.tests;

import api.core.BaseApiTest;
import api.objects.AuthApi;

public class AuthApiTest extends BaseApiTest {
    public void run() {
        System.out.println("Running AuthApiTest …");
        String token = new AuthApi(REQ).createToken("admin", "password123");
        assert !token.isBlank() : "Token must not be blank";
    }
}