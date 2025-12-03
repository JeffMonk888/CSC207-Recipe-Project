package data.category;

import domain.entity.Category;
import usecase.category.CategoryDataAccessInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Category gateway backed by in-memory maps + CSV files.
 *
 * - Categories are per user (userId field).
 * - Recipe assignments are stored as: categoryId -> set of recipeIds (the recipeKey).
 *
 * This class still keeps everything in memory for fast access,
 * but all changes are persisted into CSV files so that data is not lost
 * when the application exits.
 */
public class InMemoryCategoryGateway implements CategoryDataAccessInterface {

    /** CSV file for categories: categoryId,userId,name */
    private static final String DEFAULT_CATEGORY_FILE = "categories.csv";

    /** CSV file for category-recipe links: userId,categoryId,recipeId */
    private static final String DEFAULT_LINK_FILE = "category_recipe_links.csv";

    /** Map of categoryId -> Category. */
    private final Map<Long, Category> categories = new HashMap<>();

    /** Map of categoryId -> set of recipeIds assigned to this category. */
    private final Map<Long, Set<String>> categoryToRecipeIds = new HashMap<>();

    /** Simple counter for new category IDs. */
    private final AtomicLong nextCategoryId = new AtomicLong(1L);

    private final String categoryFilePath;
    private final String linkFilePath;

    // ===== Constructors =====

    /**
     * Default constructor using default CSV file names.
     */
    public InMemoryCategoryGateway() {
        this(DEFAULT_CATEGORY_FILE, DEFAULT_LINK_FILE);
    }

    /**
     * Constructor allowing custom file paths (useful for tests).
     */
    public InMemoryCategoryGateway(String categoryFilePath, String linkFilePath) {
        this.categoryFilePath = categoryFilePath;
        this.linkFilePath = linkFilePath;
        loadFromFiles();
    }

    // ====== Interface methods ======

    @Override
    public boolean categoryNameExists(Long userId, String name) {
        if (userId == null || name == null) {
            return false;
        }
        String target = name.trim().toLowerCase(Locale.ROOT);
        for (Category c : categories.values()) {
            if (userId.equals(c.getUserId())
                    && c.getName() != null
                    && c.getName().trim().toLowerCase(Locale.ROOT).equals(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Category createCategory(Long userId, String name) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        long id = nextCategoryId.getAndIncrement();
        Category category = new Category(id, userId, name);
        categories.put(id, category);
        // ensure mapping exists
        categoryToRecipeIds.computeIfAbsent(id, k -> new HashSet<>());
        saveToFiles();
        return category;
    }

    @Override
    public boolean categoryExistsForUser(Long userId, Long categoryId) {
        if (userId == null || categoryId == null) {
            return false;
        }
        Category c = categories.get(categoryId);
        return c != null && userId.equals(c.getUserId());
    }

    @Override
    public List<Category> findCategoriesForUser(Long userId) {
        List<Category> result = new ArrayList<>();
        if (userId == null) {
            return result;
        }
        for (Category c : categories.values()) {
            if (userId.equals(c.getUserId())) {
                result.add(c);
            }
        }
        // sort by id for stable order (optional)
        result.sort(Comparator.comparingLong(Category::getId));
        return result;
    }

    @Override
    public void assignRecipesToCategory(Long userId, Long categoryId, List<String> recipeIds) {
        if (!categoryExistsForUser(userId, categoryId) || recipeIds == null || recipeIds.isEmpty()) {
            return;
        }
        Set<String> set = categoryToRecipeIds.computeIfAbsent(categoryId, k -> new HashSet<>());
        set.addAll(recipeIds);
        saveToFiles();
    }

    @Override
    public List<String> getRecipeIdsForCategory(Long userId, Long categoryId) {
        if (!categoryExistsForUser(userId, categoryId)) {
            return new ArrayList<>();
        }
        Set<String> set = categoryToRecipeIds.get(categoryId);
        if (set == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(set);
    }

    @Override
    public void removeRecipeFromCategory(Long userId, Long categoryId, String recipeId) {
        if (!categoryExistsForUser(userId, categoryId) || recipeId == null) {
            return;
        }
        Set<String> set = categoryToRecipeIds.get(categoryId);
        if (set == null) {
            return;
        }
        if (set.remove(recipeId)) {
            if (set.isEmpty()) {
                categoryToRecipeIds.remove(categoryId);
            }
            saveToFiles();
        }
    }

    @Override
    public void deleteCategory(Long userId, Long categoryId) {
        if (!categoryExistsForUser(userId, categoryId)) {
            return;
        }
        categories.remove(categoryId);
        categoryToRecipeIds.remove(categoryId);
        saveToFiles();
    }

    // ====== File persistence ======

    /**
     * Loads categories and links from CSV files into memory.
     * If files do not exist, this is treated as "no data yet".
     */
    private void loadFromFiles() {
        loadCategoriesFromFile();
        loadLinksFromFile();

        // Update nextCategoryId based on existing categories.
        long maxId = 0;
        for (Long id : categories.keySet()) {
            if (id != null && id > maxId) {
                maxId = id;
            }
        }
        nextCategoryId.set(maxId + 1);
    }

    private void loadCategoriesFromFile() {
        File file = new File(categoryFilePath);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            // Optional header; if present and starts with "categoryId", skip it.
            if (line != null && line.startsWith("categoryId")) {
                line = reader.readLine();
            }

            while (line != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",", 3);
                    if (parts.length == 3) {
                        Long categoryId = parseLongSafe(parts[0]);
                        Long userId = parseLongSafe(parts[1]);
                        String name = parts[2];
                        if (categoryId != null && userId != null) {
                            Category c = new Category(categoryId, userId, name);
                            categories.put(categoryId, c);
                        }
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            // For this course project, printing the stack trace is acceptable.
            e.printStackTrace();
        }
    }

    private void loadLinksFromFile() {
        File file = new File(linkFilePath);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            // Optional header; if present and starts with "userId", skip it.
            if (line != null && line.startsWith("userId")) {
                line = reader.readLine();
            }

            while (line != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",", 3);
                    if (parts.length == 3) {
                        Long userId = parseLongSafe(parts[0]);
                        Long categoryId = parseLongSafe(parts[1]);
                        String recipeId = parts[2];
                        // Only load if the category actually exists for that user.
                        if (userId != null && categoryId != null
                                && categoryExistsForUser(userId, categoryId)
                                && recipeId != null && !recipeId.isEmpty()) {

                            Set<String> set =
                                    categoryToRecipeIds.computeIfAbsent(categoryId, k -> new HashSet<>());
                            set.add(recipeId);
                        }
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes all categories and links to CSV files.
     * This method is called after any mutation.
     */
    private void saveToFiles() {
        saveCategoriesToFile();
        saveLinksToFile();
    }

    private void saveCategoriesToFile() {
        File file = new File(categoryFilePath);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            writer.write("categoryId,userId,name");
            writer.newLine();

            for (Category c : categories.values()) {
                if (c.getId() == null || c.getUserId() == null) {
                    continue;
                }
                String name = c.getName() == null ? "" : c.getName().replace(",", " ");
                writer.write(c.getId() + "," + c.getUserId() + "," + name);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveLinksToFile() {
        File file = new File(linkFilePath);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            writer.write("userId,categoryId,recipeId");
            writer.newLine();

            for (Map.Entry<Long, Set<String>> entry : categoryToRecipeIds.entrySet()) {
                Long categoryId = entry.getKey();
                Set<String> recipes = entry.getValue();
                if (recipes == null || recipes.isEmpty()) {
                    continue;
                }
                Category c = categories.get(categoryId);
                if (c == null || c.getUserId() == null) {
                    continue;
                }
                Long userId = c.getUserId();
                for (String recipeId : recipes) {
                    if (recipeId == null || recipeId.isEmpty()) {
                        continue;
                    }
                    writer.write(userId + "," + categoryId + "," + recipeId);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== Utility =====

    private static Long parseLongSafe(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
