package br.com.contadin.infrastructure.dashboard;

import br.com.contadin.application.port.in.dashboard.DashQueryInputPort;
import br.com.contadin.application.port.out.dashboard.DashMetricsOutputPort;
import br.com.contadin.application.usecase.dashboard.DashQueryUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashConfig {

    @Bean
    public DashQueryInputPort dashQueryInputPort(
            DashMetricsOutputPort dashMetricsOutputPort
    ) {
        return new DashQueryUseCase(
                dashMetricsOutputPort
        );
    }
}
