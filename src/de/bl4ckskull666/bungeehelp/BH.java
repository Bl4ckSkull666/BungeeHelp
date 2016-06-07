/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeehelp;

import de.bl4ckskull666.bungeehelp.commands.Help;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Bl4ckSkull666
 */
public class BH extends Plugin {
    private FileConfiguration _config; 
    @Override
    public void onEnable() {
        _plugin = this;
        _config = Mu1ti1ingu41.loadConfig(this);
        
        if(_config.isConfigurationSection("commands")) {
            for(String cmd: _config.getConfigurationSection("commands").getKeys(false)) {
                if(_config.isString("commands." + cmd))
                    ProxyServer.getInstance().getPluginManager().registerCommand(this, new Help(cmd, _config.getString("commands." + cmd)));
            }
        }
        Mu1ti1ingu41.loadExternalDefaultLanguage(this, "helps");
    }
    
    public void loadConfig() {
        _config = Mu1ti1ingu41.loadConfig(_plugin);
    }
    
    public void saveConfig() {
        Mu1ti1ingu41.saveConfig(_config, _plugin);
    }
    
    private static BH _plugin = null;
    public static BH getPlugin() {
        return _plugin;
    }
}
