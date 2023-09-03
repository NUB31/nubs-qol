package net.nub31.nubsqol;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.nub31.nubsqol.constant.RunningConfig;
import net.nub31.nubsqol.event.ServerJoinHandler;
import net.nub31.nubsqol.networking.ModMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NubsQol implements ModInitializer {
	public static final String MOD_ID = "nubs-qol";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static RunningConfig RUNNING_CONFIG;

	@Override
	public void onInitialize() {
		LOGGER.info(String.format("Initializing %s", MOD_ID));

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			RUNNING_CONFIG = RunningConfig.CLIENT_ONLY;
		} else {
			RUNNING_CONFIG = RunningConfig.SERVER_ONLY;
		}

		ModMessages.registerC2SPackets();

		ServerPlayConnectionEvents.JOIN.register(new ServerJoinHandler());
	}
}
