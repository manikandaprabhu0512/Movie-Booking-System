package com.manikanda.payment_service.repo.reads;

import com.manikanda.payment_service.entity.read_model.PaymentReadModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentReadRepo extends MongoRepository<PaymentReadModel, String> {
}
