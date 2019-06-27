/**
 * TrackingDocumentSpecification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.fedex.track.stub;

public class TrackingDocumentSpecification  implements java.io.Serializable {
    private com.fedex.track.stub.TrackingDocumentType[] documentTypes;

    private com.fedex.track.stub.TrackingBillOfLadingDocumentDetail billOfLadingDocumentDetail;

    private com.fedex.track.stub.TrackingFreightBillingDocumentDetail freightBillingDocumentDetail;

    private com.fedex.track.stub.TrackingSignatureProofOfDeliveryDetail signatureProofOfDeliveryDetail;

    public TrackingDocumentSpecification() {
    }

    public TrackingDocumentSpecification(
           com.fedex.track.stub.TrackingDocumentType[] documentTypes,
           com.fedex.track.stub.TrackingBillOfLadingDocumentDetail billOfLadingDocumentDetail,
           com.fedex.track.stub.TrackingFreightBillingDocumentDetail freightBillingDocumentDetail,
           com.fedex.track.stub.TrackingSignatureProofOfDeliveryDetail signatureProofOfDeliveryDetail) {
           this.documentTypes = documentTypes;
           this.billOfLadingDocumentDetail = billOfLadingDocumentDetail;
           this.freightBillingDocumentDetail = freightBillingDocumentDetail;
           this.signatureProofOfDeliveryDetail = signatureProofOfDeliveryDetail;
    }


    /**
     * Gets the documentTypes value for this TrackingDocumentSpecification.
     * 
     * @return documentTypes
     */
    public com.fedex.track.stub.TrackingDocumentType[] getDocumentTypes() {
        return documentTypes;
    }


    /**
     * Sets the documentTypes value for this TrackingDocumentSpecification.
     * 
     * @param documentTypes
     */
    public void setDocumentTypes(com.fedex.track.stub.TrackingDocumentType[] documentTypes) {
        this.documentTypes = documentTypes;
    }

    public com.fedex.track.stub.TrackingDocumentType getDocumentTypes(int i) {
        return this.documentTypes[i];
    }

    public void setDocumentTypes(int i, com.fedex.track.stub.TrackingDocumentType _value) {
        this.documentTypes[i] = _value;
    }


    /**
     * Gets the billOfLadingDocumentDetail value for this TrackingDocumentSpecification.
     * 
     * @return billOfLadingDocumentDetail
     */
    public com.fedex.track.stub.TrackingBillOfLadingDocumentDetail getBillOfLadingDocumentDetail() {
        return billOfLadingDocumentDetail;
    }


    /**
     * Sets the billOfLadingDocumentDetail value for this TrackingDocumentSpecification.
     * 
     * @param billOfLadingDocumentDetail
     */
    public void setBillOfLadingDocumentDetail(com.fedex.track.stub.TrackingBillOfLadingDocumentDetail billOfLadingDocumentDetail) {
        this.billOfLadingDocumentDetail = billOfLadingDocumentDetail;
    }


    /**
     * Gets the freightBillingDocumentDetail value for this TrackingDocumentSpecification.
     * 
     * @return freightBillingDocumentDetail
     */
    public com.fedex.track.stub.TrackingFreightBillingDocumentDetail getFreightBillingDocumentDetail() {
        return freightBillingDocumentDetail;
    }


    /**
     * Sets the freightBillingDocumentDetail value for this TrackingDocumentSpecification.
     * 
     * @param freightBillingDocumentDetail
     */
    public void setFreightBillingDocumentDetail(com.fedex.track.stub.TrackingFreightBillingDocumentDetail freightBillingDocumentDetail) {
        this.freightBillingDocumentDetail = freightBillingDocumentDetail;
    }


    /**
     * Gets the signatureProofOfDeliveryDetail value for this TrackingDocumentSpecification.
     * 
     * @return signatureProofOfDeliveryDetail
     */
    public com.fedex.track.stub.TrackingSignatureProofOfDeliveryDetail getSignatureProofOfDeliveryDetail() {
        return signatureProofOfDeliveryDetail;
    }


    /**
     * Sets the signatureProofOfDeliveryDetail value for this TrackingDocumentSpecification.
     * 
     * @param signatureProofOfDeliveryDetail
     */
    public void setSignatureProofOfDeliveryDetail(com.fedex.track.stub.TrackingSignatureProofOfDeliveryDetail signatureProofOfDeliveryDetail) {
        this.signatureProofOfDeliveryDetail = signatureProofOfDeliveryDetail;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TrackingDocumentSpecification)) return false;
        TrackingDocumentSpecification other = (TrackingDocumentSpecification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.documentTypes==null && other.getDocumentTypes()==null) || 
             (this.documentTypes!=null &&
              java.util.Arrays.equals(this.documentTypes, other.getDocumentTypes()))) &&
            ((this.billOfLadingDocumentDetail==null && other.getBillOfLadingDocumentDetail()==null) || 
             (this.billOfLadingDocumentDetail!=null &&
              this.billOfLadingDocumentDetail.equals(other.getBillOfLadingDocumentDetail()))) &&
            ((this.freightBillingDocumentDetail==null && other.getFreightBillingDocumentDetail()==null) || 
             (this.freightBillingDocumentDetail!=null &&
              this.freightBillingDocumentDetail.equals(other.getFreightBillingDocumentDetail()))) &&
            ((this.signatureProofOfDeliveryDetail==null && other.getSignatureProofOfDeliveryDetail()==null) || 
             (this.signatureProofOfDeliveryDetail!=null &&
              this.signatureProofOfDeliveryDetail.equals(other.getSignatureProofOfDeliveryDetail())));
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
        if (getDocumentTypes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDocumentTypes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDocumentTypes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getBillOfLadingDocumentDetail() != null) {
            _hashCode += getBillOfLadingDocumentDetail().hashCode();
        }
        if (getFreightBillingDocumentDetail() != null) {
            _hashCode += getFreightBillingDocumentDetail().hashCode();
        }
        if (getSignatureProofOfDeliveryDetail() != null) {
            _hashCode += getSignatureProofOfDeliveryDetail().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TrackingDocumentSpecification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingDocumentSpecification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("documentTypes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "DocumentTypes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingDocumentType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billOfLadingDocumentDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "BillOfLadingDocumentDetail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingBillOfLadingDocumentDetail"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("freightBillingDocumentDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "FreightBillingDocumentDetail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingFreightBillingDocumentDetail"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureProofOfDeliveryDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "SignatureProofOfDeliveryDetail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fedex.com/ws/track/v16", "TrackingSignatureProofOfDeliveryDetail"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
