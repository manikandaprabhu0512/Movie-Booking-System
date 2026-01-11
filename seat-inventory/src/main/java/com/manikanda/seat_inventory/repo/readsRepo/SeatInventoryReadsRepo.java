package com.manikanda.seat_inventory.repo.readsRepo;

import com.manikanda.seat_inventory.entity.read_model.SeatInventoryReadModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface SeatInventoryReadsRepo extends MongoRepository<SeatInventoryReadModel, String> {

    Optional<SeatInventoryReadModel> findByTheatreIdAndShowIdAndScreenId(String theatreId, String showId, String screenId);
}
