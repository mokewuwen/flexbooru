package onlymash.flexbooru.data.model.sankaku

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import onlymash.flexbooru.common.Values.BOORU_TYPE_SANKAKU
import onlymash.flexbooru.data.model.common.Tag

@Serializable
data class TagSankaku(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: Int,
    @SerialName("count")
    val count: Int
) {
    fun toTag(): Tag {
        return Tag(
            booruType = BOORU_TYPE_SANKAKU,
            id = id,
            name = name,
            category = type,
            count = count
        )
    }
}