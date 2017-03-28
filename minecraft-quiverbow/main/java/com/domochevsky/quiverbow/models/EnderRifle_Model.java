package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class EnderRifle_Model extends ModelBase
{
	// Parts
	ModelRenderer Barrel;
	ModelRenderer Frame;
	ModelRenderer ScopeBase1;
	ModelRenderer ScopeBase2;
	ModelRenderer Scope;
	ModelRenderer Stock1;
	ModelRenderer Stock2;
	ModelRenderer Stock3;
	ModelRenderer Ammo;
  
	public EnderRifle_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		Barrel = new ModelRenderer(this, 0, 27);
		Barrel.addBox(0F, 0F, 0F, 22, 3, 2);
		Barrel.setRotationPoint(0F, -1F, 0F);
		Barrel.setTextureSize(64, 32);
		Barrel.mirror = true;
		setRotation(Barrel, 0F, 0F, 0F);
		Frame = new ModelRenderer(this, 0, 20);
		Frame.addBox(0F, 0F, 0F, 21, 2, 3);
		Frame.setRotationPoint(0F, 1F, -0.5333334F);
		Frame.setTextureSize(64, 32);
		Frame.mirror = true;
		setRotation(Frame, 0F, 0F, 0F);
		ScopeBase1 = new ModelRenderer(this, 0, 17);
		ScopeBase1.addBox(0F, 0F, 0F, 2, 1, 1);
		ScopeBase1.setRotationPoint(3F, -2F, 0.5F);
		ScopeBase1.setTextureSize(64, 32);
		ScopeBase1.mirror = true;
		setRotation(ScopeBase1, 0F, 0F, 0F);
		ScopeBase2 = new ModelRenderer(this, 0, 17);
		ScopeBase2.addBox(0F, 0F, 0F, 2, 1, 1);
		ScopeBase2.setRotationPoint(7F, -2F, 0.5F);
		ScopeBase2.setTextureSize(64, 32);
		ScopeBase2.mirror = true;
		setRotation(ScopeBase2, 0F, 0F, 0F);
		Scope = new ModelRenderer(this, 19, 17);
		Scope.addBox(0F, 0F, 0F, 8, 1, 1);
		Scope.setRotationPoint(2F, -3F, 0.5333334F);
		Scope.setTextureSize(64, 32);
		Scope.mirror = true;
		setRotation(Scope, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 0, 14);
		Stock1.addBox(0F, 0F, 0F, 7, 3, 2);
		Stock1.setRotationPoint(-6F, 1F, 0F);
		Stock1.setTextureSize(64, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 0, 14);
		Stock2.addBox(0F, 0F, 0F, 7, 3, 2);
		Stock2.setRotationPoint(-11F, 2F, -0.1F);
		Stock2.setTextureSize(64, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Stock3 = new ModelRenderer(this, 49, 25);
		Stock3.addBox(0F, 0F, 0F, 3, 5, 2);
		Stock3.setRotationPoint(-1F, 0F, -0.1F);
		Stock3.setTextureSize(64, 32);
		Stock3.mirror = true;
		setRotation(Stock3, 0F, 0F, 0F);
		Ammo = new ModelRenderer(this, 49, 17);
		Ammo.addBox(0F, 0F, 0F, 3, 5, 2);
		Ammo.setRotationPoint(4F, 3F, 0.03333334F);
		Ammo.setTextureSize(64, 32);
		Ammo.mirror = true;
		setRotation(Ammo, 0F, 0F, 0F);
	}
  
	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Barrel.render(f5);
		Frame.render(f5);
		ScopeBase1.render(f5);
		ScopeBase2.render(f5);
		Scope.render(f5);
		Stock1.render(f5);
		Stock2.render(f5);
		Stock3.render(f5);
		Ammo.render(f5);
	}
  
	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
  
	public void setRotationAngles(float x, float y, float z, float yaw, float pitch, float tick, Entity entity) { super.setRotationAngles(x, y, z, yaw, pitch, tick, entity); }
}
