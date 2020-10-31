package de.bloody9.worsman;

import de.bloody9.core.Bot;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.interfaces.ConfigUpdater;
import de.bloody9.core.models.objects.BotInitObject;
import de.bloody9.worsman.commands.FactCommand;
import de.bloody9.worsman.features.guildfact.GuildFactUpdater;
import de.bloody9.worsman.features.guildfact.GuildFacts;
import de.bloody9.worsman.features.joinrole.JoinRoleFeature;
import net.dv8tion.jda.api.entities.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorsBot extends Bot {

    public static WorsBot INSTANCE;

    private List<GuildFacts> guildFactsList;

    public WorsBot(String[] args) {
        this(enterArgs(args));
    }

    public WorsBot(BotInitObject initObject) {
        super(initObject);
    }

    @Override
    public void preInit(BotInitObject initObject) {
        super.preInit(initObject);
        INSTANCE = this;
        guildFactsList = new ArrayList<>();
    }

    @Override
    public void afterInit(BotInitObject initObject) {
        super.afterInit(initObject);
        setActivity(Activity.watching("worsman's videos"));
        getCommandManager().setDeleteMessages(false);
    }

    @Override
    public void addBotCommands(HashMap<String, BotCommand> commands) {
        super.addBotCommands(commands);
        commands.put("fact", new FactCommand());
    }

    @Override
    public void addConfigUpdater(List<ConfigUpdater> updater) {
        super.addConfigUpdater(updater);
        updater.add(new GuildFactUpdater());
    }

    @Override
    public void addFeatures() {
        super.addFeatures();
        features.add(new JoinRoleFeature());
    }

    public void addGuildFacts(GuildFacts guildFacts) {
        guildFactsList.add(guildFacts);
    }

    public List<GuildFacts> getGuildFactsList() {
        return guildFactsList;
    }
}
