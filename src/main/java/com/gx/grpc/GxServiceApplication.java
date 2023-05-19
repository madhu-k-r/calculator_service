package com.gx.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"tmt.pcrf.sp", "tmt.pcrf.sp.sp_cache", "tmt.pcrf.sp.sp_session", "com.gx.grpc"})
public class GxServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GxServiceApplication.class, args);
        System.out.println("welcome with Server");
    }

}
