/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeehelp.utils;

/**
 *
 * @author Bl4ckSkull666
 */
import de.bl4ckskull666.bungeehelp.BH;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ResourceList {
    public static String getResourceFile(Class pl, String file) {
        CodeSource src = pl.getProtectionDomain().getCodeSource();
        if(src != null) {
            try {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream( jar.openStream());
                ZipEntry ze;
                
                while( ( ze = zip.getNextEntry() ) != null ) {
                    String entryName = ze.getName();
                    if(entryName.equals(file))
                        return entryName;
                }
            } catch (IOException ex) {
                BH.getPlugin().getLogger().log(Level.WARNING, "Error : ", ex);
            }
        }
        return "";
    }
    
    public static Collection<String> getResourceFiles(Class pl, String folder) {
        CodeSource src = pl.getProtectionDomain().getCodeSource();
        Collection<String> list = new ArrayList<>();
        if(src != null) {
            try {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream( jar.openStream());
                ZipEntry ze;
                
                while( ( ze = zip.getNextEntry() ) != null ) {
                    String entryName = ze.getName();
                    if(entryName.toLowerCase().startsWith(folder.toLowerCase()) && entryName.toLowerCase().endsWith(".yml"))
                        list.add(entryName);
                }
            } catch (IOException ex) {
                BH.getPlugin().getLogger().log(Level.WARNING, "Error : ", ex);
            }
        }
        return list;
    }
}