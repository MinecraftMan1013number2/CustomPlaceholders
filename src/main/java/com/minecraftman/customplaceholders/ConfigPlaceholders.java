package com.minecraftman.customplaceholders;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigPlaceholders {
	// Static values
	public static void register(@NotNull String placeholder, @NotNull Object constantVal) {
		new CustomPlaceholder.StaticPlaceholder(placeholder, constantVal);
	}
	
	
	// Dynamic global values
	public static <T> void register(@NotNull String placeholder, @NotNull Supplier<T> supplier, DoubleSupplier raw) {
		new CustomPlaceholder.DynamicGlobalPlaceholder(placeholder, supplier, raw);
	}
	public static <T> void register(@NotNull String placeholder, @NotNull Supplier<T> supplier) {
		new CustomPlaceholder.DynamicGlobalPlaceholder(placeholder, supplier, null);
	}
	
	// Dynamic per-player values
	public static void register(@NotNull String placeholder, @NotNull Function<Player, ?> function, DoubleSupplier raw) {
		new CustomPlaceholder.DynamicPerPlayerPlaceholder(placeholder, function, raw);
	}
	public static void register(@NotNull String placeholder, @NotNull Function<Player, ?> function) {
		new CustomPlaceholder.DynamicPerPlayerPlaceholder(placeholder, function, null);
	}
	
	// Get the result of a placeholder
	public static String get(String placeholder, Player input) {
		return CustomPlaceholder.resultOf(placeholder, input);
	}
	
	public static String replace(String input, Player player) {
		Matcher matcher = Pattern.compile("%([^%]*)%").matcher(input);
		
		while (matcher.find()) {
			String placeholderFound = matcher.group(1);
			String replacement = get(placeholderFound, player);
			input = input.replace("%" + placeholderFound + "%", replacement);
		}
		return input;
	}
}