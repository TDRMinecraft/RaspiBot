package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.ddns.rootrobo.RaspiBot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("unused")
public class KickCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        User u = MessageUtils.getFirstUser(msg, args);
        if(u == null) {
            return;
        }

        Member target = guild.getMember(u);

        if(target == null) {
            target = guild.getMemberById(u.getId());
            if(target == null) return;
        }

        if(!guild.getSelfMember().canInteract(target)) {
            msg.getChannel().sendMessage(EmbedUtils.getSelfNoPermissionEmbed(msg.getAuthor(), "to kick that user!")).complete();
        } else {
            if(!Objects.requireNonNull(msg.getMember()).canInteract(target)) {
                Message errorMSG = new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle("Error")
                        .setColor(EmbedUtils.ERROR_COLOR)
                        .setDescription("You can't interact with that member!")
                        .build()).build();
                msg.getChannel().sendMessage(errorMSG).complete();
                return;
            }

            final String[] reason = new String[1];
            if(Arrays.copyOfRange(args, 1, args.length).length == 0) {
                reason[0] = null;
            } else {
                reason[0] = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }

            String AvatarURL = target.getUser().getAvatarUrl();
            if(AvatarURL == null) AvatarURL = target.getUser().getDefaultAvatarUrl();
            if(reason[0] == null) {
                reason[0] = "literally no reason";
            } else {
                reason[0] = "*"+reason[0]+"*";
            }

            String finalAvatarURL = AvatarURL;
            Member finalTarget = target;
            guild.kick(target, reason[0]).queue(
                    success -> {
                        Message embed = new MessageBuilder().setEmbed(new EmbedBuilder()
                                .setTitle(MessageUtils.CHECK_MARK+" Success!")
                                .setDescription(msg.getAuthor().getAsMention()+" kicked "+ finalTarget.getUser().getAsTag()+" for "+reason[0])
                                .setThumbnail(finalAvatarURL)
                                .setColor(new Color(EmbedUtils.SUCCESS_COLOR))
                                .setTimestamp(OffsetDateTime.now())
                                .setFooter(EmbedUtils.FOOTER_TEXT, EmbedUtils.FOOTER_ICON)
                                .build()).build();
                        msg.getChannel().sendMessage(embed).queue();
                    }
            );
        }
    }

    @Override
    public String getName() { return "kick"; }

    @Override
    public String getDescription() { return null; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return Permission.KICK_MEMBERS; }
}
