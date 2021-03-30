package com.project.springapirest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ticket")
public class Ticket implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String comment;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"tickets", "hibernateLazyInitializer", "handler"})
    private User user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id")
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, allowSetters = true)
    private List<TicketItem> items;

    @Column(length = 200)
    private String pdf;

    @Lob
    @Column(name = "pdf_data", length =  10000)
    private byte[] pdfBits;

    @Column(name = "pdf_type", length = 100)
    private String pdfType;

    @Column(nullable = false, length = 1)
    private Boolean checked;

    public Ticket() {
        this.items = new ArrayList<>();
    }

    @PrePersist
    public void prePersist() {
        date = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<TicketItem> getItems() {
        return items;
    }

    public void setItems(List<TicketItem> items) {
        this.items = items;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getPdf() {
        return pdf;
    }

    public byte[] getPdfBits() {
        return pdfBits;
    }

    public void setPdfBits(byte[] pdfBits) {
        this.pdfBits = pdfBits;
    }

    public String getPdfType() {
        return pdfType;
    }

    public void setPdfType(String pdfType) {
        this.pdfType = pdfType;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public Double getTotal() {
        Double total = 0.0;

        for(TicketItem item: items) {
            total += item.getAmount();
        }

        return total;
    }

    private static final long serialVersionUID = 1L;
}
