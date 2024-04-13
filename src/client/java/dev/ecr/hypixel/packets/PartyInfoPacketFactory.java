package dev.ecr.hypixel.packets;

import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket;

public class PartyInfoPacketFactory implements IPacketFactory<ServerboundPartyInfoPacket> {
    @Override
    public ServerboundPartyInfoPacket build(String... args) {
        return new ServerboundPartyInfoPacket();
    }
}
