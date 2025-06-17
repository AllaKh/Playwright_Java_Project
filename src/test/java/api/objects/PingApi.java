package api.objects;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

/** Simple health‑check for /ping. */
public class PingApi {
    private final APIRequestContext req;

    public PingApi(APIRequestContext req) {
        this.req = req;
    }

    public void ping() {
        APIResponse r = req.get("/ping");
        assert r.status() == 201 : "Ping should return 201";
    }
}