package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class OSR_Model extends ModelBase
{
	// Parts
	ModelRenderer Barrel;
	ModelRenderer Barrel2;
	ModelRenderer Stock1;
	ModelRenderer Trigger1;
	ModelRenderer Trigger2;
	ModelRenderer Ammo;

  
  public OSR_Model()
  {
    textureWidth = 64;
    textureHeight = 32;
    
    Barrel = new ModelRenderer(this, 0, 18);
    Barrel.addBox(0F, 0F, 0F, 18, 4, 3);
    Barrel.setRotationPoint(0F, -1F, -0.5F);
    Barrel.setTextureSize(64, 32);
    Barrel.mirror = true;
    setRotation(Barrel, 0F, 0F, 0F);
    Barrel2 = new ModelRenderer(this, 0, 0);
    Barrel2.addBox(0F, 0F, 0F, 23, 4, 2);
    Barrel2.setRotationPoint(-4F, 0F, 0.03333334F);
    Barrel2.setTextureSize(64, 32);
    Barrel2.mirror = true;
    setRotation(Barrel2, 0F, 0F, 0F);
    Stock1 = new ModelRenderer(this, 13, 8);
    Stock1.addBox(0F, 0F, 0F, 7, 4, 2);
    Stock1.setRotationPoint(-11F, 1F, 0F);
    Stock1.setTextureSize(64, 32);
    Stock1.mirror = true;
    setRotation(Stock1, 0F, 0F, 0F);
    Trigger1 = new ModelRenderer(this, 0, 15);
    Trigger1.addBox(0F, 0F, 0F, 5, 1, 1);
    Trigger1.setRotationPoint(-4F, 6F, 0.5F);
    Trigger1.setTextureSize(64, 32);
    Trigger1.mirror = true;
    setRotation(Trigger1, 0F, 0F, 0F);
    Trigger2 = new ModelRenderer(this, 0, 11);
    Trigger2.addBox(0F, 0F, 0F, 1, 2, 1);
    Trigger2.setRotationPoint(0F, 4F, 0.5F);
    Trigger2.setTextureSize(64, 32);
    Trigger2.mirror = true;
    setRotation(Trigger2, 0F, 0F, 0F);
    Ammo = new ModelRenderer(this, 32, 8);
    Ammo.addBox(0F, 0F, 0F, 4, 6, 2);
    Ammo.setRotationPoint(3F, 4F, 0F);
    Ammo.setTextureSize(64, 32);
    Ammo.mirror = true;
    setRotation(Ammo, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
  {
    super.render(entity, x, y, z, yaw, pitch, f5);
    setRotationAngles(x, y, z, yaw, pitch, f5, entity);
    
    Barrel.render(f5);
    Barrel2.render(f5);
    Stock1.render(f5);
    Trigger1.render(f5);
    Trigger2.render(f5);
    Ammo.render(f5);
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
