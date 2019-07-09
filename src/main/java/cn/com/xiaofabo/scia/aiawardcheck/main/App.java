package cn.com.xiaofabo.scia.aiawardcheck.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"cn.com.xiaofabo.scia.aiawardcheck"})
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
