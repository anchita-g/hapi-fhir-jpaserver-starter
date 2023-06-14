package ca.uhn.fhir.jpa.starter.ips;

import org.springframework.context.annotation.Bean;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.ips.api.IIpsGenerationStrategy;
import ca.uhn.fhir.jpa.ips.strategy.DefaultIpsGenerationStrategy;
import ca.uhn.fhir.jpa.ips.generator.IIpsGeneratorSvc;
import org.springframework.context.annotation.Conditional;
import ca.uhn.fhir.jpa.ips.provider.IpsOperationProvider;
import ca.uhn.fhir.jpa.ips.generator.IpsGeneratorSvcImpl;


@Conditional(IpsConfigCondition.class)
public class StarterIpsConfig {
	@Bean 
	IIpsGenerationStrategy IpsGenerationStrategy()
	{
		return new DefaultIpsGenerationStrategy();
	}

	@Bean
	public IpsOperationProvider OhsIpsOperationProvider(IIpsGeneratorSvc theIpsGeneratorSvc, DaoRegistry theDaoRegistry, FhirContext fhirContext){
		return new OhsIpsOperationProvider(theIpsGeneratorSvc, theDaoRegistry, fhirContext.newJsonParser());
	}

	@Bean
	public IIpsGeneratorSvc IpsGeneratorSvcImpl(FhirContext theFhirContext, IIpsGenerationStrategy theGenerationStrategy, DaoRegistry theDaoRegistry)
	{
		return new IpsGeneratorSvcImpl(theFhirContext, theGenerationStrategy, theDaoRegistry);
	}
	
}
