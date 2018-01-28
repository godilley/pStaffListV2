package com.dachiimp.pstafflist2.Util;

import com.dachiimp.pstafflist2.StaffList;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Created by DaChiimp on 6/19/2016. For StaffList
 */
public class Logger {

    private static StaffList sl;
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Minecraft");


    public Logger(StaffList sl) {
        this.sl = sl;
    }

    public static void print(String s, ChatColor color) {
        ConsoleCommandSender ccs = sl._plugin.getServer().getConsoleSender();
        ccs.sendMessage(color + "[pStaffList] " + s);
    }

    public static void print(String s) {
        ConsoleCommandSender ccs = sl._plugin.getServer().getConsoleSender();
        ccs.sendMessage("[pStaffList] " + s);
    }

    public static void log(String s) {
        if (s.length() == 0) return;

        s = ChatColor.stripColor(s);

        logger.info("[pStaffList] " + s);
    }

    public static void warn(String s) {
        if (s.length() == 0) return;

        s = ChatColor.stripColor(s);

        logger.warning("[pStaffList] " + s);
    }

    public static void severe(String s) {
        if (s.length() == 0) return;

        s = ChatColor.stripColor("[pStaffList] " + s);

        logger.severe(s);
    }

}
