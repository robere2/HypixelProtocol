package dev.ecr.commands.hyproto;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.hypixel.modapi.HypixelModAPI;

import java.util.concurrent.CompletableFuture;

public class HyprotoPacketArgumentSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder){
        for(String identifier : HypixelModAPI.getInstance().getRegistry().getIdentifiers()) {
            if(!identifier.contains(":")) {
                continue;
            }
            builder.suggest(identifier.split(":")[1]);
        }
        return builder.buildFuture();
    }
}
