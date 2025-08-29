package com.allra.assignment.dev.order.service;

import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.cart.repository.CartItemRepository;
import com.allra.assignment.dev.item.model.entity.Item;
import com.allra.assignment.dev.item.repository.ItemRepository;
import com.allra.assignment.dev.order.constant.OrderStatus;
import com.allra.assignment.dev.order.constant.PaymentStatus;
import com.allra.assignment.dev.order.model.entity.Order;
import com.allra.assignment.dev.order.model.entity.OrderDetail;
import com.allra.assignment.dev.order.model.entity.Payment;
import com.allra.assignment.dev.order.model.response.OrderResponse;
import com.allra.assignment.dev.order.model.response.PaymentResponse;
import com.allra.assignment.dev.order.repository.OrderDetailRepository;
import com.allra.assignment.dev.order.repository.OrderRepository;
import com.allra.assignment.dev.order.repository.PaymentRepository;
import com.allra.assignment.dev.user.model.entity.User;
import com.allra.assignment.dev.user.repository.UserRepository;
import com.allra.assignment.exception.custom.ItemException;
import com.allra.assignment.exception.custom.OrderException;
import com.allra.assignment.exception.custom.UserException;
import com.allra.assignment.exception.result.ItemErrorResult;
import com.allra.assignment.exception.result.OrderErrorResult;
import com.allra.assignment.exception.result.UserErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final PaymentClient paymentClient;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderDetailRepository orderDetailRepository;


    /**
     * 결제 및 재고 관련 동시성에 관한 Flow
     * 1. 주문 생성 및 재고 차감
     *    - 트랜잭션 시작
     *    - 각 상품에 대해 비관적 락을 걸어 재고 동시성 문제 방지
     *
     * 2. 결제 API 연동
     *    - 주문 트랜잭션은 잠시 중단 (NOT_SUPPORTED)
     *    - 외부 API 호출 지연이 있어도 다른 상품 주문에는 영향 없음
     *    - 같은 상품에 대해서만 비관적 락으로 대기 발생
     *
     * 3. 결제 API 응답 처리
     *    - 주문 트랜잭션으로 복귀
     *    - 응답 결과에 따라 주문 상태 업데이트, 재고 복원 또는 장바구니 정리 등 처리
     *
     * TODO 트래픽 증가시 낙관적 락 + Retry 로직 도입 검토
     */
    @Transactional
    public OrderResponse createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 장바구니 조회
        List<CartItem> cartItems = user.getCartItems();

        // 재고 체크 및 차감 (비관적 락)
        List<String> insufficientItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Item item = itemRepository.findByIdWithPessimisticLock(cartItem.getItem().getItemId())
                    .orElseThrow(() -> new ItemException(ItemErrorResult.ITEM_NOT_FOUND));

            if (item.getStock() < cartItem.getQuantity()) {
                insufficientItems.add(item.getName());
            }
        }

        if (!insufficientItems.isEmpty()) {
            throw new OrderException(
                    OrderErrorResult.NOT_ENOUGH_STOCK,
                    " 재고 부족 상품: " + String.join(", ", insufficientItems)
            );
        }

        // 주문 생성
        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(OrderStatus.AWAITING_PAYMENT);
        order.setTotalAmount(cartItems.stream().mapToLong(CartItem::getTotalDiscountAmount).sum());
        orderRepository.save(order);

        // 주문 상세 생성
        cartItems.forEach(cartItem -> orderDetailRepository.save(OrderDetail.builder()
                .orderDetailId(generateOrderDetailId())
                .amount(cartItem.getTotalDiscountAmount())
                .item(cartItem.getItem())
                .order(order)
                .quantity(cartItem.getQuantity())
                .build()));

        PaymentResponse result;
        try {
            // 외부 결제 호출 (트랜잭션 분리)
            result = paymentClient.payment(order);
        } catch (Exception e) {
            log.error("{} - payment Fail: {}", order.getOrderId(), e.getMessage());
            // 결제 API 호출 자체 실패 시 강제로 실패 Response 생성
            result = new PaymentResponse(PaymentStatus.FAILED.name(), null, e.getMessage());
        }

        // 결제 결과 처리 (보상 트랜잭션)
        processPaymentResult(order, cartItems, result);

        return buildOrderResponse(order, result);
    }


    /**
     * 결제 결과 처리
     * - 성공: 주문 상태 업데이트, 장바구니 삭제
     * - 실패: 주문 상태 업데이트, 재고 복원
     */
    private void processPaymentResult(Order order, List<CartItem> cartItems, PaymentResponse result) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentStatus(PaymentStatus.fromCode(result.getStatus()));
        payment.setFailedReason(
                PaymentStatus.fromCode(result.getStatus()).equals(PaymentStatus.FAILED)
                        ? result.getMessage()
                        : null
        );
        payment.setExternalTid(result.getTransactionId());
        paymentRepository.save(payment);

        if (result.getStatus().equals(PaymentStatus.SUCCESS.name())) {
            order.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);
            cartItemRepository.deleteAllByUser(order.getUser());
        } else {
            order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
            // 실패 시 재고 복원 (보상 트랜잭션)
            for (CartItem cartItem : cartItems) {
                Item item = itemRepository.findByIdWithPessimisticLock(cartItem.getItem().getItemId())
                        .orElseThrow(() -> new ItemException(ItemErrorResult.ITEM_NOT_FOUND));
                item.setStock(item.getStock() + cartItem.getQuantity());
                itemRepository.save(item);
            }
        }
        orderRepository.save(order);
    }

    private OrderResponse buildOrderResponse(Order order, PaymentResponse result) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(PaymentStatus.fromCode(result.getStatus()))
                .paymentAmount(order.getTotalAmount())
                .transactionId(result.getTransactionId())
                .message(result.getStatus().equals(PaymentStatus.SUCCESS.name()) ? "결제 완료!" : "결제 실패")
                .failReason(result.getStatus().equals(PaymentStatus.SUCCESS.name()) ? null : result.getMessage())
                .items(order.getOrderDetails().stream()
                        .map(i -> new OrderResponse.OrderItemDto(
                                i.getItem().getItemId(),
                                i.getItem().getName(),
                                i.getQuantity(),
                                i.getAmount()))
                        .toList())
                .build();
    }


    private String generateOrderDetailId() {
        String timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(java.time.ZoneId.systemDefault())
                .format(Instant.now());

        int randomNum = new Random().nextInt(90000) + 10000; // 10000~99999

        return timestamp + "_" + randomNum;
    }
}
