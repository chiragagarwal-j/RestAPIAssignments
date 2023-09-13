package com.spring.learningRest.repository;

import org.springframework.data.repository.CrudRepository;

import com.spring.learningRest.entity.CycleStock;

public interface CycleStockRepository extends CrudRepository<CycleStock, Integer> {

}