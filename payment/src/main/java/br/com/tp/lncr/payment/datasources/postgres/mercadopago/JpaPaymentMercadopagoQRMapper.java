package br.com.tp.lncr.payment.datasources.postgres.mercadopago;

import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import br.com.tp.lncr.core.enums.PaymentStatus;

public class JpaPaymentMercadopagoQRMapper {
    public  PaymentMercadopagoQrDTO jpaMercadopagoQrToDTO(JpaMercadopagoQrEntity entity) {
        if (entity == null) return null;
        return new PaymentMercadopagoQrDTO.Builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .status(PaymentStatus.fromId(entity.getStatusId()).getDescription())
                .amount(entity.getAmount())
                .paymentProvider(entity.getPaymentProvider())
                .paymentMethod(entity.getPaymentMethod())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .externalPaymentId(entity.getExternalPaymentId())
                .qrData(entity.getQrData())
                .meliId(entity.getMeliId())
                .build();
    }

    public JpaMercadopagoQrEntity mercadopagoQrDtoToJpa(PaymentMercadopagoQrDTO dto) {
        if (dto == null) return null;
        return JpaMercadopagoQrEntity.builder()
                .id(dto.getId())
                .orderId(dto.getOrderId())
                .status(PaymentStatus.fromDescription(dto.getStatus()).getId())
                .amount(dto.getAmount())
                .paymentProvider(dto.getPaymentProvider())
                .paymentMethod(dto.getPaymentMethod())
                .externalPaymentId(dto.getExternalPaymentId())
                .created(dto.getCreated())
                .updated(dto.getUpdated())
                .meliId(dto.getMeliId())
                .qrData(dto.getQrData())
                .build();
    }
}

