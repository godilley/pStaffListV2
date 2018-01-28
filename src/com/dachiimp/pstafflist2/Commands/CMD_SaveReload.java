package com.dachiimp.pstafflist2.Commands;

import com.dachiimp.pstafflist2.StaffList;
import com.dachiimp.pstafflist2.Util.Enum.SavingMethod;
import com.dachiimp.pstafflist2.Util.GeneralMethods;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Created by George on 15/01/2017. For SL
 */
public class CMD_SaveReload {

    private StaffList staffList;
    private GeneralMethods generalMethods;

    public CMD_SaveReload(StaffList staffList) {
        this.staffList = staffList;
        this.generalMethods = staffList.generalMethods;
    }

    void execute(CommandSender sender, String[] args) {
        /*
            /staff <save/reload> [toWhere]
         */
        String subCmd = args[0];
        if(subCmd.equalsIgnoreCase("save")) {
            if (!staffList.generalMethods.hasPermission(sender, "save")) {
                generalMethods.message(sender, "noPerm");
                return;
            }
            if(args.length == 1) {
                if(staffList.savingMethod == SavingMethod.FileTree) {
                    staffList.saving_FileTree.executeSave();
                    sender.sendMessage(ChatColor.GREEN + "Sent save command for FileTree saving");
                } else {
                    staffList.saving_Database.executeSave();
                    sender.sendMessage(ChatColor.GREEN + "Sent save command for Database saving");
                }
            } else {
                // save specified
                String type = args[1].toLowerCase();
                if(Arrays.asList("database","mysql","sql").contains(type)) {
                    // Save database
                    staffList.saving_Database.executeSave();
                    sender.sendMessage(ChatColor.GREEN + "Sent save command for Database saving");
                } else if(Arrays.asList("file","filetree","files","yml").contains(type)) {
                    // Save filetree
                    staffList.saving_FileTree.executeSave();
                    sender.sendMessage(ChatColor.GREEN + "Sent save command for FileTree saving");
                } else {
                    sender.sendMessage(ChatColor.RED + type + " is not a valid save method (Database or FileTree");
                }
            }
        } else if(subCmd.equalsIgnoreCase("reload")) {
            if (!staffList.generalMethods.hasPermission(sender, "reload")) {
                generalMethods.message(sender, "noPerm");
                return;
            }
            staffList.setupConfig();
            staffList.setupGenSettings();
            staffList.setupMessages.setupStrings();
            sender.sendMessage(ChatColor.GREEN + "config.yml/messages.yml reloaded");
        } else {
            sender.sendMessage(ChatColor.RED + "CMD_SaveReload() I don't know how you got here, but you shouldn't be here...");
        }
    }
}
