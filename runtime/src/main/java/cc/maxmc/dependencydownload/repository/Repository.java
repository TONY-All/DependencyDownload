package cc.maxmc.dependencydownload.repository;

import cc.maxmc.dependencydownload.dependency.MavenObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public interface Repository {

    /**
     * The maven repository host's address.
     *
     * @return the host's address, without the path to the actual dependency and slash before the path
     */
    String getHost();

    /**
     * Creates an url for the given {@link MavenObject} with the {@link #getHost()}.
     *
     * @param mavenObject the dependency to generate the url for
     * @return the url
     * @throws MalformedURLException if the url syntax is invalid
     */
    default URL createURL(MavenObject mavenObject) throws MalformedURLException {
        return new URL(getHost() + '/' + mavenObject.getMavenPath());
    }

    /**
     * Creates an url for the given {@link MavenObject} with the {@link #getHost()}.
     *
     * @param mavenObject the dependency to generate the url for
     * @return the url
     * @throws MalformedURLException if the url syntax is invalid
     */
    default URL createHashURL(MavenObject mavenObject) throws MalformedURLException {
        return new URL(getHost() + '/' + mavenObject.getHashMavenPath());
    }

    /**
     * Opens a connection from an url generated with {@link #createURL(MavenObject)}.
     *
     * @param mavenObject the dependency used to generate the url
     * @return the opened {@link HttpsURLConnection}
     * @throws IOException if opening connection fails
     */
    default HttpsURLConnection openConnection(MavenObject mavenObject) throws IOException {
        return (HttpsURLConnection) createURL(mavenObject).openConnection();
    }
}
