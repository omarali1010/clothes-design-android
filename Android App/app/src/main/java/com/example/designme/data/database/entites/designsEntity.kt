package com.example.designme.data.database.entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.designme.models.designs

/**
 * the table which we will save the main Designs into
 * which will have just 1 row including all the designs
 */
@Entity(tableName = "DESIGNS_TABLE")
class designsEntity(
    var designs: designs
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}