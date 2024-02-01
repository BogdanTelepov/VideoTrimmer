package kg.dev.videoeditor.adapter

sealed class BaseCells {
    data class Item<T>(val data: T) : BaseCells()
    object CircleShapeItem : BaseCells()
}