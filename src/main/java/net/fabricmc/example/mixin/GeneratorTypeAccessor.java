package net.fabricmc.example.mixin;

import net.fabricmc.example.BackroomsMod;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.world.GeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GeneratorType.class)
public interface GeneratorTypeAccessor {
	@Accessor("VALUES")
	public static List<GeneratorType> getValues() {
		throw new AssertionError();
	}
}
