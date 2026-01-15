package ru.webrelab.kie.cerealstorage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CerealStorageImplTest {
    private val storage = CerealStorageImpl(10f, 20f)

    @Test
    fun `should throw if containerCapacity is negative`() {
        // Проверяет требование: выбрасывает IllegalArgumentException если containerCapacity < 0
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(-4f, 10f)
        }
    }

    @Test
    fun `addCereal - should throw on negative amount`() {
        // Проверяет требование: выбрасывает IllegalArgumentException если amount < 0
        assertThrows<IllegalArgumentException> {
            storage.addCereal(Cereal.BUCKWHEAT, -1f)
        }
    }

    @Test
    fun `addCereal - should add to existing container and return 0`() {
        // Проверяет требование: добавляет к существующему контейнеру и возвращает 0
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        val result = storage.addCereal(Cereal.BUCKWHEAT, 3f)
        assertEquals(0f, result)
        assertEquals(8f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `addCereal - should create new container and return 0`() {
        // Проверяет требование: создаёт новый контейнер если его не было и возвращает 0
        val result = storage.addCereal(Cereal.RICE, 7f)
        assertEquals(0f, result)
        assertEquals(7f, storage.getAmount(Cereal.RICE))
    }

    @Test
    fun `addCereal - should return remainder when container full`() {
        // Проверяет требование: возвращает остаток если контейнер заполнился
        storage.addCereal(Cereal.MILLET, 10f)
        val result = storage.addCereal(Cereal.MILLET, 5f)
        assertEquals(5f, result)
        assertEquals(10f, storage.getAmount(Cereal.MILLET))
    }

    @Test
    fun `addCereal - should throw when no space for new container`() {
        // Проверяет требование: выбрасывает IllegalStateException если нет места для нового контейнера
        storage.addCereal(Cereal.BUCKWHEAT, 1f)
        storage.addCereal(Cereal.RICE, 1f)
        assertThrows<IllegalStateException> {
            storage.addCereal(Cereal.PEAS, 1f)
        }
    }

    @Test
    fun `getCereal - should throw on negative amount`() {
        // Проверяет требование: выбрасывает IllegalArgumentException если amount < 0
        assertThrows<IllegalArgumentException> {
            storage.getCereal(Cereal.BUCKWHEAT, -1f)
        }
    }

    @Test
    fun `getCereal - should return requested amount`() {
        // Проверяет требование: возвращает запрошенное количество если его достаточно
        storage.addCereal(Cereal.BUCKWHEAT, 8f)
        val result = storage.getCereal(Cereal.BUCKWHEAT, 3f)
        assertEquals(3f, result)
        assertEquals(5f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `getCereal - should return container remainder if less than requested`() {
        // Проверяет требование: возвращает остаток содержимого если крупы меньше запрошенного
        storage.addCereal(Cereal.RICE, 4f)
        val result = storage.getCereal(Cereal.RICE, 10f)
        assertEquals(4f, result)
        assertEquals(0f, storage.getAmount(Cereal.RICE))
    }

    @Test
    fun `getCereal - should return 0 if container empty`() {
        // Проверяет поведение при пустом контейнере
        val result = storage.getCereal(Cereal.MILLET, 5f)
        assertEquals(0f, result)
    }

    @Test
    fun `removeContainer - should return true only for empty container`() {
        // Проверяет требование: true только для пустого контейнера, false если не пуст или отсутствует
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        assertFalse(storage.removeContainer(Cereal.BUCKWHEAT))
        storage.getCereal(Cereal.BUCKWHEAT, 5f)
        assertTrue(storage.removeContainer(Cereal.BUCKWHEAT))
        assertEquals(0f, storage.getAmount(Cereal.BUCKWHEAT))
    }

    @Test
    fun `getAmount - should return 0 for missing container`() {
        // Проверяет требование: возвращает 0 если контейнера нет
        assertEquals(0f, storage.getAmount(Cereal.PEAS))
    }

    @Test
    fun `getSpace - should throw for missing container`() {
        // Проверяет требование: выбрасывает IllegalStateException если контейнера нет
        assertThrows<IllegalStateException> {
            storage.getSpace(Cereal.BULGUR)
        }
    }

    @Test
    fun `getSpace - should return correct free space`() {
        // Проверяет требование: возвращает свободное место с учётом текущей заполненности
        storage.addCereal(Cereal.BUCKWHEAT, 3f)
        assertEquals(7f, storage.getSpace(Cereal.BUCKWHEAT))
    }
}