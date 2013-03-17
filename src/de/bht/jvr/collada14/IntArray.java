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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.collada.org/2005/11/COLLADASchema>ListOfInts">
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="count" use="required" type="{http://www.collada.org/2005/11/COLLADASchema}uint" />
 *       &lt;attribute name="minInclusive" type="{http://www.w3.org/2001/XMLSchema}integer" default="-2147483648" />
 *       &lt;attribute name="maxInclusive" type="{http://www.w3.org/2001/XMLSchema}integer" default="2147483647" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "values" })
@XmlRootElement(name = "int_array")
public class IntArray {

    @XmlValue
    protected List<Long> values;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(required = true)
    protected BigInteger count;
    @XmlAttribute
    protected BigInteger minInclusive;
    @XmlAttribute
    protected BigInteger maxInclusive;

    /**
     * Gets the value of the count property.
     * 
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return possible object is {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the value of the maxInclusive property.
     * 
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getMaxInclusive() {
        if (maxInclusive == null)
            return new BigInteger("2147483647");
        else
            return maxInclusive;
    }

    /**
     * Gets the value of the minInclusive property.
     * 
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getMinInclusive() {
        if (minInclusive == null)
            return new BigInteger("-2147483648");
        else
            return minInclusive;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the values property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the values property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getValues().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Long }
     */
    public List<Long> getValues() {
        if (values == null)
            values = new ArrayList<Long>();
        return values;
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
     * Sets the value of the id property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setId(String value) {
        id = value;
    }

    /**
     * Sets the value of the maxInclusive property.
     * 
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setMaxInclusive(BigInteger value) {
        maxInclusive = value;
    }

    /**
     * Sets the value of the minInclusive property.
     * 
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setMinInclusive(BigInteger value) {
        minInclusive = value;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setName(String value) {
        name = value;
    }

}
