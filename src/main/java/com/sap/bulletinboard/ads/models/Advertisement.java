package com.sap.bulletinboard.ads.models;

import org.hibernate.validator.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "advertisements")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(name = "mytitle")
    private String title;

    @Column(insertable = true, updatable = false)
    private Timestamp createdAt;

    @Column(insertable = false, updatable = true)
    private Timestamp updatedAt;

    @Version
    private long version;

    public Advertisement() {
    }

    public Advertisement(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    protected void onPersist() {
        setCreatedAt(now());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = now();
    }

    protected void setCreatedAt(Timestamp now) {
        if (createdAt == null) {
            this.createdAt = now;
        }
    }

    protected static Timestamp now() {
        return new Timestamp(new Date().getTime());
    }

    public long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Advertisement [id=" + id + ", title=" + title + "]";
    }

    // use only in tests
    public void setId(long id) {
        this.id = id;
    }
}
