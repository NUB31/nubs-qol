package net.nub31.nubsqol;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NubsQol implements ModInitializer {
	public static final String MOD_ID = "nubs-qol";
	public static final RunningConfig RUNNING_CONFIG = RunningConfig.CLIENT_ONLY; // No checking yet
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info(String.format("Initializing %s", MOD_ID));
	}
}
