package de.bloody9.worsman.features.joinrole;

import de.bloody9.core.feature.Feature;
import de.bloody9.core.logging.Logger;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/*
Requires database table

CREATE TABLE join_roles
(
	role_id char(18) primary key,
    guild_id char(18)
);
*/
public class JoinRoleFeature extends Feature {

    public static JoinRoleGuildManager getJoinRoleManagerByGuildId(@NotNull String guildId) {
        JoinRoleFeature rrf = (JoinRoleFeature) JoinRoleFeature.INSTANCE;

        if (rrf == null || !rrf.isEnabled()) { return null; }
        Logger.debug("getting role section manager by guildID: " + guildId);

        for (JoinRoleGuildManager joinRoleManager : rrf.getJoinRoleManagers()) {
            if (joinRoleManager.getGuildId().equals(guildId)) {
                return joinRoleManager;
            }
        }

        Logger.debug("adding new roleSectionManager because non is already loaded: " + guildId);
        Guild guild = rrf.getBot().getJda().getGuildById(guildId);

        if (guild == null) {
            Logger.error("Can't get guild object from id: " + guildId);
            return null;
        }

        JoinRoleGuildManager joinRoleManager = new JoinRoleGuildManager(guild);
        rrf.addJoinRoleManager(joinRoleManager);
        return joinRoleManager;
    }

    private final List<JoinRoleGuildManager> manager;
    public static Feature INSTANCE;

    public JoinRoleFeature() {
        super();

        INSTANCE = this;
        manager = new ArrayList<>();

        addCommands();
        addListener();
    }

    @Override
    public void load() {
        debug("loading guild managers");
        getBot().getJda().getGuilds().forEach(guild -> getJoinRoleManagerByGuildId(guild.getId()));
    }

    @Override
    public @NotNull String getName() {
        return "JoinRole";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public void addCommands() {
        addCommand("joinrole", new JoinRoleCommand());
    }

    public void addListener() {
        addListener(new JoinListener());
    }

    public void addJoinRoleManager(JoinRoleGuildManager manager) {
        this.manager.add(manager);
    }

    public List<JoinRoleGuildManager> getJoinRoleManagers() {
        return manager;
    }
}
