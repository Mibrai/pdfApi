package sad.test.pdfscan.utils;

import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;

public class StringUtils {

    public static int getInitIndexOfString(String original , String stringToFind){
        int index = 0;
        if(!original.isBlank() && !original.isEmpty()){
           return original.indexOf(stringToFind);
        }
        return index;
    }

    public static boolean textContainIban(String original, String strToFind){
        return !original.isBlank() && original.contains(strToFind);
    }

    public static boolean ibanMatchCountrySpec(final DefaultIbanProperties defaultIbanProperties, String iban){
        //check size , check if contain country code
        boolean state = false;

        if(defaultIbanProperties.getCountry().equalsIgnoreCase(iban.substring(0,2))){
            if(defaultIbanProperties.isWithWhiteSpace()){
                state = iban.replaceAll(" ","").length() == defaultIbanProperties.getSize();
            } else {
                state = iban.length() == (defaultIbanProperties.getSize() + (int)(defaultIbanProperties.getSize() / 4));
            }
        }

        return state;
    }

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

    public static boolean isBlackListed(final String iban, final BlackListedProperties blackListedProperties){
        boolean state = false;
        String blockIban = iban.replaceAll(" ","");

        if(blackListedProperties.getIbans().contains(blockIban))
            state = true;

        if(blackListedProperties.getCountries().contains(iban.substring(0,2)))
            state = true;

        return state;
    }
}
