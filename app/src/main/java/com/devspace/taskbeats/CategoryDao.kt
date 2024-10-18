package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDao {

    @Query("Select * From categoryentity")
    fun getAll(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categoriesEntity: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inset(categoryEntity: CategoryEntity)
}