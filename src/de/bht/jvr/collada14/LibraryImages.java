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
import javax.xml.bind.annotation.XmlID;
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
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}asset" minOccurs="0"/>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}image" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.collada.org/2005/11/COLLADASchema}extra" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "asset", "images", "extras" })
@XmlRootElement(name = "library_images")
public class LibraryImages {

    protected Asset asset;
    @XmlElement(name = "image", required = true)
    protected List<Image> images;
    @XmlElement(name = "extra")
    protected List<Extra> extras;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    /**
     * The library_images element may contain an asset element.
     * 
     * @return possible object is {@link Asset }
     */
    public Asset getAsset() {
        return asset;
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
     * Gets the value of the id property.
     * 
     * @return possible object is {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * There must be at least one image element. Gets the value of the images
     * property.
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the images property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getImages().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Image }
     */
    public List<Image> getImages() {
        if (images == null)
            images = new ArrayList<Image>();
        return images;
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
     * Sets the value of the asset property.
     * 
     * @param value
     *            allowed object is {@link Asset }
     */
    public void setAsset(Asset value) {
        asset = value;
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
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     */
    public void setName(String value) {
        name = value;
    }

}
