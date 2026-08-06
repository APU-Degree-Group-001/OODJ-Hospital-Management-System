/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.FileHandler;

/**
 *
 * @author sheaw
 */

//private String consultationId;
//private ConsultationSlot consultationSlot;
//private Patient patient;
//private String description;
//private MedicalAssessment medicalAssessment;
//private ArrayList<LabResult> labResults;
//private ArrayList<Prescription> prescriptions;
//private HospitalAssetAllocation hospitalAssetAllocation;
//private Feedback feedback;
//private Bill bill;

public class ConsultationManagement {
    private static final FileHandler handler = new FileHandler(SystemConfiguration.userFilePath);
    private static final List<Consultation> consultations = new ArrayList<>();
    private User activeUser;
    
    public ConsultationManagement(User user) {
        this.activeUser = user;
        loadConsultations();
    }
    
    public void loadConsultations() {
        consultations.clear();
        List<LinkedHashMap<String, String>> rawData = handler.readFile();
    }
}
