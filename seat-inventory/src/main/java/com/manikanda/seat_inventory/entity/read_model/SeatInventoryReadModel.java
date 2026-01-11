package com.manikanda.seat_inventory.entity.read_model;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "seat_inventory_read_model")
@CompoundIndexes({
    @CompoundIndex(name = "idx_theatre_show", def = "{'theatreId': 1, 'showId': 1}"),
    @CompoundIndex(name = "idx_show_status", def = "{'showId': 1, 'status': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatInventoryReadModel {

    @Id
    private String id;

    private String theatreId;
    private String showId;
    private String screenId;

    //SeatId - Status
    private Map<String, SeatStatus> seats;

    private LocalDateTime updatedAt;

    public enum SeatStatus {
        AVAILABLE,
        RESERVED,
        BOOKED
    }

}
