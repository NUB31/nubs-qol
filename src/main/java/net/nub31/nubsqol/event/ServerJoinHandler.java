package net.nub31.nubsqol.event;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.nub31.nubsqol.networking.ModMessages;

public class ServerJoinHandler implements ServerPlayConnectionEvents.Join {

	@Override
	public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		ServerPlayNetworking.send(handler.player, ModMessages.PING_PACKET_ID, PacketByteBufs.create());
	}
}
