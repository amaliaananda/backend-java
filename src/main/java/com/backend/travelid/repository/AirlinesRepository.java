package com.backend.travelid.repository;


import com.backend.travelid.entity.Airlines;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface AirlinesRepository extends PagingAndSortingRepository<Airlines, Long> {
    Airlines findOneByAirline(String airline);
    Optional<Airlines> findByAirline(String airline);
}

