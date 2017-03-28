package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class RPG_Model extends ModelBase
{
	//fields
	ModelRenderer Rocket;
    ModelRenderer BarrelR;
    ModelRenderer Stock1;
    ModelRenderer Stock2;
    ModelRenderer BarrelL;
    ModelRenderer BarrelB;
    ModelRenderer Metal1;
    ModelRenderer Metal2;
    ModelRenderer Metal3;
    ModelRenderer RocketHead1;
    ModelRenderer RocketHead2;

	public RPG_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
	
		Rocket = new ModelRenderer(this, 0, 12);
		Rocket.addBox(0F, 0F, 0F, 20, 4, 3);
		Rocket.setRotationPoint(-2F, -1F, -0.5F);
		Rocket.setTextureSize(64, 32);
		Rocket.mirror = true;
		setRotation(Rocket, 0F, 0F, 0F);
		BarrelR = new ModelRenderer(this, 0, 0);
		BarrelR.addBox(0F, 0F, 0F, 21, 4, 1);
		BarrelR.setRotationPoint(-4F, 0F, -0.9666666F);
		BarrelR.setTextureSize(64, 32);
		BarrelR.mirror = true;
		setRotation(BarrelR, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 0, 20);
		Stock1.addBox(0F, 0F, 0F, 8, 4, 2);
		Stock1.setRotationPoint(-11F, 1F, -0.1F);
		Stock1.setTextureSize(64, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 51, 9);
		Stock2.addBox(0F, 0F, 0F, 4, 1, 2);
		Stock2.setRotationPoint(-6F, 0F, 0F);
		Stock2.setTextureSize(64, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		BarrelL = new ModelRenderer(this, 0, 0);
		BarrelL.addBox(0F, 0F, 0F, 21, 4, 1);
		BarrelL.setRotationPoint(-4F, 0F, 2F);
		BarrelL.setTextureSize(64, 32);
		BarrelL.mirror = true;
		setRotation(BarrelL, 0F, 0F, 0F);
		BarrelB = new ModelRenderer(this, 0, 6);
		BarrelB.addBox(0F, 0F, 0F, 21, 1, 4);
		BarrelB.setRotationPoint(-4F, 3F, -1F);
		BarrelB.setTextureSize(64, 32);
		BarrelB.mirror = true;
		setRotation(BarrelB, 0F, 0F, 0F);
		Metal1 = new ModelRenderer(this, 51, 0);
		Metal1.addBox(0F, 0F, 0F, 1, 4, 4);
		Metal1.setRotationPoint(-1F, -1.533333F, -1F);
		Metal1.setTextureSize(64, 32);
		Metal1.mirror = true;
		setRotation(Metal1, 0F, 0F, 0F);
		Metal2 = new ModelRenderer(this, 51, 0);
		Metal2.addBox(0F, 0F, 0F, 1, 4, 4);
		Metal2.setRotationPoint(7F, -1.533333F, -1F);
		Metal2.setTextureSize(64, 32);
		Metal2.mirror = true;
		setRotation(Metal2, 0F, 0F, 0F);
		Metal3 = new ModelRenderer(this, 51, 0);
		Metal3.addBox(0F, 0F, 0F, 1, 4, 4);
		Metal3.setRotationPoint(15F, -1.5F, -1F);
		Metal3.setTextureSize(64, 32);
		Metal3.mirror = true;
		setRotation(Metal3, 0F, 0F, 0F);
		RocketHead1 = new ModelRenderer(this, 47, 13);
		RocketHead1.addBox(0F, 0F, 0F, 1, 3, 2);
		RocketHead1.setRotationPoint(18F, -0.4666667F, 0F);
		RocketHead1.setTextureSize(64, 32);
		RocketHead1.mirror = true;
		setRotation(RocketHead1, 0F, 0F, 0F);
		RocketHead2 = new ModelRenderer(this, 48, 14);
		RocketHead2.addBox(0F, 0F, 0F, 1, 2, 1);
		RocketHead2.setRotationPoint(19F, 0F, 0.4666667F);
		RocketHead2.setTextureSize(64, 32);
		RocketHead2.mirror = true;
		setRotation(RocketHead2, 0F, 0F, 0F);

	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
	    super.render(entity, x, y, z, yaw, pitch, f5);
	    setRotationAngles(x, y, z, yaw, pitch, f5, entity);
	    
	    Rocket.render(f5);
	    BarrelR.render(f5);
	    Stock1.render(f5);
	    Stock2.render(f5);
	    BarrelL.render(f5);
	    BarrelB.render(f5);
	    Metal1.render(f5);
	    Metal2.render(f5);
	    Metal3.render(f5);
	    RocketHead1.render(f5);
	    RocketHead2.render(f5);
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
