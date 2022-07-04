package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.block.ModBlocks;
import net.fabricmc.example.world.dimension.ModDimensions;
import net.fabricmc.example.world.biome.ModBiomes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackroomsMod implements ModInitializer
{
	
	public static final String modID = "backrooms";
	public static final Logger LOGGER = LoggerFactory.getLogger(modID);
	
	@Override
	public void onInitialize()
	{
		LOGGER.debug("Initializing");
		
		ModBlocks.register();
		ModDimensions.register();
		ModBiomes.register();
	}
}
