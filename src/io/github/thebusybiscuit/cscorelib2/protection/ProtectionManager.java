package io.github.thebusybiscuit.cscorelib2.protection;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import io.github.thebusybiscuit.cscorelib2.protection.ProtectionModule.Action;
import io.github.thebusybiscuit.cscorelib2.protection.modules.ASkyBlockProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.BentoBoxProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.BlockLockerProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.FactionsProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.FactionsUUIDProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.GriefPreventionProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.LWCProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.LocketteProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.PlotSquaredProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.PreciousStonesProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.RedProtectProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.TownyProtectionModule;
import io.github.thebusybiscuit.cscorelib2.protection.modules.WorldGuardProtectionModule;
import lombok.NonNull;

public final class ProtectionManager {
	
	private final Set<ProtectionModule> cscorelibProtectionModules = new HashSet<>();
	private final Logger logger;
	
	public ProtectionManager(Server server) {
		logger = getLogger(server);
		
		logger.log(Level.INFO, "Loading Protection Modules...");
		logger.log(Level.INFO, "This may happen more than once.");
		
		if (server.getPluginManager().isPluginEnabled("WorldGuard") && server.getPluginManager().isPluginEnabled("WorldEdit")) {
			registerModule(new WorldGuardProtectionModule());
		}
		if (server.getPluginManager().isPluginEnabled("Factions")) {
			if (server.getPluginManager().getPlugin("Factions").getDescription().getDepend().contains("MassiveCore")) {
				registerModule(new FactionsProtectionModule());
			}
			else {
				registerModule(new FactionsUUIDProtectionModule());
			}
		}
		if (server.getPluginManager().isPluginEnabled("Towny")) {
			registerModule(new TownyProtectionModule());
		}
		if (server.getPluginManager().isPluginEnabled("GriefPrevention")) {
			registerModule(new GriefPreventionProtectionModule());
		}
		if (server.getPluginManager().isPluginEnabled("ASkyBlock")) {
			registerModule(new ASkyBlockProtectionModule());
		}
		if(server.getPluginManager().isPluginEnabled("LWC")){
			registerModule(new LWCProtectionModule());
		}
		if (server.getPluginManager().isPluginEnabled("PreciousStones")) {
			registerModule(new PreciousStonesProtectionModule());
		}
		if (server.getPluginManager().isPluginEnabled("Lockette")) {
			registerModule(new LocketteProtectionModule());
		}
		if(server.getPluginManager().isPluginEnabled("PlotSquared")) {
			registerModule(new PlotSquaredProtectionModule());
		}
		if (server.getPluginManager().isPluginEnabled("RedProtect")) {
			registerModule(new RedProtectProtectionModule());
		}
		if (server.getPluginManager().isPluginEnabled("BentoBox")) {
			registerModule(new BentoBoxProtectionModule());
		}
		if (server.getPluginManager().isPluginEnabled("BlockLocker")) {
			registerModule(new BlockLockerProtectionModule());
		}
		
		/*
		 * The following Plugins work by utilising one of the above listed
		 * Plugins in the background.
		 * We do not need a module for them, but let us make the server owner
		 * aware that this compatibility exists.
		 */
		
		if(server.getPluginManager().isPluginEnabled("ProtectionStones")) {
            loadModuleMSG("ProtectionStones");
		}
		if (server.getPluginManager().isPluginEnabled("uSkyblock")) {
			loadModuleMSG("uSkyblock");
		}
	}

	private Logger getLogger(Server server) {
		Logger logger = new Logger("CS-CoreLib2", null) {
			
			@Override
			public void log(@NonNull LogRecord logRecord) {
				logRecord.setMessage("[CS-CoreLib2 - Protection]" + logRecord.getMessage());
			}
			
		};
		
		logger.setParent(server.getLogger());
		logger.setLevel(Level.ALL);
		
		return logger;
	}

	public void registerModule(String name, ProtectionModule module) {
		cscorelibProtectionModules.add(module);
		loadModuleMSG(name);
	}

	public void registerModule(ProtectionModule module) {
		try {
			module.load();
			registerModule(module.getName(), module);
		}
		catch(Exception x) {
			logger.log(Level.SEVERE, "An Error occured while registering the Protection Module: \"" + module.getName() + "\"", x);
		}
	}

	protected void loadModuleMSG(String module) {
		logger.log(Level.INFO, "Loaded Protection Module \"" + module + "\"");
	}

	public boolean hasPermission(@NonNull OfflinePlayer p, @NonNull Location l, @NonNull Action action) {
		for (ProtectionModule module: cscorelibProtectionModules) {
			try {
				if (!module.hasPermission(p, l, action)) {
					return false;
				}
			}
			catch(Exception x) {
				logger.log(Level.SEVERE, "An Error occured while querying the Protection Module: \"" + module.getName() + "\"", x);
				return false;
			}
		}

		return true;
	}

}
