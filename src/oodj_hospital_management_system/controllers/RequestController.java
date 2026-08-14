/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.Request;
import oodj_hospital_management_system.models.Request.RequestType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.RequestManagement;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author paure
 */
public class RequestController {
    private RequestManagement requestServices;
    private User activeUser;

    public RequestController(User activeUser) {
        this.activeUser = activeUser;
        this.requestServices = new RequestManagement(activeUser);
    }
    
    // Get All Requests
    public List<Request> getAllRequests() {
        return RequestManagement.getAllRequests();
    }
 
    // Lookup by ID
    public Request getRequestById(String requestId) {
        return RequestManagement.getRequestById(requestId);
    }
 
    public List<Request> getPendingRequestsByType(RequestType type) {
        return RequestManagement.getPendingRequestsByType(type);
    }
 
    public List<Request> getRequestsByRequester(User requester) {
        return RequestManagement.getRequestsByRequester(requester);
    }
 
    // Search and Sort
    public List<Request> searchRequests(String keywords) {
        return requestServices.searchRequests(keywords);
    }
 
    public List<Request> searchRequests(String keywords, List<Request> requestList) {
        return requestServices.searchRequests(keywords, requestList);
    }
 
    public List<Request> sortRequestsById(List<Request> list) {
        return requestServices.sortRequestsById(list);
    }
 
    public List<Request> sortRequestsByCreatedDateTime(List<Request> list) {
        return requestServices.sortRequestsByCreatedDateTime(list);
    }
 
    // CRUD
    public LinkedHashMap<String, String> createRequest(RequestType type, User target, String referenceId, String reason) {
        LinkedHashMap<String, String> msg = requestServices.createRequest(type, target, referenceId, reason);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} submitted Request {%s} of type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newRequestId"), type));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to submit a Request of type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), type, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> updateRequest(Request request, String referenceId, String reason) {
        LinkedHashMap<String, String> msg = requestServices.updateRequest(request, referenceId, reason);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated Request {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    request != null ? request.getRequestId() : "?"));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update Request {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    request != null ? request.getRequestId() : "?", msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> withdrawRequest(Request request) {
        LinkedHashMap<String, String> msg = requestServices.withdrawRequest(request);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} withdrew Request {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    request != null ? request.getRequestId() : "?"));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to withdraw Request {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    request != null ? request.getRequestId() : "?", msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> approveRequest(Request request) {
        LinkedHashMap<String, String> msg = requestServices.approveRequest(request);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} approved Request {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    request != null ? request.getRequestId() : "?"));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to approve Request {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    request != null ? request.getRequestId() : "?", msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> rejectRequest(Request request, String reason) {
        LinkedHashMap<String, String> msg = requestServices.rejectRequest(request, reason);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} rejected Request {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    request != null ? request.getRequestId() : "?"));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to reject Request {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    request != null ? request.getRequestId() : "?", msg.get("msg")));
        }
        return msg;
    }
}
