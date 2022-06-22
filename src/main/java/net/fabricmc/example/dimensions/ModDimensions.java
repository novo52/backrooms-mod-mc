package net.fabricmc.example.dimensions;

import net.fabricmc.example.BackroomsMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class ModDimensions
{
	private static final RegistryKey<DimensionOptions> DIMENSION_KEY = RegistryKey.of(
			Registry.DIMENSION_KEY,
			new Identifier(BackroomsMod.modID, "rooms")
	);
	
	private static RegistryKey<World> ROOMS_KEY = RegistryKey.of(
			Registry.WORLD_KEY,
			DIMENSION_KEY.getValue()
	);
	
	public static void register() {
		BackroomsMod.LOGGER.info("Registering ModDimensions");
	}
}
