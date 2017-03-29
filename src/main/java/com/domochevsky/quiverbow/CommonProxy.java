package com.domochevsky.quiverbow;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public class CommonProxy 
{
    public void registerProjectileRenderer(Class<? extends Entity> entityClass) { }
	public void registerWeaponRenderer(Item item, byte number) { }
	
	public void registerTurretRenderer() {  }
}
