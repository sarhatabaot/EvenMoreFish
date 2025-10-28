package com.oheers.fish.commands;

import com.oheers.fish.FishUtils;
import com.oheers.fish.commands.arguments.FishArgument;
import com.oheers.fish.commands.arguments.RarityArgument;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import net.strokkur.commands.annotations.*;
import net.strokkur.commands.annotations.arguments.CustomArg;
import net.strokkur.commands.annotations.arguments.IntArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class AdminCommand {

    @Subcommand("database")
    @Permission("emf.admin.debug.database")
    AdminDatabaseCommand adminDatabaseCommand;

    @Subcommand("fish")
    record FishCommand(Rarity rarity, Fish fish, Integer amount, List<Player> targets) {
        @Executes
        void execute(CommandSender sender, @CustomArg(RarityArgument.class) Rarity rarity, @CustomArg(FishArgument.class) Fish initialFish,@IntArg(min = 1) Integer amount, List<Player> targets) {
            if (targets.isEmpty() && sender instanceof Player player) {
                targets.add(player);
            }

            for (Player target : targets) {
                Fish fish = initialFish.createCopy();
                fish.init();

                if (fish.hasFishRewards()) {
                    fish.getFishRewards().forEach(reward -> reward.rewardPlayer(target, target.getLocation()));
                }

                fish.setFisherman(target.getUniqueId());

                final ItemStack fishItem = fish.give();
                fishItem.setAmount(amount);

                FishUtils.giveItem(fishItem, target);
            }

            EMFMessage message = ConfigMessage.ADMIN_GIVE_PLAYER_FISH.getMessage();
            message.setVariable("{player}", getPlayersVariable(arguments.getRaw("targets"), targets));

            message.setFishCaught(initialFish.getName());
            message.send(sender);
        }
        @Executes
        void execute(CommandSender sender,Rarity rarity, Fish fish,@IntArg(min = 1) Integer amount) {

            execute(sender, rarity, fish, amount, );
        }

        @Executes
        void execute(CommandSender sender, Rarity rarity, Fish fish) {

        }
    }
    
    //probablt should be a subcommand too
    @Executes("list")
    public void onList(CommandSender sender) {

    }

    //probably should be subcommand too
    @Executes("competition")
    public void onCompetition(CommandSender sender) {

    }

    public void onCustomRod(CommandSender sender) {

    }

    public void onBait(CommandSender sender) {

    }

    public void onClearBaits(CommandSender sender) {

    }

    public void onReload(CommandSender sender) {

    }

    public void onVersion(CommandSender sender) {

    }

    public void onRewardTypes(CommandSender sender) {

    }

    public void onMigrate(CommandSender sender) {

    }

    public void onRawItem(CommandSender sender) {

    }

    @DefaultExecutes
    public void onHelp(CommandSender sender) {

    }


}
