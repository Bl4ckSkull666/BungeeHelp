/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeehelp.commands;

import de.bl4ckskull666.bungeehelp.BH;
import de.bl4ckskull666.bungeehelp.LanguageManager;
import de.bl4ckskull666.bungeehelp.utils.Numbers;
import de.bl4ckskull666.mu1ti1ingu41.utils.Utils;
import java.util.HashMap;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Bl4ckSkull666
 */
public class Help extends Command {
    
    public Help(String cmd, String perm) {
        super(cmd, perm);
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        if(!(s instanceof ProxiedPlayer))
            return;
        
        ProxyServer.getInstance().getScheduler().runAsync(BH.getPlugin(), new runCommand((ProxiedPlayer)s, a));
    }
    
    
    private class runCommand implements Runnable {
        private final ProxiedPlayer _p;
        private final String[] _a;
        
        public runCommand(ProxiedPlayer p, String[] a) {
            _p = p;
            _a = a;
        }
        
        @Override
        public void run() {
            HashMap<String, String> sr = new HashMap<>();
            FileConfiguration fc = LanguageManager.getLanguageConfiguration(_p);
            if(fc == null) {
                LanguageManager.sendMessage(_p, "internal-error", "We are Sorry, it's take a internal error. Please try again.", sr);
                return;
            }

            String section = "help.global";
            String server = _p.getServer().getInfo().getName().toLowerCase();
            if(fc.isConfigurationSection("help." + server))
                section = "help." + server;
            String ssection = "index";
            int page = 1;

            if(_a.length > 0) {
                for(String arg: _a) {
                    if(Numbers.isNumeric(arg)) {
                        page = Integer.parseInt(arg);
                    } else if(fc.isConfigurationSection(section + "." + arg)) {
                        ssection = arg;
                    }
                }
            }

            if(!fc.isConfigurationSection(section + "." + ssection)) {
                LanguageManager.sendMessage(_p, "unknow-section", "We are Sorry, but we can't find the given menu.", sr);
                return;
            }
            
            int pages = 0;
            for(String k: fc.getConfigurationSection(section + "." + ssection).getKeys(false)) {
                if(Numbers.isNumeric(k))
                    pages++;
            }

            if(!fc.isList(section + "." + ssection + "." + String.valueOf(page)) && !fc.isConfigurationSection(section + "." + ssection + "." + String.valueOf(page))) {
                if(!fc.isList(section + "." + ssection + ".1")) {
                    LanguageManager.sendMessage(_p, "no-page-found", "Can't find the giveb Page. View you Page 1.", sr);
                    page = 1;
                } else {
                    LanguageManager.sendMessage(_p, "internal-error", "We are Sorry, it's take a internal error. Please try again.", sr);
                    return;
                }
            }

            LanguageManager.sendMessage(_p, "header", "&e=============== &6Help Menu &e===============", sr);
            if(fc.isList(section + "." + ssection + "." + String.valueOf(page))) {
                for(String line: fc.getStringList(section + "." + ssection + "." + String.valueOf(page)))
                    _p.sendMessage(LanguageManager.convertString(setColor(line, _p)));
            } else if(fc.isConfigurationSection(section + "." + ssection + "." + String.valueOf(page))) {
                for(String k: fc.getConfigurationSection(section + "." + ssection + "." + String.valueOf(page)).getKeys(false)) {
                    String path = section + "." + ssection + "." + String.valueOf(page) + "." + k;
                    if(fc.isConfigurationSection(path) && fc.isString(path + ".message")) {
                        TextComponent msg = new TextComponent(setColor(fc.getString(path + ".message"), _p));
                        if(fc.isString(path + ".hover-text")) {
                            msg.setHoverEvent(
                                    new HoverEvent(
                                    Utils.isHoverAction("show_" + fc.getString(path + ".hover-type", "text"))?HoverEvent.Action.valueOf(("show_" + fc.getString(path + ".hover-type", "text")).toUpperCase()):HoverEvent.Action.SHOW_TEXT, 
                                    new Text(setColor(fc.getString(path + ".hover-text"), _p))
                                )
                            );
                        }
                        if(fc.isString(path + ".click-text")) {
                            msg.setClickEvent(
                                    new ClickEvent(
                                    Utils.isClickAction(fc.getString(path + ".click-type", "open_url"))?ClickEvent.Action.valueOf(fc.getString(path + ".click-type", "open_url").toUpperCase()):ClickEvent.Action.OPEN_URL, 
                                    setColor(fc.getString(path + ".click-text"), _p)
                                )
                            );
                        }
                        _p.sendMessage(msg);
                    } else if(fc.isString(path)) {
                        TextComponent msg = new TextComponent(setColor(fc.getString(path), _p));
                        _p.sendMessage(msg);
                    } else {
                        BH.getPlugin().getLogger().log(Level.INFO, "Missing Help path: " + path);
                    }
                }
            } else {
                BH.getPlugin().getLogger().log(Level.INFO, "Wrong configuration of: " + section + "." + ssection + "." + String.valueOf(page));
                return;
            }
            
            if(pages > 1) {
                sr.put("%cur%", String.valueOf(page));
                sr.put("%max%", String.valueOf(pages));
                LanguageManager.sendMessage(_p, "page", "Page %cur% of %max% pages.", sr);
            }
            LanguageManager.sendMessage(_p, "footer", "&e=============== &6Help Menu &e===============", sr);
                
        }
    }
    
    private String setColor(String str, ProxiedPlayer p) {
        return ChatColor.translateAlternateColorCodes('&', str.replace("%name%", p.getName()).replace("%uuid%", p.getUniqueId().toString()).replace("%displayname%", p.getDisplayName()));
    }
}
