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

fun getPlaceById(placeId: Int): MapPlaceMode? = when (placeId) {
        MapPlaceMode.Nothing.getPlaceId() -> MapPlaceMode.Nothing
        MapPlaceMode.AnotherPlace.getPlaceId() -> MapPlaceMode.Nothing
        MapPlaceMode.Toilet.getPlaceId() -> MapPlaceMode.Nothing
        MapPlaceMode.GarbagePlace.getPlaceId() -> MapPlaceMode.Nothing
        MapPlaceMode.StartPlace.getPlaceId() -> MapPlaceMode.Nothing
        MapPlaceMode.QuestZone.getPlaceId() -> MapPlaceMode.QuestZone
        else -> null
    }


/**
 * Режимы действия над маркерами: ничего, переименование, удаление, перемещение (маркера)
 */
enum class MarkerActions {
    Nothing, Rename, Remove, Move
}

data class MarkerUnique(
    val placeMode: MapPlaceMode,
    val id: Long? = null
)

fun makeMarkerUnique(placeMode: MapPlaceMode, id: Long?): String {
    return "${placeMode.getPlaceId()} ${id?.let { it }.also { 0 }}"
}

fun parseMarkerUnique(tag: String): MarkerUnique {
    val parts = tag.split(" ")
    return MarkerUnique(placeMode = getPlaceById(parts[0].toInt())!!, id = parts[1].toLongOrNull())
}