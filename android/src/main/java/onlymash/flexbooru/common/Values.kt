package onlymash.flexbooru.common

object Values {
    const val DB_FILE_NAME = "flexbooru.db"

    const val BASE_URL = "http://fiepi.me"

    const val DATE_PATTERN_DAN = "yyyy-MM-dd'T'HH:mm:ss.sss"
    const val DATE_PATTERN_GEL = "EEE MMM dd HH:mm:ss Z yyyy"

    const val SCHEME_HTTP = "http"
    const val SCHEME_HTTPS = "https"

    const val BOORU_TYPE_DAN = 0
    const val BOORU_TYPE_MOE = 1
    const val BOORU_TYPE_DAN1 = 2
    const val BOORU_TYPE_GEL = 3
    const val BOORU_TYPE_SANKAKU = 4
    const val BOORU_TYPE_UNKNOWN = -1

    const val ONLY_FIELD_POSTS_DAN = "id,created_at,score,source,parent_id,preview_file_url,large_file_url,file_url,rating,image_width,image_height,updated_at,created_at,tag_string,tag_string_general,tag_string_character,tag_string_copyright,tag_string_artist,tag_string_meta,is_favorited?,children_ids,pixiv_id,file_size,fav_count,file_ext,uploader"

    const val PAGE_TYPE_POSTS = 0
    const val PAGE_TYPE_POPULAR = 1

    const val HASH_SALT_CONTAINED = "your-password"

    const val REQUEST_CODE_OPEN_DIRECTORY = 101
}