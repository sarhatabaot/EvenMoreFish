package com.oheers.fish.commands.admin.subcommand;

import com.oheers.fish.api.addons.ItemAddon;
import com.oheers.fish.api.requirement.RequirementType;
import com.oheers.fish.api.reward.RewardType;
import com.oheers.fish.commands.admin.AdminCommand;
import com.oheers.fish.commands.arguments.RarityArgument;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.FishManager;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.EMFSingleMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.strokkur.commands.annotations.DefaultExecutes;
import net.strokkur.commands.annotations.Executes;
import net.strokkur.commands.annotations.arguments.CustomArg;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ListSubcommand {

    @DefaultExecutes
    public void onDefault(CommandSender sender) {
        AdminCommand.sendHelpMessage(sender);
    }

    @Executes("fish")
    public void onFish(CommandSender sender, @CustomArg(RarityArgument.class) Rarity rarity) {
        if (rarity == null) {
            ConfigMessage.RARITY_INVALID.getMessage().send(sender);
            return;
        }
        TextComponent.Builder builder = Component.text();
        builder.append(rarity.getDisplayName().getComponentMessage());
        builder.append(Component.space());
        for (Fish fish : rarity.getOriginalFishList()) {
            TextComponent.Builder fishBuilder = Component.text();
            EMFSingleMessage message = EMFSingleMessage.fromString("<gray>[</gray>{fish}<gray>]</gray>");
            message.setVariable("{fish}", fish.getDisplayName());
            fishBuilder.append(message.getComponentMessage());
            fishBuilder.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Click to receive fish")));
            fishBuilder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/emf admin fish " + rarity.getId() + " " + fish.getName().replace(" ", "_")));
            builder.append(fishBuilder);
        }
        sender.sendMessage(builder.build());
    }

    @Executes("rarities")
    public void onRarities(CommandSender sender) {
        TextComponent.Builder builder = Component.text();
        for (Rarity rarity : FishManager.getInstance().getRarityMap().values()) {
            TextComponent.Builder rarityBuilder = Component.text();
            EMFSingleMessage message = EMFSingleMessage.fromString("<gray>[</gray>{rarity}<gray>]</gray>");
            message.setVariable("{rarity}", rarity.getDisplayName());
            rarityBuilder.append(message.getComponentMessage());
            rarityBuilder.hoverEvent(HoverEvent.hoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                EMFSingleMessage.fromString("Click to view " + rarity.getId() + " fish.").getComponentMessage()
            ));
            rarityBuilder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/emf admin list fish " + rarity.getId()));
            builder.append(rarityBuilder);
        }
        sender.sendMessage(builder.build());
    }

    @Executes("rewardTypes")
    public void onRewardTypes(CommandSender sender) {
        listRewardTypes(sender);
    }

    @Executes("requirementTypes")
    public void onRequirementTypes(CommandSender sender) {
        listRequirementTypes(sender);
    }

    @Executes("itemAddons")
    public void onItemAddons(CommandSender sender) {
        listItemAddons(sender);
    }

    private void listRewardTypes(@NotNull Audience audience) {
        TextComponent.Builder builder = Component.text();

        EMFMessage listMessage = ConfigMessage.ADMIN_LIST_ADDONS.getMessage();
        listMessage.setVariable("{addon-type}", RewardType.class.getSimpleName());
        builder.append(listMessage.getComponentMessage());

        RewardType.getLoadedTypes().forEach((string, rewardType) -> {
            Component show = EMFSingleMessage.fromString(
                "Author: " + rewardType.getAuthor() + "\n" +
                    "Registered Plugin: " + rewardType.getPlugin().getName()
            ).getComponentMessage();

            TextComponent.Builder typeBuilder = Component.text().content(rewardType.getIdentifier());
            typeBuilder.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, show));
            builder.append(typeBuilder).append(Component.text(", "));
        });
        audience.sendMessage(builder.build());
    }

    private void listRequirementTypes(@NotNull Audience audience) {
        TextComponent.Builder builder = Component.text();

        EMFMessage listMessage = ConfigMessage.ADMIN_LIST_ADDONS.getMessage();
        listMessage.setVariable("{addon-type}", RequirementType.class.getSimpleName());
        builder.append(listMessage.getComponentMessage());

        RequirementType.getLoadedTypes().forEach((string, requirementType) -> {
            Component show = EMFSingleMessage.fromString(
                "Author: " + requirementType.getAuthor() + "\n" +
                    "Registered Plugin: " + requirementType.getPlugin().getName()
            ).getComponentMessage();

            TextComponent.Builder typeBuilder = Component.text().content(requirementType.getIdentifier());
            typeBuilder.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, show));
            builder.append(typeBuilder).append(Component.text(", "));
        });
        audience.sendMessage(builder.build());
    }

    private void listItemAddons(@NotNull Audience audience) {
        TextComponent.Builder builder = Component.text();

        EMFMessage listMessage = ConfigMessage.ADMIN_LIST_ADDONS.getMessage();
        listMessage.setVariable("{addon-type}", ItemAddon.class.getSimpleName());
        builder.append(listMessage.getComponentMessage());

        ItemAddon.getLoadedAddons().forEach((string, itemAddon) -> {
            Component show = EMFSingleMessage.fromString(
                "Author: " + itemAddon.getAuthor()
            ).getComponentMessage();

            TextComponent.Builder typeBuilder = Component.text().content(itemAddon.getIdentifier());
            typeBuilder.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, show));
            builder.append(typeBuilder).append(Component.text(", "));
        });
        audience.sendMessage(builder.build());
    }

}
