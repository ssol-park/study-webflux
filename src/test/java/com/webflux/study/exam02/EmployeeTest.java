package com.webflux.study.exam02;

import com.webflux.study.exam02.router.EmployeeConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeTest {

    private static final Logger log = LoggerFactory.getLogger(EmployeeTest.class);

    @Autowired
    private EmployeeConfig employeeConfig;

    @Autowired
    private EmployeeHandler employeeHandler;

    @Test
    @DisplayName("Id로 Employee 조회")
    void getEmployeeById() {
        WebTestClient client = WebTestClient.bindToRouterFunction(employeeConfig.getEmployeeByIdRoute(employeeHandler)).build();

        Employee employee = new Employee("1", "Employee 1");

        client.get()
                .uri("/employees/{id}", employee.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Employee.class)
                .isEqualTo(employee);
    }
}
