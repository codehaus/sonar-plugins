package org.sonar.replay;

import lombok.Data;

import java.net.URI;

/**
 * TODO: Add documentation for SonarConfig
 *
 * @author <a href="mailto:jaxzin@codehaus.org">Brian R. Jackson</a>
 * @version $Change$
 */
@Data
public class SonarConfig {
    private URI jdbcUrl;
    private URI serverUrl;
    private String jdbcDriver;
    private String jdbcUserName;
    private String jdbcPassword;
    
}
