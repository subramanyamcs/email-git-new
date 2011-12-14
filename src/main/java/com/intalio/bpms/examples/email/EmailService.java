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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Map.Entry;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Email Service:  Axis2-based web service to send emails.
 */
public class EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    public static final String CONFIG_DIR_PROP = "com.intalio.bpms.configDirectory";
    public static final String CONFIG_FILE = "EmailWS.properties";

    private EmailConfiguration _config;

    /**
     * Public no-arg constructor for Axis2
     */
    public EmailService() {
        String configDir = System.getProperties().getProperty(CONFIG_DIR_PROP);
        if (configDir == null)
            throw new IllegalStateException("Property " + CONFIG_DIR_PROP + " is not set.");
        File f = new File(configDir, CONFIG_FILE);
        try {
            _config = EmailConfiguration.load(new FileInputStream(f));
        } catch (IOException e) {
            LOG.error("Error while loading email configuration file: " + f, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor for dependency-injection
     */
    public EmailService(EmailConfiguration config) {
        _config = config;
    }
    
    /**
     * Send email operation. See corresponding WSDL for XML schema description.
     */
    public OMElement send(OMElement request) throws AxisFault {
        LOG.debug("send()");
        return sendEmailWithXMLBody(request);
    }

    public OMElement sendEmailWithXMLBody(OMElement request) throws AxisFault {
        try {
            LOG.debug("sendEmailWithXMLBody()");
            EmailMessage email = EmailMessage.parseWithXMLBody(request);
            return send(email);
        } catch (Exception e) {
            LOG.error("Error while sending email", e);
            throw makeFault(e);
        }
    }
    
    private AxisFault makeFault(Exception e) {
    	OMElement response = null;
        AxisFault axisFault = new AxisFault(e.getMessage(), e);
        response = Constants.OM_FACTORY.createOMElement(Constants.INVALID_INPUT_FORMAT);
        OMNamespace xmlnsNamespace = Constants.OM_FACTORY.createOMNamespace(Constants.XMLNS, Constants.EMAIL_NAMESPACE_PREFIX);
        OMElement reasonElement = Constants.OM_FACTORY.createOMElement("reason", xmlnsNamespace, response);        
        reasonElement.setText(e.getMessage());
        axisFault.setDetail(response);      
        return axisFault;
    }

    /**
     * Send email operation. See corresponding WSDL for XML schema description.
     */
    public OMElement sendEmailWithTextBody(OMElement request) throws AxisFault {
        try {
            LOG.debug("sendEmailWithTextBody()");
            EmailMessage email = EmailMessage.parseWithTextBody(request);
            return send(email);
        } catch (Exception e) {
            LOG.error("Error while sending email", e);
            throw makeFault(e);
        }
    }

    protected OMElement send(EmailMessage email) throws AxisFault {
        try {
            Properties props = new Properties();
            props.put("mail.user", email.from);
            props.put("mail.from", email.from);
            props.put("mail.smtp.from", email.from);
            for (Entry<Object,Object> e : _config.props.entrySet()) {
                if (e.getKey() instanceof String && ((String)e.getKey()).startsWith("mail.")) {
                    props.put(e.getKey(), e.getValue());
                }
            }
            
            Session session;
            if (_config.authPassword == null) {
                session = Session.getInstance(props);
            } else {
                Authenticator auth = new Authenticator() { 
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(_config.authUsername, _config.authPassword);
                    }
                };
                session = Session.getInstance(props, auth);
            }
            
            if (LOG.isDebugEnabled())
                session.setDebug(true);
    
            MimeMessage msg = new MimeMessage(session);
    
            // --from field
            if (email.from == null || email.from.length() == 0)
                throw new MessagingException("No From: address specified");

            // --to fields
            if (email.to == null)
                throw new MessagingException("No To: address specified");
    
            msg.setFrom(new InternetAddress(email.from));
            msg.setSender(new InternetAddress(email.from));

            String tempTo = "";
            for (int i = 0; i < email.to.length; i++) {
                LOG.debug("Adding To: field:" + email.to[i]);
                tempTo = email.to[i] + ',' + tempTo;
            }
            msg.addRecipients(Message.RecipientType.TO, tempTo);
    
            // Cc:
            String tempCC = "";
            for (int i = 0; i < email.cc.length; i++) {
                LOG.debug("Adding Cc: field:" + email.cc[i]);
                tempCC = email.cc[i] + ',' + tempCC;
            }
            msg.setRecipients(Message.RecipientType.CC, tempCC);
    
            // Bcc:
            String tempBCC = "";
            for (int i = 0; i < email.bcc.length; i++) {
                LOG.debug("Adding bcc field:" + email.bcc[i]);
                tempBCC = email.bcc[i] + ',' + tempBCC;
            }
            msg.setRecipients(Message.RecipientType.BCC, tempBCC);
    
            // Subject:
            msg.setSubject(email.subject);
    
            // Content
            LOG.debug("Content type:" + _config.contentType);
            Multipart multipart = new MimeMultipart("alternative");
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(email.body, _config.contentType);
            
            LOG.debug("Body Part:" + bodyPart.getContent());
            multipart.addBodyPart(bodyPart);
            msg.setContent(multipart);
    
            LOG.debug("Sending message: " + msg);
            msg.saveChanges();
            
            if (_config.sendURL == null) {
                Transport.send(msg);
            } else {
                URLName sendURL = new URLName(_config.sendURL);
                Transport transport = createTransport(session, sendURL);
                transport.sendMessage(msg, msg.getAllRecipients());
            }
            LOG.debug("Message sent: " + msg);
    
            OMElement success = Constants.OM_FACTORY.createOMElement(Constants.SUCCESS);
            success.setText("true");
            return success;
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Create the transport specified by the URL.
     * 
     * @param session the session (required)
     * @param url the URL (required)
     * @param logWriter the log writer (optional)
     * @param category the category (optional)
     * @throws ResourceException if there is a problem creating the transport
     */
    private static Transport createTransport(Session session, URLName url) {
        Transport transport;

        try {
            LOG.debug("Creating mail transport for URL:"+url);
            LOG.debug("Host:"+url.getHost());
            LOG.debug("Port:"+url.getPort());
            LOG.debug("UserName:"+url.getUsername());
            LOG.debug("Password:"+url.getPassword());
            
            transport = session.getTransport(url.getProtocol());
            LOG.debug("Transport identified:"+transport);

            transport = session.getTransport(url.getProtocol());
            LOG.debug("Transport identified:"+transport);
            
            transport.connect(url.getHost(),
                              url.getPort(),
                              url.getUsername(), 
                              url.getPassword());
            return transport;
        } catch (NoSuchProviderException e) {
            LOG.error("Failed find provider for transport protocol - " + url.getProtocol(), e);
            throw new RuntimeException(e);
        }
        catch (MessagingException e) {
            LOG.error("Failed to connect to transport- " + url.getProtocol(), e);
            throw new RuntimeException(e);
        }
    }
}