package de.szut.pms.employee;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestEmployeeClientTest {

    private HttpServer server;
    private RestEmployeeClient client;
    private final AtomicInteger responseStatus = new AtomicInteger(200);
    private final AtomicReference<String> requestPath = new AtomicReference<>();
    private final AtomicReference<String> authorization = new AtomicReference<>();

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/employees", exchange -> {
            requestPath.set(exchange.getRequestURI().getPath());
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            exchange.sendResponseHeaders(responseStatus.get(), -1);
            exchange.close();
        });
        server.start();
        client = new RestEmployeeClient("http://127.0.0.1:" + server.getAddress().getPort());
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
        SecurityContextHolder.clearContext();
    }

    @Test
    void verifiesRequestedEmployeeAndForwardsJwt() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .claim("sub", "test-user")
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));

        client.verifyExists(2L);

        assertEquals("/employees/2", requestPath.get());
        assertEquals("Bearer test-token", authorization.get());
    }

    @Test
    void mapsMissingEmployeeToNotFound() {
        responseStatus.set(404);

        EmployeeNotFoundException error =
                assertThrows(EmployeeNotFoundException.class, () -> client.verifyExists(2L));

        assertEquals("Es gibt keinen Mitarbeiter mit der Kennung 2.", error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 403})
    void mapsRejectedTokenToUnauthorized(int status) {
        responseStatus.set(status);

        assertThrows(EmployeeServiceUnauthorizedException.class, () -> client.verifyExists(2L));
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 500, 503})
    void mapsOtherServiceErrorsToUnavailable(int status) {
        responseStatus.set(status);

        assertThrows(EmployeeServiceUnavailableException.class, () -> client.verifyExists(2L));
    }
}
