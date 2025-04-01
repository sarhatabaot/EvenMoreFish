package com.oheers.fish.config;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.messages.EMFSingleMessage;
import com.oheers.fish.messages.PrefixType;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

public class MessageConfig extends ConfigBase {

    private static MessageConfig instance = null;

    public MessageConfig() {
        super("messages.yml", "locales/" + "messages_" + MainConfig.getInstance().getLocale() + ".yml", EvenMoreFish.getInstance(), true);
        instance = this;
    }

    public static MessageConfig getInstance() {
        return instance;
    }

    public EMFSingleMessage getSTDPrefix() {
        EMFSingleMessage message = EMFSingleMessage.empty();
        message.prependMessage(PrefixType.DEFAULT.getPrefix());
        message.appendString("<reset>");
        return message;
    }

    public int getLeaderboardCount() {
        return getConfig().getInt("leaderboard-count", 5);
    }

    @Override
    protected boolean postLoad() {
        boolean changed = false;
        YamlDocument document = getConfig();

        // Bossbar config relocation - Hour Color
        if (document.contains("bossbar.hour-color")) {
            String hourColor = document.getString("bossbar.hour-color", "<white>");
            String hour = document.getString("bossbar.hour", "h");
            document.set("bossbar.hour", hourColor + "{hour}" + hour);
            document.remove("bossbar.hour-color");
            changed = true;
        }

        // Bossbar config relocation - Minute Color
        if (document.contains("bossbar.minute-color")) {
            String minuteColor = document.getString("bossbar.minute-color", "<white>");
            String minute = document.getString("bossbar.minute", "m");
            document.set("bossbar.minute", minuteColor + "{minute}" + minute);
            document.remove("bossbar.minute-color");
            changed = true;
        }

        // Bossbar config relocation - Second Color
        if (document.contains("bossbar.second-color")) {
            String secondColor = document.getString("bossbar.second-color", "<white>");
            String second = document.getString("bossbar.second", "s");
            document.set("bossbar.second", secondColor + "{second}" + second);
            document.remove("bossbar.second-color");
            changed = true;
        }

        // Prefix config relocation

        if (document.contains("prefix")) {
            String prefix = document.getString("prefix");

            String oldRegular = document.getString("prefix-regular");
            document.set("prefix-regular", oldRegular + prefix);

            String oldAdmin = document.getString("prefix-admin");
            document.set("prefix-admin", oldAdmin + prefix);

            String oldError = document.getString("prefix-error");
            document.set("prefix-error", oldError + prefix);

            document.remove("prefix");
        }

        return changed;
    }

}
