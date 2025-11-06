package br.com.tp.lncr.payment.webhooks.mercadopago;

import com.fasterxml.jackson.annotation.JsonProperty;


public record MercadoPagoCallbackDTO(
    String action,
    @JsonProperty("api_version")
    String apiVersion,
    @JsonProperty("application_id")
    String applicationId,
    Data data,
    @JsonProperty("date_created")
    String dateCreated,
    @JsonProperty("live_mode")
    Boolean liveMode,
    String type,
    @JsonProperty("user_id")
    String userId
) {
    public record Data(
            @JsonProperty("external_reference")
            String externalReference,
            String id,
            String status,
            @JsonProperty("status_detail")
            String statusDetail,
            @JsonProperty("total_amount")
            String totalAmount,
            @JsonProperty("total_paid_amount")
            String totalPaidAmount,
            Object transactions,
            String type,
            Integer version
    ) {}
}
