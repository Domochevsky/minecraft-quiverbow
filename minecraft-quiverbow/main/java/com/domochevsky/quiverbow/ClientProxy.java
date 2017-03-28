package com.domochevsky.quiverbow;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.FlyingAA.Entity_BB;
import com.domochevsky.quiverbow.Renderer.Render_FlyingAA;
import com.domochevsky.quiverbow.Renderer.Render_Projectile;
import com.domochevsky.quiverbow.Renderer.Render_AA;
import com.domochevsky.quiverbow.Renderer.Render_Weapon;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerProjectileRenderer(Class<? extends Entity> entityClass)
    {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, new Render_Projectile() );
    }
	
	
	@Override
	public void registerWeaponRenderer(Item item, byte number)
	{ 
		MinecraftForgeClient.registerItemRenderer(item, new Render_Weapon(number));
	}
	
	
	@Override
	public void registerTurretRenderer() 
	{  
		RenderingRegistry.registerEntityRenderingHandler(Entity_AA.class, new Render_AA() );
		RenderingRegistry.registerEntityRenderingHandler(Entity_BB.class, new Render_FlyingAA() );
	}
}
