package com.petcare.petcareapp.controller;

import com.petcare.petcareapp.domain.model.ProductCategory;
import com.petcare.petcareapp.domain.model.TargetSpecies;
import com.petcare.petcareapp.dto.product.ProductDto;
import com.petcare.petcareapp.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "Public APIs for browsing products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "List all available products", description = "Retrieves a paginated list of products. Supports filtering by name, category, and target species.")
    @Parameters({
        @Parameter(name = "page", description = "Page number (0-indexed)", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
        @Parameter(name = "sort", description = "Sort criteria (e.g., 'name,asc' or 'price,desc')", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
        @Parameter(name = "name", description = "Filter by product name (case-insensitive, partial match)", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
        @Parameter(name = "category", description = "Filter by product category", in = ParameterIn.QUERY, schema = @Schema(implementation = ProductCategory.class)),
        @Parameter(name = "species", description = "Filter by target species", in = ParameterIn.QUERY, schema = @Schema(implementation = TargetSpecies.class))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))) // Page of ProductDto
    })
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) TargetSpecies species) {
        Page<ProductDto> products = productService.getAllProducts(pageable, name, category, species);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Retrieves details for a specific product by its ID.")
    @Parameter(name = "productId", description = "ID of the product to retrieve", required = true, in = ParameterIn.PATH)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId) {
        try {
            ProductDto productDto = productService.getProductById(productId);
            return ResponseEntity.ok(productDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
