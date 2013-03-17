//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2010.03.05 at 12:27:35 PM MEZ
//

package de.bht.jvr.collada14;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for fx_opaque_enum.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="fx_opaque_enum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="A_ONE"/>
 *     &lt;enumeration value="RGB_ZERO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "fx_opaque_enum")
@XmlEnum
public enum FxOpaqueEnum {

    /**
     * When a transparent opaque attribute is set to A_ONE, it means the
     * transparency information will be taken from the alpha channel of the
     * color, texture, or parameter supplying the value. The value of 1.0 is
     * opaque in this mode.
     */
    A_ONE,

    /**
     * When a transparent opaque attribute is set to RGB_ZERO, it means the
     * transparency information will be taken from the red, green, and blue
     * channels of the color, texture, or parameter supplying the value. Each
     * channel is modulated independently. The value of 0.0 is opaque in this
     * mode.
     */
    RGB_ZERO;

    public static FxOpaqueEnum fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}
