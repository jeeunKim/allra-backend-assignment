package com.allra.assignment.dev.order.service;

import com.allra.assignment.dev.order.constant.PaymentStatus;
import com.allra.assignment.dev.order.model.entity.Order;
import com.allra.assignment.dev.order.model.request.PaymentRequest;
import com.allra.assignment.dev.order.model.response.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class PaymentClient {

    private final RestTemplate restTemplate;


    @Value("${payment-url}")
    private String paymentUrl;

    @Value("${payment-end-point}")
    private String paymentEndPoint;

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * 결제 요청
     * - 트랜잭션 분리: 외부 API 호출 시 DB 락 오래 점유 방지, 연동 종료 후 외부 트랜잭션 복귀
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public PaymentResponse payment(Order order) {
        // Mock API 호출
        try {
            PaymentRequest request = new PaymentRequest(order.getOrderId(), order.getTotalAmount());
            // 성공 케이스
            return restTemplate.postForObject(paymentUrl + paymentEndPoint, request, PaymentResponse.class);
            // 실패 케이스
//            return restTemplate.postForObject(paymentUrl + paymentEndPoint, "fail", PaymentResponse.class);
        } catch (Exception e) {
            log.error("{}-{} Payment Error Occur: {}", order.getUser().getUserId(), order.getOrderId(), e.getMessage());
            return new PaymentResponse(PaymentStatus.FAILED.name(), null, e.getMessage());
        }
    }



}
