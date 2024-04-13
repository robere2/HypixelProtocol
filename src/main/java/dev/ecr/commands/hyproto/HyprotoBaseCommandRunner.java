package dev.ecr.commands.hyproto;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.ecr.HyprotoExperimentation;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.hypixel.modapi.HypixelModAPI;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

public class HyprotoBaseCommandRunner implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        if(!HyprotoExperimentation.isConnectedToHypixel()) {
            context.getSource().sendError(Text.literal("This command only works when connected to Hypixel"));
            return 1;
        }

        final Set<String> validPackets = new HashSet<>();
        for(final String identifier : HypixelModAPI.getInstance().getRegistry().getIdentifiers()) {
            if(!identifier.contains(":")) {
                continue;
            }
            validPackets.add(identifier.split(":")[1]);

        }

        context.getSource().sendError(Text.literal("Valid packets: %s".formatted(String.join(", ", validPackets))));
        return 1;
    }
}
