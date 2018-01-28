package com.dachiimp.pstafflist2.Util;

import com.dachiimp.pstafflist2.StaffList;
import com.dachiimp.pstafflist2.Util.ClassSaving.StaffMember;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

/**
 * Created by George on 16/01/2017. For SL
 */
public class GeneralMethods {

    private StaffList staffList;

    public GeneralMethods(StaffList staffList) {
        this.staffList = staffList;
    }

    public String getMessage(String message) {
        if (staffList.messages.containsKey(message.toLowerCase())) {
            return ChatColor.translateAlternateColorCodes('&', staffList.messages.get(message.toLowerCase()).replaceAll("%prefix%", staffList.prefix));
        }

        return ChatColor.RED + "Error getting message '" + message + "'";
    }

    public void message(CommandSender player, String message) {
        String msg = getMessage(message);
        if (msg.length() <= 0)
            return;
        player.sendMessage(msg);
    }

    public void message(CommandSender player, String message, String replaceFrom, String replaceTo) {
        String msg = getMessage(message);
        if (msg.length() <= 0)
            return;
        msg = msg.replaceAll(replaceFrom, replaceTo);
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        player.sendMessage(msg);
    }

    public void message(CommandSender player, String message, String replaceFrom, String replaceTo, String replaceFrom2, String replaceTo2) {
        String msg = getMessage(message);
        if (msg.length() <= 0)
            return;
        msg = msg.replaceAll(replaceFrom, replaceTo);
        msg = msg.replaceAll(replaceFrom2, replaceTo2);
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        player.sendMessage(msg);
    }

    public boolean hasPermission(CommandSender player, String permission) {
        if (player.isOp()) return true;

        String base = "stafflist.";
        String member = base + "member.";
        switch (permission.toLowerCase()) {
            case "add": {
                if (player.hasPermission(member + "add") || player.hasPermission(member + "*"))
                    return true;
                break;
            }
            case "remove": {
                if (player.hasPermission(member + "remove") || player.hasPermission(member + "*"))
                    return true;
                break;
            }
            case "save": {
                if (player.hasPermission(base + "save"))
                    return true;
                break;
            }
            case "reload": {
                if (player.hasPermission(base + "reload"))
                    return true;
                break;
            }
        }

        return false;
    }

    public StaffMember getStaffFromName(String name) {
        for (Integer position : staffList.getStaffMembersMap().keySet()) {
            StaffMember member = staffList.getStaffMembersMap().get(position);
            if (member.getName().equalsIgnoreCase(name))
                return member;
        }

        return null;
    }

    public int getIndexFromName(String name) {
        for (Integer position : staffList.getStaffMembersMap().keySet()) {
            StaffMember member = staffList.getStaffMembersMap().get(position);
            if (member.getName().equalsIgnoreCase(name))
                return position;
        }
        return -1;
    }

    public Integer getFirstFreeSlot() {
        HashMap<Integer, StaffMember> map = staffList.getStaffMembersMap();
        int size = map.size() + 1;
        for (int i = 0; i < size; i++) {
            if (!map.containsKey(i))
                return i;
        }

        return -1;
    }
}
