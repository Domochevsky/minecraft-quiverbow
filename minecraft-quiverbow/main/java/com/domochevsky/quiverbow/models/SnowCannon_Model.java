package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SnowCannon_Model extends ModelBase
{
	//fields
	ModelRenderer Barrel1;
	ModelRenderer Stock1;
	ModelRenderer Trigger;
	ModelRenderer Shield;
	ModelRenderer Ice;

	public SnowCannon_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		Barrel1 = new ModelRenderer(this, 0, 0);
		Barrel1.addBox(0F, 0F, 0F, 16, 4, 3);
		Barrel1.setRotationPoint(0F, -1F, -1.5F);
		Barrel1.setTextureSize(64, 32);
		Barrel1.mirror = true;
		setRotation(Barrel1, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 0, 8);
		Stock1.addBox(0F, 0F, 0F, 8, 3, 2);
		Stock1.setRotationPoint(-8F, 0F, -1F);
		Stock1.setTextureSize(64, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 0, 14);
		Trigger.addBox(0F, 0F, 0F, 2, 5, 1);
		Trigger.setRotationPoint(1F, 3F, -0.5F);
		Trigger.setTextureSize(64, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		Shield = new ModelRenderer(this, 0, 21);
		Shield.addBox(0F, 0F, 0F, 1, 6, 2);
		Shield.setRotationPoint(7F, -7F, -1F);
		Shield.setTextureSize(64, 32);
		Shield.mirror = true;
		setRotation(Shield, 0F, 0F, 0F);
		Ice = new ModelRenderer(this, 7, 14);
		Ice.addBox(0F, 0F, 0F, 5, 7, 4);
		Ice.setRotationPoint(2F, -8F, -2F);
		Ice.setTextureSize(64, 32);
		Ice.mirror = true;
		setRotation(Ice, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
	    super.render(entity, x, y, z, yaw, pitch, f5);
	    setRotationAngles(x, y, z, yaw, pitch, f5, entity);

	    Barrel1.render(f5);
	    Stock1.render(f5);
	    Trigger.render(f5);
	    Shield.render(f5);
	    Ice.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
  
	public void setRotationAngles(float x, float y, float z, float yaw, float pitch, float tick, Entity entity)
	{
		super.setRotationAngles(x, y, z, yaw, pitch, tick, entity);
	}
}
