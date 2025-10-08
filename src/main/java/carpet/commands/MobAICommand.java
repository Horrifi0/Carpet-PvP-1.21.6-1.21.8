package carpet.commands;

import carpet.CarpetSettings;
import carpet.utils.CommandHelper;
import carpet.utils.MobAI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.SharedSuggestionProvider.suggest;
import static net.minecraft.commands.arguments.ResourceArgument.getSummonableEntityType;
import static net.minecraft.commands.arguments.ResourceArgument.resource;

public class MobAICommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext)
    {
        LiteralArgumentBuilder<CommandSourceStack> command = literal("track").
                requires((player) -> CommandHelper.canUseCommand(player, CarpetSettings.commandTrackAI)).
                then(argument("entity type", resource(commandBuildContext, Registries.ENTITY_TYPE)).

                        suggests( (c, b) -> suggest(MobAI.availbleTypes(c.getSource()), b)).
                        then(literal("clear").executes( (c) ->
                                {
                                    MobAI.clearTracking(c.getSource().getServer(), getSummonableEntityType(c, "entity type").value());
                                    return 1;
                                }
                        )).
                        then(argument("aspect", StringArgumentType.word()).
                                suggests( (c, b) -> suggest(MobAI.availableFor(getSummonableEntityType(c, "entity type").value()),b)).
                                executes( (c) -> {
                                    String aspect = StringArgumentType.getString(c, "aspect").toUpperCase();
                                    try {
                                        MobAI.TrackingType trackingType = MobAI.TrackingType.valueOf(aspect);
                                        MobAI.startTracking(
                                                getSummonableEntityType(c, "entity type").value(),
                                                trackingType
                                        );
                                        return 1;
                                    } catch (IllegalArgumentException e) {
                                        carpet.utils.Messenger.m(c.getSource(), "r Invalid tracking type: " + aspect);
                                        return 0;
                                    }
                                })));
        dispatcher.register(command);
    }
}
