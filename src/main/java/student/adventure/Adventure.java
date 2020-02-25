package student.adventure;

//import sun.security.util.Length;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Adventure {

    /**
     * Length of the string "go "
     */
    private static final int LENGTH_OF_GO = 3;

    /**
     * As long as the player has not reached the final room, the game continues
     * This function then handles user input to change rooms if the user enters a valid input
     * Relies on helper functions to make this happen
     *
     * @param gameLayout A Layout object that contains the list of the rooms, starting & ending room
     *                   Used those objects to create functionality
     */
    public static void continueGame(Layout gameLayout) {
        Scanner consoleInput = new Scanner(System.in);
        String currentRoomStr = gameLayout.getStartingRoom(); //string
        boolean isGameOver = (currentRoomStr.equals(gameLayout.getEndingRoom()));
        System.out.println("Instructions for commands: \nType in 'go (valid direction)' to go somewhere" +
                "\nType in 'add (item)' and answer the puzzle to add an item to the room" +
                "\nType in 'remove (item)' to remove an item from" +
                " the room \nType in 'examine' to see the room's description and items available\n");

        //while the current room is not the final room, continue to allow user to play game
        while (!isGameOver) {
            Room currentRoom = getRoomObj(currentRoomStr, gameLayout.getRooms());
            printRoomDescription(currentRoom);
            String userAnswer = consoleInput.nextLine();

            if (isCommandLessThanThreeCharacters(userAnswer)) {
                System.out.println("I don't understand '" + userAnswer + "'");
            } else if (userAnswer.equals("quit") || userAnswer.equals("exit")) {
                System.exit(0);
            } else if (userAnswer.equalsIgnoreCase("examine")) {
                //this will exit this iteration of the loop and print out the description of the room
                continue;
            } else if (isCommandInvalid(userAnswer)) {
                System.out.println("I don't understand '" + userAnswer + "'");
            } else {
                if (addedOrRemovedItem(currentRoom, userAnswer)) {
                    continue;
                }
                //calls on helper function to check if the room that they want to go to is valid
                boolean isValid = gameLayout.isGivenRoomValid(userAnswer, currentRoomStr);

                // if they typed in a valid command
                if (isValid && userAnswer.substring(0, LENGTH_OF_GO).equalsIgnoreCase("go ")) {
                    userAnswer = userAnswer.toLowerCase();
                    //changes the current room string to where they'd like to go and updates isGameOver
                    currentRoomStr = gameLayout.changeRoom(userAnswer, currentRoomStr);
                    isGameOver = checkGameOver(gameLayout, currentRoomStr);
                } else if (containsGo(userAnswer.substring(0, LENGTH_OF_GO))) {
                    //if they typed in somewhere they can't go
                    System.out.println("I can't " + userAnswer);
                }
            }
        }

        System.out.println("You've reached the final room, the game is over!");
    }

    /**
     * This function takes a string of a room object, returns the corresponding room object
     * Used to change the room object when user moves
     *
     * @param room The current room the user is in (as a string object)
     * @param roomObjs The list of all rooms
     * @return Corresponding (to String) room object
     */
    static Room getRoomObj(String room, List<Room> roomObjs) {
        for (int i = 0; i < roomObjs.size(); i++) {
            Room currentRoom = roomObjs.get(i);
            if (currentRoom.getName().equals(room)) {
                return currentRoom;
            }
        }
        return null;
    }

    /**
     * Takes directions list and outputs a String of all to display to console
     *
     * @param directionsList A list of possible directions that the user can move to
     * @return String of directions with space between them
     */
    static String combineDirections(List<Direction> directionsList) {
        if (directionsList == null || directionsList.size() == 0) {
            return null;
        }

        StringBuilder returned = new StringBuilder("");
        for (Direction direction : directionsList) {
            returned.append(direction.getDirectionName() + " ");
        }

        return returned.toString();
    }

    /**
     * Takes items list and outputs a String of all to display to console
     *
     * @param itemsList A list of possible items that the user sees
     * @return String of items with space between them
     */
    static String combineItems(List<String> itemsList) {
        if (itemsList == null || itemsList.size() == 0) {
            return "";
        }

        StringBuilder returned = new StringBuilder("");
        for (String item : itemsList) {
            returned.append(item + " ");
        }

        return returned.toString();
    }

    /**
     * Checks if the game is over by checking if the current room is the ending room (used every time the user goes
     * somewhere valid)
     * @param gameLayout the game Layout being played on
     * @param currentRoomString the current room, as a string
     * @return whether the game is over or not
     */
    static boolean checkGameOver(Layout gameLayout, String currentRoomString) {
        if (currentRoomString.equals(gameLayout.getEndingRoom())) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param currentRoom the current room, as a Room object
     * @param userAnswer the user's input into the console
     * @return
     */
    static boolean addedOrRemovedItem(Room currentRoom, String userAnswer) {
        String addOrRemove = userAnswer.substring(0, userAnswer.indexOf(" "));
        String userItemChoice = userAnswer.substring(userAnswer.indexOf(" ") + 1);

        if (currentRoom.getItems() == null) {
            return false;
        } else if (addOrRemove.equalsIgnoreCase("add")
                && !(currentRoom.getItems().contains(userItemChoice))) {
            //if they said add + (item) that doesn't already exist in the room at the time
            List<String> items = currentRoom.getItems();
            items.add(userItemChoice);
            currentRoom.setItems(items);
            return true;
        }

        for (String item : currentRoom.getItems()) {
            //if they said remove + (item) that exists in the room
             if (addOrRemove.equalsIgnoreCase("remove")
                    && userItemChoice.equalsIgnoreCase(item)) {
                List<String> items = currentRoom.getItems();
                items.remove(item);
                currentRoom.setItems(items);
                return true;
            }
        }

        if (addOrRemove.equalsIgnoreCase("add")
                || addOrRemove.equalsIgnoreCase("remove")) {
            System.out.println("I can't do that!");
            return false;
        }

        return false;
    }

    /**
     * Checks for if a string contains go; makes code easier to test and read
     * @param userAnswer the user input into the console
     * @return whether a string contains go or not
     */
    static boolean containsGo(String userAnswer) {
        if (userAnswer.toLowerCase().contains("go ")) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the user input is invalid (contains add, remove or go); easier for testing too
     * @param userAnswer the user input into the console
     * @return if the input is invalid
     */
    static boolean isCommandInvalid(String userAnswer) {
        if (!(containsGo(userAnswer.substring(0, LENGTH_OF_GO))) && !userAnswer.contains("add ")
                && !userAnswer.contains("remove ")) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the user input is less than three characters (invalid argument); also makes it easier to test
     * @param userAnswer the user input into the console
     * @return if the input is less than three characters
     */
    static boolean isCommandLessThanThreeCharacters(String userAnswer) {
        if (userAnswer.length() < LENGTH_OF_GO) {
            return true;
        }
        return false;
    }

    /**
     * This method makes the code easier to read and prints the room's description, directions, and items
     * @param currentRoom The current room the player is in
     */
    static void printRoomDescription(Room currentRoom) {
        System.out.println(currentRoom.getDescription());
        String directions = combineDirections(currentRoom.getDirections());
        System.out.println("From here, you can go: " + directions);
        String items = combineItems(currentRoom.getItems());
        System.out.println("Items visible: " + items);
    }

}