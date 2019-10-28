/*******************************************************************************
 * Copyright 2009-2017 Amazon Services. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 *
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 *******************************************************************************
 * Message
 * API Version: 2011-10-01
 * Library Version: 2017-03-22
 * Generated: Wed Mar 22 23:24:32 UTC 2017
 */
package com.rondaful.cloud.seller.common.mwsProducts.model;

import com.rondaful.cloud.seller.common.mwsProducts.client.AbstractMwsObject;
import com.rondaful.cloud.seller.common.mwsProducts.client.MwsReader;
import com.rondaful.cloud.seller.common.mwsProducts.client.MwsWriter;

import javax.xml.bind.annotation.*;


/**
 * Message complex type.
 *
 * XML schema:
 *
 * <pre>
 * &lt;complexType name="Message"&gt;
 *    &lt;complexContent&gt;
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *          &lt;sequence&gt;
 *             &lt;element name="Locale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *             &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *          &lt;/sequence&gt;
 *       &lt;/restriction&gt;
 *    &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Message", propOrder={
    "locale",
    "text"
})
@XmlRootElement(name = "Message")
public class Message extends AbstractMwsObject {

    @XmlElement(name="Locale")
    private String locale;

    @XmlElement(name="Text")
    private String text;

    /**
     * Get the value of Locale.
     *
     * @return The value of Locale.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Set the value of Locale.
     *
     * @param locale
     *            The new value to set.
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Check to see if Locale is set.
     *
     * @return true if Locale is set.
     */
    public boolean isSetLocale() {
        return locale != null;
    }

    /**
     * Set the value of Locale, return this.
     *
     * @param locale
     *             The new value to set.
     *
     * @return This instance.
     */
    public Message withLocale(String locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Get the value of Text.
     *
     * @return The value of Text.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the value of Text.
     *
     * @param text
     *            The new value to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Check to see if Text is set.
     *
     * @return true if Text is set.
     */
    public boolean isSetText() {
        return text != null;
    }

    /**
     * Set the value of Text, return this.
     *
     * @param text
     *             The new value to set.
     *
     * @return This instance.
     */
    public Message withText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Read members from a MwsReader.
     *
     * @param r
     *      The reader to read from.
     */
    @Override
    public void readFragmentFrom(MwsReader r) {
        locale = r.read("Locale", String.class);
        text = r.read("Text", String.class);
    }

    /**
     * Write members to a MwsWriter.
     *
     * @param w
     *      The writer to write to.
     */
    @Override
    public void writeFragmentTo(MwsWriter w) {
        w.write("Locale", locale);
        w.write("Text", text);
    }

    /**
     * Write tag, xmlns and members to a MwsWriter.
     *
     * @param w
     *         The Writer to write to.
     */
    @Override
    public void writeTo(MwsWriter w) {
        w.write("http://mws.amazonservices.com/schema/Products/2011-10-01", "Message",this);
    }


    /** Default constructor. */
    public Message() {
        super();
    }

}
