package com.dachiimp.pstafflist2.Util.Saving;

import com.dachiimp.pstafflist2.StaffList;
import com.dachiimp.pstafflist2.Util.Enum.SavingMethod;
import com.dachiimp.pstafflist2.Util.Logger;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by George on 16/01/2017. For SL
 */
public class SavingScheduler extends BukkitRunnable {

    private StaffList staffList;

    public SavingScheduler(StaffList staffList) {
        this.staffList = staffList;
    }

    @Override
    public void run() {
        Logger.log("Saving data....");
        if (staffList.savingMethod == SavingMethod.FileTree) {
            staffList.saving_FileTree.executeSave();
        } else {
            staffList.saving_Database.executeSave();
        }
    }
}
