package com.Chuper.Booking.rest.repository;

import com.Chuper.Booking.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerController extends CrudRepository<Customer,Long> {
}
