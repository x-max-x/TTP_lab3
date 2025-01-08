package com.tpp.tpplab3.repository;


import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tpp.tpplab3.models.Shop;


@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {

}
