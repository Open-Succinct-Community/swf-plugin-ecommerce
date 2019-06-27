/**
 * TrackPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.fedex.track.stub;

public interface TrackPortType extends java.rmi.Remote {
    public com.fedex.track.stub.TrackReply track(com.fedex.track.stub.TrackRequest trackRequest) throws java.rmi.RemoteException;
    public com.fedex.track.stub.GetTrackingDocumentsReply getTrackingDocuments(com.fedex.track.stub.GetTrackingDocumentsRequest getTrackingDocumentsRequest) throws java.rmi.RemoteException;
    public com.fedex.track.stub.SendNotificationsReply sendNotifications(com.fedex.track.stub.SendNotificationsRequest sendNotificationsRequest) throws java.rmi.RemoteException;
}
