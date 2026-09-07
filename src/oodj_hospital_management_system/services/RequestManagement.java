/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.Request;
import oodj_hospital_management_system.models.Request.RequestStatus;
import oodj_hospital_management_system.models.Request.RequestType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.ErrorHandler;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.utils.exceptions.RequestException;
import oodj_hospital_management_system.utils.exceptions.UserException;

/**
 *
 * @author sheaw
 */
public class RequestManagement {
    private static final FileHandler requestHandler = new FileHandler(SystemConfiguration.requestFilePath);
    private static final List<Request> requests = new ArrayList<>();
    private User activeUser;

    public RequestManagement(User usr) {
        this.activeUser = usr;
        loadRequests();
    }

    public static void loadRequests() {
        requests.clear();
        List<LinkedHashMap<String, String>> rawData = requestHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // requestId,requestType,requesterId,requesterType,targetId,referenceId,reason,status,createdDateTime,resolvedDateTime,resolverId
            String targetId = row.get("targetId");
            String resolverId = row.get("resolverId");
            Request request = new Request(
                    row.get("requestId"),
                    getRequestType(row.get("requestType")),
                    UserManagement.getUserById(row.get("requesterId")),
                    (targetId == null || targetId.isBlank()) ? null : UserManagement.getUserById(targetId),
                    row.get("referenceId"),
                    row.get("reason"),
                    getRequestStatus(row.get("status")),
                    row.get("createdDateTime"),
                    row.get("resolvedDateTime"),
                    (resolverId == null || resolverId.isBlank()) ? null : UserManagement.getUserById(resolverId)
            );
            requests.add(request);
        }
    }

    public static List<Request> getAllRequests() {
        loadRequests();
        return requests;
    }

    public static Request getRequestById(String requestId) {
        loadRequests();
        try {
            if (!requests.stream().anyMatch(request -> request.getRequestId().equals(requestId))) {
                throw new RequestException(String.format("Invalid Request ID: Request ID '%s' is not found", requestId));
            }
            for (Request request : requests) {
                if (request.getRequestId().equals(requestId)) {
                    return request;
                }
            }
            return null;
        } catch (RequestException e) {
            System.out.println("Error finding request -> " + e.getMessage());
            Logger.errorLog("Error finding request -> " + e.getMessage());
            return null;
        }
    }

    public static RequestType getRequestType(String type) {
        RequestType requestType = null;
        switch (type) {
            case "CANCEL_SHIFT": requestType = RequestType.CANCEL_SHIFT; break;
            case "ASSET_REQUEST": requestType = RequestType.ASSET_REQUEST; break;
            default: System.out.println("Unknown Request Type");
        }
        return requestType;
    }

    public static RequestStatus getRequestStatus(String status) {
        RequestStatus requestStatus = null;
        switch (status) {
            case "PENDING": requestStatus = RequestStatus.PENDING; break;
            case "APPROVED": requestStatus = RequestStatus.APPROVED; break;
            case "REJECTED": requestStatus = RequestStatus.REJECTED; break;
            case "WITHDRAWN": requestStatus = RequestStatus.WITHDRAWN; break;
            default: System.out.println("Unknown Request Status");
        }
        return requestStatus;
    }

    // File Handler (Write and Append)
    private static boolean writeRequests() {
        ArrayList<LinkedHashMap<String, String>> requestMaps = new ArrayList<>();
        for (Request request : requests) {
            requestMaps.add(request.getRequestMap());
        }
        requestHandler.writeFile(requestMaps);
        return true;
    }

    private static boolean appendRequest(Request request) {
        try {
            ArrayList<LinkedHashMap<String, String>> requestMaps = new ArrayList<>();
            requestMaps.add(request.getRequestMap());
            if (!requestHandler.checkFileExistence() && !requestHandler.createFile()) {
                System.out.printf("File 'requests.txt' failed to be created, request '%s' cannot be appended%n", request.getRequestId());
                return false;
            }
            requestHandler.appendFile(requestMaps);
            System.out.printf("Request '%s' is appended to requests.txt!%n", request.getRequestId());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new request: " + e.getMessage());
            Logger.errorLog("Error appending new request (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }

    // Create - generic entrypoint used by all 3 request types
    public LinkedHashMap<String, String> createRequest(RequestType type, User target, String referenceId, String reason) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.requestErrorHandler(Arrays.asList("requester", "requestType", "referenceId", "reason"), activeUser, type, referenceId, reason);
        } catch (RequestException e) {
            System.out.println("Error creating request\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not created\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            System.out.println("Error creating request\n" + ue.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not created\n" + ue.getMessage());
            return msgMap;
        }

        String newRequestId = generateRequestId();
        String now = DateTime.currentDateTime().toString();
        Request newRequest = new Request(newRequestId, type, activeUser, target, referenceId, reason, RequestStatus.PENDING, now, null, null);
        requests.add(newRequest);
        if (requestHandler.checkFileExistence()) {
            appendRequest(newRequest);
        } else {
            writeRequests();
        }
        loadRequests();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Request '%s' is submitted successfully!", newRequestId));
        msgMap.put("newRequestId", newRequestId);
        return msgMap;
    }
    
    public LinkedHashMap<String, String> updateRequest(Request request, String referenceId, String reason) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            ErrorHandler.requestErrorHandler(Arrays.asList("update request", "referenceId", "reason"), request, referenceId, reason);
        } catch (RequestException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not updated\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not updated\n" + ue.getMessage());
            return msgMap;
        }

        Request targetRequest = getRequestById(request.getRequestId());
        if (targetRequest == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not found");
            return msgMap;
        }

        if (!RequestStatus.PENDING.equals(targetRequest.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Request '%s' has already been resolved and cannot be modified!", targetRequest.getRequestId()));
            return msgMap;
        }

        targetRequest.setReferenceId(referenceId);
        targetRequest.setReason(reason);
        writeRequests();
        loadRequests();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Request '%s' is updated successfully!", targetRequest.getRequestId()));
        return msgMap;
    }

    public LinkedHashMap<String, String> withdrawRequest(Request request) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            ErrorHandler.requestErrorHandler(Arrays.asList("withdraw request"), request);
        } catch (RequestException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not withdrawn\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not withdrawn\n" + ue.getMessage());
            return msgMap;
        }

        Request targetRequest = getRequestById(request.getRequestId());
        if (targetRequest == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not found");
            return msgMap;
        }

        if (!RequestStatus.PENDING.equals(targetRequest.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Request '%s' has already been resolved and cannot be withdrawn!", targetRequest.getRequestId()));
            return msgMap;
        }

        // Withdrawn is a distinct outcome from Rejected (requester pulled it, not staff decision)
        targetRequest.setStatus(RequestStatus.WITHDRAWN);
        targetRequest.setResolvedDateTime(DateTime.currentDateTime().toString());
        targetRequest.setResolver(targetRequest.getRequester());
        writeRequests();
        loadRequests();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Request '%s' is withdrawn!", targetRequest.getRequestId()));
        return msgMap;
    }

    // Approve - dispatches to the right downstream service based on request type
    public LinkedHashMap<String, String> approveRequest(Request request) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            ErrorHandler.requestErrorHandler(Arrays.asList("resolve request"), request);
        } catch (RequestException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not approved\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not approved\n" + ue.getMessage());
            return msgMap;
        }

        Request targetRequest = getRequestById(request.getRequestId()); // re-fetch live reference
        if (targetRequest == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not found");
            return msgMap;
        }

        LinkedHashMap<String, String> downstreamResult;
        switch (targetRequest.getRequestType()) {
            case CANCEL_SHIFT:
                DoctorShift shift = DoctorManagement.getDoctorShiftById(targetRequest.getReferenceId());
                DoctorManagement doctorManagement = new DoctorManagement(activeUser);
                downstreamResult = doctorManagement.cancelShift(shift);
                break;
            case ASSET_REQUEST:
                downstreamResult = new LinkedHashMap<>();
                downstreamResult.put("success", "true");
                break;
            default:
                downstreamResult = new LinkedHashMap<>();
                downstreamResult.put("success", null);
                downstreamResult.put("msg", "Unknown request type");
        }

        if (!"true".equals(downstreamResult.get("success"))) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Request '%s' could not be approved.\n%s", targetRequest.getRequestId(), downstreamResult.get("msg")));
            return msgMap;
        }

        targetRequest.setStatus(RequestStatus.APPROVED);
        targetRequest.setResolvedDateTime(DateTime.currentDateTime().toString());
        targetRequest.setResolver(activeUser);
        writeRequests();
        loadRequests();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Request '%s' is approved!\n%s", targetRequest.getRequestId(), downstreamResult.get("msg")));
        return msgMap;
    }
    
    public LinkedHashMap<String, String> rejectRequest(Request request, String reason) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            ErrorHandler.requestErrorHandler(Arrays.asList("resolve request"), request);
        } catch (RequestException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not rejected\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not rejected\n" + ue.getMessage());
            return msgMap;
        }

        Request targetRequest = getRequestById(request.getRequestId());
        if (targetRequest == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Request is not found");
            return msgMap;
        }

        targetRequest.setStatus(RequestStatus.REJECTED);
        targetRequest.setResolvedDateTime(DateTime.currentDateTime().toString());
        targetRequest.setResolver(activeUser);
        writeRequests();
        loadRequests();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Request '%s' is rejected!", targetRequest.getRequestId()));
        return msgMap;
    }

    // Searching and Sorting
    public List<Request> searchRequests(String keywords) {
        List<Request> matched = new ArrayList<>();
        for (Request request : requests) {
            if (request.getRequestId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || request.getRequestType().toString().toLowerCase().contains(keywords.toLowerCase().trim())
                    || request.getRequester().getName().toLowerCase().contains(keywords.toLowerCase().trim())
                    || request.getStatus().toString().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matched.add(request);
            }
        }
        return matched;
    }

    public List<Request> searchRequests(String keywords, List<Request> requestList) {
        List<Request> matched = new ArrayList<>();
        for (Request request : requestList) {
            if (request.getRequestId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || request.getRequestType().toString().toLowerCase().contains(keywords.toLowerCase().trim())
                    || request.getRequester().getName().toLowerCase().contains(keywords.toLowerCase().trim())
                    || request.getStatus().toString().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matched.add(request);
            }
        }
        return matched;
    }

    public List<Request> sortRequestsById(List<Request> list) {
        List<Request> sorted = list;
        sorted.sort(Comparator.comparing(Request::getRequestId));
        return sorted;
    }

    public List<Request> sortRequestsByCreatedDateTime(List<Request> list) {
        List<Request> sorted = list;
        sorted.sort(Comparator.comparing(request -> DateTime.toDateTimeObject(request.getCreatedDateTime())));
        return sorted;
    }

    // Filtering helpers - likely useful for "requests waiting on me" views
    public static List<Request> getPendingRequestsByType(RequestType type) {
        loadRequests();
        return requests.stream()
                .filter(r -> type.equals(r.getRequestType()) && RequestStatus.PENDING.equals(r.getStatus()))
                .toList();
    }

    public static List<Request> getRequestsByRequester(User requester) {
        loadRequests();
        return requests.stream()
                .filter(r -> requester.equals(r.getRequester()))
                .toList();
    }

    // Generate ID
    public static String generateRequestId() {
        List<String> existingIds = requests.stream().map(Request::getRequestId).toList();
        return SystemConfiguration.generateId("#REQ", existingIds);
    }
}
