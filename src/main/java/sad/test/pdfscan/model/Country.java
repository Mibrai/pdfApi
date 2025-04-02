package sad.test.pdfscan.model;

public class Country {
    private String name;
    private String code;
    private long size;
    private boolean withWhiteSpace;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
