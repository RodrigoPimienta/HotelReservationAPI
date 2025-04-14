package com.revature.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "user_businesses")
public class UserBusiness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int businessId;

    @Column(nullable = false)
    private String legalName;

    @Column(nullable = false, length = 13)
    private String phoneNumber;

    @Column(nullable = false, length = 20)
    private String taxCode;

    @JsonBackReference("user-business") // Matching name
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserBusiness(){}

    public UserBusiness(int businessId, String legalName, String phoneNumber, String taxCode) {
        this.businessId = businessId;
        this.legalName = legalName;
        this.phoneNumber = phoneNumber;
        this.taxCode = taxCode;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
