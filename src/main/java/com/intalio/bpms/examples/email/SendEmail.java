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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java application to send emails based on configuration and email XML file.
 * <p>
 * Usage:  java com.intalio.bpms.examples.email.SendEmail [config_file] [email_xml_file]
 */
public class SendEmail {
    private static final Logger LOG = LoggerFactory.getLogger(SendEmail.class);

    /**
     * Application entrypoint
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage:  java Sendmail [config_file] [email_xml_file]");
            return;
        }
        String configFile = args[0];
        String emailFile = args[1];

        System.out.println("Config file: "+configFile);
        System.out.println("Email XML file: "+emailFile);
        
        try {
            InputStream is = new FileInputStream(configFile);
            if (is == null) {
                throw new Error("Unable to load configuration file: "+configFile);
            }
            
            EmailConfiguration config;
            try {
                config = EmailConfiguration.load(is);
            } catch (IOException e) {
                LOG.error("Error while loading email configuration file: " + configFile);
                throw new RuntimeException(e);
            }
    
            EmailService service = new EmailService(config);
            is = new FileInputStream(emailFile);
            if (is == null) {
                throw new Error("Unable to load email file: "+emailFile);
            }
            
            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(is);
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            OMElement xml =  builder.getDocumentElement();
            service.send(xml);
        } catch (Exception e) {
            LOG.error("Error while sending email: ", e);
            throw new RuntimeException(e);
        }
    }

}