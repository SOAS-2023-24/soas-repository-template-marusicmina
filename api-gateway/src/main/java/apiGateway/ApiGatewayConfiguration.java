package apiGateway;

import java.util.function.Function;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		
		return builder
				.routes()
				.route(p -> p.path("/currency-exchange/**").uri("lb://currency-exchange"))
				.route(p -> p.path("/currency-conversion-feign").uri("lb://currency-conversion"))
				.route(p -> p.path("/users-service/**").uri("lb://users-service"))
				.route(p -> p.path("/bank-account/**").uri("lb://bank-account"))
				.route(p -> p.path("/crypto-wallet/**").uri("lb://crypto-wallet"))
				.route(p -> p.path("/crypto-exchange/**").uri("lb://crypto-exchange"))
				.route(p -> p.path("/crypto-conversion-feign/**").uri("lb://crypto-conversion"))
				.route(p -> p.path("/trade-service-feign/**").uri("lb://trade-service"))
				.build();
	}

}
