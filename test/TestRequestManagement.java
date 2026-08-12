/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.Request;
import oodj_hospital_management_system.models.Request.RequestType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.RequestManagement;
import oodj_hospital_management_system.services.UserManagement;

/**
 *
 * @author sheaw
 */
public class TestRequestManagement {

    private static RequestManagement requestMng;

    public static void main(String[] args) {

        // Get the active user once - a doctor to test requester-side operations
        User doctor = UserManagement.getDoctorById("#DOCd4e8f");

        if (doctor == null) {
            System.out.println(
                    "Test failed: Doctor '#DOCd4e8f' was not found."
            );
            return;
        }

        // Create RequestManagement once
        requestMng = new RequestManagement(doctor);

        // Test loading
//        testLoadRequests();

        // Test get by ID
//        testGetRequestById();

        // Test search
//        testSearchRequests();

        // Test sort
//        testSortRequestsById();
//        testSortRequestsByCreatedDateTime();

        // Test filtering helpers
//        testGetPendingRequestsByType();
//        testGetRequestsByRequester();

        // Test ID generation
//        testGenerateRequestId();

        // Test creation
        // Uncomment when needed because these modify requests.txt
//        testCreateRequest();

        // Test modification
        // Uncomment when needed because these modify requests.txt
//        testUpdateRequest();
//        testWithdrawRequest();

        // Test resolution
        // Uncomment when needed because these modify requests.txt (and downstream data e.g. doctorShifts.txt)
//        testApproveRequest();
//        testRejectRequest();
    }

    // =========================================================
    // PRINT METHODS
    // =========================================================

    public static void testPrintRequests(List<Request> requestList) {

        System.out.printf(
                "%-10s %-24s %-12s %-12s %-12s %-30s %-10s %-20s%n",
                "Req. ID",
                "Type",
                "Requester",
                "Target",
                "Ref. ID",
                "Reason",
                "Status",
                "Created"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------"
        );

        for (Request request : requestList) {

            System.out.printf(
                    "%-10s %-24s %-12s %-12s %-12s %-30s %-10s %-20s%n",
                    request.getRequestId(),
                    request.getRequestType(),
                    request.getRequester() != null ? request.getRequester().getId() : "-",
                    request.getTarget() != null ? request.getTarget().getId() : "-",
                    request.getReferenceId(),
                    request.getReason(),
                    request.getStatus(),
                    request.getCreatedDateTime()
            );
        }
    }

    // =========================================================
    // LOAD TEST
    // =========================================================

    static void testLoadRequests() {

        System.out.println("\n===== TEST LOAD REQUESTS =====");

        List<Request> requests =
                RequestManagement.getAllRequests();

        System.out.println("Total Requests: " + requests.size());

        testPrintRequests(requests);
    }

    // =========================================================
    // GET BY ID TEST
    // =========================================================

    static void testGetRequestById() {

        System.out.println("\n===== TEST GET REQUEST BY ID =====");

        List<Request> requests =
                RequestManagement.getAllRequests();

        if (requests.isEmpty()) {
            System.out.println("No requests available.");
            return;
        }

        String requestId = requests.get(0).getRequestId();

        Request request =
                RequestManagement.getRequestById(requestId);

        if (request != null) {

            System.out.println("Request found:");
            System.out.println("Request ID : " + request.getRequestId());
            System.out.println("Type       : " + request.getRequestType());
            System.out.println("Status     : " + request.getStatus());

        } else {
            System.out.println("Test failed: Request not found.");
        }
    }

    // =========================================================
    // SEARCH TEST
    // =========================================================

    static void testSearchRequests() {

        System.out.println("\n===== TEST SEARCH REQUESTS =====");

        String keyword = "shift";

        List<Request> result =
                requestMng.searchRequests(keyword);

        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched requests: " + result.size());

        testPrintRequests(result);
    }

    // =========================================================
    // SORT TESTS
    // =========================================================

    static void testSortRequestsById() {

        System.out.println("\n===== TEST SORT REQUESTS BY ID =====");

        List<Request> requests =
                RequestManagement.getAllRequests();

        List<Request> sorted =
                requestMng.sortRequestsById(requests);

        testPrintRequests(sorted);
    }

    static void testSortRequestsByCreatedDateTime() {

        System.out.println("\n===== TEST SORT REQUESTS BY CREATED DATETIME =====");

        List<Request> requests =
                RequestManagement.getAllRequests();

        List<Request> sorted =
                requestMng.sortRequestsByCreatedDateTime(requests);

        testPrintRequests(sorted);
    }

    // =========================================================
    // FILTERING HELPER TESTS
    // =========================================================

    static void testGetPendingRequestsByType() {

        System.out.println("\n===== TEST GET PENDING REQUESTS BY TYPE =====");

        List<Request> pendingCancelShifts =
                RequestManagement.getPendingRequestsByType(RequestType.CANCEL_SHIFT);

        System.out.println("Pending CANCEL_SHIFT requests: " + pendingCancelShifts.size());

        testPrintRequests(pendingCancelShifts);
    }

    static void testGetRequestsByRequester() {

        System.out.println("\n===== TEST GET REQUESTS BY REQUESTER =====");

        User doctor = UserManagement.getDoctorById("#DOCd4e8f");

        if (doctor == null) {
            System.out.println("Doctor not found for this test.");
            return;
        }

        List<Request> result =
                RequestManagement.getRequestsByRequester(doctor);

        System.out.println("Requests by " + doctor.getId() + ": " + result.size());

        testPrintRequests(result);
    }

    // =========================================================
    // GENERATE ID TEST
    // =========================================================

    static void testGenerateRequestId() {

        System.out.println("\n===== TEST GENERATE REQUEST ID =====");

        String requestId = RequestManagement.generateRequestId();

        System.out.println("Generated Request ID : " + requestId);
    }

    // =========================================================
    // CREATE TEST
    // =========================================================

    static void testCreateRequest() {

        System.out.println("\n===== TEST CREATE REQUEST =====");

        User manager = UserManagement.getMedicalManagerById("#MNG74295");

        LinkedHashMap<String, String> result = requestMng.createRequest(
                RequestType.CANCEL_SHIFT,
                manager,
                "#SHIFT001",
                "Testing request creation - please ignore"
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Request ID : " + result.get("newRequestId"));
    }

    // =========================================================
    // MODIFY TESTS
    // =========================================================

    static void testUpdateRequest() {

        System.out.println("\n===== TEST UPDATE REQUEST =====");

        List<Request> requests =
                RequestManagement.getPendingRequestsByType(RequestType.CANCEL_SHIFT);

        if (requests.isEmpty()) {
            System.out.println("No pending CANCEL_SHIFT requests available to update.");
            return;
        }

        Request request = requests.get(0);

        LinkedHashMap<String, String> result = requestMng.updateRequest(
                request,
                request.getReferenceId(),
                "Updated reason for testing"
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    static void testWithdrawRequest() {

        System.out.println("\n===== TEST WITHDRAW REQUEST =====");

        List<Request> requests =
                RequestManagement.getPendingRequestsByType(RequestType.CANCEL_SHIFT);

        if (requests.isEmpty()) {
            System.out.println("No pending CANCEL_SHIFT requests available to withdraw.");
            return;
        }

        Request request = requests.get(0);

        LinkedHashMap<String, String> result = requestMng.withdrawRequest(request);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    // =========================================================
    // RESOLVE TESTS
    // =========================================================

    static void testApproveRequest() {

        System.out.println("\n===== TEST APPROVE REQUEST =====");

        // NOTE: approving usually requires the manager/admin as activeUser, not the doctor.
        // Swap active user for this test if your ErrorHandler enforces role checks.
        User manager = UserManagement.getMedicalManagerById("#MNG74295");
        RequestManagement managerRequestMng = new RequestManagement(manager);

        List<Request> requests =
                RequestManagement.getPendingRequestsByType(RequestType.CANCEL_SHIFT);

        if (requests.isEmpty()) {
            System.out.println("No pending CANCEL_SHIFT requests available to approve.");
            return;
        }

        Request request = requests.get(0);

        LinkedHashMap<String, String> result = managerRequestMng.approveRequest(request);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    static void testRejectRequest() {

        System.out.println("\n===== TEST REJECT REQUEST =====");

        User manager = UserManagement.getMedicalManagerById("#MNG74295");
        RequestManagement managerRequestMng = new RequestManagement(manager);

        List<Request> requests =
                RequestManagement.getPendingRequestsByType(RequestType.CANCEL_SHIFT);

        if (requests.isEmpty()) {
            System.out.println("No pending CANCEL_SHIFT requests available to reject.");
            return;
        }

        Request request = requests.get(0);

        LinkedHashMap<String, String> result = managerRequestMng.rejectRequest(request, "Testing rejection - please ignore");

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}
