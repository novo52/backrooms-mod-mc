package net.fabricmc.example.world.gen;

import net.fabricmc.example.world.gen.chunk.BackroomsChunkGenerator;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BackroomsGeneratorType extends GeneratorType
{
	
	public BackroomsGeneratorType() {
		super("backrooms");
	}
	
	@Override
	protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed)
	{
		return new BackroomsChunkGenerator(registryManager.get(Registry.STRUCTURE_SET_KEY),
				registryManager.get(Registry.BIOME_KEY));
	}
}
