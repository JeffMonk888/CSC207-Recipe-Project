package data.category;

import domain.entity.Category;
import usecase.category.CategoryDataAccessInterface;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Simple in-memory implementation of CategoryDataAccessInterface.
 *
 * - Categories are per user (userId field).
 * - Recipe assignments are stored as: categoryId -> set of recipeIds.
 */
public class InMemoryCategoryGateway implements CategoryDataAccessInterface {

    private final Map<Long, Category> categories = new HashMap<>();
    private final Map<Long, Set<Long>> categoryToRecipeIds = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1L);

    @Override
    public boolean categoryNameExists(Long userId, String name) {
        String normalized = name.trim().toLowerCase();
        return categories.values().stream()
                .anyMatch(c -> Objects.equals(c.getUserId(), userId) &&
                        c.getName().trim().toLowerCase().equals(normalized));
    }

    @Override
    public Category createCategory(Long userId, String name) {
        Long id = idCounter.getAndIncrement();
        Category category = new Category(id, userId, name);
        categories.put(id, category);
        return category;
    }

    @Override
    public boolean categoryExistsForUser(Long userId, Long categoryId) {
        Category c = categories.get(categoryId);
        return c != null && Objects.equals(c.getUserId(), userId);
    }

    @Override
    public List<Category> findCategoriesForUser(Long userId) {
        return categories.values().stream()
                .filter(c -> Objects.equals(c.getUserId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public void assignRecipesToCategory(Long userId, Long categoryId, List<Long> recipeIds) {
        if (!categoryExistsForUser(userId, categoryId)) {
            return; // silently ignore; interactor负责做验证
        }
        Set<Long> set = categoryToRecipeIds
                .computeIfAbsent(categoryId, k -> new HashSet<>());
        set.addAll(recipeIds);
    }

    @Override
    public List<Long> getRecipeIdsForCategory(Long userId, Long categoryId) {
        if (!categoryExistsForUser(userId, categoryId)) {
            return List.of();
        }
        Set<Long> set = categoryToRecipeIds.getOrDefault(categoryId, Set.of());
        return new ArrayList<>(set);
    }

    @Override
    public void deleteCategory(Long userId, Long categoryId) {
        if (!categoryExistsForUser(userId, categoryId)) {
            return;
        }
        categories.remove(categoryId);
        categoryToRecipeIds.remove(categoryId);
    }
}
