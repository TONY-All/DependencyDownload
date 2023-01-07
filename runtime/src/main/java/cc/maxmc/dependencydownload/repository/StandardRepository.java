package cc.maxmc.dependencydownload.repository;

import java.util.Objects;

@SuppressWarnings("unused")
public class StandardRepository implements Repository {

    private final String host;

    public StandardRepository(String host) {
        this.host = host;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardRepository that = (StandardRepository) o;
        return Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host);
    }
}
