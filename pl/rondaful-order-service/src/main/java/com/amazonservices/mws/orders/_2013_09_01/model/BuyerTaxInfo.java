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
 * Buyer Tax Info
 * API Version: 2013-09-01
 * Library Version: 2018-10-31
 * Generated: Mon Oct 22 22:40:32 UTC 2018
 */
package com.amazonservices.mws.orders._2013_09_01.model;

import com.amazonservices.mws.client.AbstractMwsObject;
import com.amazonservices.mws.client.MwsReader;
import com.amazonservices.mws.client.MwsWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * BuyerTaxInfo complex type.
 *
 * XML schema:
 *
 * <pre>
 * &lt;complexType name="BuyerTaxInfo"&gt;
 *    &lt;complexContent&gt;
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *          &lt;sequence&gt;
 *             &lt;element name="CompanyLegalName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *             &lt;element name="TaxingRegion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *             &lt;element name="TaxClassifications" type="{https://mws.amazonservices.com/Orders/2013-09-01}TaxClassification" maxOccurs="unbounded"/&gt;
 *          &lt;/sequence&gt;
 *       &lt;/restriction&gt;
 *    &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public class BuyerTaxInfo extends AbstractMwsObject {

    private String companyLegalName;

    private String taxingRegion;

    private List<TaxClassification> taxClassifications;

    /**
     * Get the value of CompanyLegalName.
     *
     * @return The value of CompanyLegalName.
     */
    public String getCompanyLegalName() {
        return companyLegalName;
    }

    /**
     * Set the value of CompanyLegalName.
     *
     * @param companyLegalName
     *            The new value to set.
     */
    public void setCompanyLegalName(String companyLegalName) {
        this.companyLegalName = companyLegalName;
    }

    /**
     * Check to see if CompanyLegalName is set.
     *
     * @return true if CompanyLegalName is set.
     */
    public boolean isSetCompanyLegalName() {
        return companyLegalName != null;
    }

    /**
     * Set the value of CompanyLegalName, return this.
     *
     * @param companyLegalName
     *             The new value to set.
     *
     * @return This instance.
     */
    public BuyerTaxInfo withCompanyLegalName(String companyLegalName) {
        this.companyLegalName = companyLegalName;
        return this;
    }

    /**
     * Get the value of TaxingRegion.
     *
     * @return The value of TaxingRegion.
     */
    public String getTaxingRegion() {
        return taxingRegion;
    }

    /**
     * Set the value of TaxingRegion.
     *
     * @param taxingRegion
     *            The new value to set.
     */
    public void setTaxingRegion(String taxingRegion) {
        this.taxingRegion = taxingRegion;
    }

    /**
     * Check to see if TaxingRegion is set.
     *
     * @return true if TaxingRegion is set.
     */
    public boolean isSetTaxingRegion() {
        return taxingRegion != null;
    }

    /**
     * Set the value of TaxingRegion, return this.
     *
     * @param taxingRegion
     *             The new value to set.
     *
     * @return This instance.
     */
    public BuyerTaxInfo withTaxingRegion(String taxingRegion) {
        this.taxingRegion = taxingRegion;
        return this;
    }

    /**
     * Get the value of TaxClassifications.
     *
     * @return The value of TaxClassifications.
     */
    public List<TaxClassification> getTaxClassifications() {
        if (taxClassifications==null) {
            taxClassifications = new ArrayList<TaxClassification>();
        }
        return taxClassifications;
    }

    /**
     * Set the value of TaxClassifications.
     *
     * @param taxClassifications
     *            The new value to set.
     */
    public void setTaxClassifications(List<TaxClassification> taxClassifications) {
        this.taxClassifications = taxClassifications;
    }

    /**
     * Clear TaxClassifications.
     */
    public void unsetTaxClassifications() {
        this.taxClassifications = null;
    }

    /**
     * Check to see if TaxClassifications is set.
     *
     * @return true if TaxClassifications is set.
     */
    public boolean isSetTaxClassifications() {
        return taxClassifications != null && !taxClassifications.isEmpty();
    }

    /**
     * Add values for TaxClassifications, return this.
     *
     * @param taxClassifications
     *             New values to add.
     *
     * @return This instance.
     */
    public BuyerTaxInfo withTaxClassifications(TaxClassification... values) {
        List<TaxClassification> list = getTaxClassifications();
        for (TaxClassification value : values) {
            list.add(value);
        }
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
        companyLegalName = r.read("CompanyLegalName", String.class);
        taxingRegion = r.read("TaxingRegion", String.class);
        taxClassifications = r.readList("TaxClassifications", "TaxClassification", TaxClassification.class);
    }

    /**
     * Write members to a MwsWriter.
     *
     * @param w
     *      The writer to write to.
     */
    @Override
    public void writeFragmentTo(MwsWriter w) {
        w.write("CompanyLegalName", companyLegalName);
        w.write("TaxingRegion", taxingRegion);
        w.writeList("TaxClassifications", "TaxClassification", taxClassifications);
    }

    /**
     * Write tag, xmlns and members to a MwsWriter.
     *
     * @param w
     *         The Writer to write to.
     */
    @Override
    public void writeTo(MwsWriter w) {
        w.write("https://mws.amazonservices.com/Orders/2013-09-01", "BuyerTaxInfo",this);
    }


    /** Default constructor. */
    public BuyerTaxInfo() {
        super();
    }

    @Override
    public String toString() {
        return "BuyerTaxInfo{" +
                "companyLegalName='" + companyLegalName + '\'' +
                ", taxingRegion='" + taxingRegion + '\'' +
                ", taxClassifications=" + taxClassifications +
                '}';
    }
}
