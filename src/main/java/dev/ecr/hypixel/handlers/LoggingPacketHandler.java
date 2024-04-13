package dev.ecr.hypixel.handlers;

import dev.ecr.HyprotoExperimentation;
import net.hypixel.modapi.handler.ClientboundPacketHandler;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundLocationPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPingPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPlayerInfoPacket;

import java.util.UUID;

public class LoggingPacketHandler implements ClientboundPacketHandler {

    @Override
    public void onPingPacket(ClientboundPingPacket packet) {
        ClientboundPacketHandler.super.onPingPacket(packet);
        HyprotoExperimentation.LOGGER.info("Received Ping packet");
        HyprotoExperimentation.LOGGER.info(packet.getResponse());
    }

    @Override
    public void onLocationPacket(ClientboundLocationPacket packet) {
        ClientboundPacketHandler.super.onLocationPacket(packet);
        HyprotoExperimentation.LOGGER.info("Received Location packet");
        HyprotoExperimentation.LOGGER.info(packet.getProxyName());
        HyprotoExperimentation.LOGGER.info(packet.getServerName());
        HyprotoExperimentation.LOGGER.info("{}", packet.getServerType());
        HyprotoExperimentation.LOGGER.info(packet.getEnvironment().toString());
        HyprotoExperimentation.LOGGER.info(packet.getLobbyName().orElse(null));
        HyprotoExperimentation.LOGGER.info(packet.getMap().orElse(null));
        HyprotoExperimentation.LOGGER.info(packet.getMode().orElse(null));
    }

    @Override
    public void onPartyInfoPacket(ClientboundPartyInfoPacket packet) {
        ClientboundPacketHandler.super.onPartyInfoPacket(packet);
        HyprotoExperimentation.LOGGER.info("Received Party Info packet");
        HyprotoExperimentation.LOGGER.info("{}", packet.isInParty());
        HyprotoExperimentation.LOGGER.info("{}", packet.getLeader().orElse(null));
        HyprotoExperimentation.LOGGER.info("{}", String.join(", ", packet.getMembers().stream().map(UUID::toString).toArray(String[]::new)));
    }

    @Override
    public void onPlayerInfoPacket(ClientboundPlayerInfoPacket packet) {
        ClientboundPacketHandler.super.onPlayerInfoPacket(packet);
        HyprotoExperimentation.LOGGER.info("Received Player Info packet");
        HyprotoExperimentation.LOGGER.info(packet.getPlayerRank().toString());
        HyprotoExperimentation.LOGGER.info(packet.getPrefix().orElse("No prefix"));
        HyprotoExperimentation.LOGGER.info(packet.getPackageRank().toString());
        HyprotoExperimentation.LOGGER.info(packet.getMonthlyPackageRank().toString());
    }
}
