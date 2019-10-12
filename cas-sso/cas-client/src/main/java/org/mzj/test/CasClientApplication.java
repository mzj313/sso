package org.mzj.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.unicon.cas.client.configuration.EnableCasClient;

@SpringBootApplication
@EnableCasClient
public class CasClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(CasClientApplication.class, args);
	}
}
