package sad.test.pdfscan.services;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesSpecificationProperties;
import sad.test.pdfscan.config.DefaultSpecificationProperties;
import sad.test.pdfscan.model.CheckElement;

import java.util.List;

public interface PdfService {
    ResponseEntity checkSpecifications(final String countryCode,
                                       final String url,
                                       final DefaultSpecificationProperties defaultSpecificationProperties,
                                       final BlackListedProperties blackListedProperties,
                                       final CountriesSpecificationProperties countriesSpecificationProperties,
                                       final List<CheckElement> checkElementList);

    @Async
    ResponseEntity downloadAndStorePdf(final String countryCode,final String url,final String store);

    boolean deleteFile(String url);

}
