/*
Class to implement the listener that will receive the events registered and execute the appropriate code in the handlers.
Author: Matt Brockman
*/


package me.MucTweezer.activityreporter;


import java.util.logging.Logger;
import java.util.ListIterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class LoggingListener implements Listener {

    // Colours for messages.
	private static final ChatColor BREAKING_COLOUR = ChatColor.RED;
	private static final ChatColor PLACING_COLOUR = ChatColor.GREEN;
	private static final ChatColor COMBAT_COLOUR = ChatColor.BLUE;
	private static final ChatColor MOVEMENT_COLOUR = ChatColor.GRAY;
	
	private ActivityReporter plugin;
    private Logger log;

    public LoggingListener(ActivityReporter tempPlugin, Logger tempLog) {
        plugin = tempPlugin;
        log = tempLog;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // This method will extract the name of the player from the damage event, if it involved a player. If it did not involve a player, an empty String will be returned.
    private String extractPlayerNameFromDamageEvent(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            return ((Player)event.getEntity()).getName();
        } else if (event.getDamager().getType() == EntityType.PLAYER) {
            return ((Player)event.getDamager()).getName();
        } else {
            return "";
        }
    }

    // Message Generation Methods
    private String blockBreakMessage(BlockBreakEvent event) {
        return (event.getPlayer().getName() + " broke a " + event.getBlock().getType() + " block at " + getCoordinates(event.getBlock().getLocation()) + ".");
    }
    private String blockPlaceMessage(BlockPlaceEvent event) {
        return (event.getPlayer().getName() + " placed a " + event.getBlock().getType() + " block at " + getCoordinates(event.getBlock().getLocation()) + ".");
    }
    private String entityDamageByEntityMessage(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) { // Player was damaged by creature.
            return (((Player)event.getEntity()).getName() + " suffered " + event.getDamage() + " damage from " + event.getDamager().getType().getName());
        } else if (event.getDamager().getType() == EntityType.PLAYER) { // Player damaged creature.
            return (((Player)event.getDamager()).getName() + " dealt " + event.getDamage() + " damage to " + event.getEntity().getType().getName() + ".");
        } else { // Creature damaged creature.
            return (event.getEntity().getType().getName() + " suffered " + event.getDamage() + " damage from " + event.getDamager().getType().getName());
        }
    }
    private String entityDeathMessage(EntityDeathEvent event, String deadEntity) {
        return (deadEntity + " died at " + getCoordinates(event.getEntity().getLocation()) + ".");
    }
    private String playerMoveMessage(PlayerMoveEvent event) {
    	return (event.getPlayer().getName() + " moved to " + getCoordinates(event.getFrom()) + " to " + getCoordinates(event.getTo()) + ".");
    }
    private String getCoordinates(Location tempLocation) {
    	// Wrote my own method to replace the toString() method of the Location class, which I found to be too long for my purposes.
    	return ("x: " + (int)tempLocation.getX() + ", z: " + (int)tempLocation.getZ() + ", y: " + (int)tempLocation.getY());
    }

    // Event Handlers

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBrokenBlock(BlockBreakEvent event) {
        ListIterator<PlayerProfile> tempIterator = plugin.getPlayerProfiles().listIterator();
        PlayerProfile tempProfile;

        while (tempIterator.hasNext()) {
        	tempProfile = (PlayerProfile)tempIterator.next();
        	if (plugin.loggingIsEnabledFor(event.getPlayer().getName(), ActivityReporter.BREAKING_LOGGING, tempProfile)) {
        		tempProfile.getPlayer().sendMessage(BREAKING_COLOUR + blockBreakMessage(event));
        	}
        }

        if (plugin.consoleLoggingIsEnabledFor(event.getPlayer().getName(), ActivityReporter.BREAKING_LOGGING)) {
            log.info(blockBreakMessage(event));
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlacedBlock(BlockPlaceEvent event) {
        ListIterator<PlayerProfile> tempIterator = plugin.getPlayerProfiles().listIterator();
        PlayerProfile tempProfile;

        while (tempIterator.hasNext()) {
        	tempProfile = (PlayerProfile)tempIterator.next();
        	if (plugin.loggingIsEnabledFor(event.getPlayer().getName(), ActivityReporter.PLACING_LOGGING, tempProfile)) {
        		tempProfile.getPlayer().sendMessage(PLACING_COLOUR + blockPlaceMessage(event));
        	}
        }

        if (plugin.consoleLoggingIsEnabledFor(event.getPlayer().getName(), ActivityReporter.PLACING_LOGGING)) {
            log.info(blockPlaceMessage(event));
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        ListIterator<PlayerProfile> tempIterator = plugin.getPlayerProfiles().listIterator();
        PlayerProfile tempProfile;

        while (tempIterator.hasNext()) {
        	tempProfile = (PlayerProfile)tempIterator.next();
        	if (plugin.loggingIsEnabledFor(extractPlayerNameFromDamageEvent(event), ActivityReporter.COMBAT_LOGGING, tempProfile)) {
        		tempProfile.getPlayer().sendMessage(COMBAT_COLOUR + entityDamageByEntityMessage(event));
        	}
        }

        if (plugin.consoleLoggingIsEnabledFor(extractPlayerNameFromDamageEvent(event), ActivityReporter.COMBAT_LOGGING)) {
            log.info(entityDamageByEntityMessage(event));
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
    // I think this should capture both player death and creature deaths.
        String deadEntity;
        ListIterator<PlayerProfile> tempIterator = plugin.getPlayerProfiles().listIterator();
        PlayerProfile tempProfile;

        if (event.getEntityType() == EntityType.PLAYER) { // The dead entity is a player.
            deadEntity = ((Player)event.getEntity()).getName();
        } else { // the dead entity is a creature.
            deadEntity = event.getEntityType().getName(); // I don't know if this will work.
        }

        while (tempIterator.hasNext()) {
        	tempProfile = (PlayerProfile)tempIterator.next();
        	if (plugin.loggingIsEnabledFor(deadEntity, ActivityReporter.COMBAT_LOGGING, tempProfile) || tempProfile.getNormalLogging()) {
        		tempProfile.getPlayer().sendMessage(COMBAT_COLOUR + entityDeathMessage(event, deadEntity));
        	}
        }

        if (plugin.consoleLoggingIsEnabledFor(deadEntity, ActivityReporter.COMBAT_LOGGING) || plugin.getConsoleProfile().getNormalLogging()) {
            log.info(entityDeathMessage(event, deadEntity));
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        ListIterator<PlayerProfile> tempIterator = plugin.getPlayerProfiles().listIterator();
        PlayerProfile tempProfile;

        while (tempIterator.hasNext()) {
            tempProfile = (PlayerProfile)tempIterator.next();
            if (plugin.loggingIsEnabledFor(event.getPlayer().getName(), ActivityReporter.MOVEMENT_LOGGING, tempProfile)) {
                tempProfile.getPlayer().sendMessage(MOVEMENT_COLOUR + playerMoveMessage(event));
            }
        }

        if (plugin.consoleLoggingIsEnabledFor(event.getPlayer().getName(), ActivityReporter.MOVEMENT_LOGGING)) {
            log.info(playerMoveMessage(event));
        }
    }
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	ListIterator<PlayerProfile> tempIterator = plugin.getPlayerProfiles().listIterator();
    	PlayerProfile tempProfile;
    	ConsoleProfile tempConsole;
    	
    	while (tempIterator.hasNext()) {
    		tempProfile = (PlayerProfile)tempIterator.next();
    		if (tempProfile.getNormalLogging()) {
    			tempProfile.setDefaultLogging(event.getPlayer().getName());
    			tempProfile.getPlayer().sendMessage("Logging enabled for " + event.getPlayer().getName());
    		}
    	}
    	
    	tempConsole = plugin.getConsoleProfile();
    	if (tempConsole.getNormalLogging()) {
    		tempConsole.setDefaultLogging(event.getPlayer().getName());
    		tempConsole.getLogger().info("Logging enabled for " + event.getPlayer().getName());
    	}
    }
    
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
    	ListIterator<PlayerProfile> tempIterator = plugin.getPlayerProfiles().listIterator();
    	PlayerProfile tempProfile;
    	ConsoleProfile tempConsole;
    	
    	while (tempIterator.hasNext()) {
    		tempProfile = (PlayerProfile)tempIterator.next();
    		tempProfile.stopLoggingPlayer(event.getPlayer().getName());
    		tempProfile.trimAllLoggingLists();
    		//tempProfile.getPlayer().sendMessage("Logging disabled for " + event.getPlayer().getName());
    	}
    	
    	plugin.removePlayerProfile(event.getPlayer());
    	
    	tempConsole = plugin.getConsoleProfile();
    	tempConsole.stopLoggingPlayer(event.getPlayer().getName());
    	tempConsole.trimAllLoggingLists();
    	//tempConsole.getLogger().info("Logging disabled for " + event.getPlayer().getName());
    }
}