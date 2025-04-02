package sad.test.pdfscan.config;

import sad.test.pdfscan.model.Country;

import java.util.Optional;

public class Config {

    private DefaultIbanProperties getIbanProperties(final DefaultIbanProperties defaultIbanProperties,
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

    private boolean countryPropertiesExist(String countryCode, final CountriesIbanProperties countriesIbanProperties){
        return countriesIbanProperties.getCountry().stream().anyMatch(country -> country.getCode().equalsIgnoreCase(countryCode));
    }

    private Optional<Country> getCountryProperties(String countryCode, final CountriesIbanProperties countriesIbanProperties){
        return countriesIbanProperties.getCountry().stream().filter(country -> country.getCode().equalsIgnoreCase(countryCode)).findFirst();
    }
}
