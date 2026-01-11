package com.manikanda.seat_inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableMongoAuditing
@EnableTransactionManagement
@EnableScheduling
public class SeatInventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatInventoryApplication.class, args);
	}

}
