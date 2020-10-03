package de.bloody9.worsman.features.guildfact;

import de.bloody9.core.models.objects.GuildObject;
import net.dv8tion.jda.api.entities.Guild;

public class Fact extends GuildObject {
    private final String fact;
    private final int id;

    public Fact(Guild guild, String fact, int id) {
        super(guild);

        this.fact = fact;
        this.id = id;
    }

    public String getFact() {
        return fact;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "id: " + id + ", fact: " + fact;
    }
}
