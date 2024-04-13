package dev.ecr.hypixel.handlers;

import dev.ecr.HyprotoExperimentation;
import net.hypixel.modapi.handler.ClientboundPacketHandler;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundLocationPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPingPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPlayerInfoPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.UUID;

public class ChatPacketHandler implements ClientboundPacketHandler {

    private void sendMessage(String msg) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(msg).withColor(HyprotoExperimentation.CHAT_COLOR));
    }

    @Override
    public void onPingPacket(ClientboundPingPacket packet) {
        ClientboundPacketHandler.super.onPingPacket(packet);
        this.sendMessage("Received ping response");
        this.sendMessage(packet.getResponse());
    }

    @Override
    public void onLocationPacket(ClientboundLocationPacket packet) {
        ClientboundPacketHandler.super.onLocationPacket(packet);
        this.sendMessage("Received location response");
        this.sendMessage("Proxy: %s".formatted(packet.getProxyName()));
        this.sendMessage("Server: %s".formatted(packet.getServerName()));
        this.sendMessage("Type: %s".formatted(packet.getServerType().orElse(null)));
        this.sendMessage("Environment: %s".formatted(packet.getEnvironment()));
        this.sendMessage("Lobby Name: %s".formatted(packet.getLobbyName().orElse(null)));
        this.sendMessage("Mode: %s".formatted(packet.getMode().orElse(null)));
        this.sendMessage("Map: %s".formatted(packet.getMap().orElse(null)));
    }

    @Override
    public void onPartyInfoPacket(ClientboundPartyInfoPacket packet) {
        ClientboundPacketHandler.super.onPartyInfoPacket(packet);
        this.sendMessage("Received party_info response");
        this.sendMessage("In Party: %s".formatted(packet.isInParty()));
        this.sendMessage("Party Leader: %s".formatted(packet.getLeader().orElse(null)));
        this.sendMessage("Party Members: %s".formatted(String.join(", ", packet.getMembers().stream().map(UUID::toString).toArray(String[]::new))));
    }

    @Override
    public void onPlayerInfoPacket(ClientboundPlayerInfoPacket packet) {
        ClientboundPacketHandler.super.onPlayerInfoPacket(packet);
        this.sendMessage("Received player_info response");
        this.sendMessage("Player Rank: %s (ID %d)".formatted(packet.getPlayerRank(), packet.getPlayerRank().getId()));
        this.sendMessage("Package Rank: %s (ID %d)".formatted(packet.getPackageRank(), packet.getPackageRank().getId()));
        this.sendMessage("Monthly Package Rank: %s (ID %d)".formatted(packet.getMonthlyPackageRank(), packet.getMonthlyPackageRank().getId()));
        this.sendMessage("Prefix: %s".formatted(packet.getPrefix().orElse(null)));
    }
}
