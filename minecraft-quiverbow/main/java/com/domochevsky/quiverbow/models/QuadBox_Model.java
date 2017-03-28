package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class QuadBox_Model extends ModelBase
{
	//fields
	ModelRenderer Barrel;
	ModelRenderer Barrel2;
	ModelRenderer Barrel3;
	ModelRenderer Barrel4;
	ModelRenderer Stock2;
	ModelRenderer Stock3;
	ModelRenderer Ammo;
	ModelRenderer Trigger;

	public QuadBox_Model()
	{
		textureWidth = 32;
		textureHeight = 32;
		
		Barrel = new ModelRenderer(this, 0, 0);
		Barrel.addBox(0F, 0F, 0F, 13, 3, 3);
		Barrel.setRotationPoint(0F, 0F, -0.5F);
		Barrel.setTextureSize(32, 32);
		Barrel.mirror = true;
		setRotation(Barrel, 0F, 0F, 0F);
		Barrel2 = new ModelRenderer(this, 0, 0);
		Barrel2.addBox(0F, 0F, 0F, 13, 3, 3);
		Barrel2.setRotationPoint(-1F, -1F, -3.5F);
		Barrel2.setTextureSize(32, 32);
		Barrel2.mirror = true;
		setRotation(Barrel2, 0F, 0F, 0F);
		Barrel3 = new ModelRenderer(this, 0, 0);
		Barrel3.addBox(0F, 0F, 0F, 13, 3, 3);
		Barrel3.setRotationPoint(-1F, -1F, 2.5F);
		Barrel3.setTextureSize(32, 32);
		Barrel3.mirror = true;
		setRotation(Barrel3, 0F, 0F, 0F);
		Barrel4 = new ModelRenderer(this, 0, 0);
		Barrel4.addBox(0F, 0F, 0F, 13, 3, 3);
		Barrel4.setRotationPoint(-0.5F, -3F, -0.5F);
		Barrel4.setTextureSize(32, 32);
		Barrel4.mirror = true;
		setRotation(Barrel4, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 0, 7);
		Stock2.addBox(0F, 0F, 0F, 5, 3, 2);
		Stock2.setRotationPoint(-4F, 1F, 0F);
		Stock2.setTextureSize(32, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Stock3 = new ModelRenderer(this, 15, 7);
		Stock3.addBox(0F, 0F, 0F, 6, 3, 2);
		Stock3.setRotationPoint(-8F, 2F, 0F);
		Stock3.setTextureSize(32, 32);
		Stock3.mirror = true;
		setRotation(Stock3, 0F, 0F, 0F);
		Ammo = new ModelRenderer(this, 0, 13);
		Ammo.addBox(0F, 0F, 0F, 11, 5, 4);
		Ammo.setRotationPoint(0F, 3F, -1F);
		Ammo.setTextureSize(32, 32);
		Ammo.mirror = true;
		setRotation(Ammo, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 0, 23);
		Trigger.addBox(0F, 0F, 0F, 3, 2, 1);
		Trigger.setRotationPoint(-3F, 4F, 0.5F);
		Trigger.setTextureSize(32, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);

	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
	    super.render(entity, x, y, z, yaw, pitch, f5);
	    setRotationAngles(x, y, z, yaw, pitch, f5, entity);
	    
	    Barrel.render(f5);
	    Barrel2.render(f5);
	    Barrel3.render(f5);
	    Barrel4.render(f5);
	    Stock2.render(f5);
	    Stock3.render(f5);
	    Ammo.render(f5);
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
