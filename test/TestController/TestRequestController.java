/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestController;

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.controllers.RequestController;
import oodj_hospital_management_system.models.Request;
import oodj_hospital_management_system.models.Request.RequestType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;
 
/**
 *
 * @author paure
 */
public class TestRequestController {
    private static RequestController requestCtrl;
 
    public static void main(String[] args) {

        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");
 
        if (adminStaff == null) {
            System.out.println("Test failed: Admin Staff '#ADMd4e8f' was not found.");
            return;
        }
 
        requestCtrl = new RequestController(adminStaff);
 
        // Test loading
//        testGetAllRequests();
 
        // Test lookup
//        testGetRequestById();
//        testGetPendingRequestsByType();
//        testGetRequestsByRequester();
 
        // Test search
//        testSearchRequests();
 
        // Test sort
//        testSortRequestsById();
//        testSortRequestsByCreatedDateTime();
 
        // Test creation
        // Uncomment when needed because these modify requests.txt AND write to the log file
//        testCreateRequest();
 
        // Test modification / lifecycle
        // Uncomment when needed because these modify requests.txt AND write to the log file
//        testUpdateRequest();
//        testWithdrawRequest();
//        testApproveRequest();
//        testRejectRequest();
    }
 
    // =========================================================
    // PRINT METHOD
    // =========================================================
 
    public static void testPrintRequests(List<Request> requestList) {
 
        System.out.printf(
                "%-10s %-14s %-10s %-20s%n",
                "Request ID", "Type", "Status", "Reason"
        );
        System.out.println("-----------------------------------------------------------------------");
 
        for (Request request : requestList) {
            System.out.printf(
                    "%-10s %-14s %-10s %-20s%n",
                    request.getRequestId(),
                    request.getRequestType(),
                    request.getStatus(),
                    request.getReason()
            );
        }
    }
 
    // =========================================================
    // LOAD TEST
    // =========================================================
 
    static void testGetAllRequests() {
 
        System.out.println("\n===== TEST GET ALL REQUESTS (via Controller) =====");
 
        List<Request> requests = requestCtrl.getAllRequests();
 
        System.out.println("Total Requests: " + requests.size());
 
        testPrintRequests(requests);
    }
 
    // =========================================================
    // LOOKUP TESTS
    // =========================================================
 
    static void testGetRequestById() {
 
        System.out.println("\n===== TEST GET REQUEST BY ID (via Controller) =====");
 
        List<Request> requests = requestCtrl.getAllRequests();
 
        if (requests.isEmpty()) {
            System.out.println("No requests available.");
            return;
        }
 
        String requestId = requests.get(0).getRequestId();
 
        Request request = requestCtrl.getRequestById(requestId);
 
        if (request != null) {
            System.out.println("Request found: " + request.getRequestId() + " | " + request.getStatus());
        } else {
            System.out.println("Test failed: Request not found.");
        }
    }
 
    static void testGetPendingRequestsByType() {
 
        System.out.println("\n===== TEST GET PENDING REQUESTS BY TYPE (via Controller) =====");
 
        List<Request> pending = requestCtrl.getPendingRequestsByType(RequestType.ASSET_REQUEST);
 
        System.out.println("Pending requests found: " + pending.size());
 
        testPrintRequests(pending);
    }
 
    static void testGetRequestsByRequester() {
 
        System.out.println("\n===== TEST GET REQUESTS BY REQUESTER (via Controller) =====");
 
        User requester = UserManagement.getDoctorById("#DOCd4e8f");
 
        List<Request> requests = requestCtrl.getRequestsByRequester(requester);
 
        System.out.println("Requests by requester: " + requests.size());
 
        testPrintRequests(requests);
    }
 
    // =========================================================
    // SEARCH TEST
    // =========================================================
 
    static void testSearchRequests() {
 
        System.out.println("\n===== TEST SEARCH REQUESTS (via Controller) =====");
 
        String keyword = "cancel";
 
        List<Request> result = requestCtrl.searchRequests(keyword);
 
        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched requests: " + result.size());
 
        testPrintRequests(result);
    }
 
    // =========================================================
    // SORT TESTS
    // =========================================================
 
    static void testSortRequestsById() {
 
        System.out.println("\n===== TEST SORT REQUESTS BY ID (via Controller) =====");
 
        List<Request> requests = requestCtrl.getAllRequests();
        List<Request> sorted = requestCtrl.sortRequestsById(requests);
 
        testPrintRequests(sorted);
    }
 
    static void testSortRequestsByCreatedDateTime() {
 
        System.out.println("\n===== TEST SORT REQUESTS BY CREATED DATETIME (via Controller) =====");
 
        List<Request> requests = requestCtrl.getAllRequests();
        List<Request> sorted = requestCtrl.sortRequestsByCreatedDateTime(requests);
 
        testPrintRequests(sorted);
    }
 
    // =========================================================
    // CREATE TEST
    // =========================================================
 
    static void testCreateRequest() {
 
        System.out.println("\n===== TEST CREATE REQUEST (via Controller) =====");
 
        User target = UserManagement.getAdminStaffById("#ADMd4e8f");
 
        LinkedHashMap<String, String> result = requestCtrl.createRequest(
                RequestType.ASSET_REQUEST,
                target,
                "#CON003",
                "Testing request creation via controller"
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Request ID : " + result.get("newRequestId"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    // =========================================================
    // MODIFY / LIFECYCLE TESTS
    // =========================================================
 
    static void testUpdateRequest() {
 
        System.out.println("\n===== TEST UPDATE REQUEST (via Controller) =====");
 
        List<Request> requests = requestCtrl.getAllRequests();
 
        if (requests.isEmpty()) {
            System.out.println("No requests available to update.");
            return;
        }
 
        Request request = requests.get(8);
 
        LinkedHashMap<String, String> result = requestCtrl.updateRequest(
                request, "#CON003", "Updated reason via controller test"
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testWithdrawRequest() {
 
        System.out.println("\n===== TEST WITHDRAW REQUEST (via Controller) =====");
 
        List<Request> requests = requestCtrl.getAllRequests();
 
        if (requests.isEmpty()) {
            System.out.println("No requests available to withdraw.");
            return;
        }
 
        Request request = requests.get(8);
 
        LinkedHashMap<String, String> result = requestCtrl.withdrawRequest(request);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testApproveRequest() {
 
        System.out.println("\n===== TEST APPROVE REQUEST (via Controller) =====");
 
        List<Request> requests = requestCtrl.getAllRequests();
 
        if (requests.isEmpty()) {
            System.out.println("No requests available to approve.");
            return;
        }
 
        Request request = requests.get(9);
 
        LinkedHashMap<String, String> result = requestCtrl.approveRequest(request);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testRejectRequest() {
 
        System.out.println("\n===== TEST REJECT REQUEST (via Controller) =====");
 
        List<Request> requests = requestCtrl.getAllRequests();
 
        if (requests.isEmpty()) {
            System.out.println("No requests available to reject.");
            return;
        }
 
        Request request = requests.get(requests.size() - 1);
 
        LinkedHashMap<String, String> result = requestCtrl.rejectRequest(request, "Rejected via controller test");
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}
