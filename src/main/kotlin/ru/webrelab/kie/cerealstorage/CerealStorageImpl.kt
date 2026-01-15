package ru.webrelab.kie.cerealstorage

class CerealStorageImpl(
    override val containerCapacity: Float,
    override val storageCapacity: Float
) : CerealStorage {

    /**
     * Блок инициализации класса.
     * Выполняется сразу при создании объекта
     */
    init {
        require(containerCapacity >= 0f) {
            "Ёмкость контейнера не может быть отрицательной"
        }
        require(storageCapacity >= containerCapacity) {
            "Ёмкость хранилища не должна быть меньше ёмкости одного контейнера"
        }
    }

    private val storage = mutableMapOf<Cereal, Float>()
    private var totalContainersUsed: Int = 0

    override fun addCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0f) { "Количество не может быть отрицательным" }

        val currentAmount = storage.getOrDefault(cereal, 0f)

        // Если контейнер новый, проверяем место для нового контейнера
        if (currentAmount == 0f) {
            val requiredCapacity = totalContainersUsed * containerCapacity + containerCapacity
            if (requiredCapacity > storageCapacity) {
                throw IllegalStateException("Хранилище не позволяет разместить ещё один контейнер для новой крупы")
            }
        }

        val newAmount = currentAmount + amount

        if (newAmount > containerCapacity) {
            val spaceLeft = containerCapacity - currentAmount
            storage[cereal] = containerCapacity
            return amount - spaceLeft
        }

        storage[cereal] = newAmount
        if (currentAmount == 0f) {
            totalContainersUsed++
        }

        return 0f
    }

    override fun getCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0f) { "Количество не может быть отрицательным" }

        val currentAmount = storage[cereal] ?: 0f
        if (currentAmount == 0f) return 0f

        val actualAmount = minOf(amount, currentAmount)
        storage[cereal] = currentAmount - actualAmount
        return actualAmount
    }

    override fun removeContainer(cereal: Cereal): Boolean {
        val amount = storage[cereal]
        if (amount == 0f) {
            storage.remove(cereal)
            totalContainersUsed--
            return true
        }
        return false
    }

    override fun getAmount(cereal: Cereal): Float = storage.getOrDefault(cereal, 0f)

    override fun getSpace(cereal: Cereal): Float {
        val amount = storage[cereal] ?: 0f
        if (amount == 0f) {
            throw IllegalStateException("Проверяемый контейнер для крупы ${cereal.local} отсутствует")
        }
        return containerCapacity - amount
    }

    override fun toString(): String {
        val containersInfo = storage.map { (cereal, amount) ->
            "${cereal.local}: ${amount}/${containerCapacity}"
        }.joinToString(", ")

        return "Склад круп (${storage.size} контейнеров): $containersInfo | " +
                "Свободно: ${(storageCapacity - totalContainersUsed * containerCapacity)}/${storageCapacity}"
    }
}
