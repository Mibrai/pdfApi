package sad.test.pdfscan.config;

import org.springframework.context.annotation.Configuration;
import sad.test.pdfscan.model.Country;

import java.util.Optional;

@Configuration
public class Config {
    /**
     * Get default Iban Properties to be use
     * default-size and default-withWitheSpace will be used only when the default-country not exist in countries-iban-properties
     * @param defaultSpecificationProperties
     * @param countriesSpecificationProperties
     * @return
     */
    public DefaultSpecificationProperties getIbanProperties(final DefaultSpecificationProperties defaultSpecificationProperties,
                                                            final CountriesSpecificationProperties countriesSpecificationProperties){
        if(countryPropertiesExist(defaultSpecificationProperties.getCountry(), countriesSpecificationProperties)){
            Country country = getCountryProperties(defaultSpecificationProperties.getCountry(), countriesSpecificationProperties).get();
            DefaultSpecificationProperties defaultSpecificationProperties1 = new DefaultSpecificationProperties();
            defaultSpecificationProperties1.setCountry(country.getCode());
            defaultSpecificationProperties1.setSize(country.getSize());
            defaultSpecificationProperties1.setWithWhiteSpace(country.isWithWhiteSpace());

            return defaultSpecificationProperties1;
        }
        return defaultSpecificationProperties;
    }

    /**
     * check if Country has Iban-Specification
     * @param countryCode
     * @param countriesSpecificationProperties
     * @return
     */
    private boolean countryPropertiesExist(String countryCode, final CountriesSpecificationProperties countriesSpecificationProperties){
        return countriesSpecificationProperties.getCountry().stream().anyMatch(country -> country.getCode().equalsIgnoreCase(countryCode));
    }

    /**
     * Get Country Spec-Properties
     * @param countryCode
     * @param countriesSpecificationProperties
     * @return
     */
    private Optional<Country> getCountryProperties(String countryCode, final CountriesSpecificationProperties countriesSpecificationProperties){
        return countriesSpecificationProperties.getCountry().stream().filter(country -> country.getCode().equalsIgnoreCase(countryCode)).findFirst();
    }
}
