/*
Main class for the Activity Reporter Plugin.
Author: Matt Brockman
*/


package me.MucTweezer.activityreporter;


import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.ListIterator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class ActivityReporter extends JavaPlugin {

    // Global constants to be used across the plugin for identifying which type of logging is being dealt with. These should help cut down on the amount of times certain code blocks are reproduced.
    // These will be used as arguments for various methods and in switch statements in those methods. The corresponding argument will be labelled loggingID.
    public static final int BREAKING_LOGGING = 0;
    public static final int PLACING_LOGGING = 1;
    public static final int COMBAT_LOGGING = 2;
    public static final int MOVEMENT_LOGGING = 3;

    private ArrayList<PlayerProfile> myPlayerProfiles;
    private Logger log;
    private ConsoleProfile myConsole;

    public void onEnable() {
        log = this.getLogger();
        myConsole = new ConsoleProfile(log);
        myPlayerProfiles = new ArrayList<PlayerProfile>();

        new LoggingListener(this, log);

        log.info("Activity Reporter has been enabled.");
    }
    public void onDisable() {
        log.info("Activity Reporter has been disabled.");
    }

    // Methods to handle the commands entered by the user or the console.
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	if (correctNumberOfArguments(args)) {
    		if (cmd.getName().equals("enablelogging")) {
    			switch (args.length) {
    				case 0:
    					return enableLoggingBare(sender);
    				case 1:
    					return enableLoggingPlayer(sender, args[0]);
    				case 2:
    					return enableLoggingTypeForPlayer(sender, args[0], args[1]);
    				default:
    					sender.sendMessage("Invalid number of arguments."); // This message will never be shown. I put it here for posterity.
    			}
    			return true;
    		} else if (cmd.getName().equals("disablelogging")) {
    			switch (args.length) {
    				case 0:
    					disableLoggingBare(sender);
    					break;
    				case 1:
    					disableLoggingPlayer(sender, args[0]);
    					break;
    				case 2:
    					disableLoggingTypeForPlayer(sender, args[0], args[1]);
    					break;
    				default:
    					sender.sendMessage("Invalid number of arguments.");
    			}
    			return true;
    		} else {
    			return false; // not one of the two valid commands
    		}
    	} else {
    		return false; // Incorrect number of arguments
    	}
    }
    private boolean correctNumberOfArguments(String[] args) {
    	if (args.length > 2) {
    		return false;
    	} else {
    		return true;
    	}
    }
    private int convertToLoggingType(String tempString) {
    	// The user will input a command using a string. This method will convert it to the integer being used by the code.
    	// Perhaps if I had coded those constants better (say, as enums) then I wouldn't have to do this. Oh well.
    	
    	if (tempString.equalsIgnoreCase("breaking")) {
    		return BREAKING_LOGGING;
    	} else if (tempString.equalsIgnoreCase("placing")) {
    		return PLACING_LOGGING;
    	} else if (tempString.equalsIgnoreCase("combat")) {
    		return COMBAT_LOGGING;
    	} else if (tempString.equalsIgnoreCase("movement")) {
    		return MOVEMENT_LOGGING;
    	} else {
    		return -1;
    	}
    }
    private boolean isLoggedOn(String tempPlayerName) {
    	Player[] players = this.getServer().getOnlinePlayers();
    	boolean foundPlayer = false;
    	
    	for (int i = 0; i < players.length; i++) {
    		if (players[i].getName().equals(tempPlayerName)) {
    			foundPlayer = true;
    		}
    	}
    	
    	return foundPlayer;
    }
    
    // Commands with no arguments.
    public boolean enableLoggingBare(CommandSender sender) {
    	boolean success = true;
    	
    	if (sender instanceof Player) {
    		PlayerProfile tempProfile = getPlayerProfile(sender.getName());
    		if (tempProfile != null) {
    			Player[] players = this.getServer().getOnlinePlayers();
    			for (int i = 0; i<players.length; i++) {
    				if (!tempProfile.getPlayer().getName().equalsIgnoreCase(players[i].getName())) {
    					if (!tempProfile.setDefaultLogging(players[i].getName())) {
    						success = false;
    					}
    				}
    			}
    			tempProfile.setNormalLogging(true);
    		} else {
    			tempProfile = new PlayerProfile(((Player)sender).getPlayer());
    			Player[] players = this.getServer().getOnlinePlayers();
    			for (int i = 0; i<players.length; i++) {
    				if (!tempProfile.getPlayer().getName().equalsIgnoreCase(players[i].getName())) {
    					if (!tempProfile.setDefaultLogging(players[i].getName())) {
    						success = false;
    					}
    				}
    			}
    			tempProfile.setNormalLogging(true);
    			myPlayerProfiles.add(tempProfile);
    		}
    	} else {
    		Player[] players = this.getServer().getOnlinePlayers();
    		for (int i = 0; i < players.length; i++) {
    			if (!myConsole.setDefaultLogging(players[i].getName())) {
    				success = false;
    			}
    		}
    		myConsole.setNormalLogging(true);
    	}
    	
    	if (success) {
    		sender.sendMessage("Reporting has been enabled.");
    	}
    	
    	return success;
    }
    public void disableLoggingBare(CommandSender sender) {
    	if (sender instanceof Player) {
    		PlayerProfile tempProfile = getPlayerProfile(sender.getName());
    		if (tempProfile != null) {
    			tempProfile.resetLogging();
    		}
    		tempProfile.setNormalLogging(false);
    	} else {
    		myConsole.resetLogging();
    		myConsole.setNormalLogging(false);
    	}
    	
    	sender.sendMessage("Reporting has been disabled.");
    }
    // Commands with one argument; the player.
    public boolean enableLoggingPlayer(CommandSender sender, String tempPlayerName) {
    	if (isLoggedOn(tempPlayerName)) {
    		boolean success = true;
    		
    		if (sender instanceof Player) {
    			PlayerProfile tempProfile = getPlayerProfile(sender.getName());
    			if (tempProfile != null) {
    				success = tempProfile.setDefaultLogging(tempPlayerName);
    			} else {
    				tempProfile = new PlayerProfile(((Player)sender).getPlayer());
    				success = tempProfile.setDefaultLogging(tempPlayerName);

    				myPlayerProfiles.add(tempProfile);
    			}
    		} else {
    			success = myConsole.setDefaultLogging(tempPlayerName);
    		}
    		
    		if (success) {
    			sender.sendMessage("Default logging has been enabled for " + tempPlayerName + ".");
    		}
    		
    		return success;
    	} else {
    		sender.sendMessage(tempPlayerName + " is not online.");
    		return false;
    	}
    }
    public void disableLoggingPlayer(CommandSender sender, String tempPlayerName) {
    	if (sender instanceof Player) {
    		PlayerProfile tempProfile = getPlayerProfile(sender.getName());
    		if (tempProfile != null) {
    			tempProfile.stopLoggingPlayer(tempPlayerName);
    			tempProfile.trimAllLoggingLists();
    		}
    	} else {
    		myConsole.stopLoggingPlayer(tempPlayerName);
    		myConsole.trimAllLoggingLists();
    	}
    	
    	sender.sendMessage("Logging has been disabled for " + tempPlayerName + ".");
    }
    
    // Commands with two arguments; the player and the type of logging.
    public boolean enableLoggingTypeForPlayer(CommandSender sender, String tempPlayerName, String loggingType) {
    	if (isLoggedOn(tempPlayerName)) {
    		boolean success = true;
    		
    		if (sender instanceof Player) {
    			PlayerProfile tempProfile = getPlayerProfile(sender.getName());
    			if (tempProfile != null) {
    				success = tempProfile.logPlayer(convertToLoggingType(loggingType), tempPlayerName);
    			}
    		} else {
    			success = myConsole.logPlayer(convertToLoggingType(loggingType), tempPlayerName);
    		}
    		
    		if (success) {
    			sender.sendMessage("Logging " + loggingType + " enabled for " + tempPlayerName + ".");
    		}
    		
    		return success;
    	} else {
    		sender.sendMessage(tempPlayerName + " is not logged on.");
    		return false;
    	}
    }
    public void disableLoggingTypeForPlayer(CommandSender sender, String tempPlayerName, String loggingType) {
    	if (sender instanceof Player) {
    		PlayerProfile tempProfile = getPlayerProfile(sender.getName());
    		if (tempProfile != null) {
    			tempProfile.stopLogging(convertToLoggingType(loggingType), tempPlayerName);
    			tempProfile.trimLoggingList(convertToLoggingType(loggingType));
    		}
    	} else {
    		myConsole.stopLogging(convertToLoggingType(loggingType), tempPlayerName);
    		myConsole.trimLoggingList(convertToLoggingType(loggingType));
    	}
    	
    	sender.sendMessage("Logging " + loggingType + " disabled for " + tempPlayerName + ".");
    }
    
    // Getter methods
    public ArrayList<PlayerProfile> getPlayerProfiles() {
        return myPlayerProfiles;
    }
    public ConsoleProfile getConsoleProfile() {
        return myConsole;
    }
    public Logger getLog() {
        return log;
    }
    
    public boolean removePlayerProfile(Player tempPlayer) {
    	return myPlayerProfiles.remove(getPlayerProfile(tempPlayer.getName()));
    }

    // Method to return the PlayerProfile object with the name corresponding to the argument.
    public PlayerProfile getPlayerProfile(String tempPlayerName) {
        PlayerProfile tempProfile = null;

        ListIterator<PlayerProfile> myIterator = myPlayerProfiles.listIterator();
        boolean foundPlayer = false;

        while (myIterator.hasNext() && foundPlayer == false) {
            tempProfile = (PlayerProfile)myIterator.next();
            if (tempProfile.getPlayer().getName() == tempPlayerName) {
                foundPlayer = true;
            }
        }

        if (foundPlayer == true) {
            return tempProfile; // The player was found, and the loop ended with the correct PlayerProfile in tempProfile
        } else {
            return null; // The player was not found.
        }
    }

    // Method to determine if a certain type of logging is already enabled for a certain player name given a certain PlayerProfile.
    public boolean loggingIsEnabledFor(String tempPlayerName, int loggingID, PlayerProfile tempPlayerProfile) {
        boolean foundPlayer = false;
        ListIterator<String> myIterator = tempPlayerProfile.getLoggedPlayers(loggingID).listIterator();

        while (myIterator.hasNext() && foundPlayer == false) {
            if (tempPlayerName.equals((String)myIterator.next())) {
                foundPlayer = true;
            }
        }

        return foundPlayer;
    }

    // Method to determine if a certain type of logging is already enabled for a certain player name for the console.
    public boolean consoleLoggingIsEnabledFor(String tempPlayerName, int loggingID) {
        boolean foundPlayer = false;
        ListIterator<String> myIterator = myConsole.getLoggedPlayers(loggingID).listIterator();

        while (myIterator.hasNext() && foundPlayer == false) {
            if (tempPlayerName.equals((String)myIterator.next())) {
                foundPlayer = true;
            }
        }

        return foundPlayer;
    }
}