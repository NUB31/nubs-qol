package net.nub31.nubsqol;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.nub31.nubsqol.event.ClientPlayConnectionEventHandlers;
import net.nub31.nubsqol.networking.ModMessages;

public class NubsQolClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		NubsQol.LOGGER.info(String.format("Initializing %s client", NubsQol.MOD_ID));

		ModMessages.registerS2CPackets();

		ClientPlayConnectionEvents.DISCONNECT.register(new ClientPlayConnectionEventHandlers());
		ClientPlayConnectionEvents.JOIN.register(new ClientPlayConnectionEventHandlers());
	}
}
