/*
 * Copyright (C) 2019. by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package onlymash.flexbooru.data.model.common

import androidx.room.*
import onlymash.flexbooru.data.model.common.Booru

@Entity(tableName = "muzei", indices = [(Index(value = ["booru_uid", "keyword"], unique = true))],
    foreignKeys = [(ForeignKey(
        entity = Booru::class,
        parentColumns = ["uid"],
        childColumns = ["booru_uid"],
        onDelete = ForeignKey.CASCADE))])
data class Muzei(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Long = 0L,
    @ColumnInfo(name = "booru_uid")
    val booruUid: Long,
    @ColumnInfo(name = "keyword")
    var keyword: String
)