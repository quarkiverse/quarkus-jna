package io.quarkiverse.jna.test;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
public class JnaTestCase {

    @Test
    @TestHTTPEndpoint(JnaTestEndpoint.class)
    public void testJna() {
        RestAssured.get("5")
                .then()
                .body(Matchers.equalTo("6"));
    }
}
