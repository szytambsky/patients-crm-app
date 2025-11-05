package com.pmpatient.warehouseservice.core.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productId;

    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private String imageUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedDate
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_inventory",
            joinColumns = @JoinColumn(referencedColumnName = "product_id"),
            inverseJoinColumns = @JoinColumn(referencedColumnName = "inventory_id")
    )
    private List<Inventory> inventories;

    private BigDecimal updatePrice(BigDecimal newPrice) {
        this.price = newPrice;
        return newPrice;
    }

    private ProductCategory changeCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
        return productCategory;
    }
}
