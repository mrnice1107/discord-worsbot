package de.bloody9.worsman.commands;

import static de.bloody9.core.logging.Logger.*;
import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;

import de.bloody9.core.exceptions.Command.*;
import de.bloody9.core.exceptions.Feature.FeatureException;
import de.bloody9.core.exceptions.Feature.FeatureLoadException;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.PermissionObject;
import de.bloody9.worsman.features.guildfact.GuildFacts;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class FactCommand implements BotCommand {

    private static final String generalPermission = "commands.fact";
    private static final String editPermission = "commands.fact.edit";
    private final List<PermissionObject> permissionObjects;
    private final String description;

    private final String help;

    private final List<String> aliases;


    public FactCommand() {
        permissionObjects = new ArrayList<>();

        permissionObjects.add(new PermissionObject(generalPermission, "Execute command"));
        permissionObjects.add(new PermissionObject(editPermission, "add/remove/list permissions"));

        aliases = new ArrayList<>();

        description = "What this commands says is fact!";

        help = "fact Command\n" +
                "<prefix> fact [add/remove/list]\n" +
                "<prefix> fact add <fact> | adds a new fact\n" +
                "<prefix> fact remove <fact_id> | removes a fact by id (get id from list)\n" +
                "<prefix> fact list | lists the available facts";
    }

    private void sendHelp(User user) {
        Helper.sendPrivateMessage(user, getHelp());
    }

    @Override
    public List<String> getAlias() {
        return aliases;
    }

    @Override
    public String getHelp() {
        return Helper.constructHelp(help);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<PermissionObject> getPermissions() {
        return permissionObjects;
    }

    @Override
    public boolean performCommand(String command, User sender, Message message, String[] args) {

        debug("start guildCommand");

        if (!memberHasPermission(generalPermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, generalPermission);
        }

        GuildFacts facts = GuildFacts.getGuildFactsByGuildID(message.getGuild().getId());
        if (facts == null) {
            error("failed to get GuildFactObject by id");
            Helper.sendPrivateMessage(sender, "An internal error occurred while executing your command!");
            throw new FeatureLoadException("GuildFacts");
        }

        if (args.length == 0) {
            TextChannel channel = message.getTextChannel();
            channel.sendMessage(facts.getRandomFact()).queue();
            return false;
        }

        if (!memberHasPermission(editPermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, editPermission);
        }

        switch (args[0].toLowerCase()) {
            case "add": {
                if (args.length == 1) {
                    sendHelp(sender);
                    return true;
                }
                StringJoiner joiner = new StringJoiner(" ");
                for (int i = 1; i < args.length; i++) {
                    joiner.add(args[i]);
                }
                String fact = joiner.toString();

                if (facts.addFact(fact)) {
                    Helper.sendPrivateMessage(sender, "You successfully added the fact: " + fact);
                    return true;
                } else {
                    Helper.sendPrivateMessage(sender, "An internal error occurred while inserting your fact: " + fact);
                    throw new FeatureException("GuildFact failed add fact");
                }
            }
            case "remove": {
                if (args.length != 2) {
                    sendHelp(sender);
                    return true;
                }

                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Helper.sendPrivateMessage(sender, "You need to enter a number as id!");
                    throw new NoNumberCommandException();
                }

                if (facts.removeFact(id)) {
                    Helper.sendPrivateMessage(sender, "You successfully removed the fact with id: " + id);
                    return true;
                } else {
                    Helper.sendPrivateMessage(sender, "Failed to remove the fact. *" + id + "* is no valid or used id!");
                    throw new FeatureException("GuildFact failed remove fact");
                }
            }
            case "list": {
                String factsString = facts.getFactString();

                if (factsString.equals("")) {
                    factsString = "There are no facts :sob:";
                }

                facts.debug("facts:" + factsString);
                Helper.sendPrivateMessage(sender, factsString);
                return true;
            }
            default: {
                sendHelp(sender);
                throw new WrongArgumentCommandException();
            }
        }
    }

}