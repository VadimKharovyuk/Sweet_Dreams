package com.example.sweet_dreams.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class IngredientValidator {
    private static final int MAX_INGREDIENT_LENGTH = 100;
    private static final Pattern VALID_INGREDIENT_PATTERN =
            Pattern.compile("^[а-яА-Яa-zA-Z0-9\\s,.-]+$");

    public boolean isValidIngredient(String ingredient) {
        if (ingredient == null || ingredient.trim().isEmpty()) {
            return false;
        }

        String trimmed = ingredient.trim();
        if (trimmed.length() > MAX_INGREDIENT_LENGTH) {
            return false;
        }

        return VALID_INGREDIENT_PATTERN.matcher(trimmed).matches();
    }
}
