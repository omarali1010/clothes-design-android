package com.example.designme.data.database.entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.designme.models.designs
import com.example.designme.models.designsItem

/**
 * the table which will have the favourite designs
 */
@Entity(tableName = "FAVORITE_DESIGNS_TABLE")
class favEntity(
    @PrimaryKey(autoGenerate = false)
    var title: String,
    var design: designsItem
)