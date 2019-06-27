/**
 * TrackingDocument.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.fedex.track.stub;

public class TrackingDocument  implements java.io.Serializable {
    private com.fedex.track.stub.TrackingDocumentType type;

    private com.fedex.track.stub.Localization[] localizations;

    private com.fedex.track.stub.TrackingDocumentImageType imageType;

    private org.apache.axis.types.NonNegativeInteger resolution;

    private com.fedex.track.stub.DocumentPart[] parts;

    public TrackingDocument() {
    }

    public TrackingDocument(
           com.fedex.track.stub.TrackingDocumentType type,
           com.fedex.track.stub.Localization[] localizations,
           com.fedex.track.stub.TrackingDocumentImageType imageType,
           org.apache.axis.types.NonNegativeInteger resolution,
           com.fedex.track.stub.DocumentPart[] parts) {
           this.type = type;
           this.localizations = localizations;
           this.imageType = imageType;
           this.resolution = resolution;
           this.parts = parts;
    }


    /**
     * Gets the type value for this TrackingDocument.
     * 
     * @return type
     */
    public com.fedex.track.stub.TrackingDocumentType getType() {
        return type;
    }


    /**
     * Sets the type value for this TrackingDocument.
     * 
     * @param type
     */
    public void setType(com.fedex.track.stub.TrackingDocumentType type) {
        this.type = type;
    }


    /**
     * Gets the localizations value for this TrackingDocument.
     * 
     * @return localizations
     */
    public com.fedex.track.stub.Localization[] getLocalizations() {
        return localizations;
    }


    /**
     * Sets the localizations value for this TrackingDocument.
     * 
     * @param localizations
     */
    public void setLocalizations(com.fedex.track.stub.Localization[] localizations) {
        this.localizations = localizations;
    }

    public com.fedex.track.stub.Localization getLocalizations(int i) {
        return this.localizations[i];
    }

    public void setLocalizations(int i, com.fedex.track.stub.Localization _value) {
        this.localizations[i] = _value;
    }


    /**
     * Gets the imageType value for this TrackingDocument.
     * 
     * @return imageType
     */
    public com.fedex.track.stub.TrackingDocumentImageType getImageType() {
        return imageType;
    }


    /**
     * Sets the imageType value for this TrackingDocument.
     * 
     * @param imageType
     */
    public void setImageType(com.fedex.track.stub.TrackingDocumentImageType imageType) {
        this.imageType = imageType;
    }


    /**
     * Gets the resolution value for this TrackingDocument.
     * 
     * @return resolution
     */
    public org.apache.axis.types.NonNegativeInteger getResolution() {
        return resolution;
    }


    /**
     * Sets the resolution value for this TrackingDocument.
     * 
     * @param resolution
     */
    public void setResolution(org.apache.axis.types.NonNegativeInteger resolution) {
        this.resolution = resolution;
    }


    /**
     * Gets the parts value for this TrackingDocument.
     * 
     * @return parts
     */
    public com.fedex.track.stub.DocumentPart[] getParts() {
        return parts;
    }


    /**
     * Sets the parts value for this TrackingDocument.
     * 
     * @param parts
     */
    public void setParts(com.fedex.track.stub.DocumentPart[] parts) {
        this.parts = parts;
    }

    public com.fedex.track.stub.DocumentPart getParts(int i) {
        return this.parts[i];
    }

    public void setParts(int i, com.fedex.track.stub.DocumentPart _value) {
        this.parts[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TrackingDocument)) return false;
        TrackingDocument other = (TrackingDocument) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.localizations==null && other.getLocalizations()==null) || 
             (this.localizations!=null &&
              java.util.Arrays.equals(this.localizations, other.getLocalizations()))) &&
            ((this.imageType==null && other.getImageType()==null) || 
             (this.imageType!=null &&
              this.imageType.equals(other.getImageType()))) &&
            ((this.resolution==null && other.getResolution()==null) || 
             (this.resolution!=null &&
              this.resolution.equals(other.getResolution()))) &&
            ((this.parts==null && other.getParts()==null) || 
             (this.parts!=null &&
              java.util.Arrays.equals(this.parts, other.getParts())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getLocalizations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLocalizations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLocalizations(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getImageType() != null) {
            _hashCode += getImageType().hashCode();
        }
        if (getResolution() != null) {
            _hashCode += getResolution().hashCode();
        }
        if (getParts() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParts());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParts(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TrackingDocument.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingDocument"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingDocumentType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localizations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "Localizations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "Localization"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "ImageType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingDocumentImageType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resolution");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "Resolution"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "Parts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "DocumentPart"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
