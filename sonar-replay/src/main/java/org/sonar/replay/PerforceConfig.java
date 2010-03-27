package org.sonar.replay;

import lombok.Data;

import java.net.URI;

/**
 * TODO: Add documentation for PerforceConfig
 *
 * @author <a href="mailto:jaxzin@codehaus.org">Brian R. Jackson</a>
 * @version $Change$
 */
@Data
public class PerforceConfig {
    private URI serverUrl;
    private String userName;
    private String password;
}
