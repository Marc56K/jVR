//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2010.03.05 at 12:27:35 PM MEZ
//

package de.bht.jvr.collada14;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}source" maxOccurs="unbounded"/>
 *         &lt;element name="control_vertices">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="input" type="{http://www.collada.org/2005/11/COLLADASchema}InputLocal" maxOccurs="unbounded"/>
 *                   &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="closed" type="{http://www.collada.org/2005/11/COLLADASchema}bool" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "sources", "controlVertices", "extras" })
@XmlRootElement(name = "spline")
public class Spline {

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
     *         &lt;element name="input" type="{http://www.collada.org/2005/11/COLLADASchema}InputLocal" maxOccurs="unbounded"/>
     *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "inputs", "extras" })
    public static class ControlVertices {

        @XmlElement(name = "input", required = true)
        protected List<InputLocal> inputs;
        @XmlElement(name = "extra")
        protected List<Extra> extras;

        /**
         * The extra element may appear any number of times. Gets the value of
         * the extras property.
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the extras property.
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getExtras().add(newItem);
         * </pre>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Extra }
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
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the inputs property.
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getInputs().add(newItem);
         * </pre>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link InputLocal }
         */
        public List<InputLocal> getInputs() {
            if (inputs == null)
                inputs = new ArrayList<InputLocal>();
            return inputs;
        }

    }

    @XmlElement(name = "source", required = true)
    protected List<Source> sources;
    @XmlElement(name = "control_vertices", required = true)
    protected Spline.ControlVertices controlVertices;
    @XmlElement(name = "extra")
    protected List<Extra> extras;

    @XmlAttribute
    protected Boolean closed;

    /**
     * Gets the value of the controlVertices property.
     * 
     * @return possible object is {@link Spline.ControlVertices }
     */
    public Spline.ControlVertices getControlVertices() {
        return controlVertices;
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
     * The mesh element must contain one or more source elements. Gets the value
     * of the sources property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the sources property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getSources().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Source }
     */
    public List<Source> getSources() {
        if (sources == null)
            sources = new ArrayList<Source>();
        return sources;
    }

    /**
     * Gets the value of the closed property.
     * 
     * @return possible object is {@link Boolean }
     */
    public boolean isClosed() {
        if (closed == null)
            return false;
        else
            return closed;
    }

    /**
     * Sets the value of the closed property.
     * 
     * @param value
     *            allowed object is {@link Boolean }
     */
    public void setClosed(Boolean value) {
        closed = value;
    }

    /**
     * Sets the value of the controlVertices property.
     * 
     * @param value
     *            allowed object is {@link Spline.ControlVertices }
     */
    public void setControlVertices(Spline.ControlVertices value) {
        controlVertices = value;
    }

}
