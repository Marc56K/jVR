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
import javax.xml.bind.annotation.XmlList;
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
 *         &lt;element name="bind_shape_matrix" type="{http://www.collada.org/2005/11/COLLADASchema}float4x4" minOccurs="0"/>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}source" maxOccurs="unbounded" minOccurs="3"/>
 *         &lt;element name="joints">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="input" type="{http://www.collada.org/2005/11/COLLADASchema}InputLocal" maxOccurs="unbounded" minOccurs="2"/>
 *                   &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="vertex_weights">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="input" type="{http://www.collada.org/2005/11/COLLADASchema}InputLocalOffset" maxOccurs="unbounded" minOccurs="2"/>
 *                   &lt;element name="vcount" type="{http://www.collada.org/2005/11/COLLADASchema}ListOfUInts" minOccurs="0"/>
 *                   &lt;element name="v" type="{http://www.collada.org/2005/11/COLLADASchema}ListOfInts" minOccurs="0"/>
 *                   &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="count" use="required" type="{http://www.collada.org/2005/11/COLLADASchema}uint" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="source" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "bindShapeMatrix", "sources", "joints", "vertexWeights", "extras" })
@XmlRootElement(name = "skin")
public class Skin {

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
     *         &lt;element name="input" type="{http://www.collada.org/2005/11/COLLADASchema}InputLocal" maxOccurs="unbounded" minOccurs="2"/>
     *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "inputs", "extras" })
    public static class Joints {

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
     *         &lt;element name="input" type="{http://www.collada.org/2005/11/COLLADASchema}InputLocalOffset" maxOccurs="unbounded" minOccurs="2"/>
     *         &lt;element name="vcount" type="{http://www.collada.org/2005/11/COLLADASchema}ListOfUInts" minOccurs="0"/>
     *         &lt;element name="v" type="{http://www.collada.org/2005/11/COLLADASchema}ListOfInts" minOccurs="0"/>
     *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="count" use="required" type="{http://www.collada.org/2005/11/COLLADASchema}uint" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "inputs", "vcount", "v", "extras" })
    public static class VertexWeights {

        @XmlElement(name = "input", required = true)
        protected List<InputLocalOffset> inputs;
        @XmlList
        protected List<BigInteger> vcount;
        @XmlList
        @XmlElement(type = Long.class)
        protected List<Long> v;
        @XmlElement(name = "extra")
        protected List<Extra> extras;
        @XmlAttribute(required = true)
        protected BigInteger count;

        /**
         * Gets the value of the count property.
         * 
         * @return possible object is {@link BigInteger }
         */
        public BigInteger getCount() {
            return count;
        }

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
         * {@link InputLocalOffset }
         */
        public List<InputLocalOffset> getInputs() {
            if (inputs == null)
                inputs = new ArrayList<InputLocalOffset>();
            return inputs;
        }

        /**
         * Gets the value of the v property.
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the v property.
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getV().add(newItem);
         * </pre>
         * <p>
         * Objects of the following type(s) are allowed in the list {@link Long }
         */
        public List<Long> getV() {
            if (v == null)
                v = new ArrayList<Long>();
            return v;
        }

        /**
         * Gets the value of the vcount property.
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the vcount property.
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getVcount().add(newItem);
         * </pre>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BigInteger }
         */
        public List<BigInteger> getVcount() {
            if (vcount == null)
                vcount = new ArrayList<BigInteger>();
            return vcount;
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

    }

    @XmlList
    @XmlElement(name = "bind_shape_matrix", type = Double.class)
    protected List<Double> bindShapeMatrix;
    @XmlElement(name = "source", required = true)
    protected List<Source> sources;
    @XmlElement(required = true)
    protected Skin.Joints joints;
    @XmlElement(name = "vertex_weights", required = true)
    protected Skin.VertexWeights vertexWeights;

    @XmlElement(name = "extra")
    protected List<Extra> extras;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String source;

    /**
     * Gets the value of the bindShapeMatrix property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the bindShapeMatrix property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getBindShapeMatrix().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Double }
     */
    public List<Double> getBindShapeMatrix() {
        if (bindShapeMatrix == null)
            bindShapeMatrix = new ArrayList<Double>();
        return bindShapeMatrix;
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
     * Gets the value of the joints property.
     * 
     * @return possible object is {@link Skin.Joints }
     */
    public Skin.Joints getJoints() {
        return joints;
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
     * The skin element must contain at least three source elements. Gets the
     * value of the sources property.
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
     * Gets the value of the vertexWeights property.
     * 
     * @return possible object is {@link Skin.VertexWeights }
     */
    public Skin.VertexWeights getVertexWeights() {
        return vertexWeights;
    }

    /**
     * Sets the value of the joints property.
     * 
     * @param value
     *            allowed object is {@link Skin.Joints }
     */
    public void setJoints(Skin.Joints value) {
        joints = value;
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
     * Sets the value of the vertexWeights property.
     * 
     * @param value
     *            allowed object is {@link Skin.VertexWeights }
     */
    public void setVertexWeights(Skin.VertexWeights value) {
        vertexWeights = value;
    }

}