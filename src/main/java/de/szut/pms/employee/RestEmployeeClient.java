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

@Component
public class RestEmployeeClient implements EmployeeClient {

    private final RestClient restClient;

    public RestEmployeeClient(@Value("${employee-service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public void verifyExists(Long employeeId) {
        try {
            restClient.get()
                    .uri("/employees/{id}", employeeId)
                    .headers(this::forwardToken)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EmployeeNotFoundException(employeeId);
            }
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new EmployeeServiceUnauthorizedException();
            }
            throw new EmployeeServiceUnavailableException(e);
        } catch (RestClientException e) {
            throw new EmployeeServiceUnavailableException(e);
        }
    }

    private void forwardToken(HttpHeaders headers) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            headers.setBearerAuth(jwt.getTokenValue());
        }
    }
}
