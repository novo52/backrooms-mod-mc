package net.fabricmc.example.world.dimension;

import net.fabricmc.example.BackroomsMod;
import net.fabricmc.example.world.gen.chunk.BackroomsChunkGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

import static net.fabricmc.example.BackroomsMod.modID;

public class ModDimensions
{
	
	public static final String roomsID = "rooms";
	
	public static final RegistryKey<DimensionOptions> ROOMS_DIMENSION_KEY = RegistryKey.of(
			Registry.DIMENSION_KEY,
			new Identifier(modID, roomsID)
	);
	
	public static RegistryKey<World> ROOMS = RegistryKey.of(
			Registry.WORLD_KEY,
			ROOMS_DIMENSION_KEY.getValue()
	);
	
	public static final RegistryKey<DimensionType> ROOMS_DIMENSION_TYPE_KEY = RegistryKey.of(
			Registry.DIMENSION_TYPE_KEY,
			new Identifier(modID, "rooms_type")
	);
	
	public static void register() {
		BackroomsMod.LOGGER.debug("Registering Dimensions");
		
		Registry.register(Registry.CHUNK_GENERATOR, new Identifier(modID, roomsID), BackroomsChunkGenerator.CODEC);
		ROOMS = RegistryKey.of(Registry.WORLD_KEY, new Identifier(modID, roomsID));
	}
}
