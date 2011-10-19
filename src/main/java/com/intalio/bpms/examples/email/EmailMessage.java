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

import org.apache.axiom.om.OMElement;

/**
 * Data object representing an email
 */
public class EmailMessage {

    public static final String[] EMPTY = new String[0];
    
    public String from = "";
    
    public String[] to = EMPTY;
    
    public String[] cc = EMPTY;

    public String[] bcc = EMPTY;
    
    public String subject = "";

    public String body = "";
    
    public static EmailMessage parseWithXMLBody(OMElement msg) throws IOException {
        return parse(msg, true);
    }

    public static EmailMessage parseWithTextBody(OMElement msg) throws IOException {
        return parse(msg, false);
    }

    private static EmailMessage parse(OMElement msg, boolean parseXmlBody) throws IOException {
        EmailMessage email = new EmailMessage();
        OMParser p = new OMParser(msg);
        String toAddressValue = p.getRequiredString(Constants.TO);
        if (toAddressValue  != null) {
        	email.to = p.getStringArray(Constants.TO);
        }
        email.from = p.getRequiredString(Constants.FROM);
        email.cc = p.getStringArray(Constants.CC);
        email.bcc = p.getStringArray(Constants.BCC);
        email.subject = p.getRequiredString(Constants.SUBJECT);
        if (parseXmlBody) {
            email.body = p.getXmlContent(Constants.BODY);
        } else {
            email.body = p.getTextContent(Constants.BODY);
        }
        return email;
    }
}