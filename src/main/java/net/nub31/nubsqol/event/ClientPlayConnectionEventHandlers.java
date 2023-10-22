package net.nub31.nubsqol.event;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.nub31.nubsqol.NubsQol;
import net.nub31.nubsqol.networking.ModMessages;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientPlayConnectionEventHandlers implements ClientPlayConnectionEvents.Disconnect, ClientPlayConnectionEvents.Join {
	@Override
	public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
		NubsQol.hasServerSupport = false;
	}

	@Override
	public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
		NubsQol.hasServerSupport = false;
		ClientPlayNetworking.send(ModMessages.SYN_SERVER_SUPPORT_PACKET_ID, PacketByteBufs.create());

		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> {
			String message = NubsQol.hasServerSupport
					? "NUB's Qol: Full easy elytra launch functionality enabled."
					: "NUB's Qol: Limited easy elytra launch functionality due to missing mod on the server.";

			if (client.player != null) {
				client.player.sendMessage(Text.literal(message));
			}
		}, 3, TimeUnit.SECONDS);
	}
}
