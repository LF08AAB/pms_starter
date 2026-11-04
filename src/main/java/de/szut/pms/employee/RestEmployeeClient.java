package de.szut.pms.employee;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class RestEmployeeClient implements EmployeeClient {

    private final RestClient restClient;

    public RestEmployeeClient(
            RestClient.Builder restClientBuilder,
            @Value("${employee-service.base-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public void verifyExists(Long employeeId) {
        try {
            restClient.get()
                    .uri("/employees/{id}", employeeId)
                    .headers(this::forwardToken)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new EmployeeNotFoundException(employeeId);
        } catch (HttpClientErrorException ex) {
            if (isUnauthorized(ex)) {
                throw new EmployeeServiceUnauthorizedException();
            }
            throw new EmployeeServiceUnavailableException(ex);
        } catch (RestClientException ex) {
            throw new EmployeeServiceUnavailableException(ex);
        }
    }

    @Override
    public Optional<EmployeeDetails> findById(Long employeeId) {
        try {
            EmployeeDetails employee = restClient.get()
                    .uri("/employees/{id}", employeeId)
                    .headers(this::forwardToken)
                    .retrieve()
                    .body(EmployeeDetails.class);
            if (employee == null) {
                throw new EmployeeServiceUnavailableException(
                        new IllegalStateException("Employee-Service returned an empty response."));
            }
            return Optional.of(employee);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (HttpClientErrorException ex) {
            if (isUnauthorized(ex)) {
                throw new EmployeeServiceUnauthorizedException();
            }
            throw new EmployeeServiceUnavailableException(ex);
        } catch (RestClientException ex) {
            throw new EmployeeServiceUnavailableException(ex);
        }
    }

    private boolean isUnauthorized(HttpClientErrorException ex) {
        return ex.getStatusCode() == HttpStatus.UNAUTHORIZED
                || ex.getStatusCode() == HttpStatus.FORBIDDEN;
    }

    private void forwardToken(HttpHeaders headers) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            headers.setBearerAuth(jwt.getTokenValue());
        }
    }
}
