package net.fabricmc.example.world.biome;

import net.fabricmc.example.BackroomsMod;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.*;

import static net.fabricmc.example.BackroomsMod.modID;

public class ModBiomes
{
	
	private static final RegistryKey<Biome> LEVEL_1 = RegistryKey.of(Registry.BIOME_KEY, new Identifier(modID, "level_1"));
	
	public static void register() {
		BackroomsMod.LOGGER.info("Registering Biomes");
		
		Registry.register(BuiltinRegistries.BIOME, LEVEL_1.getValue(), createLevel1());
	}
	
	private static Biome createLevel1() {
		
		Biome.Builder level_1_builder = new Biome.Builder()
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NONE)
				.temperature(1.0f)
				.downfall(0.0f)
				.effects(new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(0x330808)
						.skyColor(4159204)
		.build());
		
		SpawnSettings spawnSettings = new SpawnSettings.Builder().build(); // Nothing spawns here
		GenerationSettings generationSettings = new GenerationSettings.Builder().build();
		
		return level_1_builder.spawnSettings(spawnSettings).generationSettings(generationSettings).build();
		
	}
	
	public static RegistryKey<Biome> getLevel1()
	{
		return LEVEL_1;
	}
}
