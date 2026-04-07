package core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "controller", "service", "core", "xml", "iso8583" })
public class DigitalGatewayApplication
{
	public static void main(String[] args)
	{
		System.out.println("Iniciando Digital Gateway REST ISO-8583 por el Puerto 8080...");
		SpringApplication.run(DigitalGatewayApplication.class, args);
	}
}
