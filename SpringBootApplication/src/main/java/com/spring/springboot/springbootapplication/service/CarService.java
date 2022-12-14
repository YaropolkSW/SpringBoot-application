package com.spring.springboot.springbootapplication.service;

import com.spring.springboot.springbootapplication.dao.CarDAO;
import com.spring.springboot.springbootapplication.dao.ShopDAO;
import com.spring.springboot.springbootapplication.dto.CarDTO;
import com.spring.springboot.springbootapplication.entity.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    private static final String NO_SUCH_CAR_EXC_MESSAGE = "There is no car with Id = %s";
    private static final String NO_SUCH_SHOP_EXC_MESSAGE = "There is no shop with Id = %s";

    private final CarDAO carDAO;
    private final ShopDAO shopDAO;

    @Autowired
    public CarService(
        final CarDAO carDAO,
        final ShopDAO shopDAO
    ) {
        this.carDAO = carDAO;
        this.shopDAO = shopDAO;
    }

    public List<CarDTO> getAllCars() {
        return carDAO
            .findAll()
            .stream()
            .map(CarDTO::of)
            .collect(Collectors.toList());
    }

    public List<CarDTO> getCarsInShop(final int shopId) {
        return shopDAO
            .findById(shopId)
            .orElseThrow(() -> new EntityNotFoundException(String.format(NO_SUCH_SHOP_EXC_MESSAGE, shopId)))
            .getCars().stream()
            .map(CarDTO::of)
            .collect(Collectors.toList());
    }

    public CarDTO getCarById(final int carId) {
        final Car car = carDAO
            .findById(carId)
            .orElseThrow(() -> new EntityNotFoundException(String.format(NO_SUCH_CAR_EXC_MESSAGE, carId)));

        final CarDTO carDTO = CarDTO.of(car);

        return carDTO;
    }

    @Transactional
    public void removeCarFromShopById(final int shopId, final int carId) {
        final Car car = carDAO
            .findById(carId)
            .orElseThrow(() -> new EntityNotFoundException(String.format(NO_SUCH_CAR_EXC_MESSAGE, carId)));

        carDAO.deleteConnectionBetweenCarAndClient(car);
        carDAO.deleteConnectionBetweenCarAndShop(shopId, carId);
    }

    @Transactional
    public void saveNewCar(final CarDTO carDTO) {

        final Car car = Car.builder()
            .brand(carDTO.getBrand())
            .model(carDTO.getModel())
            .ageOfProduce(carDTO.getAgeOfProduce())
            .price(carDTO.getPrice())
            .build();

        carDAO.save(car);
    }

    @Transactional
    public void deleteCarEverywhere(final int carId) {
        final Car car = carDAO
            .findById(carId)
            .orElseThrow(() -> new EntityNotFoundException(String.format(NO_SUCH_CAR_EXC_MESSAGE, carId)));

        carDAO.deleteConnectionBetweenCarAndClient(car);
        carDAO.deleteConnectionBetweenCarAndEveryShop(carId);
        carDAO.deleteById(carId);
    }
}
