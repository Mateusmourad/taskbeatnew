package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity() : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var tasks = listOf<TaskUiData>()


    private val categoryAdapter = CategoryListAdapter()
    private val taskAdapter  by lazy {
        TaskListAdapter()
    }

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskBeatDataBase::class.java, "database-task-beat"
        ).build()
    }

    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()
    }

    private val taskDao: TaskDao by lazy {
        db.getTaskDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        insertDefaulCategory()
        // insertDefaultTasks()

        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)
        val fabCreatTask = findViewById<FloatingActionButton>(R.id.fab_creat_task)

        fabCreatTask.setOnClickListener {
            showCreateUpdateTaskBottomSheet()

        }

        taskAdapter.setOnClickListener { task ->
            showCreateUpdateTaskBottomSheet(task)
        }

        categoryAdapter.setOnLongListener {
            categoryToBeDeleted ->
//            val categoryEntityToBeDeleted = CategoryEntity(
//                categoryToBeDeleted.name,
//                categoryToBeDeleted.isSelected
//            )
//            deleteCategory(categoryEntityToBeDeleted)

            if(categoryToBeDeleted.name != "+" && categoryToBeDeleted.name != "ALL"){
                //this.getString(R.string.category_delete_title)
                val tittle: String = "Important"
                val description: String = "By deleting a category you will delete all tasks with this"
                    // this.getString(R.string.category_delete_description)
                val btnText: String = "delete"
                    //this.getString(R.string.delete)


                showInfoDialog(
                    tittle,
                    description,
                    btnText
                ){
                    val categoryEntityToBeDeleted = CategoryEntity(
                        categoryToBeDeleted.name,
                        categoryToBeDeleted.isSelected
                    )
                    deleteCategory(categoryEntityToBeDeleted)
                }

            }

        } //

        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {

                val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->
                    val categoryEntity = CategoryEntity(
                        name = categoryName,
                        isSelected = false
                    )
                    insertCategory(categoryEntity)
                }

                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")


                //Snackbar.make(rvCategory, "+ is selected", Snackbar.LENGTH_LONG).show()

            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected -> item.copy(
                            isSelected = true
                        )

                        item.name == selected.name && item.isSelected -> item.copy(isSelected = true)
                        item.name != selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }

                    if (selected.name != "ALL") {
                        filterTaskByCategoryName(selected.name)
                    // && selected.name != "+") {


                    } else {
                        GlobalScope.launch(Dispatchers.IO){
                            getTasksFromDataBase()
                        }

                    }
                categoryAdapter.submitList(categoryTemp)

            }

        }

        rvCategory.adapter = categoryAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDataBase()
        }

        rvTask.adapter = taskAdapter

        GlobalScope.launch(Dispatchers.IO) {
            getTasksFromDataBase()
        }

        //taskAdapter.submitList(tasks)
    }

//    private fun insertDefaulCategory(){
//        val categoriesEntity = categories.map {
//            CategoryEntity(
//                name = it.name,
//                isSelected = it.isSelected
//            )
//        }
//        GlobalScope.launch(Dispatchers.IO) {
//            categoryDao.insertAll(categoriesEntity)
//        }
//    }

//    private fun insertDefaultTasks() {
//        val taskEntities = tasks.map {
//            TaskEntity(
//                name = it.name,
//                category = it.category
//            )
    //  }

    //    GlobalScope.launch(Dispatchers.IO){
    //   TaskDao.insertAll(taskEntities)
    // }
    //  }

    private fun  showInfoDialog(
    tittle:String,
    description: String,
    btnText: String,
    onClick: () -> Unit ){
        val infoBottomSheet = InfoBottomSheet(
            tittle = tittle,
            description = description,
            btnText = btnText,
            onClick
        )
        infoBottomSheet.show(
            supportFragmentManager,
            "InfoBottomSheet"
        )
    }

    private fun getCategoriesFromDataBase() {
        val categoriesFromDb: List<CategoryEntity> = categoryDao.getAll()
        val categoriesUiData = categoriesFromDb.map {
            CategoryUiData(
                name = it.name,
                isSelected = it.isSelected
            )
        }.toMutableList()
        categoriesUiData.add(
            CategoryUiData(
                name = "+",
                isSelected = false
            )
        )

        val categoryListTemp = mutableListOf(
            CategoryUiData(
                name = "ALL",
                isSelected = false,
            )

        )

        categoryListTemp.addAll(categoriesUiData)
        GlobalScope.launch(Dispatchers.Main) {
            categories = categoryListTemp
            categoryAdapter.submitList(categoriesUiData)
        }


    }

    private fun getTasksFromDataBase() {
            val taskFromDb: List<TaskEntity> = taskDao.getAll()
            val tasksUiData:List<TaskUiData> = taskFromDb.map {
                TaskUiData(
                  //  id = 0,
                    id = it.id,
                    name = it.name,
                    category = it.category
                )
            }

            GlobalScope.launch(Dispatchers.Main) {
                tasks = tasksUiData
                taskAdapter.submitList(tasksUiData)
            }
    }

    private fun insertCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.inset(categoryEntity)
            getCategoriesFromDataBase()
        }
    }


    private fun insertTask(taskEntity: TaskEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.insert(taskEntity)
            getTasksFromDataBase()

        }
    }

    private fun updateTask(taskEntity: TaskEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.update(taskEntity)
            getTasksFromDataBase()
        }
    }

    private fun deleteTask(taskEntity: TaskEntity){
        GlobalScope.launch(Dispatchers.IO){
            taskDao.delete(taskEntity)
            getTasksFromDataBase()
        }
    }

    private fun deleteCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch (Dispatchers.IO){
            val tasksToBeDeleted = taskDao.getAllByCategoryName(categoryEntity.name)
            taskDao.deleteAll(tasksToBeDeleted)
            categoryDao.delete(categoryEntity)
            getCategoriesFromDataBase()
            getTasksFromDataBase()
        }
    }

    private fun filterTaskByCategoryName(category: String){
        GlobalScope.launch(Dispatchers.IO) {
            val tasksFromDb: List<TaskEntity> = taskDao.getAllByCategoryName(category)
            val tasksUiData: List<TaskUiData> = tasksFromDb.map {
                TaskUiData(
                    id = it.id,
                    name = it.name,
                    category = it.category
                )
            }
            GlobalScope.launch(Dispatchers.IO){
                taskAdapter.submitList(tasksUiData)
            }
        }
    }



    private fun showCreateUpdateTaskBottomSheet(taskUiData: TaskUiData? = null) {
        val createTaskBottomSheet = CreateOrUpdateTaskBottomSheet(
            task = taskUiData,
            categoryList = categories,
            onCreateClicked = { taskToBeCreated ->
                    val taskEntityToBeInsert = TaskEntity(
                        name = taskToBeCreated.name,
                        category = taskToBeCreated.category
                    )
                insertTask(taskEntityToBeInsert)
            },
            onUpdateClicked = { taskToBeUpdated ->
                val taskEntityToBeUpdate = TaskEntity(
                    id = taskToBeUpdated.id,
                    name = taskToBeUpdated.name,
                    category = taskToBeUpdated.category
                )
                updateTask(taskEntityToBeUpdate)
            },
            onDeleteClicked = { taskToBeDeleted ->
                val taskEntityToBeDeleted = TaskEntity(
                    id = taskToBeDeleted.id,
                    name = taskToBeDeleted.name,
                    category = taskToBeDeleted.category
                )
                deleteTask(taskEntityToBeDeleted)
            }
        )

        createTaskBottomSheet.show(
            supportFragmentManager,
            "createTaskBottomSheet"
        )
    }
}




//
//val categories = listOf(
//    CategoryUiData(
//        name = "ALL",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "STUDY",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "WORK",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "WELLNESS",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "HOME",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "HEALTH",
//        isSelected = false
//    ),
//)

//val tasks = listOf(
//    TaskUiData(
//        "Ler 10 páginas do livro atual",
//        "STUDY"
//    ),
//    TaskUiData(
//        "45 min de treino na academia",
//        "HEALTH"
//    ),
//    TaskUiData(
//        "Correr 5km",
//        "HEALTH"
//    ),
//    TaskUiData(
//        "Meditar por 10 min",
//        "WELLNESS"
//    ),
//    TaskUiData(
//        "Silêncio total por 5 min",
//        "WELLNESS"
//    ),
//    TaskUiData(
//        "Descer o livo",
//        "HOME"
//    ),
//    TaskUiData(
//        "Tirar caixas da garagem",
//        "HOME"
//    ),
//    TaskUiData(
//        "Lavar o carro",
//        "HOME"
//    ),
//    TaskUiData(
//        "Gravar aulas DevSpace",
//        "WORK"
//    ),
//    TaskUiData(
//        "Criar planejamento de vídeos da semana",
//        "WORK"
//    ),
//    TaskUiData(
//        "Soltar reels da semana",
//        "WORK"
//    ),
//)
