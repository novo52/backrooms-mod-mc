package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.block.ModBlocks;
import net.fabricmc.example.mixin.GeneratorTypeAccessor;
import net.fabricmc.example.world.dimension.ModDimensions;
import net.fabricmc.example.world.biome.ModBiomes;
import net.fabricmc.example.world.gen.BackroomsGeneratorType;
import net.minecraft.client.world.GeneratorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

public class BackroomsMod implements ModInitializer
{
	
	public static final String modID = "backrooms";
	public static final Logger LOGGER = LoggerFactory.getLogger(modID);
	
	public static final GeneratorType BackroomsGeneratorType = new BackroomsGeneratorType();
	
	@Override
	public void onInitialize()
	{
		LOGGER.debug("Initializing");
		
		ModBlocks.register();
		ModDimensions.register();
		ModBiomes.register();
		
		GeneratorTypeAccessor.getValues().add(BackroomsGeneratorType);
		
		
	}
}
