package com.example.back_end.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.back_end.model.Brand;
import com.example.back_end.repository.BrandRepository;

@ExtendWith(MockitoExtension.class)
class BrandServiceImplTests {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl service;

    @Test
    void createChecksSlugUniquenessAgainstSlugColumn() {
        Brand brand = new Brand();
        brand.setName("New brand");
        brand.setSlug("existing-slug");

        when(brandRepository.existsBySlugIgnoreCase("existing-slug")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createBrand(brand)
        );

        assertEquals("A Brand with existing-slug already exist", exception.getMessage());
    }
}
