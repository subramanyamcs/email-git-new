/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package com.intalio.bpms.examples.email;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Email service configuration
 */
public class EmailConfiguration {

    public String sendURL = "smtp://user:password@smtp.example.com";
    
    public String contentType = "text/html;charset=utf-8";
    
    public String returnAddress = "user@example.com";
    
    public Properties props = new Properties();

    public static EmailConfiguration load(InputStream input) throws IOException {
        EmailConfiguration config = new EmailConfiguration();
        config.props.load(input);
        config.sendURL = config.props.getProperty("sendURL");
        config.contentType = config.props.getProperty("contentType");
        config.returnAddress = config.props.getProperty("returnAddress");
        return config;
    }

}