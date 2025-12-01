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
        for (FridgeRow row : rows) {
            if (row.userId == userId && row.item.equals(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addItem(Long userId, String item) {
        String trimmed = item.trim();
        if (hasItem(userId, trimmed)) return;

        long newId = ++idCounter;
        rows.add(new FridgeRow(newId, userId, trimmed));

        saveToFile();
    }

    @Override
    public boolean removeItem(Long userId, String item) {
        String trimmed = item.trim();

        for (int i = 0; i < rows.size(); i++) {
            FridgeRow row = rows.get(i);

            if (row.userId == userId && row.item.equals(trimmed)) {
                rows.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getItems(Long userId) {
        List<String> result = new ArrayList<>();
        for (FridgeRow row : rows) {
            if (row.userId == userId) {
                result.add(row.item);
            }
        }
        return result;
    }

    // File I/O

    private void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String header = br.readLine();
            String line;

            long maxId = 0;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                // id,userId,item
                String[] parts = line.split(",", 3);
                long id = Long.parseLong(parts[0]);
                long userId = Long.parseLong(parts[1]);
                String item = parts[2];

                rows.add(new FridgeRow(id, userId, item));
                maxId = Math.max(maxId, id);
            }

            this.idCounter = maxId;

        }
        catch (IOException e) {
            throw new RuntimeException("Error reading fridge CSV", e);
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {

            bw.write(HEADER);
            bw.newLine();

            for (FridgeRow row : rows) {
                bw.write(row.id + "," + row.userId + "," + row.item);
                bw.newLine();
            }

        }
        catch (IOException e) {
            throw new RuntimeException("Error writing fridge CSV", e);
        }
    }

    // Inner Data Class
    private static class FridgeRow {
        long id;
        long userId;
        String item;

        FridgeRow(long id, long userId, String item) {
            this.id = id;
            this.userId = userId;
            this.item = item;
        }
    }
}
