package com.rydvi.clean_vrn.ui.map

/**
 * Режимы редактирования карты: чтение, редактирование, добавление (создание)
 */
enum class MapEditMode {
    Reading, Edit, Add
}

/**
 * Места: ничего, другое место, туалет, мусор (отходы), место старта, зона проведения квеста
 */
enum class MapPlaceMode {
    Nothing {
        override fun getPlaceId() = 0
    },
    AnotherPlace {
        override fun getPlaceId() = 1
    },
    Toilet {
        override fun getPlaceId() = 2
    },
    GarbagePlace {
        override fun getPlaceId() = 3
    },
    StartPlace {
        override fun getPlaceId() = 4
    },
    QuestZone {
        override fun getPlaceId() = 5
    };

    abstract fun getPlaceId(): Int
}

/**
 * Режимы действия над маркерами: ничего, переименование, удаление, перемещение (маркера)
 */
enum class MarkerActions {
    Nothing, Rename, Remove, Move
}