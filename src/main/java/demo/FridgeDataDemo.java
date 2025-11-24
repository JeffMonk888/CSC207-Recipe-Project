package demo;

import data.saved_ingredient.FileFridgeAccessObject;
import usecase.common.FridgeAccess;

public class FridgeDataDemo {
    public static void main(String[] args) {

        // Path must match where your fridge_items.csv actually lives
        FridgeAccess fridge = new FileFridgeAccessObject("fridge_items.csv");

        Long userId = 1L;

        System.out.println("ADD ITEMS");
        fridge.addItem(userId, "milk");
        fridge.addItem(userId, "eggs");
        fridge.addItem(userId, "spinach");

        System.out.println(fridge.getItems(userId));

        System.out.println("REMOVE ITEM");
        fridge.removeItem(userId, "eggs");
        System.out.println(fridge.getItems(userId));

        System.out.println("TRY REMOVE NON-EXISTENT");
        System.out.println(fridge.removeItem(userId, "chicken"));

        System.out.println("FINAL STATE");
        System.out.println(fridge.getItems(userId));
    }
}
