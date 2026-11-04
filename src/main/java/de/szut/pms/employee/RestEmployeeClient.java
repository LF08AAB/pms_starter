package de.szut.pms.employee;

import org.springframework.beans.factory.annotation.Value;
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
    public Optional<EmployeeDetails> findById(Long employeeId) {
        try {
            EmployeeDetails employee = restClient.get()
                    .uri("/employee/{id}", employeeId)
                    .retrieve()
                    .body(EmployeeDetails.class);
            if (employee == null) {
                throw new EmployeeServiceUnavailableException(
                        new IllegalStateException("Employee-Service returned an empty response."));
            }
            return Optional.of(employee);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new EmployeeServiceUnavailableException(ex);
        }
    }
}
