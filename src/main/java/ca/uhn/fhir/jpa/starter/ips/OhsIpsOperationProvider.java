package ca.uhn.fhir.jpa.starter.ips;

import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.ips.generator.IIpsGeneratorSvc;
import ca.uhn.fhir.jpa.ips.provider.IpsOperationProvider;
import ca.uhn.fhir.jpa.model.util.JpaConstants;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.model.valueset.BundleTypeEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.ResourceType;

import java.util.Base64;

public class OhsIpsOperationProvider extends IpsOperationProvider {

	private final DaoRegistry myDaoRegistry;

	private final IParser jsonParser;

	public OhsIpsOperationProvider(IIpsGeneratorSvc theIpsGeneratorSvc, DaoRegistry theDaoRegistry, IParser jsonParser) {
		super(theIpsGeneratorSvc);
		this.myDaoRegistry = theDaoRegistry;
		this.jsonParser = jsonParser;
	}

	/**
	 * Patient/123/$summary
	 */
	@Operation(name = JpaConstants.OPERATION_SUMMARY, idempotent = true, bundleType = BundleTypeEnum.DOCUMENT, typeName = "Patient", canonicalUrl = JpaConstants.SUMMARY_OPERATION_URL)
	public IBaseBundle patientInstanceSummary(
		@IdParam IIdType thePatientId,
		RequestDetails theRequestDetails
	) {
		SearchParameterMap searchParameterMap = new SearchParameterMap()
			.add(DocumentReference.SP_PATIENT, new ReferenceParam("Patient/"+thePatientId.getIdPart()))
			.add(DocumentReference.SP_TYPE, new TokenParam("IpsBundle"));
		IBundleProvider searchResults = myDaoRegistry
			.getResourceDao(ResourceType.DocumentReference.name())
			.search(searchParameterMap, theRequestDetails);
		if (searchResults.size() == 0) {
			return new Bundle();
		}
		DocumentReference dr = (DocumentReference)searchResults.getResources(0, 1).get(0);
		byte[] encodedIps = dr.getContent().get(0).getAttachment().getData();
		String decodeIps = new String(Base64.getDecoder().decode(encodedIps));
		return  (Bundle)jsonParser.parseResource(decodeIps);
	}

}
