/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class ReportManagement {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    // =========================================================
    // BILLING / REVENUE REPORT
    // =========================================================

    public static LinkedHashMap<String, String> getBillingTotals() {
        List<Bill> bills = BillingManagement.getAllBills();
        LinkedHashMap<String, String> totals = new LinkedHashMap<>();

        double totalRevenue = 0; // sum of payable amount, excludes VOID bills
        double totalPaid = 0;
        double totalUnpaid = 0;
        int countPaid = 0;
        int countUnpaid = 0;
        int countVoid = 0;

        for (Bill bill : bills) {
            if (Bill.BillStatus.VOID.equals(bill.getStatus())) {
                countVoid++;
                continue;
            }
            totalRevenue += bill.getPayableAmount();
            if (Bill.BillStatus.PAID.equals(bill.getStatus())) {
                totalPaid += bill.getPayableAmount();
                countPaid++;
            } else if (Bill.BillStatus.UNPAID.equals(bill.getStatus())) {
                totalUnpaid += bill.getPayableAmount();
                countUnpaid++;
            }
        }

        totals.put("totalBills", String.valueOf(bills.size()));
        totals.put("totalRevenue", String.format("%.2f", totalRevenue));
        totals.put("totalPaid", String.format("%.2f", totalPaid));
        totals.put("totalUnpaid", String.format("%.2f", totalUnpaid));
        totals.put("countPaid", String.valueOf(countPaid));
        totals.put("countUnpaid", String.valueOf(countUnpaid));
        totals.put("countVoid", String.valueOf(countVoid));

        return totals;
    }

    // Revenue grouped by month (yyyy-MM), based on the linked consultation slot's start date/time.
    // Bills whose consultation/slot cannot be resolved are skipped (should not normally happen).
    public static LinkedHashMap<String, Double> getRevenueByMonth() {
        List<Bill> bills = BillingManagement.getAllBills();
        Map<String, Double> revenueByMonth = new TreeMap<>(); // TreeMap keeps yyyy-MM keys chronologically sorted

        for (Bill bill : bills) {
            if (Bill.BillStatus.VOID.equals(bill.getStatus())) {
                continue; // exclude voided bills from revenue
            }
            String monthKey = getBillMonthKey(bill);
            if (monthKey == null) {
                continue;
            }
            revenueByMonth.merge(monthKey, bill.getPayableAmount(), Double::sum);
        }

        return new LinkedHashMap<>(revenueByMonth);
    }

    // Bill count grouped by status (PAID / UNPAID / VOID), ready for a pie/donut chart
    public static LinkedHashMap<String, Double> getBillStatusBreakdown() {
        List<Bill> bills = BillingManagement.getAllBills();
        LinkedHashMap<String, Double> breakdown = new LinkedHashMap<>();
        breakdown.put(Bill.BillStatus.PAID.toString(), 0.0);
        breakdown.put(Bill.BillStatus.UNPAID.toString(), 0.0);
        breakdown.put(Bill.BillStatus.VOID.toString(), 0.0);

        for (Bill bill : bills) {
            if (bill.getStatus() == null) {
                continue;
            }
            breakdown.merge(bill.getStatus().toString(), 1.0, Double::sum);
        }

        return breakdown;
    }

    // Resolves a bill's "month" via Bill -> Consultation -> ConsultationSlot -> startDateTime
    private static String getBillMonthKey(Bill bill) {
        Consultation consultation = ConsultationManagement.getConsultationById(bill.getConsultationId());
        if (consultation == null || consultation.getConsultationSlot() == null) {
            return null;
        }
        String startDateTime = consultation.getConsultationSlot().getStartDateTime();
        if (startDateTime == null || startDateTime.isEmpty()) {
            return null;
        }
        LocalDateTime dateTime = DateTime.toDateTimeObject(startDateTime);
        return dateTime.format(MONTH_FORMAT);
    }
    
    // Billing totals filtered to a date range (inclusive), based on each bill's
    // consultation slot start date. Pass null for either bound to leave it open.
    public static LinkedHashMap<String, String> getBillingTotals(LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = BillingManagement.getAllBills();
        LinkedHashMap<String, String> totals = new LinkedHashMap<>();

        double totalRevenue = 0;
        double totalPaid = 0;
        double totalUnpaid = 0;
        int countPaid = 0;
        int countUnpaid = 0;
        int countVoid = 0;
        int billsInRange = 0;

        for (Bill bill : bills) {
            LocalDate billDate = getBillDate(bill);
            if (startDate != null && (billDate == null || billDate.isBefore(startDate))) continue;
            if (endDate != null && (billDate == null || billDate.isAfter(endDate))) continue;

            billsInRange++;
            if (Bill.BillStatus.VOID.equals(bill.getStatus())) {
                countVoid++;
                continue;
            }
            totalRevenue += bill.getPayableAmount();
            if (Bill.BillStatus.PAID.equals(bill.getStatus())) {
                totalPaid += bill.getPayableAmount();
                countPaid++;
            } else if (Bill.BillStatus.UNPAID.equals(bill.getStatus())) {
                totalUnpaid += bill.getPayableAmount();
                countUnpaid++;
            }
        }

        totals.put("totalBills", String.valueOf(billsInRange));
        totals.put("totalRevenue", String.format("%.2f", totalRevenue));
        totals.put("totalPaid", String.format("%.2f", totalPaid));
        totals.put("totalUnpaid", String.format("%.2f", totalUnpaid));
        totals.put("countPaid", String.valueOf(countPaid));
        totals.put("countUnpaid", String.valueOf(countUnpaid));
        totals.put("countVoid", String.valueOf(countVoid));
        return totals;
    }
    
    // Revenue grouped by week within a date range (e.g. "Week 1", "Week 2", ...),
    // based on the day-of-month of each bill's consultation slot start date.
    // Excludes VOID bills, same as getRevenueByMonth().
    public static LinkedHashMap<String, Double> getRevenueByWeek(LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = BillingManagement.getAllBills();
        Map<Integer, Double> revenueByWeek = new TreeMap<>(); // week number -> total

        for (Bill bill : bills) {
            if (Bill.BillStatus.VOID.equals(bill.getStatus())) {
                continue;
            }
            LocalDate billDate = getBillDate(bill);
            if (billDate == null) continue;
            if (startDate != null && billDate.isBefore(startDate)) continue;
            if (endDate != null && billDate.isAfter(endDate)) continue;

            // Week number relative to the range's start, 1-indexed.
            LocalDate rangeStart = (startDate != null) ? startDate : billDate.withDayOfMonth(1);
            int dayOffset = (int) java.time.temporal.ChronoUnit.DAYS.between(rangeStart, billDate);
            int weekNumber = (dayOffset / 7) + 1;

            revenueByWeek.merge(weekNumber, bill.getPayableAmount(), Double::sum);
        }

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> entry : revenueByWeek.entrySet()) {
            result.put("Week " + entry.getKey(), entry.getValue());
        }
        return result;
    }

    // Bill status breakdown filtered to a date range, same rules as above.
    public static LinkedHashMap<String, Double> getBillStatusBreakdown(LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = BillingManagement.getAllBills();
        LinkedHashMap<String, Double> breakdown = new LinkedHashMap<>();
        breakdown.put(Bill.BillStatus.PAID.toString(), 0.0);
        breakdown.put(Bill.BillStatus.UNPAID.toString(), 0.0);
        breakdown.put(Bill.BillStatus.VOID.toString(), 0.0);

        for (Bill bill : bills) {
            if (bill.getStatus() == null) continue;
            LocalDate billDate = getBillDate(bill);
            if (startDate != null && (billDate == null || billDate.isBefore(startDate))) continue;
            if (endDate != null && (billDate == null || billDate.isAfter(endDate))) continue;
            breakdown.merge(bill.getStatus().toString(), 1.0, Double::sum);
        }
        return breakdown;
    }

    // Resolves a bill's actual consultation date (not just yyyy-MM) via Bill -> Consultation -> ConsultationSlot
    private static LocalDate getBillDate(Bill bill) {
        Consultation consultation = ConsultationManagement.getConsultationById(bill.getConsultationId());
        if (consultation == null || consultation.getConsultationSlot() == null) return null;
        String startDateTime = consultation.getConsultationSlot().getStartDateTime();
        if (startDateTime == null || startDateTime.isEmpty()) return null;
        LocalDateTime dt = DateTime.toDateTimeObject(startDateTime);
        return dt == null ? null : dt.toLocalDate();
    }

    // =========================================================
    // INSURANCE USAGE REPORT
    // =========================================================

    public static LinkedHashMap<String, String> getInsuranceTotals(LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = BillingManagement.getAllBills();
        List<Bill> filteredBills = bills.stream()
                                    .filter(b -> {
                                    LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(b.getGeneratedDateTime()));
                                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                                    })
                                    .toList();
        
        LinkedHashMap<String, String> totals = new LinkedHashMap<>();
        long billsWithInsurance = filteredBills.stream().filter(b -> b.getInsurance() != null).count();
        long billsWithoutInsurance = filteredBills.size() - billsWithInsurance;

        totals.put("totalBills", String.valueOf(bills.size()));
        totals.put("billsWithInsurance", String.valueOf(billsWithInsurance));
        totals.put("billsWithoutInsurance", String.valueOf(billsWithoutInsurance));

        return totals;
    }

    // Number of bills grouped by insurance provider, ready for a bar/pie chart
    public static LinkedHashMap<String, Double> getInsuranceUsageByProvider(LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = BillingManagement.getAllBills();
        List<Bill> filteredBills = bills.stream()
                                    .filter(b -> {
                                    LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(b.getGeneratedDateTime()));
                                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                                    })
                                    .toList();
        LinkedHashMap<String, Double> usage = new LinkedHashMap<>();

        for (Bill bill : filteredBills) {
            Insurance insurance = bill.getInsurance();
            String key = (insurance == null) ? "No Insurance" : insurance.getInsuranceName();
            usage.merge(key, 1.0, Double::sum);
        }

        return usage;
    }

    // Total payable amount grouped by insurance provider (excludes VOID bills)
    public static LinkedHashMap<String, Double> getPayableAmountByInsurance(LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = BillingManagement.getAllBills();
        List<Bill> filteredBills = bills.stream()
                                    .filter(b -> {
                                    LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(b.getGeneratedDateTime()));
                                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                                    })
                                    .toList();
        LinkedHashMap<String, Double> amountByInsurance = new LinkedHashMap<>();

        for (Bill bill : filteredBills) {
            if (Bill.BillStatus.VOID.equals(bill.getStatus())) {
                continue;
            }
            Insurance insurance = bill.getInsurance();
            String key = (insurance == null) ? "No Insurance" : insurance.getInsuranceName();
            amountByInsurance.merge(key, bill.getPayableAmount(), Double::sum);
        }

        return amountByInsurance;
    }

    // =========================================================
    // CONSULTATION REPORT
    // =========================================================

    public static LinkedHashMap<String, String> getConsultationTotals(LocalDate startDate, LocalDate endDate) {
        List<Consultation> consultations = ConsultationManagement.getAllConsultations();
        List<Consultation> filteredConsultations = consultations.stream()
                                                    .filter(c -> {
                                                        LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(c.getConsultationSlot().getEndDateTime()));
                                                        return !date.isBefore(startDate) && !date.isAfter(endDate);
                                                    })
                                                    .toList();
        LinkedHashMap<String, String> totals = new LinkedHashMap<>();
        totals.put("totalConsultations", String.valueOf(filteredConsultations.size()));
        return totals;
    }

    // Consultation count grouped by status, ready for a pie chart
    public static LinkedHashMap<String, Double> getConsultationsByStatus(LocalDate startDate, LocalDate endDate) {
        List<Consultation> consultations = ConsultationManagement.getAllConsultations();
        LinkedHashMap<String, Double> breakdown = new LinkedHashMap<>();

        for (Consultation.ConsultationStatus status : Consultation.ConsultationStatus.values()) {
            breakdown.put(status.toString(), 0.0);
        }

        for (Consultation consultation : consultations) {
            if (consultation.getConsultationStatus() == null) {
                continue;
            }
            breakdown.merge(consultation.getConsultationStatus().toString(), 1.0, Double::sum);
        }

        return breakdown;
    }

    // Consultation count grouped by doctor, via ConsultationSlot -> DoctorShift -> Doctor
    public static LinkedHashMap<String, Double> getConsultationsByDoctor(LocalDate startDate, LocalDate endDate) {
        List<Consultation> consultations = ConsultationManagement.getAllConsultations();
        List<Consultation> filteredConsultations = consultations.stream()
                                                    .filter(c -> {
                                                        LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(c.getConsultationSlot().getEndDateTime()));
                                                        return !date.isBefore(startDate) && !date.isAfter(endDate);
                                                    })
                                                    .toList();
        LinkedHashMap<String, Double> breakdown = new LinkedHashMap<>();

        for (Consultation consultation : filteredConsultations) {
            Doctor doctor = getDoctorFromConsultation(consultation);
            String key = (doctor == null) ? "Unknown Doctor" : doctor.getName();
            breakdown.merge(key, 1.0, Double::sum);
        }
        return breakdown;
    }

    // Consultation count grouped by month, based on the linked consultation slot's start date/time
    public static LinkedHashMap<String, Double> getConsultationsByMonth(LocalDate startDate, LocalDate endDate) {
        List<Consultation> consultations = ConsultationManagement.getAllConsultations();
        Map<String, Double> byMonth = new TreeMap<>();

        for (Consultation consultation : consultations) {
            ConsultationSlot slot = consultation.getConsultationSlot();
            if (slot == null || slot.getStartDateTime() == null || slot.getStartDateTime().isEmpty()) {
                continue;
            }
            LocalDateTime dateTime = DateTime.toDateTimeObject(slot.getStartDateTime());
            byMonth.merge(dateTime.format(MONTH_FORMAT), 1.0, Double::sum);
        }

        return new LinkedHashMap<>(byMonth);
    }

    // Resolves the doctor tied to a consultation via ConsultationSlot -> DoctorShift -> Doctor
    private static Doctor getDoctorFromConsultation(Consultation consultation) {
        if (consultation.getConsultationSlot() == null) {
            return null;
        }
        DoctorShift shift = consultation.getConsultationSlot().getDoctorShift();
        return (shift == null) ? null : shift.getDoctor();
    }

    // =========================================================
    // DOCTOR SHIFT REPORT
    // =========================================================

    public static LinkedHashMap<String, String> getDoctorShiftTotals() {
        List<DoctorShift> shifts = DoctorManagement.getAllDoctorShifts();
        LinkedHashMap<String, String> totals = new LinkedHashMap<>();
        totals.put("totalShifts", String.valueOf(shifts.size()));
        return totals;
    }

    // Shift count grouped by status, ready for a pie chart
    public static LinkedHashMap<String, Double> getShiftsByStatus() {
        List<DoctorShift> shifts = DoctorManagement.getAllDoctorShifts();
        LinkedHashMap<String, Double> breakdown = new LinkedHashMap<>();

        for (DoctorShift.DoctorShiftStatus status : DoctorShift.DoctorShiftStatus.values()) {
            breakdown.put(status.toString(), 0.0);
        }

        for (DoctorShift shift : shifts) {
            if (shift.getStatus() == null) {
                continue;
            }
            breakdown.merge(shift.getStatus().toString(), 1.0, Double::sum);
        }

        return breakdown;
    }

    // Total scheduled shift hours grouped by doctor (excludes cancelled shifts), ready for a bar chart
    public static LinkedHashMap<String, Double> getScheduledShiftHoursByDoctor() {
        List<DoctorShift> shifts = DoctorManagement.getAllDoctorShifts();
        LinkedHashMap<String, Double> hoursByDoctor = new LinkedHashMap<>();

        for (DoctorShift shift : shifts) {
            if (DoctorShift.DoctorShiftStatus.CANCELLED.equals(shift.getStatus())) {
                continue;
            }
            LocalDateTime start = DateTime.toDateTimeObject(shift.getStartDateTime());
            LocalDateTime end = DateTime.toDateTimeObject(shift.getEndDateTime());
            double hours = DateTime.calculateDurationInHours(start, end);

            String key = (shift.getDoctor() == null) ? "Unknown Doctor" : shift.getDoctor().getName();
            hoursByDoctor.merge(key, hours, Double::sum);
        }

        return hoursByDoctor;
    }

    // =========================================================
    // ROOM / HOSPITAL ASSET UTILIZATION REPORT
    // =========================================================

    public static LinkedHashMap<String, String> getRoomUtilizationTotals() {
        List<HospitalAssetAllocation> allocations = HospitalAssetManagement.getAllHospitalAssetAllocations();
        LinkedHashMap<String, String> totals = new LinkedHashMap<>();

        long currentlyOccupied = allocations.stream()
                .filter(a -> a.getReleasedDateTime() == null || a.getReleasedDateTime().isEmpty())
                .count();

        totals.put("totalAllocations", String.valueOf(allocations.size()));
        totals.put("currentlyOccupied", String.valueOf(currentlyOccupied));
        totals.put("totalConsultationRooms", String.valueOf(HospitalAssetManagement.getConsultationRoomCount()));

        return totals;
    }

    // Allocation count grouped by asset (room/ward/etc), ready for a bar chart
    public static LinkedHashMap<String, Double> getAllocationCountByAsset() {
        List<HospitalAssetAllocation> allocations = HospitalAssetManagement.getAllHospitalAssetAllocations();
        LinkedHashMap<String, Double> breakdown = new LinkedHashMap<>();

        for (HospitalAssetAllocation allocation : allocations) {
            HospitalAsset asset = HospitalAssetManagement.getHospitalAssetById(allocation.getAssetId());
            String key = (asset == null) ? allocation.getAssetId() : asset.getAssetName();
            breakdown.merge(key, 1.0, Double::sum);
        }

        return breakdown;
    }

    // Total occupied minutes grouped by asset. Only counts allocations that have already
    // been released (an allocation with no releasedDateTime is still ongoing, so its
    // duration is not yet final and is excluded here).
    public static LinkedHashMap<String, Double> getOccupiedMinutesByAsset() {
        List<HospitalAssetAllocation> allocations = HospitalAssetManagement.getAllHospitalAssetAllocations();
        LinkedHashMap<String, Double> minutesByAsset = new LinkedHashMap<>();

        for (HospitalAssetAllocation allocation : allocations) {
            if (allocation.getReleasedDateTime() == null || allocation.getReleasedDateTime().isEmpty()) {
                continue;
            }
            LocalDateTime allocatedStart = DateTime.toDateTimeObject(allocation.getAllocationDateTime());
            LocalDateTime released = DateTime.toDateTimeObject(allocation.getReleasedDateTime());
            double minutes = DateTime.calculateDurationInMinutes(allocatedStart, released);

            HospitalAsset asset = HospitalAssetManagement.getHospitalAssetById(allocation.getAssetId());
            String key = (asset == null) ? allocation.getAssetId() : asset.getAssetName();
            minutesByAsset.merge(key, minutes, Double::sum);
        }

        return minutesByAsset;
    }
}
