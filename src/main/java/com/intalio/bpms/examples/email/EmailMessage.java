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

    public String content = "";
    
    public static EmailMessage parse(OMElement msg) throws IOException {
        EmailMessage email = new EmailMessage();
        OMParser p = new OMParser(msg);
        email.to = p.getStringArray(Constants.TO);
        email.from = p.getRequiredString(Constants.FROM);
        email.cc = p.getStringArray(Constants.CC);
        email.bcc = p.getStringArray(Constants.BCC);
        email.subject = p.getRequiredString(Constants.SUBJECT);
        email.content = p.getXmlContent(Constants.BODY);
        return email;
    }

}