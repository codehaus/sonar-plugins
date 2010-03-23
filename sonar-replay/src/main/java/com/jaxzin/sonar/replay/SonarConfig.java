/*
 *  Revision Information:
 *  $Id$
 *  $Author$
 *  $DateTime$
 *
 * Copyright ©2010 ESPN.com and Disney Interactive Media Group.  All rights reserved.
 */
package com.jaxzin.sonar.replay;

import lombok.Data;

import java.net.URI;

/**
 * TODO: Add documentation for SonarConfig
 *
 * @author <a href="mailto:Brian.R.Jackson@espn3.com">Brian R. Jackson</a>
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
