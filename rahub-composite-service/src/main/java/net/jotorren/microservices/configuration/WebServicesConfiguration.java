package net.jotorren.microservices.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.jotorren.microservices.composite.controller.CompositeController;
import net.jotorren.microservices.rs.ExceptionRestHandler;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.atomikos.icatch.tcc.rest.CoordinatorImp;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Configuration
public class WebServicesConfiguration {

	@Value("${cxf.swagger.resourcePackage}")
	private String resourcePackage;

	@Value("${cxf.swagger.contact}")
	private String contact;
	
	@Value("${cxf.swagger.description}")
	private String description;
	
	@Value("${cxf.swagger.title}")
	private String title;
	
	@Value("${cxf.swagger.version}")
	private String version;
	
	
    @Autowired
    private Bus bus;
    
    @Autowired
    private CoordinatorImp tccCoordinatorService;
    
    @Autowired
    private CompositeController compositeController;
    
    @Bean
    public Server rsServer() {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(bus);
        endpoint.setAddress("/");
        endpoint.setProviders(Arrays.asList(new JacksonJsonProvider(), new ExceptionRestHandler()));
        
        Map<Object, Object> mappings = new HashMap<Object, Object>();
        mappings.put("json", "application/json");
        endpoint.setExtensionMappings(mappings);
        
        Swagger2Feature swagger2Feature = new Swagger2Feature();
        swagger2Feature.setResourcePackage(resourcePackage);
        swagger2Feature.setContact(contact);
        swagger2Feature.setDescription(description);
        swagger2Feature.setTitle(title);
        swagger2Feature.setVersion(version);
        
        endpoint.setFeatures(Arrays.asList(new LoggingFeature(), swagger2Feature));
        endpoint.setServiceBeans(Arrays.asList(tccCoordinatorService, compositeController));
        
        return endpoint.create();
    }
    
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
