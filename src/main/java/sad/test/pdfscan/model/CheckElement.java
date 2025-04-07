package sad.test.pdfscan.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CheckElement {
    private String name;
    private String initialString;
    private String lastString;

    private long size;
    private boolean withWhiteSpace;
    private List<String> blacklisted;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitialString() {
        return initialString;
    }

    public void setInitialString(String initialString) {
        this.initialString = initialString;
    }

    public String getLastString() {
        return lastString;
    }

    public void setLastString(String lastString) {
        this.lastString = lastString;
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

    public List<String> getBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(List<String> blacklisted) {
        this.blacklisted = blacklisted;
    }
}
