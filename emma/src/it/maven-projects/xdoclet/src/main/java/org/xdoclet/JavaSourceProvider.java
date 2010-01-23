package org.xdoclet;

import java.util.Collection;
import java.io.IOException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 29 $
 */
public interface JavaSourceProvider {
    /**
     * @return a Collection of {@link java.net.URL} pointing to Java sources
     */
    Collection getURLs() throws IOException;

    /**
     * @return encoding
     */
    String getEncoding();
}
