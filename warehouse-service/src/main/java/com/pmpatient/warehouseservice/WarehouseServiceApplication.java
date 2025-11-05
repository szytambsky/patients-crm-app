package com.pmpatient.warehouseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// make it in patient-service product and inventory staff
// order and payments, shipping, notification and users are on the distinct services
@SpringBootApplication
public class WarehouseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseServiceApplication.class, args);
    }

}
