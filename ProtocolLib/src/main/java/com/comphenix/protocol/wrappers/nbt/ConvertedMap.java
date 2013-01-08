/*
 *  ProtocolLib - Bukkit server library that allows access to the Minecraft protocol.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.protocol.wrappers.nbt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

abstract class ConvertedMap<Key, VInner, VOuter> extends AbstractConverted<VInner, VOuter> implements Map<Key, VOuter> {
	// Inner map
	private Map<Key, VInner> inner;

	public ConvertedMap(Map<Key, VInner> inner) {
		if (inner == null)
			throw new IllegalArgumentException("Inner map cannot be NULL.");
		this.inner = inner;
	}

	@Override
	public void clear() {
		inner.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return inner.containsKey(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containsValue(Object value) {
		return inner.containsValue(toInner((VOuter) value));
	}

	@Override
	public Set<Entry<Key, VOuter>> entrySet() {
		return new ConvertedSet<Entry<Key,VInner>, Entry<Key,VOuter>>(inner.entrySet()) {
			@Override
			protected Entry<Key, VInner> toInner(final Entry<Key, VOuter> outer) {
				return new Entry<Key, VInner>() {
					@Override
					public Key getKey() {
						return outer.getKey();
					}

					@Override
					public VInner getValue() {
						return ConvertedMap.this.toInner(outer.getValue());
					}

					@Override
					public VInner setValue(VInner value) {
						return ConvertedMap.this.toInner(outer.setValue(ConvertedMap.this.toOuter(value))); 
					}
					
					@Override
					public String toString() {
						return String.format("\"%s\": %s", getKey(), getValue());
					}
				};
			}
			
			@Override
			protected Entry<Key, VOuter> toOuter(final Entry<Key, VInner> inner) {
				return new Entry<Key, VOuter>() {
					@Override
					public Key getKey() {
						return inner.getKey();
					}

					@Override
					public VOuter getValue() {
						return ConvertedMap.this.toOuter(inner.getValue());
					}

					@Override
					public VOuter setValue(VOuter value) {
						return ConvertedMap.this.toOuter(inner.setValue(ConvertedMap.this.toInner(value))); 
					}
					
					@Override
					public String toString() {
						return String.format("\"%s\": %s", getKey(), getValue());
					}
				};
			}
		};
	}

	@Override
	public VOuter get(Object key) {
		return toOuter(inner.get(key));
	}

	@Override
	public boolean isEmpty() {
		return inner.isEmpty();
	}

	@Override
	public Set<Key> keySet() {
		return inner.keySet();
	}

	@Override
	public VOuter put(Key key, VOuter value) {
		return toOuter(inner.put(key, toInner(value)));
	}

	@Override
	public void putAll(Map<? extends Key, ? extends VOuter> m) {
		for (Entry<? extends Key, ? extends VOuter> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public VOuter remove(Object key) {
		return toOuter(inner.remove(key));
	}

	@Override
	public int size() {
		return inner.size();
	}

	@Override
	public Collection<VOuter> values() {
		return new ConvertedCollection<VInner, VOuter>(inner.values()) {
			@Override
			protected VOuter toOuter(VInner inner) {
				return ConvertedMap.this.toOuter(inner);
			}

			@Override
			protected VInner toInner(VOuter outer) {
				return ConvertedMap.this.toInner(outer);
			}
		};
	}
}
