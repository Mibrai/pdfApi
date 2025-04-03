package sad.test.pdfscan.config;

import org.springframework.context.annotation.Configuration;
import sad.test.pdfscan.model.Country;

import java.util.Optional;

@Configuration
public class Config {
    /**
     * Get default Iban Properties to be use
     * default-size and default-withWitheSpace will be used only when the default-country not exist in countries-iban-properties
     * @param defaultIbanProperties
     * @param countriesIbanProperties
     * @return
     */
    public DefaultIbanProperties getIbanProperties(final DefaultIbanProperties defaultIbanProperties,
                                                    final CountriesIbanProperties countriesIbanProperties){
        if(countryPropertiesExist(defaultIbanProperties.getCountry(),countriesIbanProperties)){
            Country country = getCountryProperties(defaultIbanProperties.getCountry(),countriesIbanProperties).get();
            DefaultIbanProperties defaultIbanProperties1 = new DefaultIbanProperties();
            defaultIbanProperties1.setCountry(country.getCode());
            defaultIbanProperties1.setSize(country.getSize());
            defaultIbanProperties1.setWithWhiteSpace(country.isWithWhiteSpace());

            return  defaultIbanProperties1;
        }
        return defaultIbanProperties;
    }

    /**
     * check if Country has Iban-Specification
     * @param countryCode
     * @param countriesIbanProperties
     * @return
     */
    private boolean countryPropertiesExist(String countryCode, final CountriesIbanProperties countriesIbanProperties){
        return countriesIbanProperties.getCountry().stream().anyMatch(country -> country.getCode().equalsIgnoreCase(countryCode));
    }

    /**
     * Get Country Spec-Properties
     * @param countryCode
     * @param countriesIbanProperties
     * @return
     */
    private Optional<Country> getCountryProperties(String countryCode, final CountriesIbanProperties countriesIbanProperties){
        return countriesIbanProperties.getCountry().stream().filter(country -> country.getCode().equalsIgnoreCase(countryCode)).findFirst();
    }
}
