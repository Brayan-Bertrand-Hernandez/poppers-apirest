package com.project.springapirest.repository;

import com.project.springapirest.model.User;
import com.project.springapirest.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepository extends JpaRepository<User, Long> {
    @Query("from Country")
    List<Country> findAllCountries();
}
