package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private MapsClient mapsClient;
    private PriceClient priceClient;
    private final CarRepository carRepository;

    public CarService(MapsClient mapsClient, PriceClient priceClient, CarRepository carRepository) {
        this.mapsClient = mapsClient;
        this.priceClient = priceClient;
        this.carRepository = carRepository;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return carRepository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Optional<Car> optionalCar = carRepository.findById(id);

        if(optionalCar.isPresent()){
            Car car = optionalCar.get();
            car.setPrice(priceClient.getPrice(id));
            car.setLocation(mapsClient.getAddress(car.getLocation()));
            return car;
        }else {
            throw new CarNotFoundException();
        }

    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return carRepository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        carToBeUpdated.setCondition(car.getCondition());
                        return carRepository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return carRepository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        Optional<Car> optionalCar = carRepository.findById(id);

        if(optionalCar.isPresent()) {
            Car car = optionalCar.get();
            carRepository.delete(car);
        }else {
            throw new CarNotFoundException();
        }

    }
}
