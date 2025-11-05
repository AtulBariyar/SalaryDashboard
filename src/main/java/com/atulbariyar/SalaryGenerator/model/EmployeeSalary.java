package com.atulbariyar.SalaryGenerator.model;



public class EmployeeSalary {
    private int empId;
    private String name;
    private String email;
    private double basic;
    private double hra;
    private double allowance;
    private double deductions;
    private double netSalary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBasic() {
        return basic;
    }

    public void setBasic(double basic) {
        this.basic = basic;
    }

    public double getHra() {
        return hra;
    }

    public void setHra(double hra) {
        this.hra = hra;
    }

    public double getAllowance() {
        return allowance;
    }

    public void setAllowance(double allowance) {
        this.allowance = allowance;
    }

    public double getDeductions() {
        return deductions;
    }

    public void setDeductions(double deductions) {
        this.deductions = deductions;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(double netSalary) {
        this.netSalary = netSalary;
    }


    @Override
    public String toString() {
        return "EmployeeSalary{" +
                "employeeId='" + empId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", basic=" + basic +
                ", hra=" + hra +
                ", allowance=" + allowance +
                ", deductions=" + deductions +
                ", netSalary=" + netSalary +
                '}';
    }
}
