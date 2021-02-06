package de.bl4ckskull666.bungeehelp;

import de.bl4ckskull666.bungeehelp.utils.ResourceList;
import de.bl4ckskull666.bungeehelp.utils.Rnd;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Bl4ckSkull666
 */
public class LanguageManager extends Command {
    private static final Map<String, FileConfiguration> _languages = new HashMap<>();
    private static Locale _defaultLocale = Locale.ENGLISH;
    
    public LanguageManager() {
        super("blanguage", "language.manager");
        load();
    }
    
    @Override
    public void execute(CommandSender s, String[] a) {
        ProxiedPlayer pp = null;
        if(s instanceof ProxiedPlayer)
            pp = (ProxiedPlayer)s;
        
        
        _languages.clear();
        load();
        sendMessage((pp != null?pp:s), "messages.language-reloaded", "&f[&cBHelp&f]&aLanguage Files has been reloaded.", null);
    }
    
    private void load() {
        File folder = new File(BH.getPlugin().getDataFolder(), "localization");
        if(!folder.isDirectory()) {
            folder.mkdirs();
            createDefaults();
        }
        
        for(File f: folder.listFiles()) {
            if(!f.getName().endsWith(".yml"))
                continue;
            
            String lang = f.getName().toLowerCase().substring(0, f.getName().length()-4);
            Locale loc = new Locale(lang);
            if(loc.getLanguage().isEmpty()) {
                BH.getPlugin().getLogger().log(Level.WARNING, "Locale for Language File " + lang + ".yml has no language.");
                continue;
            }
            
            FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
            _languages.put(loc.getLanguage().toLowerCase(), fc);
        }
        
        if(_languages.size() == 0) {
            BH.getPlugin().getLogger().log(Level.INFO, "Need min. 1 Language File. But missing ALL!!!");
            return;
        }
        
        BH.getPlugin().getLogger().log(Level.INFO, "Loaded " + _languages.size() + " Language Files.");
        setDefaultLocale();
    }
    
    private void setDefaultLocale() {
        if(BH.getPlugin().getConfig().isString("default-language")) {
            Locale defLocale = new Locale(BH.getPlugin().getConfig().getString("default-language"));
            if(defLocale.getLanguage().isEmpty()) {
                BH.getPlugin().getLogger().log(Level.WARNING, "Incorrect default-language in configuration.");
                setRandomLanguage();
                return;
            }
            
            if(!_languages.containsKey(defLocale.getLanguage().toLowerCase())) {
                String lang = "";
                for(String k: _languages.keySet())
                    lang += (lang.isEmpty()?"":", ") + k;
                
                BH.getPlugin().getLogger().log(Level.WARNING, "No Language file for default-language! Available languages for default-language: " + lang);
                setRandomLanguage();
                return;
            }
            _defaultLocale = defLocale;
        } else {
            if(!_languages.containsKey(_defaultLocale.getLanguage().toLowerCase())) {
                String lang = "";
                for(String k: _languages.keySet())
                    lang += (lang.isEmpty()?"":", ") + k;
                
                BH.getPlugin().getLogger().log(Level.WARNING, "Missing default-language in configuration! Available languages for default-language: " + lang);
                setRandomLanguage();
            }
        }
    }
    
    private void setRandomLanguage() {
        List<String> tmp = new ArrayList<>();
        tmp.addAll(_languages.keySet());
        int r = Rnd.get(1, tmp.size())-1;
        _defaultLocale = new Locale(tmp.get(r));
        BH.getPlugin().getLogger().log(Level.WARNING, "Set " + _defaultLocale.getLanguage() + " as default Language.");
    }
    
    private void createDefaults() {
        File def = getDefaultFile();
        FileConfiguration defFC = YamlConfiguration.loadConfiguration(def);
        File folder = new File(BH.getPlugin().getDataFolder(), "localization");
        for(Locale loc: Locale.getAvailableLocales()) {
            if(loc.getLanguage().isEmpty())
                continue;
            
            File lang = new File(folder, loc.getLanguage().toLowerCase() + ".yml");
            if(lang.exists())
                continue;
            
            try {
                defFC.save(lang);
            } catch(IOException ex) {
                BH.getPlugin().getLogger().log(Level.WARNING, "Can't save default localization file " + lang.getName());
            }
        }
        
        if(def.exists())
            def.delete();
        
        BH.getPlugin().getLogger().log(Level.INFO, "Added all available Languages to folder localization. You can edit or remove not useable.");
    }
    
    private File getDefaultFile() {
        File def = new File(new File(BH.getPlugin().getDataFolder(), "localization"), "defaultLanguage.yml");
        String srcFile = ResourceList.getResourceFile(BH.getPlugin().getClass(), "defaultLanguage.yml");
        if(srcFile.isEmpty()) {
            
            return def;
        }
        
        InputStream in = BH.getPlugin().getResourceAsStream(srcFile);
        int c = -1;
        try {
            OutputStream os = new FileOutputStream(def);
            while((c = in.read()) != -1)
                os.write(c);
            os.close();
            in.close();
        } catch(IOException ex) {
            
        }
        return def;
    }
    
    private static FileConfiguration getLanguageConfiguration(Locale loc) {
        if(_languages.containsKey(loc.getLanguage().toLowerCase()))
            return _languages.get(loc.getLanguage().toLowerCase());
    
        if(_languages.containsKey(_defaultLocale.getLanguage().toLowerCase()))
            _languages.get(_defaultLocale.getLanguage().toLowerCase());
        
        if(!_languages.isEmpty()) {
            int i = Rnd.get(1, _languages.size())-1;
            return _languages.get(String.valueOf(_languages.keySet().toArray()[i]));
        }
        return null;
    }
    
    public static FileConfiguration getLanguageConfiguration(Object obj) {
        Locale loc;
        if(obj instanceof ProxiedPlayer)
            loc = ((ProxiedPlayer)obj).getLocale();
        else
            loc = _defaultLocale;
        
        return getLanguageConfiguration(loc);
    }
    
    private static void sendMessage(Object obj, TextComponent[] txt) {
        if(obj == null)
            return;
        
        if(obj instanceof CommandSender)
            ((CommandSender)obj).sendMessage(txt);
        else if(obj instanceof ProxiedPlayer)
            ((ProxiedPlayer)obj).sendMessage(txt);
    }
    
    public static void sendMessage(Object obj, String path, String def, HashMap<String, String> sr) {
        Locale loc;
        if(obj instanceof ProxiedPlayer)
            loc = ((ProxiedPlayer)obj).getLocale();
        else if(obj instanceof CommandSender)
            loc = _defaultLocale;
        else
            return;
        
        sendMessage(obj, getMessage(loc, path, def, sr));
    }
    
    public static TextComponent[] getMessage(Locale loc, String path, String def, HashMap<String, String> sr) {
        FileConfiguration fc = getLanguageConfiguration(loc);
        List<TextComponent> tcl = new ArrayList<>();
        if(fc == null) {
            tcl.add(replaceAll(def, sr));
            return toArray(tcl);
        }
        
        if(fc.isString(path)) {
            tcl.add(replaceAll(fc.getString(path), sr));
        } else if(fc.isConfigurationSection(path)) {
            ConfigurationSection cs = fc.getConfigurationSection(path);
            List<Integer> keys = new ArrayList<>();
            for(String strKey: cs.getKeys(false)) {
                try {
                    keys.add(Integer.parseInt(strKey));
                } catch(Exception ex) {
                    BH.getPlugin().getLogger().warning("Please use only integer for Spigot/WorldEdit and Multi Line Messages.");
                }
            }
            Collections.sort(keys);

            TextComponent txt = new TextComponent();
            for(int ik: keys) {
                String k = String.valueOf(ik);
                if(cs.isString(k)) {
                    txt.addExtra(replaceAll(cs.getString(k), sr));
                } else if(cs.isString(k + ".message")) {
                    TextComponent tmp = replaceAll(cs.getString(k + ".message"), sr);
                    if(cs.isString(k + ".hover-msg")) {
                        Text bch;
                        bch = replaceAllText(cs.getString(k + ".hover-msg"), sr);
                        HoverEvent hoverev = new HoverEvent(
                            getHoverAction(cs.getString(k + ".hover-type", "text")), 
                            bch
                        );
                        tmp.setHoverEvent(hoverev);
                    }

                    if(cs.isString(k + ".click-msg")) {
                        ClickEvent clickev = new ClickEvent(
                            getClickAction(cs.getString(k + ".click-type", "open_url")), 
                            replaceString(cs.getString(k + ".click-msg"), sr)
                        );
                        tmp.setClickEvent(clickev);
                    }
                    
                    txt.addExtra(tmp);
                    if(cs.getBoolean(k + ".break", false)) {
                        tcl.add(txt);
                        txt = new TextComponent();
                    }
                }
            }
            
            if(txt != new TextComponent()) {
                tcl.add(txt);
            }
        } else {
            tcl.add(replaceAll(def, sr));
        }
        return toArray(tcl);
    }
    
    /*
    * You can break Line by split on |
    */
    public static String getTextMessage(Locale loc, String path, String def, HashMap<String, String> sr) {
        FileConfiguration fc = getLanguageConfiguration(loc);
        if(fc == null) {
            return replaceString(def, sr);
        }
        
        if(fc.isString(path)) {
            return replaceString(fc.getString(path), sr);
        } else if(fc.isConfigurationSection(path)) {
            ConfigurationSection cs = fc.getConfigurationSection(path);
            List<Integer> keys = new ArrayList<>();
            for(String strKey: cs.getKeys(false)) {
                try {
                    keys.add(Integer.parseInt(strKey));
                } catch(Exception ex) {
                    BH.getPlugin().getLogger().warning("Please use only integer for Spigot/WorldEdit and Multi Line Messages.");
                }
            }
            Collections.sort(keys);

            String txt = "";
            for(int ik: keys) {
                String k = String.valueOf(ik);
                if(cs.isString(k)) {
                    txt += replaceString(cs.getString(k), sr);
                } else if(cs.isString(k + ".message")) {
                    txt += replaceString(cs.getString(k + ".message"), sr);
                    
                    if(cs.getBoolean(k + ".break", false))
                        txt += " | ";
                }
            }
            return txt;
        } else {
            return replaceString(def, sr);
        }
    }
    
    public static Locale getDefaultLocale() {
        return _defaultLocale;
    }
    
    private static TextComponent[] toArray(List<TextComponent> tc) {
        TextComponent[] tca = new TextComponent[tc.size()];
        return (TextComponent[])tc.toArray(tca);
    }
    
    public static TextComponent replaceAll(String str, HashMap<String, String> sr) {
        if(sr == null) 
            return new TextComponent(ChatColor.translateAlternateColorCodes('&', str));
        
        for(Map.Entry<String, String> me: sr.entrySet()) {
            str = str.replace(me.getKey(), me.getValue());
        }
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', str));
    }
    
    public static Text replaceAllText(String str, HashMap<String, String> sr) {
        if(sr == null) 
            return new Text(ChatColor.translateAlternateColorCodes('&', str));
        
        for(Map.Entry<String, String> me: sr.entrySet()) {
            str = str.replace(me.getKey(), me.getValue());
        }
        return new Text(ChatColor.translateAlternateColorCodes('&', str));
    }
    
    public static String replaceString(String str, HashMap<String, String> sr) {
        if(sr == null) 
            return ChatColor.translateAlternateColorCodes('&', str);
        
        for(Map.Entry<String, String> me: sr.entrySet()) {
            str = str.replace(me.getKey(), me.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    private static HoverEvent.Action getHoverAction(String str) {
        if(HoverEvent.Action.valueOf("SHOW_" + str.toUpperCase()) != null)
            return HoverEvent.Action.valueOf("SHOW_" + str.toUpperCase());
        return HoverEvent.Action.SHOW_TEXT;
    }
    
    private static ClickEvent.Action getClickAction(String str) {
        if(ClickEvent.Action.valueOf(str.toUpperCase()) != null)
            return ClickEvent.Action.valueOf(str.toUpperCase());
        return ClickEvent.Action.RUN_COMMAND;
    }
    
    public static BaseComponent[] convertString(String msg) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
