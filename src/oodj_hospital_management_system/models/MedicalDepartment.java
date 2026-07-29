/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.Objects;

/**
 *
 * @author sheaw
 */
public class MedicalDepartment {
    private String departmentId;
    private String departmentName;
    private String description;

    public MedicalDepartment() {
        this.departmentId = "";
        this.departmentName = "";
        this.description = "";
    }

    public MedicalDepartment(
            String departmentId,
            String departmentName,
            String description
    ) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format(
                "Medical Department {%s - %s}",
                this.departmentId,
                this.departmentName
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof MedicalDepartment)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        MedicalDepartment departmentObj = (MedicalDepartment) obj;

        return Objects.equals(
                this.departmentId,
                departmentObj.departmentId
        );
    }

    public String getDepartmentId() {
        return this.departmentId;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
