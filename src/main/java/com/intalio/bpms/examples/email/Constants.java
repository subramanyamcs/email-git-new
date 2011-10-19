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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;

/**
 * Schema constants
 */
public class Constants {
    public static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();

    /** Email WS XML namespace */
    public static final String XMLNS = "http://bpms.intalio.com/tools/webservices/email";


    /** Email request top-level element */
    public static final QName EMAIL_MESSAGE = new QName(XMLNS, "EmailMessage");
    public static final String EMAIL_NAMESPACE_PREFIX = "email";
    
    /** Email message elements */
    public static final QName FROM = new QName(XMLNS, "From");
    public static final QName TO = new QName(XMLNS, "To");
    public static final QName CC = new QName(XMLNS, "Cc");
    public static final QName BCC = new QName(XMLNS, "Bcc");
    public static final QName SUBJECT = new QName(XMLNS, "Subject");
    public static final QName BODY = new QName(XMLNS, "Body");
    
    /** Email response */
    public static final QName SUCCESS = new QName(XMLNS, "Success");
    public static final QName INVALID_INPUT_FORMAT = new QName(XMLNS, "emailFault",EMAIL_NAMESPACE_PREFIX);
}