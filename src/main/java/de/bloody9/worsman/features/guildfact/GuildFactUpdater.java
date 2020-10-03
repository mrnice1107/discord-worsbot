package de.bloody9.worsman.features.guildfact;

import de.bloody9.core.models.interfaces.ConfigUpdater;
import de.bloody9.core.models.objects.UpdatableGuildObject;
import de.bloody9.worsman.WorsBot;

import java.util.List;

public class GuildFactUpdater implements ConfigUpdater {
    private final WorsBot INSTANCE;

    public GuildFactUpdater() {
        INSTANCE = WorsBot.INSTANCE;
    }

    @Override
    public UpdatableGuildObject getGuildConfigByGuildID(String guildId) {
        return GuildFacts.getGuildFactsByGuildID(guildId);
    }

    @Override
    public List<? extends UpdatableGuildObject> getGuildAllConfigs() {
        return INSTANCE.getGuildFactsList();
    }
}
