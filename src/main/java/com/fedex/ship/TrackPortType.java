/**
 * TrackPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.fedex.ship;

public interface TrackPortType extends java.rmi.Remote {
    public com.fedex.ship.TrackReply track(com.fedex.ship.TrackRequest trackRequest) throws java.rmi.RemoteException;
    public com.fedex.ship.GetTrackingDocumentsReply getTrackingDocuments(com.fedex.ship.GetTrackingDocumentsRequest getTrackingDocumentsRequest) throws java.rmi.RemoteException;
    public com.fedex.ship.SendNotificationsReply sendNotifications(com.fedex.ship.SendNotificationsRequest sendNotificationsRequest) throws java.rmi.RemoteException;
}
