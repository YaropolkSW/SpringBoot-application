package com.spring.springboot.springbootapplication.dao;

import com.spring.springboot.springbootapplication.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface CarDAO extends JpaRepository<Car, Integer> {
    @Query(value = "SELECT * FROM car c LEFT JOIN shop_car sc ON c.car_id = sc.car_id " +
           "LEFT JOIN shop s ON sc.shop_id = s.shop_id WHERE s.shop_id = :shopId",
           nativeQuery = true)
    List<Car> getCarsInShopByShopId(@Param("shopId") final int shopId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE client SET car_id = NULL WHERE car_id = :carId", nativeQuery = true)
    void deleteConnectionBetweenCarAndClient(@Param("carId") final int carId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM shop_car WHERE car_id = :carId AND shop_id = :shopId", nativeQuery = true)
    void deleteConnectionBetweenCarAndShop(@Param("shopId") final int shopId, @Param("carId") final int carId);

}
