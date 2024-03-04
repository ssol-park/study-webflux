package com.webflux.study.exam02;

import com.webflux.study.exam02.controller.EmployeeController;
import com.webflux.study.exam02.router.EmployeeConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeTest {

    @Autowired
    private EmployeeConfig employeeConfig;

    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("Functional Endpoints 방식으로 Employee 조회")
    void getEmployeeByIdUsingFunctionalEndpoints() {
        WebTestClient client = WebTestClient.bindToRouterFunction(employeeConfig.getEmployeeByIdRoute()).build();

        Employee employee = new Employee("1", "Employee 1");

        given(employeeRepository.findEmployeeById(employee.getId())).willReturn(Mono.just(employee));

        client.get()
                .uri("/employees/{id}", employee.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Employee.class)
                .isEqualTo(employee)
                .consumeWith(System.out::println);
    }

    @Test
    @DisplayName("Annotated Controller 방식으로 Employee 조회")
    void getEmployeeByIdUsingAnnotatedController() {
        WebTestClient client = WebTestClient.bindToController(employeeController).build();

        Employee employee = new Employee("1", "Employee 1");

        given(employeeRepository.findEmployeeById(employee.getId())).willReturn(Mono.just(employee));

        client.get()
                .uri("/employees/{id}", employee.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Employee.class)
                .isEqualTo(employee)
                .consumeWith(System.out::println);
    }
}
