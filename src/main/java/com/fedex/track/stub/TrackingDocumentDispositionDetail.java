/**
 * TrackingDocumentDispositionDetail.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.fedex.track.stub;

public class TrackingDocumentDispositionDetail  implements java.io.Serializable {
    private com.fedex.track.stub.TrackingDocumentDispositionType dispositionType;

    private com.fedex.track.stub.TrackingDocumentEmailDetail EMailDetail;

    /* Specifies the information used to fax the document. */
    private com.fedex.track.stub.FaxDetail[] faxDetails;

    public TrackingDocumentDispositionDetail() {
    }

    public TrackingDocumentDispositionDetail(
           com.fedex.track.stub.TrackingDocumentDispositionType dispositionType,
           com.fedex.track.stub.TrackingDocumentEmailDetail EMailDetail,
           com.fedex.track.stub.FaxDetail[] faxDetails) {
           this.dispositionType = dispositionType;
           this.EMailDetail = EMailDetail;
           this.faxDetails = faxDetails;
    }


    /**
     * Gets the dispositionType value for this TrackingDocumentDispositionDetail.
     * 
     * @return dispositionType
     */
    public com.fedex.track.stub.TrackingDocumentDispositionType getDispositionType() {
        return dispositionType;
    }


    /**
     * Sets the dispositionType value for this TrackingDocumentDispositionDetail.
     * 
     * @param dispositionType
     */
    public void setDispositionType(com.fedex.track.stub.TrackingDocumentDispositionType dispositionType) {
        this.dispositionType = dispositionType;
    }


    /**
     * Gets the EMailDetail value for this TrackingDocumentDispositionDetail.
     * 
     * @return EMailDetail
     */
    public com.fedex.track.stub.TrackingDocumentEmailDetail getEMailDetail() {
        return EMailDetail;
    }


    /**
     * Sets the EMailDetail value for this TrackingDocumentDispositionDetail.
     * 
     * @param EMailDetail
     */
    public void setEMailDetail(com.fedex.track.stub.TrackingDocumentEmailDetail EMailDetail) {
        this.EMailDetail = EMailDetail;
    }


    /**
     * Gets the faxDetails value for this TrackingDocumentDispositionDetail.
     * 
     * @return faxDetails   * Specifies the information used to fax the document.
     */
    public com.fedex.track.stub.FaxDetail[] getFaxDetails() {
        return faxDetails;
    }


    /**
     * Sets the faxDetails value for this TrackingDocumentDispositionDetail.
     * 
     * @param faxDetails   * Specifies the information used to fax the document.
     */
    public void setFaxDetails(com.fedex.track.stub.FaxDetail[] faxDetails) {
        this.faxDetails = faxDetails;
    }

    public com.fedex.track.stub.FaxDetail getFaxDetails(int i) {
        return this.faxDetails[i];
    }

    public void setFaxDetails(int i, com.fedex.track.stub.FaxDetail _value) {
        this.faxDetails[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TrackingDocumentDispositionDetail)) return false;
        TrackingDocumentDispositionDetail other = (TrackingDocumentDispositionDetail) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dispositionType==null && other.getDispositionType()==null) || 
             (this.dispositionType!=null &&
              this.dispositionType.equals(other.getDispositionType()))) &&
            ((this.EMailDetail==null && other.getEMailDetail()==null) || 
             (this.EMailDetail!=null &&
              this.EMailDetail.equals(other.getEMailDetail()))) &&
            ((this.faxDetails==null && other.getFaxDetails()==null) || 
             (this.faxDetails!=null &&
              java.util.Arrays.equals(this.faxDetails, other.getFaxDetails())));
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
        if (getDispositionType() != null) {
            _hashCode += getDispositionType().hashCode();
        }
        if (getEMailDetail() != null) {
            _hashCode += getEMailDetail().hashCode();
        }
        if (getFaxDetails() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFaxDetails());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFaxDetails(), i);
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
        new org.apache.axis.description.TypeDesc(TrackingDocumentDispositionDetail.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingDocumentDispositionDetail"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dispositionType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "DispositionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingDocumentDispositionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EMailDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "EMailDetail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingDocumentEmailDetail"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("faxDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "FaxDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "FaxDetail"));
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
