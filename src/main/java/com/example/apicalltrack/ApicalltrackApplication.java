package com.example.apicalltrack;
import com.example.apicalltrack.ProxyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApicalltrackApplication {
	@Autowired
	private ProxyConfig proxyConfig;

	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.debug","ssl");
		//load keystore
		System.setProperty("javax.net.ssl.trustStore","/usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts");
		String truststore = System.getProperty("javax.net.ssl.trustStore");
		System.out.println("truststore"+truststore);
		SpringApplication.run(ApicalltrackApplication.class, args);
		System.out.println("programme started");
	}
	@Bean
	public CommandLineRunner commandLineRunner(){
		return args->{
			proxyConfig.start(4454);
		};
	}

}
