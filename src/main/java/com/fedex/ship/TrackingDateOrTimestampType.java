/**
 * TrackingDateOrTimestampType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.fedex.ship;

public class TrackingDateOrTimestampType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected TrackingDateOrTimestampType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _ACTUAL_DELIVERY = "ACTUAL_DELIVERY";
    public static final java.lang.String _ACTUAL_PICKUP = "ACTUAL_PICKUP";
    public static final java.lang.String _ACTUAL_TENDER = "ACTUAL_TENDER";
    public static final java.lang.String _ANTICIPATED_TENDER = "ANTICIPATED_TENDER";
    public static final java.lang.String _APPOINTMENT_DELIVERY = "APPOINTMENT_DELIVERY";
    public static final java.lang.String _ESTIMATED_DELIVERY = "ESTIMATED_DELIVERY";
    public static final java.lang.String _ESTIMATED_PICKUP = "ESTIMATED_PICKUP";
    public static final java.lang.String _ESTIMATED_RETURN_TO_STATION = "ESTIMATED_RETURN_TO_STATION";
    public static final java.lang.String _SHIP = "SHIP";
    public static final TrackingDateOrTimestampType ACTUAL_DELIVERY = new TrackingDateOrTimestampType(_ACTUAL_DELIVERY);
    public static final TrackingDateOrTimestampType ACTUAL_PICKUP = new TrackingDateOrTimestampType(_ACTUAL_PICKUP);
    public static final TrackingDateOrTimestampType ACTUAL_TENDER = new TrackingDateOrTimestampType(_ACTUAL_TENDER);
    public static final TrackingDateOrTimestampType ANTICIPATED_TENDER = new TrackingDateOrTimestampType(_ANTICIPATED_TENDER);
    public static final TrackingDateOrTimestampType APPOINTMENT_DELIVERY = new TrackingDateOrTimestampType(_APPOINTMENT_DELIVERY);
    public static final TrackingDateOrTimestampType ESTIMATED_DELIVERY = new TrackingDateOrTimestampType(_ESTIMATED_DELIVERY);
    public static final TrackingDateOrTimestampType ESTIMATED_PICKUP = new TrackingDateOrTimestampType(_ESTIMATED_PICKUP);
    public static final TrackingDateOrTimestampType ESTIMATED_RETURN_TO_STATION = new TrackingDateOrTimestampType(_ESTIMATED_RETURN_TO_STATION);
    public static final TrackingDateOrTimestampType SHIP = new TrackingDateOrTimestampType(_SHIP);
    public java.lang.String getValue() { return _value_;}
    public static TrackingDateOrTimestampType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        TrackingDateOrTimestampType enumeration = (TrackingDateOrTimestampType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static TrackingDateOrTimestampType fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TrackingDateOrTimestampType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingDateOrTimestampType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
