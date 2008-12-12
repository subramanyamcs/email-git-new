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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OMParser {
    private static final Logger LOG = LoggerFactory.getLogger(OMParser.class);

    private OMElement _element;

    public OMParser(OMElement element) {
        _element = element;
        _element.build();
        if (_element.getParent() != null)
            _element.detach();
    }

    public String getRequiredString(QName parameter) {
        OMElement e = _element.getFirstChildWithName(parameter);
        if (e == null)
            throw new IllegalArgumentException("Missing parameter: " + parameter);
        String text = e.getText();
        if (text == null || text.trim().length() == 0)
            throw new IllegalArgumentException("Empty parameter: " + parameter);
        if (LOG.isDebugEnabled())
            LOG.debug("Parameter " + parameter + ": " + text);
        return text;
    }

    public OMElement getRequiredElement(QName name) {
        OMElement e = _element.getFirstChildWithName(name);
        if (e == null)
            throw new IllegalArgumentException("Missing element: " + name);
        return e;
    }

    @SuppressWarnings("unchecked")
    public String[] getStringArray(QName parameter) {
        ArrayList<String> strings = new ArrayList<String>();
        Iterator<OMElement> iter = _element.getChildrenWithName(parameter);
        while (iter.hasNext()) {
            OMElement e = iter.next();
            String text = e.getText();
            if (text != null && text.trim().length() > 0) strings.add(text);
        }
        return strings.toArray(new String[strings.size()]);
    }

    @SuppressWarnings("unchecked")
    public String getTextContent(QName parameter) {
        StringBuffer buf = new StringBuffer();
        OMElement el = _element.getFirstChildWithName(parameter);
        if (el == null)
            throw new IllegalArgumentException("Missing parameter: " + parameter);
        buf.append(el.getText());
        Iterator<OMElement> iter = el.getChildElements();
        while (iter.hasNext()) {
            OMElement e = iter.next();
            String text = e.getText();
            if (text != null && text.trim().length() > 0) buf.append(text);
        }
        return buf.toString();
    }

    public String getXmlContent(QName parameter) {
        OMElement el = _element.getFirstChildWithName(parameter);
        if (el == null)
            throw new IllegalArgumentException("Missing parameter: " + parameter);
        try {
            StringWriter s = new StringWriter(16*4096);
            el.serialize(s);
            StringBuffer buf = s.getBuffer();
            buf.delete(0, buf.indexOf(">")+1);
            buf.delete(buf.lastIndexOf("<"), buf.length());
            return buf.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}