package com.minecraftman.customplaceholders;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CustomPlaceholder {
	/*
	STATIC STUFF
	 */
	
	private static final HashMap<String, CustomPlaceholder> placeholderMap = new HashMap<>();
	private static final List<String> nonPerPlayerPlaceholderCache = new ArrayList<>();
	
	protected static String resultOf(String placeholder, Player player) {
		CustomPlaceholder customPlaceholder = placeholderMap.get(placeholder);
		if (customPlaceholder == null) return "%" + placeholder + "%";
		return customPlaceholder.get(player);
	}
	
	public static double getRawValue(@NotNull CustomPlaceholder customPlaceholder, Player player) {
		return customPlaceholder.getRaw(player);
	}
	
	public static CustomPlaceholder getCustomPlaceholderInstance(String placeholder) {
		return placeholderMap.get(placeholder);
	}
	
	public static List<String> getNonPerPlayerPlaceholders() {
		if (nonPerPlayerPlaceholderCache.isEmpty()) {
			for (CustomPlaceholder placeholder : placeholderMap.values()) {
				if (!(placeholder instanceof DynamicPerPlayerPlaceholder)) {
					nonPerPlayerPlaceholderCache.add(placeholder.getPlaceholder());
				}
			}
		}
		return nonPerPlayerPlaceholderCache;
	}
	
	/*
	PLACEHOLDER INSTANCE STUFF
	 */
	
	public abstract String get(Player player);
	public abstract double getRaw(Player player);
	
	private final String placeholder;
	public CustomPlaceholder(String placeholder) {
		placeholderMap.put(placeholder, this);
		this.placeholder = placeholder;
	}
	
	public String getPlaceholder() {
		return placeholder;
	}
	
	protected static class StaticPlaceholder extends CustomPlaceholder {
		private final String value;
		private final DoubleSupplier raw;
		
		public StaticPlaceholder(String placeholder, Object value, DoubleSupplier raw) {
			super(placeholder);
			this.value = String.valueOf(value);
			this.raw = raw;
		}
		
		public StaticPlaceholder(String placeholder, Object value) {
			this(placeholder, value, null);
		}
		
		@Override
		public String get(@Nullable Player player) {
			return value;
		}
		
		@Override
		public double getRaw(Player player) {
			if (raw != null) return raw.getAsDouble();

			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid input '" + value + "' - expected a number! (raw value unset in code)");
			}
		}
	}
	
	protected static class DynamicGlobalPlaceholder extends CustomPlaceholder {
		private final Supplier<?> value;
		private final DoubleSupplier raw;
		
		public DynamicGlobalPlaceholder(String placeholder, Supplier<?> value, DoubleSupplier raw) {
			super(placeholder);
			this.value = value;
			this.raw = raw;
		}
		
		public DynamicGlobalPlaceholder(String placeholder, Supplier<?> value) {
			this(placeholder, value, null);
		}
		
		@Override
		public String get(@Nullable Player player) {
			return String.valueOf(value.get());
		}
		
		@Override
		public double getRaw(Player player) {
			if (raw != null) return raw.getAsDouble();

			String val = String.valueOf(value.get());
			try {
				return Double.parseDouble(val);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid input '" + val + "' - expected a number! (raw value unset in code)");
			}
		}
	}
	
	protected static class DynamicPerPlayerPlaceholder extends CustomPlaceholder {
		private final Function<Player, ?> value;
		private final DoubleSupplier raw;
		
		public DynamicPerPlayerPlaceholder(String placeholder, Function<Player, ?> value, DoubleSupplier raw) {
			super(placeholder);
			this.value = value;
			this.raw = raw;
		}
		
		public DynamicPerPlayerPlaceholder(String placeholder, Function<Player, ?> value) {
			this(placeholder, value, null);
		}
		
		@Override
		public String get(@NotNull Player player) {
			return String.valueOf(value.apply(player));
		}
		
		@Override
		public double getRaw(Player player) {
			if (raw != null) return raw.getAsDouble();
			String val = String.valueOf(value.apply(player));
			try {
				return Double.parseDouble(val);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid input '" + val + "' - expected a number! (raw value unset in code)");
			}
		}
	}
}
