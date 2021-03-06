//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2010.03.05 at 12:27:35 PM MEZ
//

package de.bht.jvr.collada14;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}param" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="count" use="required" type="{http://www.collada.org/2005/11/COLLADASchema}uint" />
 *       &lt;attribute name="offset" type="{http://www.collada.org/2005/11/COLLADASchema}uint" default="0" />
 *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="stride" type="{http://www.collada.org/2005/11/COLLADASchema}uint" default="1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "params" })
@XmlRootElement(name = "accessor")
public class Accessor {

    @XmlElement(name = "param")
    protected List<Param> params;
    @XmlAttribute(required = true)
    protected BigInteger count;
    @XmlAttribute
    protected BigInteger offset;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String source;
    @XmlAttribute
    protected BigInteger stride;

    /**
     * Gets the value of the count property.
     * 
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getOffset() {
        if (offset == null)
            return new BigInteger("0");
        else
            return offset;
    }

    /**
     * The accessor element may have any number of param elements. Gets the
     * value of the params property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the params property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getParams().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Param }
     */
    public List<Param> getParams() {
        if (params == null)
            params = new ArrayList<Param>();
        return params;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return possible object is {@link String }
     */
    public String getSource() {
        return source;
    }

    /**
     * Gets the value of the stride property.
     * 
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getStride() {
        if (stride == null)
            return new BigInteger("1");
        else
            return stride;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setCount(BigInteger value) {
        count = value;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setOffset(BigInteger value) {
        offset = value;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setSource(String value) {
        source = value;
    }

    /**
     * Sets the value of the stride property.
     * 
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setStride(BigInteger value) {
        stride = value;
    }

}
