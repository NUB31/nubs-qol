package net.nub31.nubsqol.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.nub31.nubsqol.NubsQol;
import net.nub31.nubsqol.networking.packet.PingS2CPacket;

public class ModMessages {
	public static final Identifier PING_PACKET_ID = new Identifier(NubsQol.MOD_ID, "ping");

	public static void registerC2SPackets() {
	}

	public static void registerS2CPackets() {
		ClientPlayNetworking.registerGlobalReceiver(PING_PACKET_ID, PingS2CPacket::receive);
	}
}
