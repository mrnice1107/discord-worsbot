package de.bloody9.worsman.features.guildfact;

import de.bloody9.core.helper.Helper;
import de.bloody9.core.logging.Logger;
import de.bloody9.core.models.objects.UpdatableGuildObject;
import de.bloody9.core.mysql.MySQLConnection;
import de.bloody9.worsman.WorsBot;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

public class GuildFacts extends UpdatableGuildObject {

    public static GuildFacts getGuildFactsByGuildID(String guildID) {
        final WorsBot INSTANCE = WorsBot.INSTANCE;

        for (GuildFacts guildFacts : INSTANCE.getGuildFactsList()) {
            if (guildFacts.getGuildId().equals(guildID)) {
                return guildFacts;
            }
        }

        Logger.debug("adding new anti spam system because non is already loaded: " + guildID);
        Guild guild = INSTANCE.getJda().getGuildById(guildID);

        if (guild == null) {
            Logger.error("Can't get guild object from id: " + guildID);
            return null;
        }

        GuildFacts guildFacts = new GuildFacts(guild);
        INSTANCE.addGuildFacts(guildFacts);
        return guildFacts;
    }

    private static final String TABLE = "guild_facts";
    private static final String FACT_ID = "fact_id";
    private static final String GUILD_ID = "guild_id";
    private static final String FACT = "fact";

    private List<Fact> factList;

    public GuildFacts(Guild guild) {
        super(guild);

        update();
    }

    @Override
    public void update() {
        info("loading guild facts from database");
        super.update();

        factList = getFactListFromDB();
        info("guild facts loaded");
    }

    private List<Fact> getFactListFromDB() {
        final String columns = FACT_ID + "," + FACT;
        final String query = GUILD_ID + "=" + getGuildId();
        final String sqlQuery = Helper.constructQueryString(columns, TABLE, query);

        List<Fact> factList = new ArrayList<>();
        try {
            Connection con = MySQLConnection.getConnection();
            debug("building connection: " + con.toString());
            Statement stat = con.createStatement();
            debug("statement: " + stat.toString());
            ResultSet resultSet = stat.executeQuery(sqlQuery);
            debug("query result: " + resultSet.toString());
            while (resultSet.next()) {
                String fact = resultSet.getString(FACT);
                int fact_id = resultSet.getInt(FACT_ID);

                Fact f = new Fact(getGuild(), fact, fact_id);
                debug("adding new fact: " + f.toString());
                factList.add(f);
            }

            resultSet.close();
            stat.close();
            con.close();
        } catch (SQLException e) {
            error(e);
        }

        return factList;
    }

    public boolean addFact(String fact) {
        String quotedFact = "\""+fact+"\"";
        Helper.executeInsertSQL(TABLE, GUILD_ID + "," + FACT, getGuildId() + "," + quotedFact);
        List<Integer> list = Helper.getIntegerFromDB(FACT_ID, TABLE, GUILD_ID + "=" +getGuildId() + " AND " + FACT + "=" + quotedFact);
        if (list.isEmpty()) {
            error("Failed to insert fact: " + fact + " into database");
            return false;
        }

        int id = list.get(0);
        factList.add(new Fact(getGuild(), fact, id));

        return true;
    }

    public boolean removeFact(int id) {
        if (factList.removeIf(fact -> fact.getId() == id)) {
            final String query = GUILD_ID + "=" + getGuildId() + " AND " + FACT_ID + "=" + id;
            return Helper.executeDeleteSQL(TABLE, query);
        }
        return false;
    }

    public String getRandomFact() {
        if (factList.isEmpty()) {
            Helper.sendOwner("You have no facts! use the fact command to add some!", getGuild());
            return "nothing :D";
        }
        return factList.get(new Random().nextInt(factList.size())).getFact();
    }

    public String getFactString() {
        StringJoiner joiner = new StringJoiner("\n");

        factList.forEach(fact -> joiner.add(fact.toString()));
        return joiner.toString();
    }

    public List<Fact> getFactList() {
        return factList;
    }
}
