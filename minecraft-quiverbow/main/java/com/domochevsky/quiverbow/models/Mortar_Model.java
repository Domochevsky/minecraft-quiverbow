package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class Mortar_Model extends ModelBase
{
	//fields
	ModelRenderer Barrel;
	ModelRenderer Stock2;
	ModelRenderer Stock3;

	public Mortar_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		Barrel = new ModelRenderer(this, 0, 0);
		Barrel.addBox(0F, 0F, 0F, 18, 5, 5);
		Barrel.setRotationPoint(0F, 0F, -1.5F);
		Barrel.setTextureSize(64, 32);
		Barrel.mirror = true;
		setRotation(Barrel, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 0, 11);
		Stock2.addBox(0F, 0F, 0F, 5, 3, 2);
		Stock2.setRotationPoint(-4F, 1F, 0F);
		Stock2.setTextureSize(64, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Stock3 = new ModelRenderer(this, 15, 11);
		Stock3.addBox(0F, 0F, 0F, 6, 3, 2);
		Stock3.setRotationPoint(-8F, 2F, 0F);
		Stock3.setTextureSize(64, 32);
		Stock3.mirror = true;
		setRotation(Stock3, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
	    super.render(entity, x, y, z, yaw, pitch, f5);
	    setRotationAngles(x, y, z, yaw, pitch, f5, entity);
	    
	    Barrel.render(f5);
	    Stock2.render(f5);
	    Stock3.render(f5);
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
