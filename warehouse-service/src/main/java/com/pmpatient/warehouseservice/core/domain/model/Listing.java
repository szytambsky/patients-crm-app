package com.pmpatient.warehouseservice.core.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "listing")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID listingId;

    private UUID sellerId;
    private BigDecimal totalPrice;
    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
