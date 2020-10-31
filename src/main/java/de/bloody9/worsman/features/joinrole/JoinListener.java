package de.bloody9.worsman.features.joinrole;

import de.bloody9.core.logging.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();

        JoinRoleGuildManager jrm = JoinRoleFeature.getJoinRoleManagerByGuildId(guild.getId());
        if (jrm == null) {
            Logger.error("Failed to load joinRoleguildManager from feature");
            return;
        }
        Member member = event.getMember();

        jrm.debug("Adding join roles to member: " + member.getUser().getAsTag());
        jrm.getJoinRoles().forEach(role -> event.getGuild().addRoleToMember(member, role).queue());
    }
}
