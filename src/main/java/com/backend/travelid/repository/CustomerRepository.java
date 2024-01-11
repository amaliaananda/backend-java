package com.backend.travelid.repository;
import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.oauth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    @Query("FROM Customer c WHERE LOWER(c.identityNumber) = LOWER(:identityNumber)")
    Customer checkExistingIdentityNumber(String identityNumber);
}

