package sad.test.pdfscan.utils;

import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesIbanProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;
import sad.test.pdfscan.model.Country;

import java.util.Optional;
import java.util.UUID;

public class StringUtils {

    /**
     * return index of String-fragment in original-string
     * @param original
     * @param stringToFind
     * @return
     */
    public static int getIndexOfString(String original , String stringToFind){
        int index = 0;
        if(!original.isBlank() && !original.isEmpty()){
           return original.indexOf(stringToFind);
        }
        return index;
    }

    /**
     * find String-fragment in an original-String
     * in our case the String-fragment is IBAN:
     * @param original
     * @param strToFind
     * @return
     */
    public static boolean textContainIban(String original, String strToFind){
        return !original.isBlank() && original.contains(strToFind);
    }

    /**
     * Check if any Country is given, when not the default Country-Spec will be used
     * The default-size and default-withWhiteSpace are used only when the default-Contry Code not exist in the countries-iban-properties
     * @param countryCode
     * @param defaultIbanProperties
     * @param countriesIbanProperties
     * @param iban
     * @return boolean true if the given Iban matchs the Specifications of the given Country or DefaultCountry
     */
    public static boolean ibanMatchCountrySpec(final String countryCode,
                                               final DefaultIbanProperties defaultIbanProperties,
                                               final CountriesIbanProperties countriesIbanProperties,
                                               final String iban){
        if (countryCode != null && !countryCode.isBlank()) {
            Optional<Country> country = countriesIbanProperties.getCountry().stream()
                    .filter(country1 -> country1.getCode().equalsIgnoreCase(countryCode)).findFirst();
            if (country.isEmpty())
                return false;

            defaultIbanProperties.setCountry(country.get().getCode());
            defaultIbanProperties.setSize(country.get().getSize());
            defaultIbanProperties.setWithWhiteSpace(country.get().isWithWhiteSpace());
        }
        return matchSpec(defaultIbanProperties, iban);

    }

    /**
     * Check if Iban contains the Country-Code
     * Check if by size-verification the whiteSpace into Iban must be count or not
     * @param defaultIbanProperties
     * @param iban
     * @return
     */
    private static boolean matchSpec(final DefaultIbanProperties defaultIbanProperties, final String iban){
        if(defaultIbanProperties.getCountry().equalsIgnoreCase(iban.substring(0,2))){
            if(defaultIbanProperties.isWithWhiteSpace()){
               return iban.replaceAll(" ","").length() == defaultIbanProperties.getSize();
            } else {
                return iban.length() == (defaultIbanProperties.getSize() + (int)(defaultIbanProperties.getSize() / 4));
            }
        }
        return false;
    }

    /**
     * Extract the exact Iban value
     * @param pageText
     * @return
     */
    public static String extractIban(String pageText){
        if(pageText.contains("IBAN:")){
            // e.g :  IBAN:  DE15 3006 0601 0505 7807 80
            String[] ibanArray = pageText.split(":");
            if(ibanArray.length == 2){
                return ibanArray[1].trim();
            }
        }
        return pageText;
    }

    /**
     * We have defined two to blacklist an Iban :  trouth Country or exact listed Iban
     * we check if the given Iban are in the both case
     * @param iban
     * @param blackListedProperties
     * @return
     */
    public static boolean isBlackListed(final String iban, final BlackListedProperties blackListedProperties){
        boolean state = false;
        String blockIban = iban.trim().replaceAll(" ","");

        if(blackListedProperties.getIbans().contains(blockIban) ||
                blackListedProperties.getIbans().contains(iban.trim()))
            state = true;

        if(blackListedProperties.getCountries().contains(iban.trim().substring(0,2)))
            state = true;

        return state;
    }

    /**
     * Generate unique filename
     * @param storeUrl
     * @return
     */
    public static String generateFileName(String storeUrl){
        UUID uuid = UUID.randomUUID();
        return storeUrl+"\\file"+uuid+".pdf";
    }

    /**
     * check if given url are valid
     * set url when not valid
     * @param url
     * @return
     */
    public static String validUrl(String url){
        StringBuilder stringBuilder = new StringBuilder();
        if(!url.contains("http://") && !url.contains("https://")){
            stringBuilder.append("https://");
        }
        stringBuilder.append(url);
        return stringBuilder.toString();
    }
}
