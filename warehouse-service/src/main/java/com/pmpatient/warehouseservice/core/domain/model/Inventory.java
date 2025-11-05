package com.pmpatient.warehouseservice.core.domain.model;

import com.pmpatient.warehouseservice.core.domain.model.qualifiers_and_types.LocationQualifier;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID inventoryId;

    private BigDecimal quantity;

    @LastModifiedDate
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    private LocationQualifier warehouseLocation;

    @ManyToMany(mappedBy = "inventories")
    private List<Product> products;

    private BigDecimal addStock(BigDecimal qty) {
        this.quantity = this.quantity.add(qty);
        return this.quantity;
    }

    private BigDecimal removeStock(BigDecimal qty) {
        this.quantity = this.quantity.subtract(qty);
        return this.quantity;
    }

    private boolean reserveStock(BigDecimal qty) {
        BigDecimal quantityLeft = this.quantity.subtract(qty);
        return quantityLeft.intValue() > 0;
    }
}
