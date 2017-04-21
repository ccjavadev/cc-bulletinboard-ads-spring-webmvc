package com.sap.bulletinboard.ads.models;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class Advertisement {

    @NotBlank
    private String title;

    private Calendar purchasedOn;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String contact;

    @NotBlank
    private String currency;

    private String category;
    
    private Long id;

    public Advertisement() {
    }

    public Advertisement(String title, String contact, BigDecimal price, String currency) {
        this.title = title;
        this.contact = contact;
        this.price = price;
        this.currency = currency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Calendar getPurchaseDate() {
        return purchasedOn;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }

    public void setPurchaseDate(Calendar purchaseDate) {
        this.purchasedOn = purchaseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}