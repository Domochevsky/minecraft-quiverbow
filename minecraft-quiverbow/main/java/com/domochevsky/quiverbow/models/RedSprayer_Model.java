package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class RedSprayer_Model extends ModelBase
{
	//fields
	ModelRenderer Barrel1;
	ModelRenderer Stock1;
	ModelRenderer Stock2;
	ModelRenderer Ammo;
	ModelRenderer Ammo2;
	ModelRenderer Ammo3;
	ModelRenderer Wick;
	ModelRenderer Flame;
	ModelRenderer Trigger;
	
	public RedSprayer_Model()
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
		Stock2 = new ModelRenderer(this, 0, 8);
		Stock2.addBox(0F, 0F, 0F, 8, 3, 2);
		Stock2.setRotationPoint(-10F, 1F, -1F);
		Stock2.setTextureSize(64, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Ammo = new ModelRenderer(this, 0, 14);
		Ammo.addBox(0F, 0F, 0F, 10, 4, 4);
		Ammo.setRotationPoint(2F, 3F, -2F);
		Ammo.setTextureSize(64, 32);
		Ammo.mirror = true;
		setRotation(Ammo, 0F, 0F, 0F);
		Ammo2 = new ModelRenderer(this, 39, 0);
		Ammo2.addBox(0F, 0F, 0F, 1, 4, 5);
		Ammo2.setRotationPoint(1F, 2.5F, -2.5F);
		Ammo2.setTextureSize(64, 32);
		Ammo2.mirror = true;
		setRotation(Ammo2, 0F, 0F, 0F);
		Ammo3 = new ModelRenderer(this, 39, 0);
		Ammo3.addBox(0F, 0F, 0F, 1, 4, 5);
		Ammo3.setRotationPoint(12F, 2.5F, -2.5F);
		Ammo3.setTextureSize(64, 32);
		Ammo3.mirror = true;
		setRotation(Ammo3, 0F, 0F, 0F);
		Wick = new ModelRenderer(this, 21, 11);
		Wick.addBox(0F, 0F, 0F, 3, 1, 1);
		Wick.setRotationPoint(16F, 1.466667F, -0.5F);
		Wick.setTextureSize(64, 32);
		Wick.mirror = true;
		setRotation(Wick, 0F, 0F, 0F);
		Flame = new ModelRenderer(this, 21, 8);
		Flame.addBox(0F, 0F, 0F, 1, 1, 1);
		Flame.setRotationPoint(18F, 0.4666667F, -0.5F);
		Flame.setTextureSize(64, 32);
		Flame.mirror = true;
		setRotation(Flame, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 0, 23);
		Trigger.addBox(0F, 0F, 0F, 5, 3, 1);
		Trigger.setRotationPoint(-4F, 3F, -0.5F);
		Trigger.setTextureSize(64, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
	    super.render(entity, x, y, z, yaw, pitch, f5);
	    setRotationAngles(x, y, z, yaw, pitch, f5, entity);

	    Barrel1.render(f5);
	    Stock1.render(f5);
	    Stock2.render(f5);
	    Ammo.render(f5);
	    Ammo2.render(f5);
	    Ammo3.render(f5);
	    Wick.render(f5);
	    Flame.render(f5);
	    Trigger.render(f5);
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
