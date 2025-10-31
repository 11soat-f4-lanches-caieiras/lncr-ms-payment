package br.com.tp.lncr.payment.datasources.postgres;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "payment",
        schema = "public",
        uniqueConstraints = {
            @UniqueConstraint(name = "payment_order_id_uk", columnNames = {"orderId"})
        },
        indexes = {
            @Index(name = "payment_id_idx", columnList = "id"),
            @Index(name = "payment_order_id_idx", columnList = "orderId"),
            @Index(name = "payment_status_id_idx", columnList = "statusId")
        })
public abstract class AbstractJpaPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_id_seq")
    @SequenceGenerator(name = "payment_id_seq", sequenceName = "payment_id_seq", allocationSize = 1)
    private Integer id;
    private Integer orderId;
    private Integer statusId;
    private Double amount;
    private String paymentProvider;
    private String paymentMethod;
    private String externalPaymentId;
    private LocalDateTime created;
    private LocalDateTime updated;

    protected AbstractJpaPaymentEntity(Integer id, Integer orderId, Integer status, Double amount, String paymentProvider, String paymentMethod, String externalPaymentId, LocalDateTime created, LocalDateTime updated) {
        this.id = id;
        this.orderId = orderId;
        this.statusId = status;
        this.amount = amount;
        this.paymentProvider = paymentProvider;
        this.paymentMethod = paymentMethod;
        this.externalPaymentId = externalPaymentId;
        this.created = created;
        this.updated = updated;
    }

    protected AbstractJpaPaymentEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
    }

    public void setExternalPaymentId(String externalPaymentId) {
        this.externalPaymentId = externalPaymentId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @PrePersist
    public void prePersist() {
        this.created = LocalDateTime.now();
    }


    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}
