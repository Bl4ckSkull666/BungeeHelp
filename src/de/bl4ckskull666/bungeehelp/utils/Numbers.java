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
public final class Numbers {
    public static boolean isNumeric(String str) {
        try {
            int i = Integer.parseInt(str);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }
}
