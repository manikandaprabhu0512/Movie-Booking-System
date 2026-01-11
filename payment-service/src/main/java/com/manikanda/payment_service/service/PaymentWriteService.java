package com.manikanda.payment_service.service;

import com.manikanda.payment_service.dto.PaymentRequest;
import com.manikanda.payment_service.dto.PaymentResponse;

public interface PaymentWriteService {

    PaymentResponse doPayment(PaymentRequest request);

}
