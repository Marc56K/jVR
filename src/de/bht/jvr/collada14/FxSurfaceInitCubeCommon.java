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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for fx_surface_init_cube_common complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="fx_surface_init_cube_common">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="all">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="primary">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element name="order" type="{http://www.collada.org/2005/11/COLLADASchema}fx_surface_face_enum" maxOccurs="6" minOccurs="6"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="face" maxOccurs="6" minOccurs="6">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fx_surface_init_cube_common", propOrder = { "faces", "primary", "all" })
public class FxSurfaceInitCubeCommon {

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
     *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class All {

        @XmlAttribute(required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object ref;

        /**
         * Gets the value of the ref property.
         * 
         * @return possible object is {@link Object }
         */
        public Object getRef() {
            return ref;
        }

        /**
         * Sets the value of the ref property.
         * 
         * @param value
         *            allowed object is {@link Object }
         */
        public void setRef(Object value) {
            ref = value;
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
     *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Face {

        @XmlAttribute(required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object ref;

        /**
         * Gets the value of the ref property.
         * 
         * @return possible object is {@link Object }
         */
        public Object getRef() {
            return ref;
        }

        /**
         * Sets the value of the ref property.
         * 
         * @param value
         *            allowed object is {@link Object }
         */
        public void setRef(Object value) {
            ref = value;
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
     *       &lt;sequence minOccurs="0">
     *         &lt;element name="order" type="{http://www.collada.org/2005/11/COLLADASchema}fx_surface_face_enum" maxOccurs="6" minOccurs="6"/>
     *       &lt;/sequence>
     *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "orders" })
    public static class Primary {

        @XmlElement(name = "order")
        protected List<FxSurfaceFaceEnum> orders;
        @XmlAttribute(required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object ref;

        /**
         * Gets the value of the orders property.
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the orders property.
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getOrders().add(newItem);
         * </pre>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FxSurfaceFaceEnum }
         */
        public List<FxSurfaceFaceEnum> getOrders() {
            if (orders == null)
                orders = new ArrayList<FxSurfaceFaceEnum>();
            return orders;
        }

        /**
         * Gets the value of the ref property.
         * 
         * @return possible object is {@link Object }
         */
        public Object getRef() {
            return ref;
        }

        /**
         * Sets the value of the ref property.
         * 
         * @param value
         *            allowed object is {@link Object }
         */
        public void setRef(Object value) {
            ref = value;
        }

    }

    @XmlElement(name = "face")
    protected List<FxSurfaceInitCubeCommon.Face> faces;

    protected FxSurfaceInitCubeCommon.Primary primary;

    protected FxSurfaceInitCubeCommon.All all;

    /**
     * Gets the value of the all property.
     * 
     * @return possible object is {@link FxSurfaceInitCubeCommon.All }
     */
    public FxSurfaceInitCubeCommon.All getAll() {
        return all;
    }

    /**
     * Gets the value of the faces property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the faces property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getFaces().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FxSurfaceInitCubeCommon.Face }
     */
    public List<FxSurfaceInitCubeCommon.Face> getFaces() {
        if (faces == null)
            faces = new ArrayList<FxSurfaceInitCubeCommon.Face>();
        return faces;
    }

    /**
     * Gets the value of the primary property.
     * 
     * @return possible object is {@link FxSurfaceInitCubeCommon.Primary }
     */
    public FxSurfaceInitCubeCommon.Primary getPrimary() {
        return primary;
    }

    /**
     * Sets the value of the all property.
     * 
     * @param value
     *            allowed object is {@link FxSurfaceInitCubeCommon.All }
     */
    public void setAll(FxSurfaceInitCubeCommon.All value) {
        all = value;
    }

    /**
     * Sets the value of the primary property.
     * 
     * @param value
     *            allowed object is {@link FxSurfaceInitCubeCommon.Primary }
     */
    public void setPrimary(FxSurfaceInitCubeCommon.Primary value) {
        primary = value;
    }

}
