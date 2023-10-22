package net.nub31.nubsqol.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.nub31.nubsqol.networking.ModMessages;

public class SynServerSupportC2SPacket {
	public static void receive(MinecraftServer client, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		responseSender.sendPacket(responseSender.createPacket(ModMessages.ACK_SERVER_SUPPORT_PACKET_ID, PacketByteBufs.create()));
	}
}