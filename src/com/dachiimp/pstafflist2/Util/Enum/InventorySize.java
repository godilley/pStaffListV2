package com.dachiimp.pstafflist2.Util.Enum;

/**
 * Created by George on 16/01/2017.
 */
public enum InventorySize {
    SLOT54(54),
    SLOT45(45),
    SLOT36(36),
    SLOT27(27),
    SLOT18(18),
    SLOT9(9);

    private Integer slots;

    InventorySize(Integer slots) {
        this.slots = slots;
    }
}
