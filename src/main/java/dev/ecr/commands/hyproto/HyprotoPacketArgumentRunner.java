package dev.ecr.commands.hyproto;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.ecr.HyprotoExperimentation;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HyprotoPacketArgumentRunner implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        if(!HyprotoExperimentation.isConnectedToHypixel()) {
            context.getSource().sendError(Text.literal("This command only works when connected to Hypixel"));
        }

        final String submittedPacket = context.getArgument("packet", String.class);
        final String namespacedSubmittedPacket = "hypixel:%s".formatted(submittedPacket);

        String packetArgsRaw = "";
        try {
            packetArgsRaw = context.getArgument("args", String.class);
        } catch(IllegalArgumentException ignored) {
            // A missing args argument just means that no arguments were passed
        }
        String[] packetArgs;
        if(packetArgsRaw.isEmpty()) {
            packetArgs = new String[0];
        } else {
            packetArgs = packetArgsRaw.split(" ");
        }

        if(!HypixelModAPI.getInstance().getRegistry().getIdentifiers().contains(namespacedSubmittedPacket)) {
            context.getSource().sendError(Text.literal("%s is not a valid packet".formatted(submittedPacket)));
            return 1;
        }

        context.getSource().sendFeedback(Text.literal("Sending %s packet".formatted(submittedPacket)).withColor(HyprotoExperimentation.CHAT_COLOR));

        // This is where we're actually sending the packet to the server. We call their factory with any arguments that
        // were provided, however all currently available packets don't take any arguments, so all of their factories
        // just ignore this.
        final PacketByteBuf buf = PacketByteBufs.create();
        final PacketSerializer serializer = new PacketSerializer(buf);
        // no NPE due to verification that all factories existed at initialization
        HyprotoExperimentation.getPacketFactory(namespacedSubmittedPacket).build(packetArgs).write(serializer);
        ClientPlayNetworking.send(new Identifier(namespacedSubmittedPacket), buf);

        return 0;
    }
}
