package net.fabricmc.example.world.dimension;

import net.fabricmc.example.BackroomsMod;
import net.fabricmc.example.world.gen.chunk.BackroomsChunkGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

import static net.fabricmc.example.BackroomsMod.modID;

public class ModDimensions
{
	private static final RegistryKey<DimensionOptions> DIMENSION_KEY = RegistryKey.of(
			Registry.DIMENSION_KEY,
			new Identifier(modID, "rooms")
	);
	
	private static RegistryKey<World> ROOMS_KEY = RegistryKey.of(
			Registry.WORLD_KEY,
			DIMENSION_KEY.getValue()
	);
	
	public static void register() {
		BackroomsMod.LOGGER.debug("Registering Dimensions");
		
		Registry.register(Registry.CHUNK_GENERATOR, new Identifier(modID, "rooms"), BackroomsChunkGenerator.CODEC);
	}
}
