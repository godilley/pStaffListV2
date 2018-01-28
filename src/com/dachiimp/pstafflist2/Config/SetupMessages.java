package com.dachiimp.pstafflist2.Config;

import com.dachiimp.pstafflist2.StaffList;
import com.dachiimp.pstafflist2.Util.Logger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by DaChiimp on 6/19/2016. For stafflist
 */
public class SetupMessages {

    private StaffList stafflist;


    public SetupMessages(StaffList stafflist) {
        this.stafflist = stafflist;
    }

    public void setupStrings() {

        File file = new File(stafflist._plugin.getDataFolder(), File.separator + "messages.yml");
        YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);

        ArrayList<String> messages = new ArrayList<>();

        ArrayList<String> messagesToAdd = new ArrayList<>();

        messages.add("noPerm");
        messages.add("unknownCommand");
        messages.add("notEnoughArgs");
        messages.add("unknownPlayer");
        messages.add("notAnInt");
        messages.add("staffAlreadyAdded");
        messages.add("error");
        messages.add("addedStaff");
        messages.add("staffNotAdded");
        messages.add("removedStaff");

        // stuff

        if (yc.contains("prefix") && yc.getString("prefix") != null)
            stafflist.prefix = yc.getString("prefix").replaceAll("(&([a-f0-9k-or]))", "\u00A7$2");

        for (String s : messages) {
            if (yc.contains(s) && yc.getString(s) != null) {
                stafflist.messages.put(s.toLowerCase(), yc.getString(s).replaceAll("(&([a-f0-9k-or]))", "\u00A7$2"));
            } else {
                messagesToAdd.add(s);
            }
        }

        if (messagesToAdd.size() > 0) {
            Logger.warn("messages.yml doesn't contain " + messagesToAdd.size() + " value(s). Trying to manually update them to default, Otherwise they will be temporarily set to default values");
            ArrayList<String> added = new ArrayList<>();
            InputStream input = stafflist.getResource("messages.yml");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                String str;
                while ((str = br.readLine()) != null) {
                    for (String s : messagesToAdd) {
                        if (str.startsWith(s + ": ")) {
                            String value = str.replaceAll(s + ": ", "").replaceAll("\"", "");
                            yc.set(s, value);
                            added.add(s);
                            stafflist.messages.put(s, value);
                        }
                    }
                }
                br.close();
            } catch (IOException e) {
                Logger.log(ChatColor.RED + "Error trying to update messages.yml");
            } finally {
                if (added.size() > 0) {
                    Logger.log("Added " + added.size() + "/" + messagesToAdd.size() + " values to messages.yml. Attempting to save");
                    try {
                        yc.save(file);
                        Logger.log("Saved updated messages.yml");
                    } catch (IOException e) {
                        Logger.warn("Error saving messages.yml, retrying");
                        try {
                            yc.save(file);
                            Logger.log("Saved updated messages.yml");
                        } catch (IOException e2) {
                            Logger.warn("Error saving messages.yml for a second time");
                        }
                    }
                } else {
                    Logger.warn("Error adding any values to messages.yml");
                }
            }
        }

    }

}
