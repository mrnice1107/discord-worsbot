package de.bloody9.worsman;

import de.bloody9.core.Bot;
import de.bloody9.core.models.objects.BotInitObject;
import de.bloody9.feature.guildfact.GuildFactFeature;
import de.bloody9.feature.joinrole.JoinRoleFeature;
import net.dv8tion.jda.api.entities.Activity;

public class WorsBot extends Bot {

    public static WorsBot INSTANCE;

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
    }

    @Override
    public void afterInit(BotInitObject initObject) {
        super.afterInit(initObject);
        setActivity(Activity.watching("worsman's videos"));
        getCommandManager().setDeleteMessages(false);
    }

    @Override
    public void addFeatures() {
        super.addFeatures();
        features.add(new JoinRoleFeature());
        features.add(new GuildFactFeature());
    }
}
