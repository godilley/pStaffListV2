package com.dachiimp.pstafflist2.Commands;

import com.dachiimp.pstafflist2.StaffList;
import com.dachiimp.pstafflist2.Util.ClassSaving.StaffMember;
import com.dachiimp.pstafflist2.Util.GeneralMethods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Created by George on 15/01/2017. For SL
 */
public class CMD_Add {

    private StaffList staffList;
    private GeneralMethods generalMethods;

    public CMD_Add(StaffList staffList) {
        this.staffList = staffList;
        this.generalMethods = staffList.generalMethods;
    }

    void execute(CommandSender sender, String[] args) {
        /*
            /staff add <player> <rank> = adds to first position free
         */
        if (!staffList.generalMethods.hasPermission(sender, "add")) {
            generalMethods.message(sender, "noPerm");
            return;
        }
        if (args.length >= 3) {
            if (generalMethods.getStaffFromName(args[1]) == null) {
                // not added so add
                Integer pos = generalMethods.getFirstFreeSlot();
                if (pos == -1) {
                    generalMethods.message(sender, "error", "%error%", "getting first free slot");
                    return;
                }
                String rank = "";
                String name = args[1];
                // TODO: Optimize
                int i = -1;
                for (String s : args) {
                    i++;
                    if (i > 1) {
                        rank = rank + s + " ";
                    }
                }
                rank = rank.substring(0, rank.length() - 1);
                UUID uuid = null;
                if(Bukkit.getServer().getPlayer(name) != null) // If already online get their uuid to add (might as well)
                    uuid = Bukkit.getServer().getPlayer(name).getUniqueId();

                StaffMember member = new StaffMember(uuid, name, rank, pos, null);
                boolean done = staffList.addStaffMemberToMap(pos, member);
                if (done) {
                    generalMethods.message(sender, "addedStaff", "%staff%", name, "%rank%", rank);
                    String toRemove = null;
                    for(String s : staffList.removeFromDatabase) {
                        if(s.equalsIgnoreCase(name)) {
                            toRemove = s;
                        }
                    }
                    if(toRemove != null)
                        staffList.removeFromDatabase.remove(toRemove);

                } else {
                    generalMethods.message(sender, "error", "%error%", "Adding " + args[1] + " with rank " + rank);
                }
            } else {
                generalMethods.message(sender, "staffAlreadyAdded", "%staff%", args[1]);
                sender.sendMessage(generalMethods.getStaffFromName(args[1]).toString()); // TODO: Remove
            }
        } else {
            generalMethods.message(sender, "notEnoughArgs");
        }
    }
}
