package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SugarEngine_Model extends ModelBase
{
	//fields
	ModelRenderer MainBody;
	ModelRenderer Stock;
	ModelRenderer Barrel2;
	ModelRenderer Barrel1;
	ModelRenderer Barrel3;
	ModelRenderer Barrel4;
	ModelRenderer Stabilizer;
	ModelRenderer Ammo;
	ModelRenderer Trigger;
	
	public SugarEngine_Model()
	{
		textureWidth = 32;
		textureHeight = 32;
		
		MainBody = new ModelRenderer(this, 0, 24);
		MainBody.addBox(0F, 0F, 0F, 4, 4, 4);
		MainBody.setRotationPoint(0F, 0F, -2F);
		MainBody.setTextureSize(32, 32);
		MainBody.mirror = true;
		setRotation(MainBody, 0F, 0F, 0F);
		Stock = new ModelRenderer(this, 0, 18);
		Stock.addBox(0F, 0F, 0F, 7, 3, 2);
		Stock.setRotationPoint(-7F, 2F, -1F);
		Stock.setTextureSize(32, 32);
		Stock.mirror = true;
		setRotation(Stock, 0F, 0F, 0F);
		Barrel2 = new ModelRenderer(this, 0, 0);
		Barrel2.addBox(0F, 0F, 0F, 14, 2, 2);
		Barrel2.setRotationPoint(4F, -0.5F, -2.533333F);
		Barrel2.setTextureSize(32, 32);
		Barrel2.mirror = true;
		setRotation(Barrel2, 0F, 0F, 0F);
		Barrel1 = new ModelRenderer(this, 0, 0);
		Barrel1.addBox(0F, 0F, 0F, 14, 2, 2);
		Barrel1.setRotationPoint(4F, -0.5F, 0.5333334F);
		Barrel1.setTextureSize(32, 32);
		Barrel1.mirror = true;
		setRotation(Barrel1, 0F, 0F, 0F);
		Barrel3 = new ModelRenderer(this, 0, 0);
		Barrel3.addBox(0F, 0F, 0.4666667F, 14, 2, 2);
		Barrel3.setRotationPoint(4F, 2.5F, -3F);
		Barrel3.setTextureSize(32, 32);
		Barrel3.mirror = true;
		setRotation(Barrel3, 0F, 0F, 0F);
		Barrel4 = new ModelRenderer(this, 0, 0);
		Barrel4.addBox(0F, 0F, 0F, 14, 2, 2);
		Barrel4.setRotationPoint(4F, 2.5F, 0.5333334F);
		Barrel4.setTextureSize(32, 32);
		Barrel4.mirror = true;
		setRotation(Barrel4, 0F, 0F, 0F);
		Stabilizer = new ModelRenderer(this, 18, 24);
		Stabilizer.addBox(0F, 0F, 0F, 5, 2, 2);
		Stabilizer.setRotationPoint(4F, 1F, -1F);
		Stabilizer.setTextureSize(32, 32);
		Stabilizer.mirror = true;
		setRotation(Stabilizer, 0F, 0F, 0F);
		Ammo = new ModelRenderer(this, 12, 5);
		Ammo.addBox(0F, 0F, 0F, 3, 2, 7);
		Ammo.setRotationPoint(0.5F, 1F, 2F);
		Ammo.setTextureSize(32, 32);
		Ammo.mirror = true;
		setRotation(Ammo, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 17, 29);
		Trigger.addBox(0F, 0F, 0F, 4, 2, 1);
		Trigger.setRotationPoint(-1F, 4F, -0.4666667F);
		Trigger.setTextureSize(32, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
	}
	
	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		MainBody.render(f5);
		Stock.render(f5);
		Barrel2.render(f5);
		Barrel1.render(f5);
		Barrel3.render(f5);
		Barrel4.render(f5);
		Stabilizer.render(f5);
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
