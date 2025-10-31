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

    protected AbstractJpaPaymentEntity(BaseBuilder<?> builder) {
        this.id = builder.id;
        this.orderId = builder.orderId;
        this.statusId = builder.statusId;
        this.amount = builder.amount;
        this.paymentProvider = builder.paymentProvider;
        this.paymentMethod = builder.paymentMethod;
        this.externalPaymentId = builder.externalPaymentId;
        this.created = builder.created;
        this.updated = builder.updated;
    }

    protected AbstractJpaPaymentEntity() {
    }

    public abstract static class BaseBuilder<T extends BaseBuilder<T>> {
        protected Integer id;
        protected Integer orderId;
        protected Integer statusId;
        protected Double amount;
        protected String paymentProvider;
        protected String paymentMethod;
        protected String externalPaymentId;
        protected LocalDateTime created;
        protected LocalDateTime updated;

        protected abstract T self();

        public T id(Integer id) {
            this.id = id;
            return self();
        }

        public T orderId(Integer orderId) {
            this.orderId = orderId;
            return self();
        }

        public T statusId(Integer statusId) {
            this.statusId = statusId;
            return self();
        }

        public T amount(Double amount) {
            this.amount = amount;
            return self();
        }

        public T paymentProvider(String paymentProvider) {
            this.paymentProvider = paymentProvider;
            return self();
        }

        public T paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return self();
        }

        public T externalPaymentId(String externalPaymentId) {
            this.externalPaymentId = externalPaymentId;
            return self();
        }

        public T created(LocalDateTime created) {
            this.created = created;
            return self();
        }

        public T updated(LocalDateTime updated) {
            this.updated = updated;
            return self();
        }
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
