package de.bloody9.worsman.features.joinrole;

import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.objects.GuildObject;
import de.bloody9.core.mysql.MySQLConnection;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JoinRoleGuildManager extends GuildObject {
    public static final String TABLE = "join_roles";
    public static final String ROLE_ID = "role_id";
    public static final String GUILD_ID = "guild_id";

    private final List<Role> joinRoles;

    private static final String contentType = ROLE_ID + "," + GUILD_ID;

    public JoinRoleGuildManager(Guild guild) {
        super(guild);

        joinRoles = new ArrayList<>();
        load();
    }

    public void load() {
        final String query = GUILD_ID + "=" + getGuildId();
        Helper.getObjectFromDB(ROLE_ID, TABLE, query).forEach(this::loadRole);
    }

    private void loadRole(String roleId) {
        Role role = getGuild().getRoleById(roleId);
        if (role != null) joinRoles.add(role);
    }

    public JoinRoleGuildManager removeJoinRoles(@NotNull List<Role> joinRoles) {
        this.joinRoles.removeAll(joinRoles);
        List<String> updates = new ArrayList<>();
        joinRoles.forEach(role -> {
            final String query = ROLE_ID + "=" + role.getId();
            updates.add(Helper.getDeleteSQL(TABLE, query));
        });
        MySQLConnection.executeUpdate(updates);
        return this;
    }
    public JoinRoleGuildManager removeJoinRole(@NotNull Role joinRole) {
        joinRoles.remove(joinRole);
        final String query = ROLE_ID + "=" + joinRole.getId();
        Helper.executeDeleteSQL(TABLE, query);
        return this;
    }

    public JoinRoleGuildManager addJoinRoles(@NotNull List<Role> joinRoles) {
        this.joinRoles.addAll(joinRoles);
        List<String> updates = new ArrayList<>();
        joinRoles.forEach(role -> {
            final String content = role.getId() + "," + getGuildId();
            updates.add(Helper.getInsertSQL(TABLE, contentType, content));
        });
        MySQLConnection.executeUpdate(updates);
        return this;
    }
    public JoinRoleGuildManager addJoinRole(@NotNull Role joinRole) {
        if (!joinRoles.contains(joinRole)) {
            joinRoles.add(joinRole);
            final String content = joinRole.getId() + "," + getGuildId();
            Helper.executeInsertSQL(TABLE, contentType, content);
        }
        return this;
    }

    public List<Role> getJoinRoles() { return joinRoles; }
}
