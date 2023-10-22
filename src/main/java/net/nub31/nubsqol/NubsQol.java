package net.nub31.nubsqol;

import net.fabricmc.api.ModInitializer;
import net.nub31.nubsqol.networking.ModMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NubsQol implements ModInitializer {
	public static final String MOD_ID = "nubs-qol";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean hasServerSupport = false;

	@Override
	public void onInitialize() {
		LOGGER.info(String.format("Initializing %s", MOD_ID));

		ModMessages.registerC2SPackets();
	}
}
