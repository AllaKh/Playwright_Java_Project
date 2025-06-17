// src/main/java/api/tests/PingApiTest.java
package api.tests;

import api.core.BaseApiTest;
import api.objects.PingApi;

public class PingApiTest extends BaseApiTest {
    public void run() {
        System.out.println("Running PingApiTest …");
        new PingApi(REQ).ping();            // internal assert verifies status 201
    }
}