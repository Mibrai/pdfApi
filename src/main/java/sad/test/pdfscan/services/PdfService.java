package sad.test.pdfscan.services;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesIbanProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;

public interface PdfService {
    ResponseEntity checkBlacklistedIban(final String countryCode
            ,final String url,
            final DefaultIbanProperties defaultIbanProperties,
            final BlackListedProperties blackListedProperties,
            final CountriesIbanProperties countriesIbanProperties);

    @Async
    ResponseEntity downloadAndStorePdf(String url, String store);

    boolean deleteFile(String url);

}
