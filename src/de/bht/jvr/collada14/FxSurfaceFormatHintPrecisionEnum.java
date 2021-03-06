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
 * Java class for fx_surface_format_hint_precision_enum.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="fx_surface_format_hint_precision_enum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LOW"/>
 *     &lt;enumeration value="MID"/>
 *     &lt;enumeration value="HIGH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "fx_surface_format_hint_precision_enum")
@XmlEnum
public enum FxSurfaceFormatHintPrecisionEnum {

    /**
     * For integers this typically represents 8 bits. For floats typically 16
     * bits.
     */
    LOW,

    /**
     * For integers this typically represents 8 to 24 bits. For floats typically
     * 16 to 32 bits.
     */
    MID,

    /**
     * For integers this typically represents 16 to 32 bits. For floats
     * typically 24 to 32 bits.
     */
    HIGH;

    public static FxSurfaceFormatHintPrecisionEnum fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}
