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
public class _CMD_Base {

    private StaffList staffList;
    private GeneralMethods generalMethods;

    public _CMD_Base(StaffList staffList) {
        this.staffList = staffList;
        this.generalMethods = staffList.generalMethods;
    }

    void execute(CommandSender sender, String[] args) {
        /*
            /staff <command>
         */
        if (!staffList.generalMethods.hasPermission(sender, "command")) {
            generalMethods.message(sender, "noPerm");
            return;
        }
    }
}
