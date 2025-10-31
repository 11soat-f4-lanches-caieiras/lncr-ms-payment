package br.com.tp.lncr.payment.datasources.postgres;

import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaMercadopagoQrEntity;
import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaPaymentMercadopagoQRMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JpaPaymentMercadopagoQRMapperTest {

    private final JpaPaymentMercadopagoQRMapper mapper = new JpaPaymentMercadopagoQRMapper();

    @Test
    void jpaMercadopagoQrToDTOConvertsCorrectly() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        JpaMercadopagoQrEntity entity = new JpaMercadopagoQrEntity(
                1, 100, 2, 50.0, "mercadopago", "qrcode",
                "ext123", created, updated, "meli123", "qrdata123"
        );

        PaymentMercadopagoQrDTO dto = mapper.jpaMercadopagoQrToDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getOrderId(), dto.getOrderId());
        assertEquals(entity.getAmount(), dto.getAmount());
        assertEquals(entity.getPaymentProvider(), dto.getPaymentProvider());
        assertEquals(entity.getPaymentMethod(), dto.getPaymentMethod());
        assertEquals(entity.getExternalPaymentId(), dto.getExternalPaymentId());
        assertEquals(entity.getCreated(), dto.getCreated());
        assertEquals(entity.getUpdated(), dto.getUpdated());
        assertEquals(entity.getMeliId(), dto.getMeliId());
        assertEquals(entity.getQrData(), dto.getQrData());
    }

    @Test
    void jpaMercadopagoQrToDTOWithNullEntityReturnsNull() {
        PaymentMercadopagoQrDTO dto = mapper.jpaMercadopagoQrToDTO(null);

        assertNull(dto);
    }

    @Test
    void jpaMercadopagoQrToDTOWithNullFieldsConvertsCorrectly() {
        JpaMercadopagoQrEntity entity = new JpaMercadopagoQrEntity(
                null, null, 1, null, null, null,
                null, null, null, null, null
        );

        PaymentMercadopagoQrDTO dto = mapper.jpaMercadopagoQrToDTO(entity);

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getOrderId());
        assertNull(dto.getAmount());
        assertNull(dto.getPaymentProvider());
        assertNull(dto.getPaymentMethod());
        assertNull(dto.getExternalPaymentId());
        assertNull(dto.getCreated());
        assertNull(dto.getUpdated());
        assertNull(dto.getMeliId());
        assertNull(dto.getQrData());
    }

    @Test
    void mercadopagoQrDtoToJpaConvertsCorrectly() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        PaymentMercadopagoQrDTO dto = new PaymentMercadopagoQrDTO.Builder()
                .id(1)
                .orderId(100)
                .status("CHARGED")
                .amount(50.0)
                .paymentProvider("mercadopago")
                .paymentMethod("qrcode")
                .created(created)
                .updated(updated)
                .externalPaymentId("ext123")
                .qrData("qrdata123")
                .meliId("meli123")
                .build();

        JpaMercadopagoQrEntity entity = mapper.mercadopagoQrDtoToJpa(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getOrderId(), entity.getOrderId());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertEquals(dto.getPaymentProvider(), entity.getPaymentProvider());
        assertEquals(dto.getPaymentMethod(), entity.getPaymentMethod());
        assertEquals(dto.getExternalPaymentId(), entity.getExternalPaymentId());
        assertEquals(dto.getCreated(), entity.getCreated());
        assertEquals(dto.getUpdated(), entity.getUpdated());
        assertEquals(dto.getMeliId(), entity.getMeliId());
        assertEquals(dto.getQrData(), entity.getQrData());
    }

    @Test
    void mercadopagoQrDtoToJpaWithNullDtoReturnsNull() {
        JpaMercadopagoQrEntity entity = mapper.mercadopagoQrDtoToJpa(null);

        assertNull(entity);
    }

    @Test
    void mercadopagoQrDtoToJpaWithNullFieldsConvertsCorrectly() {
        PaymentMercadopagoQrDTO dto = new PaymentMercadopagoQrDTO.Builder()
                .status("CANCELLED")
                .build();

        JpaMercadopagoQrEntity entity = mapper.mercadopagoQrDtoToJpa(dto);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getOrderId());
        assertNull(entity.getAmount());
        assertNull(entity.getPaymentProvider());
        assertNull(entity.getPaymentMethod());
        assertNull(entity.getExternalPaymentId());
        assertNull(entity.getCreated());
        assertNull(entity.getUpdated());
        assertNull(entity.getMeliId());
        assertNull(entity.getQrData());
    }

    @Test
    void bidirectionalMappingMaintainsDataIntegrity() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        PaymentMercadopagoQrDTO originalDto = new PaymentMercadopagoQrDTO.Builder()
                .id(1)
                .orderId(100)
                .status("PAID")
                .amount(50.0)
                .paymentProvider("mercadopago")
                .paymentMethod("qrcode")
                .created(created)
                .updated(updated)
                .externalPaymentId("ext123")
                .qrData("qrdata123")
                .meliId("meli123")
                .build();

        JpaMercadopagoQrEntity entity = mapper.mercadopagoQrDtoToJpa(originalDto);
        PaymentMercadopagoQrDTO resultDto = mapper.jpaMercadopagoQrToDTO(entity);

        assertEquals(originalDto.getId(), resultDto.getId());
        assertEquals(originalDto.getOrderId(), resultDto.getOrderId());
        assertEquals(originalDto.getAmount(), resultDto.getAmount());
        assertEquals(originalDto.getPaymentProvider(), resultDto.getPaymentProvider());
        assertEquals(originalDto.getPaymentMethod(), resultDto.getPaymentMethod());
        assertEquals(originalDto.getExternalPaymentId(), resultDto.getExternalPaymentId());
        assertEquals(originalDto.getCreated(), resultDto.getCreated());
        assertEquals(originalDto.getUpdated(), resultDto.getUpdated());
        assertEquals(originalDto.getMeliId(), resultDto.getMeliId());
        assertEquals(originalDto.getQrData(), resultDto.getQrData());
    }

    @Test
    void mapperHandlesEmptyStringsCorrectly() {
        JpaMercadopagoQrEntity entity = new JpaMercadopagoQrEntity(
                1, 100, 2, 50.0, "", "",
                "", LocalDateTime.now(), LocalDateTime.now(), "", ""
        );

        PaymentMercadopagoQrDTO dto = mapper.jpaMercadopagoQrToDTO(entity);

        assertNotNull(dto);
        assertEquals("", dto.getPaymentProvider());
        assertEquals("", dto.getPaymentMethod());
        assertEquals("", dto.getExternalPaymentId());
        assertEquals("", dto.getMeliId());
        assertEquals("", dto.getQrData());
    }

    @Test
    void mapperHandlesZeroValuesCorrectly() {
        JpaMercadopagoQrEntity entity = new JpaMercadopagoQrEntity(
                0, 0, 0, 0.0, "mercadopago", "qrcode",
                "ext123", LocalDateTime.now(), LocalDateTime.now(), "meli123", "qrdata123"
        );

        PaymentMercadopagoQrDTO dto = mapper.jpaMercadopagoQrToDTO(entity);

        assertNotNull(dto);
        assertEquals(0, dto.getId());
        assertEquals(0, dto.getOrderId());
        assertEquals(0.0, dto.getAmount());
    }
}
