package sad.test.pdfscan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "defaults-iban-properties")
public class DefaultIbanProperties {
    private String country;
    private long size;
    private boolean withWhiteSpace;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isWithWhiteSpace() {
        return withWhiteSpace;
    }

    public void setWithWhiteSpace(boolean withWhiteSpace) {
        this.withWhiteSpace = withWhiteSpace;
    }
}
