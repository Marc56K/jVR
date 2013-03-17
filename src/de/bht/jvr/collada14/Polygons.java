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
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlList;
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
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}p"/>
 *           &lt;element name="ph">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}p"/>
 *                     &lt;element name="h" type="{http://www.collada.org/2005/11/COLLADASchema}ListOfUInts" maxOccurs="unbounded"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
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
@XmlType(name = "", propOrder = { "inputs", "psAndPhs", "extras" })
@XmlRootElement(name = "polygons")
public class Polygons {

    /**
     * <p>
     * Java class for anonymous complex type.
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}p"/>
     *         &lt;element name="h" type="{http://www.collada.org/2005/11/COLLADASchema}ListOfUInts" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "p", "hs" })
    public static class Ph {

        @XmlList
        @XmlElement(required = true)
        protected List<BigInteger> p;
        @XmlElementRef(name = "h", namespace = "http://www.collada.org/2005/11/COLLADASchema", type = JAXBElement.class)
        protected List<JAXBElement<List<BigInteger>>> hs;

        /**
         * Gets the value of the hs property.
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the hs property.
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getHS().add(newItem);
         * </pre>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link JAXBElement }{@code <}{@link List }{@code <}{@link BigInteger }
         * {@code >}{@code >}
         */
        public List<JAXBElement<List<BigInteger>>> getHS() {
            if (hs == null)
                hs = new ArrayList<JAXBElement<List<BigInteger>>>();
            return hs;
        }

        /**
         * Theere may only be one p element. Gets the value of the p property.
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the p property.
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getP().add(newItem);
         * </pre>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BigInteger }
         */
        public List<BigInteger> getP() {
            if (p == null)
                p = new ArrayList<BigInteger>();
            return p;
        }

    }

    @XmlElement(name = "input")
    protected List<InputLocalOffset> inputs;
    @XmlElementRefs({
            @XmlElementRef(name = "ph", namespace = "http://www.collada.org/2005/11/COLLADASchema", type = JAXBElement.class),
            @XmlElementRef(name = "p", namespace = "http://www.collada.org/2005/11/COLLADASchema", type = JAXBElement.class) })
    protected List<JAXBElement<?>> psAndPhs;
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
     * The p element may occur any number of times. Gets the value of the
     * psAndPhs property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the psAndPhs property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getPSAndPhs().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Polygons.Ph }{@code >}
     * {@link JAXBElement }{@code <}{@link List }{@code <}{@link BigInteger }
     * {@code >}{@code >}
     */
    public List<JAXBElement<?>> getPSAndPhs() {
        if (psAndPhs == null)
            psAndPhs = new ArrayList<JAXBElement<?>>();
        return psAndPhs;
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
