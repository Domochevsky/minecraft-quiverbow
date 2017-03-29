package com.domochevsky.quiverbow.FlyingAA;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import com.domochevsky.quiverbow.Frames;

public class Model_BB extends ModelBase
{
	ModelRenderer Ring1;
	ModelRenderer Ring2;
	ModelRenderer Ring3;
	ModelRenderer Ring4;
	ModelRenderer Rail;
	ModelRenderer Rod1;
	ModelRenderer Rod2;
	ModelRenderer Rod3;
	ModelRenderer Rod4;
	ModelRenderer RodC1;
	ModelRenderer RodC2;
	ModelRenderer RodC3;
	ModelRenderer RodC4;
	ModelRenderer Bit1;
	ModelRenderer Bit1b;
	ModelRenderer Bit2;
	ModelRenderer Bit2b;
	
	public Model_BB()
	{
		textureWidth = 64;
		textureHeight = 64;

		Ring1 = new ModelRenderer(this, 0, 0);
		Ring1.addBox(-12F, -1F, 12F, 24, 2, 1);
		Ring1.setRotationPoint(0F, 20F, 0F);
		Ring1.setTextureSize(64, 64);
		Ring1.mirror = true;
		setRotation(Ring1, 0F, -1.570796F, 0F);
		
		Ring2 = new ModelRenderer(this, 0, 0);
		Ring2.addBox(-12F, -1F, 12F, 24, 2, 1);
		Ring2.setRotationPoint(0F, 20F, 0F);
		Ring2.setTextureSize(64, 64);
		Ring2.mirror = true;
		setRotation(Ring2, 0F, -3.141593F, 0F);
		
		Ring3 = new ModelRenderer(this, 0, 0);
		Ring3.addBox(-12F, -1F, 12F, 24, 2, 1);
		Ring3.setRotationPoint(0F, 20F, 0F);
		Ring3.setTextureSize(64, 64);
		Ring3.mirror = true;
		setRotation(Ring3, 0F, 0F, 0F);
		
		Ring4 = new ModelRenderer(this, 0, 0);
		Ring4.addBox(-12F, -1F, -13F, 24, 2, 1);
		Ring4.setRotationPoint(0F, 20F, 0F);
		Ring4.setTextureSize(64, 64);
		Ring4.mirror = true;
		setRotation(Ring4, 0F, -1.570796F, 0F);
		
		// ---
		
		Rail = new ModelRenderer(this, 0, 4);
		Rail.addBox(-2F, -2F, -5F, 4, 4, 10);
		Rail.setRotationPoint(0F, 20F, 0F);
		Rail.setTextureSize(64, 64);
		Rail.mirror = true;
		setRotation(Rail, 0F, 0F, 0F);
		
		Rod1 = new ModelRenderer(this, 0, 4);
		Rod1.addBox(13F, -3F, -0.5F, 1, 6, 1);
		Rod1.setRotationPoint(0F, 20F, 0F);
		Rod1.setTextureSize(64, 64);
		Rod1.mirror = true;
		setRotation(Rod1, 0F, -1.570796F, 0F);
		
		Rod2 = new ModelRenderer(this, 0, 4);
		Rod2.addBox(-14F, -3F, -0.5F, 1, 6, 1);
		Rod2.setRotationPoint(0F, 20F, 0F);
		Rod2.setTextureSize(64, 64);
		Rod2.mirror = true;
		setRotation(Rod2, 0F, -1.570796F, 0F);
		
		Rod3 = new ModelRenderer(this, 0, 4);
		Rod3.addBox(-0.5F, -3F, 13F, 1, 6, 1);
		Rod3.setRotationPoint(0F, 20F, 0F);
		Rod3.setTextureSize(64, 64);
		Rod3.mirror = true;
		setRotation(Rod3, 0F, -1.570796F, 0F);
		
		Rod4 = new ModelRenderer(this, 0, 4);
		Rod4.addBox(-0.5F, -3F, -14F, 1, 6, 1);
		Rod4.setRotationPoint(0F, 20F, 0F);
		Rod4.setTextureSize(64, 64);
		Rod4.mirror = true;
		setRotation(Rod4, 0F, -1.570796F, 0F);
		
		RodC1 = new ModelRenderer(this, 0, 4);
		RodC1.addBox(12F, -3F, 12F, 1, 6, 1);
		RodC1.setRotationPoint(0F, 20F, 0F);
		RodC1.setTextureSize(64, 64);
		RodC1.mirror = true;
		setRotation(RodC1, 0F, 0F, 0F);
		
		RodC2 = new ModelRenderer(this, 0, 4);
		RodC2.addBox(12F, -3F, -13F, 1, 6, 1);
		RodC2.setRotationPoint(0F, 20F, 0F);
		RodC2.setTextureSize(64, 64);
		RodC2.mirror = true;
		setRotation(RodC2, 0F, 0F, 0F);
		
		RodC3 = new ModelRenderer(this, 0, 4);
		RodC3.addBox(-13F, -3F, 12F, 1, 6, 1);
		RodC3.setRotationPoint(0F, 20F, 0F);
		RodC3.setTextureSize(64, 64);
		RodC3.mirror = true;
		setRotation(RodC3, 0F, 0F, 0F);
		
		RodC4 = new ModelRenderer(this, 0, 4);
		RodC4.addBox(-13F, -3F, -13F, 1, 6, 1);
		RodC4.setRotationPoint(0F, 20F, 0F);
		RodC4.setTextureSize(64, 64);
		RodC4.mirror = true;
		setRotation(RodC4, 0F, 0F, 0F);
		
		Bit1 = new ModelRenderer(this, 0, 19);
		Bit1.addBox(3F, -4F, -4F, 4, 8, 8);
		Bit1.setRotationPoint(0F, 20F, 0F);
		Bit1.setTextureSize(64, 64);
		Bit1.mirror = true;
		setRotation(Bit1, 0F, 0F, 0F);
		
		Bit1b = new ModelRenderer(this, 0, 36);
		Bit1b.addBox(7F, -3F, -3F, 2, 6, 6);
		Bit1b.setRotationPoint(0F, 20F, 0F);
		Bit1b.setTextureSize(64, 64);
		Bit1b.mirror = true;
		setRotation(Bit1b, 0F, 0F, 0F);
		
		Bit2 = new ModelRenderer(this, 0, 19);
		Bit2.addBox(-7F, -4F, -4F, 4, 8, 8);
		Bit2.setRotationPoint(0F, 20F, 0F);
		Bit2.setTextureSize(64, 64);
		Bit2.mirror = true;
		setRotation(Bit2, 0F, 0F, 0F);
		
		Bit2b = new ModelRenderer(this, 0, 36);
		Bit2b.addBox(-9F, -3F, -3F, 2, 6, 6);
		Bit2b.setRotationPoint(0F, 20F, 0F);
		Bit2b.setTextureSize(64, 64);
		Bit2b.mirror = true;
		setRotation(Bit2b, 0F, 0F, 0F);
	}

	
	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		
		this.setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		this.Rail.render(f5);
		this.Bit1.render(f5);
		this.Bit1b.render(f5);
		this.Bit2.render(f5);
		this.Bit2b.render(f5);
		
		this.Ring1.render(f5);
		this.Ring2.render(f5);
		this.Ring3.render(f5);
		this.Ring4.render(f5);
		
		this.Rod1.render(f5);
		this.Rod2.render(f5);
		this.Rod3.render(f5);
		this.Rod4.render(f5);
		this.RodC1.render(f5);
		this.RodC2.render(f5);
		this.RodC3.render(f5);
		this.RodC4.render(f5);
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
		
		// Looking at things
		/*
		this.Rail.rotateAngleY = yaw / (180F / (float) Math.PI);
		this.Rail.rotateAngleX = pitch / (180F / (float) Math.PI);
		
		this.Bit1.rotateAngleY = yaw / (180F / (float) Math.PI);
		this.Bit1.rotateAngleX = pitch / (180F / (float) Math.PI);
		this.Bit1b.rotateAngleY = yaw / (180F / (float) Math.PI);
		this.Bit1b.rotateAngleX = pitch / (180F / (float) Math.PI);
		
		this.Bit2.rotateAngleY = yaw / (180F / (float) Math.PI);
		this.Bit2.rotateAngleX = pitch / (180F / (float) Math.PI);
		this.Bit2b.rotateAngleY = yaw / (180F / (float) Math.PI);
		this.Bit2b.rotateAngleX = pitch / (180F / (float) Math.PI);
		*/
		
		if (!Frames.isTickReady()) { return; }	// Ideally this will keep the ring rotating at a steady pace (30 times per second)
		if (Minecraft.getMinecraft().isGamePaused()) { return; }
		
		Entity_BB bb = (Entity_BB) entity;
		
		//bb.ringRotationAngle += 0.005f;
		//if (bb.ringRotationAngle >= 180.0f) { bb.ringRotationAngle -= 360.0f; }	// Wrapping around
		
		// Slowly rotating the ring
		this.rotateRingY(this.Ring1, bb);
		this.rotateRingY(this.Ring2, bb);
		this.rotateRingY(this.Ring3, bb);
		this.rotateRingY(this.Ring4, bb);
		
		this.rotateRingY(this.Rod1, bb);
		this.rotateRingY(this.Rod2, bb);
		this.rotateRingY(this.Rod3, bb);
		this.rotateRingY(this.Rod4, bb);
		
		this.rotateRingY(this.RodC1, bb);
		this.rotateRingY(this.RodC2, bb);
		this.rotateRingY(this.RodC3, bb);
		this.rotateRingY(this.RodC4, bb);
	}
	
	private void rotateRingY(ModelRenderer model, Entity_BB entity)
	{
		//model.rotateAngleY += entity.ringRotationAngle;
		model.rotateAngleY += 0.005f;
		if (model.rotateAngleY >= 180.0f) { model.rotateAngleY -= 360.0f; }	// Wrapping around
	}
}
