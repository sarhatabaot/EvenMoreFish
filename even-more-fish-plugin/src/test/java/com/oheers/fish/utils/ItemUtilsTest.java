package com.oheers.fish.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class ItemUtilsTest {

    @Test
    void testGetMaterial_withValidName_returnsCorrectMaterial() {
        assertEquals(Material.DIRT, ItemUtils.getMaterial("dirt"));
        assertEquals(Material.DIAMOND_SWORD, ItemUtils.getMaterial("DIAMOND_SWORD"));
        assertEquals(Material.STONE, ItemUtils.getMaterial("Stone"));
    }

    @Test
    void testGetMaterial_withInvalidName_returnsNull() {
        assertNull(ItemUtils.getMaterial("not_a_material"));
        assertNull(ItemUtils.getMaterial(""));
    }

    @Test
    void testGetMaterial_withNullName_returnsNull() {
        assertNull(ItemUtils.getMaterial(null));
    }

    @Test
    void testGetMaterial_withDefaultMaterial_returnsMaterialOrDefault() {
        assertEquals(Material.DIRT, ItemUtils.getMaterial("dirt", Material.STONE));
        assertEquals(Material.STONE, ItemUtils.getMaterial("invalid_name", Material.STONE));
        assertEquals(Material.STONE, ItemUtils.getMaterial(null, Material.STONE));
    }

    @Test
    void testIsValidMaterial_withValidNames() {
        assertTrue(ItemUtils.isValidMaterial("dirt"));
        assertTrue(ItemUtils.isValidMaterial("DIAMOND_SWORD"));
        assertTrue(ItemUtils.isValidMaterial("Stone"));
    }

    @Test
    void testIsValidMaterial_withInvalidNames() {
        assertFalse(ItemUtils.isValidMaterial(null));
        assertFalse(ItemUtils.isValidMaterial(""));
        assertFalse(ItemUtils.isValidMaterial("not_a_material"));
    }
}
