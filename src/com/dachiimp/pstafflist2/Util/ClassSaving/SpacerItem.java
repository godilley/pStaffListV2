package com.dachiimp.pstafflist2.Util.ClassSaving;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Created by DIL15151969 on 17/01/2017. For SL
 */
public class SpacerItem {

    private Material material;
    private String name;
    private List<String> lores;

    SpacerItem(Material material, String name, List<String> lores) {
        this.material = material;
        this.name = name;
        this.lores = lores;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public List<String> getLores() {
        return lores;
    }

    public void setLores(List<String> lores) {
        this.lores = lores;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getItemStack(int amount) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lores);
        item.setItemMeta(meta);
        item.setAmount(amount);

        return item;
    }
}
