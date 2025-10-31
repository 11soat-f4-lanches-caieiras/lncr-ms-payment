package br.com.tp.lncr.payment.datasources.postgres.mercadopago;

import br.com.tp.lncr.payment.datasources.postgres.AbstractJpaPaymentEntity;
import jakarta.persistence.*;


@Entity
@Table(name="payment_mercadopago",
        schema = "public",
        indexes = {
                @Index(name = "payment_meli_id_idx", columnList = "id")
        })
public class JpaMercadopagoQrEntity extends AbstractJpaPaymentEntity {
    private String meliId;
    private String qrData;

    public JpaMercadopagoQrEntity() {
        super();
    }

    private JpaMercadopagoQrEntity(Builder builder) {
        super(builder);
        this.meliId = builder.meliId;
        this.qrData = builder.qrData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseBuilder<Builder> {
        private String meliId;
        private String qrData;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder meliId(String meliId) {
            this.meliId = meliId;
            return this;
        }

        public Builder qrData(String qrData) {
            this.qrData = qrData;
            return this;
        }

        public Builder status(Integer status) {
            this.statusId = status;
            return this;
        }

        public JpaMercadopagoQrEntity build() {
            return new JpaMercadopagoQrEntity(this);
        }
    }

    public String getMeliId() {
        return meliId;
    }

    public void setMeliId(String meliId) {
        this.meliId = meliId;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }
}
