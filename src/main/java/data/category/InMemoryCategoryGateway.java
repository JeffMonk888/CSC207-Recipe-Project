package data.category;

import domain.entity.Category;
import usecase.category.CategoryDataAccessInterface;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Simple in-memory implementation of CategoryDataAccessInterface.
 *
 * - Categories are per user (userId field).
 * - Recipe assignments are stored as: categoryId -> set of recipeIds (numeric form of recipeKey).
 *
 * This class is only used in demos and tests; the real project could later
 * replace it with a persistent implementation.
 */
public class InMemoryCategoryGateway implements CategoryDataAccessInterface {

    private final Map<Long, Category> categories = new HashMap<>();
    private final Map<Long, Set<String>> categoryToRecipeIds = new HashMap<>();

    private final AtomicLong idCounter = new AtomicLong(0L);

    @Override
    public boolean categoryNameExists(Long userId, String name) {
        if (name == null) {
            return false;
        }
        String lower = name.toLowerCase(Locale.ROOT);
        return categories.values().stream()
                .anyMatch(c ->
                        Objects.equals(c.getUserId(), userId)
                                && c.getName() != null
                                && c.getName().toLowerCase(Locale.ROOT).equals(lower)
                );
    }

    @Override
    public Category createCategory(Long userId, String name) {
        Long id = idCounter.incrementAndGet();
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
                .sorted(Comparator.comparing(Category::getName))
                .collect(Collectors.toList());
    }

    @Override
    public void assignRecipesToCategory(Long userId, Long categoryId, List<String> recipeIds) {
        if (!categoryExistsForUser(userId, categoryId) || recipeIds == null || recipeIds.isEmpty()) {
            return;
        }
        Set<String> set = categoryToRecipeIds.computeIfAbsent(categoryId, k -> new HashSet<>());
        set.addAll(recipeIds);
    }

    @Override
    public List<String> getRecipeIdsForCategory(Long userId, Long categoryId) {
        if (!categoryExistsForUser(userId, categoryId)) {
            return Collections.emptyList();
        }
        Set<String> set = categoryToRecipeIds.getOrDefault(categoryId, Collections.emptySet());
        return new ArrayList<>(set);
    }

    @Override
    public void removeRecipeFromCategory(Long userId, Long categoryId, String recipeId) {
        if (!categoryExistsForUser(userId, categoryId) || recipeId == null) {
            return;
        }
        Set<String> set = categoryToRecipeIds.get(categoryId);
        if (set != null) {
            set.remove(recipeId);
            if (set.isEmpty()) {
                categoryToRecipeIds.remove(categoryId);
            }
        }
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
