package de.bloody9.worsman.features.joinrole;

import static de.bloody9.core.logging.Logger.*;
import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;

import de.bloody9.core.exceptions.Command.*;
import de.bloody9.core.exceptions.Feature.FeatureLoadException;
import de.bloody9.core.exceptions.Mentioned.NoMentionedRolesCommandException;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.PermissionObject;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class JoinRoleCommand implements BotCommand {

    private static final String generalPermission = "commands.joinrole";

    private final List<PermissionObject> permissionObjects;
    private final List<String> aliases;

    public JoinRoleCommand() {
        permissionObjects = new ArrayList<>();
        permissionObjects.add(new PermissionObject(generalPermission, "Execute JoinRole Command"));

        aliases = new ArrayList<>();
        aliases.add("jr");
    }

    private void sendHelp(User user) { Helper.sendPrivateMessage(user, getHelp()); }

    @Override
    public String getDescription() { return "This command is to set up roles that users become when they join the guild (discord server)"; }

    @Override
    public String getHelp() {
        return Helper.constructHelp("JoinRoleCommand\n" +
                "<prefix> joinrole add/remove <@Roles...>\n" +
                "<prefix> joinrole list | returns a list of all configured join roles");
    }

    @Override
    public List<PermissionObject> getPermissions() {
        return permissionObjects;
    }

    @Override
    public List<String> getAlias() {
        return aliases;
    }

    // if return true the initial command message will be removed
    @Override
    public boolean performCommand(String command, User sender, Message message, String[] args) {

        debug("start JoinRoleCommand");

        if (!memberHasPermission(generalPermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, generalPermission);
        }

        debug("checking args.length > 0");
        if (args.length <= 0) {
            sendHelp(sender);

            throw new NotEnoughArgumentCommandException(args.length);
        }

        debug("check if args[0] == help: " + args[0].toLowerCase());
        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);

            return true;
        }

        JoinRoleGuildManager manager = JoinRoleFeature.getJoinRoleManagerByGuildId(message.getGuild().getId());
        if (manager == null) {
            Helper.sendPrivateMessage("An internal error occured while executing your command. Please contact a developer", sender);
            throw new FeatureLoadException("JoinRoleFeature (GuildManager)");
        }

        switch (args[0].toLowerCase()) {
            case "set":
            case "add": {
                checkMentioendRoles(message, sender);
                manager.addJoinRoles(message.getMentionedRoles());
                Helper.sendPrivateMessage("You successfully added the roles " + message.getMentionedRoles() + " to the auto assign system", sender);
                return true;
            }
            case "rm":
            case "remove": {
                checkMentioendRoles(message, sender);
                manager.removeJoinRoles(message.getMentionedRoles());
                Helper.sendPrivateMessage("You successfully removed the roles " + message.getMentionedRoles() + " from the auto assign system", sender);
                return true;
            }
            case "ls":
            case "list": {
                StringJoiner joiner = new StringJoiner(",\n");
                manager.getJoinRoles().forEach(role -> joiner.add("@" + role.getName()));
                Helper.sendPrivateMessage(sender, "The following roles are set up for auto assignment when joining the server\n" + joiner.toString());
                return true;
            }
            default: {
                sendHelp(sender);
                throw new WrongArgumentCommandException();
            }
        }
    }
    private void checkMentioendRoles(Message message, User sender) {
        if (message.getMentionedRoles().isEmpty()) {
            Helper.sendPrivateMessage("You need to mention @roles for this command", sender);
            throw new NoMentionedRolesCommandException();
        }
    }

}