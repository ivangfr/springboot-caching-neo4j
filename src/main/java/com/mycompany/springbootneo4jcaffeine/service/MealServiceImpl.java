package com.mycompany.springbootneo4jcaffeine.service;

import com.mycompany.springbootneo4jcaffeine.model.Meal;
import com.mycompany.springbootneo4jcaffeine.repository.MealRepository;
import org.springframework.stereotype.Service;

@Service
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;

    public MealServiceImpl(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    @Override
    public Meal saveMeal(Meal meal) {
        return mealRepository.save(meal);
    }

    @Override
    public void deleteMeal(Meal meal) {
        mealRepository.delete(meal);
    }

//    @Override
//    public Meal validateAndGetMealById(UUID mealId) throws MealNotFoundException {
//        return mealRepository.findById(mealId.toString()).orElseThrow(() -> new MealNotFoundException(mealId));
//    }
}
