package sad.test.pdfscan.utils;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesIbanProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;

@SpringBootTest
public class StringUtilsTest {

    @Autowired
    DefaultIbanProperties defaultIbanProperties;

    @Autowired
    CountriesIbanProperties countriesIbanProperties;

    @Autowired
    BlackListedProperties blackListedProperties;

    private static final String IBAN = "DE27 9837 7376 6664 5553 50";
    private static final String ALLOWED_IBAN = "DE27 9837 7376 6664 5553 60";
    @Test
    void ibanMatchCountrySpecTest(){
        //test empty country code
        Truth.assertThat(StringUtils.ibanMatchCountrySpec("",defaultIbanProperties,countriesIbanProperties,"")).isFalse();
        //test with null defaultIbanProperties and null countriesIbanProperties
        Truth.assertThat(StringUtils.ibanMatchCountrySpec("",null,countriesIbanProperties,"")).isFalse();
        Truth.assertThat(StringUtils.ibanMatchCountrySpec("DE",null,null,IBAN)).isFalse();
        Truth.assertThat(StringUtils.ibanMatchCountrySpec(null,defaultIbanProperties,null,IBAN)).isTrue();
        Truth.assertThat(StringUtils.ibanMatchCountrySpec(null,null,null,IBAN)).isFalse();
        //match country code and country size
        Truth.assertThat(StringUtils.ibanMatchCountrySpec("DE",defaultIbanProperties,countriesIbanProperties,IBAN)).isTrue();
        //don't match country code
        Truth.assertThat(StringUtils.ibanMatchCountrySpec("DE",defaultIbanProperties,countriesIbanProperties,IBAN.replaceAll("DE","FR"))).isFalse();
        //match country code but don't match country size
        Truth.assertThat(StringUtils.ibanMatchCountrySpec("FR",defaultIbanProperties,countriesIbanProperties,IBAN.replaceAll("DE","FR"))).isFalse();
        //match country code and country size
        Truth.assertThat(StringUtils.ibanMatchCountrySpec("FR",defaultIbanProperties,countriesIbanProperties,IBAN.replaceAll("DE","FR").substring(0,22))).isTrue();
    }

    @Test
    void extractIbanTest(){
        StringBuilder stringBuilder = new StringBuilder("IBAN:");
        Truth.assertThat(StringUtils.extractIban("").isBlank()).isTrue();
        Truth.assertThat(StringUtils.extractIban(null) == null).isTrue();
        Truth.assertThat(StringUtils.extractIban(stringBuilder.append(IBAN).toString()).contains("IBAN:")).isFalse();
    }

    @Test
    void isBlacklistedTest(){
        Truth.assertThat(StringUtils.isBlackListed(IBAN,blackListedProperties)).isTrue();
        Truth.assertThat(StringUtils.isBlackListed(ALLOWED_IBAN,blackListedProperties)).isFalse();
        blackListedProperties.getCountries().add("DE");
        Truth.assertThat(StringUtils.isBlackListed(ALLOWED_IBAN,blackListedProperties)).isTrue();
        Truth.assertThat(StringUtils.isBlackListed(null,blackListedProperties)).isFalse();
        Truth.assertThat(StringUtils.isBlackListed("12345",blackListedProperties)).isFalse();
    }

    @Test
    void validUrlTest(){
        StringBuilder urlBuilder = new StringBuilder("test.de");
        Truth.assertThat(StringUtils.validUrl(urlBuilder.toString()).contains("https")).isTrue();
        urlBuilder.insert(0,"http://");
        Truth.assertThat(StringUtils.validUrl(urlBuilder.toString()).contains("https")).isFalse();
        Truth.assertThat(StringUtils.validUrl("test.de.https://")).isNull();
        Truth.assertThat(StringUtils.validUrl("testhttp://test.de")).isNull();
    }
}
