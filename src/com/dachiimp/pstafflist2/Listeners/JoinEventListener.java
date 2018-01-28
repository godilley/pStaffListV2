package com.dachiimp.pstafflist2.Listeners;

import com.dachiimp.pstafflist2.StaffList;
import com.dachiimp.pstafflist2.Util.ClassSaving.StaffMember;
import com.dachiimp.pstafflist2.Util.GeneralMethods;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Date;

/**
 * Created by George on 16/01/2017. For SL
 */
public class JoinEventListener implements Listener {

    private StaffList staffList;
    private GeneralMethods generalMethods;

    public JoinEventListener(StaffList staffList) {
        this.staffList = staffList;
        this.generalMethods = staffList.generalMethods;
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPreJoin(PlayerLoginEvent e) {
        StaffMember staffMember = generalMethods.getStaffFromName(e.getPlayer().getName());
        if (staffMember == null)
            return;

        Player player = e.getPlayer();

        Date date = new Date();

        staffMember.setLastlogin(date);

        if (!staffMember.getName().equals(player.getName())) {
            staffMember.setName(player.getName());
        }

        if (staffMember.getUUID() == null || staffMember.getUUID() != player.getUniqueId()) {
            staffMember.setUUID(player.getUniqueId());
        }

        int index = generalMethods.getIndexFromName(player.getName());
        if (index == -1) {
            generalMethods.message(player, "error", "%error%", "updating staff file");
            return;
        }
        staffList.removeStaffMemberFromMap(index);
        staffList.addStaffMemberToMap(index, staffMember);
    }

}
