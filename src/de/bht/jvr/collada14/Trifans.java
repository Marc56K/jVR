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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
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
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="input" type="{http://www.collada.org/2005/11/COLLADASchema}InputLocalOffset" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}p" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="count" use="required" type="{http://www.collada.org/2005/11/COLLADASchema}uint" />
 *       &lt;attribute name="material" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "inputs", "ps", "extras" })
@XmlRootElement(name = "trifans")
public class Trifans {

    @XmlElement(name = "input")
    protected List<InputLocalOffset> inputs;
    @XmlElementRef(name = "p", namespace = "http://www.collada.org/2005/11/COLLADASchema", type = JAXBElement.class)
    protected List<JAXBElement<List<BigInteger>>> ps;
    @XmlElement(name = "extra")
    protected List<Extra> extras;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(required = true)
    protected BigInteger count;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String material;

    /**
     * Gets the value of the count property.
     * 
     * @return possible object is {@link BigInteger }
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * The extra element may appear any number of times. Gets the value of the
     * extras property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the extras property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getExtras().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Extra }
     */
    public List<Extra> getExtras() {
        if (extras == null)
            extras = new ArrayList<Extra>();
        return extras;
    }

    /**
     * Gets the value of the inputs property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the inputs property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getInputs().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InputLocalOffset }
     */
    public List<InputLocalOffset> getInputs() {
        if (inputs == null)
            inputs = new ArrayList<InputLocalOffset>();
        return inputs;
    }

    /**
     * Gets the value of the material property.
     * 
     * @return possible object is {@link String }
     */
    public String getMaterial() {
        return material;
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
     * The trifans element may have any number of p elements. Gets the value of
     * the ps property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the ps property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getPS().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link List }{@code <}{@link BigInteger }
     * {@code >}{@code >}
     */
    public List<JAXBElement<List<BigInteger>>> getPS() {
        if (ps == null)
            ps = new ArrayList<JAXBElement<List<BigInteger>>>();
        return ps;
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
     * Sets the value of the material property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setMaterial(String value) {
        material = value;
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
