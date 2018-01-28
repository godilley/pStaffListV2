package com.dachiimp.pstafflist2.Commands;

import com.dachiimp.pstafflist2.StaffList;
import com.dachiimp.pstafflist2.Util.GeneralMethods;
import org.bukkit.command.CommandSender;

/**
 * Created by George on 15/01/2017. For SL
 */
public class CMD_Remove {

    private StaffList staffList;
    private GeneralMethods generalMethods;

    public CMD_Remove(StaffList staffList) {
        this.staffList = staffList;
        this.generalMethods = staffList.generalMethods;
    }

    void execute(CommandSender sender, String[] args) {
        /*
            /staff remove <player> = remove a staff member
         */
        if (!staffList.generalMethods.hasPermission(sender, "remove")) {
            generalMethods.message(sender, "noPerm");
            return;
        }
        if(args.length == 2) {
            String name = args[1];
            int index = staffList.getStaffIndexFromName(name);
            if(index != -1) {
                // contains
                staffList.removeStaffMemberFromMap(index);
                staffList.removeFromDatabase.add(name);
                generalMethods.message(sender,"removedStaff","%name%",name);
            } else {
                generalMethods.message(sender,"staffNotAdded","%name%",name);
            }
        } else {
            generalMethods.message(sender, "notEnoughArgs");
        }
    }
}
