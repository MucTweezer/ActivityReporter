/*
Class to store the logging settings for the console.
Such logging settings include what type of logging (breaking, placing, etc) to report for which players to the console screen.
Author: Matt Brockman
*/

package me.MucTweezer.activityreporter;


import java.util.ArrayList;
import java.util.logging.Logger;


public class ConsoleProfile {

    private Logger log;

    private boolean normalLogging;

    private ArrayList<String> breakingLoggedPlayers;
    private ArrayList<String> placingLoggedPlayers;
    private ArrayList<String> combatLoggedPlayers;
    private ArrayList<String> movementLoggedPlayers;

    public ConsoleProfile(Logger tempLog) {
        log = tempLog;
        normalLogging = true;

        resetLogging();
    }
    
    public void resetLogging() {
    	breakingLoggedPlayers = new ArrayList<String>();
        placingLoggedPlayers = new ArrayList<String>();
        combatLoggedPlayers = new ArrayList<String>();
        movementLoggedPlayers = new ArrayList<String>();
    }

    // Getter methods
    public Logger getLogger() {
        return log;
    }
    public boolean getNormalLogging() {
        return normalLogging;
    }
    public ArrayList<String> getLoggedPlayers(int loggingID) {
        switch (loggingID) {
            case ActivityReporter.BREAKING_LOGGING:
                return breakingLoggedPlayers;
            case ActivityReporter.PLACING_LOGGING:
                return placingLoggedPlayers;
            case ActivityReporter.COMBAT_LOGGING:
                return combatLoggedPlayers;
            case ActivityReporter.MOVEMENT_LOGGING:
                return movementLoggedPlayers;
            default:
                log.info("Erroneous loggingID: " + loggingID + " used to call getLoggedPlayers() for the console.");
                return null;
        }
    }

    // Setter methods
    public boolean setDefaultLogging(String tempPlayerName) {
    	// Default logging is breaking, placing, and combat for the argument player.
    	
    	return (logPlayer(ActivityReporter.BREAKING_LOGGING, tempPlayerName) && logPlayer(ActivityReporter.PLACING_LOGGING, tempPlayerName) && logPlayer(ActivityReporter.COMBAT_LOGGING, tempPlayerName));
    }
    public void setNormalLogging(boolean myBool) {
        normalLogging = myBool;
    }

    // Method to add a particular player to a particular logging ArrayList
    // It should be verified that the player is online BEFORE the name is passed to this method.
    public boolean logPlayer(int loggingID, String tempPlayerName) {
        ArrayList<String> tempList = null;

        switch (loggingID) {
            case ActivityReporter.BREAKING_LOGGING:
                tempList = breakingLoggedPlayers;
                break;
            case ActivityReporter.PLACING_LOGGING:
                tempList = placingLoggedPlayers;
                break;
            case ActivityReporter.COMBAT_LOGGING:
                tempList = combatLoggedPlayers;
                break;
            case ActivityReporter.MOVEMENT_LOGGING:
                tempList = movementLoggedPlayers;
                break;
            default:
                //log.info("Erroneous loggingID: " + loggingID + " used to call logPlayer() for the console.");
                return false;
        }

        if (!tempList.contains(tempPlayerName)) { // The name is not already in the list.
            return tempList.add(tempPlayerName); // So it will be added.
        } else { // The name is already in the list
            return true; // So it will do nothing and return true, because everything is fine.
        }
    }

    // Method to remove a particular player from a particular logging ArrayList
    public boolean stopLogging(int loggingID, String tempPlayerName) {
        switch (loggingID) {
            case ActivityReporter.BREAKING_LOGGING:
                return breakingLoggedPlayers.remove(tempPlayerName);
            case ActivityReporter.PLACING_LOGGING:
                return placingLoggedPlayers.remove(tempPlayerName);
            case ActivityReporter.COMBAT_LOGGING:
                return combatLoggedPlayers.remove(tempPlayerName);
            case ActivityReporter.MOVEMENT_LOGGING:
                return movementLoggedPlayers.remove(tempPlayerName);
            default:
                log.info("Erroneous logging ID: " + loggingID + " used to call stopLogging() for the console.");
                return false;
        }
    }
    public void stopLoggingPlayer(String tempPlayerName) {
    	stopLogging(ActivityReporter.BREAKING_LOGGING, tempPlayerName);
    	stopLogging(ActivityReporter.PLACING_LOGGING, tempPlayerName);
    	stopLogging(ActivityReporter.COMBAT_LOGGING, tempPlayerName);
    	stopLogging(ActivityReporter.MOVEMENT_LOGGING, tempPlayerName);
    }

    // Method to trim the particular logging ArrayList; to be called after a removal
    // This method is separate from the removal method because it cannot be called after the removal; that method ends after the removal is complete.
    public void trimLoggingList(int loggingID) {
        switch (loggingID) {
            case ActivityReporter.BREAKING_LOGGING:
                breakingLoggedPlayers.trimToSize();
                break;
            case ActivityReporter.PLACING_LOGGING:
                placingLoggedPlayers.trimToSize();
                break;
            case ActivityReporter.COMBAT_LOGGING:
                combatLoggedPlayers.trimToSize();
                break;
            case ActivityReporter.MOVEMENT_LOGGING:
                movementLoggedPlayers.trimToSize();
                break;
            default:
                log.info("Erroneous logging ID: " + loggingID + " used to call trimLoggingList() for the console.");
                break;
        }
    }
    public void trimAllLoggingLists() {
    	trimLoggingList(ActivityReporter.BREAKING_LOGGING);
    	trimLoggingList(ActivityReporter.PLACING_LOGGING);
    	trimLoggingList(ActivityReporter.COMBAT_LOGGING);
    	trimLoggingList(ActivityReporter.MOVEMENT_LOGGING);
    }
}