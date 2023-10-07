package br.com.alurafood.pagamentos.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

@Component
public class BeansConfig {
    @Bean
    public ModelMapper createBeanModelMapper2() {
        return new ModelMapper();
    }
}
