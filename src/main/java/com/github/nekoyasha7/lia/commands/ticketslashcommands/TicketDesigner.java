//<<< Package >>>//
package com.github.nekoyasha7.lia.commands.ticketslashcommands;
//<<< End Package >>>//

//<<< Imports >>>//
import com.github.nekoyasha7.lia.main.Main;
import com.github.nekoyasha7.lia.util.Vulcan;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.Color;

import java.util.EnumSet;
//<<< End Imports >>>//
/*
 /* @author NekoYasha
 */
//<<< Class >>>//
public class TicketDesigner extends TicketAutor{

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        //--+ Verifica se é Bot +--//
        if(event.getMember().getUser().isBot()) return;

        if (event.getName().equalsIgnoreCase("criar-painel-ticket-designer")) {

            String title = event.getOption("titulo").getAsString();
            String description = event.getOption("descricao").getAsString();
            buttonText = event.getOption("botao").getAsString();

            //--+ Embed setups +--//
            eb.setColor(Color.green);
            eb.setTitle(title);
            eb.setDescription(description);
            eb.addBlankField(false);

            event.getChannel().sendMessageEmbeds(eb.build())
                              .addActionRow(startButton(buttonText))
                              .queue();

            event.reply("Embed enviada com sucesso!")
                    .setEphemeral(true)
                    .queue();
        }
    }

        @Override
        public void onButtonInteraction(ButtonInteractionEvent event){

            if (event.getComponentId().equals("startButtonDesigner")) {

                Vulcan vulcan = new Vulcan();

                event.reply("Ticket aberto com sucesso!")
                        .setEphemeral(true)
                        .queue();

                Member member = event.getMember();
                Guild guild = event.getGuild();

                String tickets = String.valueOf(guild.getChannels());
                if (tickets.contains("ticket-" + event.getMember().getUser().getName())) {
                    event.reply("Você já criou um ticket!")
                            .setEphemeral(true)
                            .queue();
                    return;
                }

                Category category = guild.getCategoryById(recrutamentoID);

                TextChannel privateTicket = guild.createTextChannel("ticket-" + member.getUser().getName())
                        .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                        .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                        .addPermissionOverride(guild.getRoleById(avaliadorId), EnumSet.of(Permission.VIEW_CHANNEL), null)
                        .setParent(category)
                        .complete();


                vulcan.setRoleAvaliadorName("Vice-Líder");
                vulcan.setRoleAvaliadorId("771617590853369896");
                avaliadorId = vulcan.getRoleAvaliadorId();
                String privateTicketId = privateTicket.getId();


                //--+ Envia uma mensagem inicial no ticket +--//

                privateTicket.sendMessage(
                            "> **Avaliando:** " + member.getUser().getName() + "\n" +
                                 "> **Tipo de Avaliação:** Designer\n" +
                                 "> **Tipo de Avaliador:** " +  guild.getRoleById(avaliadorId).getAsMention() + "\n\n" +
                                 "Olá, " + member.getAsMention() + ", Fico contente que tenha optado por fazer uma avaliação na Vulcan Novels para **Designer**.\n" +
                                 "Em breve um " + guild.getRoleById(avaliadorId).getAsMention() + " iniciará o seu processo de avaliação!")
                        .addActionRow(Button.danger("closeTicket", "Fechar ticket"))
                        .queue();

                //--+Pega o ID do servidor da staff e envia informações dos novos tickets em um canal específico+--//
                Guild vulcanStaff = Main.jda.getGuildById(vulcan.getServerVulcanStaffId());
                MessageChannel ticketsLog = vulcanStaff.getTextChannelById(vulcan.getChannelTicketsLogId());
                ticketsLog.sendMessageEmbeds(vulcan.createEmbedNewTicket("Designer", member.getUser().getName(), vulcan.getServerVulcanStaffId(), vulcan.getRoleAvaliadorId(), privateTicketId).build()).queue();

                avaliando = event.getUser().getName();
            }
        }

        @Override
        protected Button startButton(String buttonText){
            return Button.success("startButtonDesigner", buttonText);
        }
    }
