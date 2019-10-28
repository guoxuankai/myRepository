/*******************************************************************************
 * Copyright 2009-2018 Amazon Services. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 *
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 *******************************************************************************
 * Tax Collection
 * API Version: 2013-09-01
 * Library Version: 2018-10-31
 * Generated: Mon Oct 22 22:40:32 UTC 2018
 */
package com.amazonservices.mws.orders._2013_09_01.model;

import com.amazonservices.mws.client.*;

/**
 * TaxCollection complex type.
 *
 * XML schema:
 *
 * <pre>
 * &lt;complexType name="TaxCollection"&gt;
 *    &lt;complexContent&gt;
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *          &lt;sequence&gt;
 *             &lt;element name="Model" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *             &lt;element name="ResponsibleParty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *          &lt;/sequence&gt;
 *       &lt;/restriction&gt;
 *    &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public class TaxCollection extends AbstractMwsObject {

    private String model;

    private String responsibleParty;

    @Override
    public String toString() {
        return "TaxCollection{" +
                "model='" + model + '\'' +
                ", responsibleParty='" + responsibleParty + '\'' +
                '}';
    }

    /**
     * Get the value of Model.
     *
     * @return The value of Model.
     */
    public String getModel() {
        return model;
    }

    /**
     * Set the value of Model.
     *
     * @param model
     *            The new value to set.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Check to see if Model is set.
     *
     * @return true if Model is set.
     */
    public boolean isSetModel() {
        return model != null;
    }

    /**
     * Set the value of Model, return this.
     *
     * @param model
     *             The new value to set.
     *
     * @return This instance.
     */
    public TaxCollection withModel(String model) {
        this.model = model;
        return this;
    }

    /**
     * Get the value of ResponsibleParty.
     *
     * @return The value of ResponsibleParty.
     */
    public String getResponsibleParty() {
        return responsibleParty;
    }

    /**
     * Set the value of ResponsibleParty.
     *
     * @param responsibleParty
     *            The new value to set.
     */
    public void setResponsibleParty(String responsibleParty) {
        this.responsibleParty = responsibleParty;
    }

    /**
     * Check to see if ResponsibleParty is set.
     *
     * @return true if ResponsibleParty is set.
     */
    public boolean isSetResponsibleParty() {
        return responsibleParty != null;
    }

    /**
     * Set the value of ResponsibleParty, return this.
     *
     * @param responsibleParty
     *             The new value to set.
     *
     * @return This instance.
     */
    public TaxCollection withResponsibleParty(String responsibleParty) {
        this.responsibleParty = responsibleParty;
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
        model = r.read("Model", String.class);
        responsibleParty = r.read("ResponsibleParty", String.class);
    }

    /**
     * Write members to a MwsWriter.
     *
     * @param w
     *      The writer to write to.
     */
    @Override
    public void writeFragmentTo(MwsWriter w) {
        w.write("Model", model);
        w.write("ResponsibleParty", responsibleParty);
    }

    /**
     * Write tag, xmlns and members to a MwsWriter.
     *
     * @param w
     *         The Writer to write to.
     */
    @Override
    public void writeTo(MwsWriter w) {
        w.write("https://mws.amazonservices.com/Orders/2013-09-01", "TaxCollection",this);
    }


    /** Default constructor. */
    public TaxCollection() {
        super();
    }

}
