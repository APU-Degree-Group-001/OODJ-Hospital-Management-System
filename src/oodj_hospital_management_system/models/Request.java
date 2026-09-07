/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 *
 * @author sheaw
 */

public class Request {
    public enum RequestType { CANCEL_SHIFT, ASSET_REQUEST }
    public enum RequestStatus { PENDING, APPROVED, REJECTED, WITHDRAWN }

    private String requestId;
    private RequestType requestType;
    private User requester;
    private User target; // nullable - e.g. any admin staff can act on ASSET_REQUEST
    private String referenceId; // shiftId / assetId  depending on type
    private String reason;
    private RequestStatus status;
    private String createdDateTime;
    private String resolvedDateTime;
    private User resolver; // who approved/rejected it

    public Request(
            String requestId,
            RequestType requestType,
            User requester,
            User target,
            String referenceId,
            String reason,
            RequestStatus status,
            String createdDateTime,
            String resolvedDateTime,
            User resolver
    ) {
        this.requestId = requestId;
        this.requestType = requestType;
        this.requester = requester;
        this.target = target;
        this.referenceId = referenceId;
        this.reason = reason;
        this.status = status;
        this.createdDateTime = createdDateTime;
        this.resolvedDateTime = resolvedDateTime;
        this.resolver = resolver;
    }

    @Override
    public String toString() {
        return String.format("Request {%s - %s - %s}", this.requestId, this.requestType, this.status);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Request)) return false;
        if (obj == this) return true;
        Request reqObj = (Request) obj;
        return Objects.equals(this.requestId, reqObj.requestId);
    }

    // Getters
    public String getRequestId() { 
        return requestId; 
    }
    
    public RequestType getRequestType() { 
        return requestType; 
    }
    
    public User getRequester() {
        return requester; 
    }
    
    public User getTarget() { 
        return target; 
    }
    
    public String getReferenceId() { 
        return referenceId; 
    }
    
    public String getReason() { 
        return reason; 
    }
    
    public RequestStatus getStatus() { 
        return status; 
    }
    
    public String getCreatedDateTime() {
        return createdDateTime; 
    }
    
    public String getResolvedDateTime() { 
        return resolvedDateTime; 
    }
    
    public User getResolver() { 
        return resolver; 
    }

    // Setters
    public void setStatus(RequestStatus status) { 
        this.status = status; 
    }
    
    public void setReferenceId(String referenceId) { 
        this.referenceId = referenceId; 
    }

    public void setReason(String reason) { 
        this.reason = reason; 
    }
    
    public void setResolvedDateTime(String resolvedDateTime) { 
        this.resolvedDateTime = resolvedDateTime; 
    }
    
    public void setResolver(User resolver) { 
        this.resolver = resolver; 
    }

    public LinkedHashMap<String, String> getRequestMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("requestId", requestId);
        map.put("requestType", requestType.toString());
        map.put("requesterId", requester.getId());
        map.put("requesterType", requester.getType());
        map.put("targetId", target != null ? target.getId() : "");
        map.put("referenceId", referenceId);
        map.put("reason", reason);
        map.put("status", status.toString());
        map.put("createdDateTime", createdDateTime);
        map.put("resolvedDateTime", resolvedDateTime != null ? resolvedDateTime : "");
        map.put("resolverId", resolver != null ? resolver.getId() : "");
        return map;
    }
}
