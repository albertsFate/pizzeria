package com.alberts.pizza.persistence.repository;

import com.alberts.pizza.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository  extends ListCrudRepository<CustomerEntity, String> {

    //la notación @Query se usa cuando se quiere poner un Query en conjunto con objetos
    @Query(value = "SELECT c FROM CustomerEntity c WHERE c.phoneNumber = :phone")
    CustomerEntity findByPhone(@Param("phone") String phone); //@Param es para mander el nombre del parametro para que se pueda usar desde la consulta

}
