package com.webflux.study.exam02;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EmployeeMain.class)
class EmployeeTest {

    @Autowired
    private EmployeeConfig employeeConfig;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("Id로 Employee 조회")
    void getEmployeeById() {
        WebTestClient client = WebTestClient.bindToRouterFunction(employeeConfig.getEmployeeByIdRoute()).build();

        Employee employee = new Employee("1", "Employee 1");

        given(employeeRepository.findEmployeeById(employee.getId())).willReturn(Mono.just(employee));

        client.get()
                .uri("/employees/{id}", employee.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Employee.class)
                .isEqualTo(employee);
    }
}
