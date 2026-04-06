package com.groupb.routemanagement.repository;

import com.groupb.routemanagement.model.entity.MapVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MapVersionRepository extends JpaRepository<MapVersion, String> {
    Optional<MapVersion> findByIsCurrentTrue();
}
