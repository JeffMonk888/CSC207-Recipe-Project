package data.saved_ingredient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import usecase.common.FridgeAccess;

public class FileFridgeAccessObject implements FridgeAccess {

    public static final String REGEX = ",";

    private static final String HEADER = "id,userId,item";

    private final File csvFile;
    private final List<FridgeRow> rows = new ArrayList<>();
    private long idCounter;

    // Constructor
    public FileFridgeAccessObject(String csvPath) {
        this.csvFile = new File(csvPath);
        if (!csvFile.exists() || csvFile.length() == 0) {
            saveToFile();
        }
        else {
            loadFromFile();
        }
    }

    // FridgeAccess Methods

    @Override
    public boolean hasItem(Long userId, String item) {
        boolean hasItem = false;
        for (FridgeRow row : rows) {
            if (row.getUserId() == userId && row.getItem().equals(item)) {
                hasItem = true;
                break;
            }
        }
        return hasItem;
    }

    @Override
    public void addItem(Long userId, String item) {
        final String trimmed = item.trim();
        if (!hasItem(userId, trimmed)) {
            final long newId = ++idCounter;
            rows.add(new FridgeRow(newId, userId, trimmed));

            saveToFile();
        }
    }

    @Override
    public boolean removeItem(Long userId, String item) {
        boolean removed = false;
        final String trimmed = item.trim();

        for (int i = 0; i < rows.size(); i++) {
            final FridgeRow row = rows.get(i);

            if (row.getUserId() == userId && row.getItem().equals(trimmed)) {
                rows.remove(i);
                saveToFile();
                removed = true;
                break;
            }
        }
        return removed;
    }

    @Override
    public List<String> getItems(Long userId) {
        final List<String> result = new ArrayList<>();
        for (FridgeRow row : rows) {
            if (row.getUserId() == userId) {
                result.add(row.getItem());
            }
        }
        return result;
    }

    // File I/O

    private void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            final String header = br.readLine();
            String line;

            long maxId = 0;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                // id,userId,item
                final String[] parts = line.split(REGEX, 3);
                final long id = Long.parseLong(parts[0]);
                final long userId = Long.parseLong(parts[1]);
                final String item = parts[2];

                rows.add(new FridgeRow(id, userId, item));
                maxId = Math.max(maxId, id);
            }

            this.idCounter = maxId;

        }
        catch (IOException exception) {
            throw new RuntimeException("Error reading fridge CSV", exception);
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {

            bw.write(HEADER);
            bw.newLine();

            for (FridgeRow row : rows) {
                bw.write(row.getId() + REGEX + row.userId + REGEX + row.getItem());
                bw.newLine();
            }

        }
        catch (IOException exception) {
            throw new RuntimeException("Error writing fridge CSV", exception);
        }
    }

    // Inner Data Class
    private static class FridgeRow {
        private long id;
        private long userId;
        private String item;

        FridgeRow(long id, long userId, String item) {
            this.id = id;
            this.userId = userId;
            this.item = item;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public long getId() {
            return id;
        }

        public long getUserId() {
            return userId;
        }

        public String getItem() {
            return item;
        }
    }
}
