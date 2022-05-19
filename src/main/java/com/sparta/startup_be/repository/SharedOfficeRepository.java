package com.sparta.startup_be.repository;

import com.sparta.startup_be.model.SharedOffice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedOfficeRepository extends JpaRepository<SharedOffice,Long> {
}
