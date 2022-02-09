package com.challenge.pdftotext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PdfToTextApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfToTextApplication.class, args);
	}

}
