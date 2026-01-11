package com.manikanda.moviebookingservice.entity.read_model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "booking_read_model")
@CompoundIndexes({
    @CompoundIndex(name = "idx_user_created", def = "{'userId': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "idx_show_status", def = "{'showId': 1, 'status': 1}")
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingReadModel {

    @Id
    private String bookingId;

    private String userId;

    private String theatreId;
    private String showId;
    private String screenId;

    private List<String> seatNumbers;

    private String status;

    private BigDecimal price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
